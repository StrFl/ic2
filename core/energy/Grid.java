/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 *  org.apache.logging.log4j.Level
 *  org.ejml.alg.dense.linsol.LinearSolver_B64_to_D64
 *  org.ejml.data.Complex64F
 *  org.ejml.data.DenseMatrix64F
 *  org.ejml.data.Matrix64F
 *  org.ejml.factory.DecompositionFactory
 *  org.ejml.factory.LinearSolverFactory
 *  org.ejml.interfaces.decomposition.EigenDecomposition
 *  org.ejml.ops.MatrixIO
 */
package ic2.core.energy;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.IC2;
import ic2.core.energy.EnergyNetGlobal;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.energy.GridCalculation;
import ic2.core.energy.GridInfo;
import ic2.core.energy.Node;
import ic2.core.energy.NodeLink;
import ic2.core.energy.NodeType;
import ic2.core.energy.StructureCache;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;
import org.ejml.alg.dense.linsol.LinearSolver_B64_to_D64;
import org.ejml.data.Complex64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.Matrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.decomposition.EigenDecomposition;
import org.ejml.ops.MatrixIO;

class Grid {
    private final int uid;
    private final EnergyNetLocal energyNet;
    private final Map<Integer, Node> nodes = new HashMap<Integer, Node>();
    private boolean hasNonZeroVoltages = false;
    private boolean lastVoltagesNeedUpdate = false;
    private final Set<Integer> activeSources = new HashSet<Integer>();
    private final Set<Integer> activeSinks = new HashSet<Integer>();
    private final StructureCache cache = new StructureCache();
    private Future<Iterable<Node>> calculation;
    private StructureCache.Data lastData = null;
    private boolean failed;

    Grid(EnergyNetLocal energyNet1) {
        this.uid = EnergyNetLocal.getNextGridUid();
        this.energyNet = energyNet1;
        energyNet1.grids.add(this);
    }

    public String toString() {
        return "Grid " + this.uid;
    }

    void add(Node node, Collection<Node> neighbors) {
        if (EnergyNetGlobal.debugGrid) {
            IC2.log.debug(LogCategory.EnergyNet, "%d Add %s to %s neighbors: %s.", this.uid, node, this, neighbors);
        }
        this.invalidate();
        assert (!this.nodes.isEmpty() || neighbors.isEmpty());
        assert (this.nodes.isEmpty() || !neighbors.isEmpty() || node.isExtraNode());
        assert (node.links.isEmpty());
        this.add(node);
        for (Node neighbor : neighbors) {
            assert (neighbor != node);
            assert (this.nodes.containsKey(neighbor.uid));
            double loss = (node.getInnerLoss() + neighbor.getInnerLoss()) / 2.0;
            NodeLink link = new NodeLink(node, neighbor, loss);
            node.links.add(link);
            neighbor.links.add(link);
        }
    }

    void remove(Node node) {
        Node neighbor;
        if (EnergyNetGlobal.debugGrid) {
            IC2.log.debug(LogCategory.EnergyNet, "%d Remove Node %s from %s with %d nodes.", this.uid, node, this, this.nodes.size());
        }
        this.invalidate();
        Iterator<NodeLink> it = node.links.iterator();
        while (it.hasNext()) {
            NodeLink link = it.next();
            neighbor = link.getNeighbor(node);
            boolean found = false;
            Iterator<NodeLink> it2 = neighbor.links.iterator();
            while (it2.hasNext()) {
                if (it2.next() != link) continue;
                it2.remove();
                found = true;
                break;
            }
            assert (found);
            if (!neighbor.links.isEmpty() || !neighbor.tile.removeExtraNode(neighbor)) continue;
            it.remove();
            this.nodes.remove(neighbor.uid);
            neighbor.clearGrid();
        }
        this.nodes.remove(node.uid);
        node.clearGrid();
        if (node.links.isEmpty()) {
            this.energyNet.grids.remove(this);
        } else if (node.links.size() > 1 && node.nodeType == NodeType.Conductor) {
            int i;
            ArrayList nodeTable = new ArrayList();
            for (i = 0; i < node.links.size(); ++i) {
                Node cNode;
                neighbor = node.links.get(i).getNeighbor(node);
                HashSet<Node> connectedNodes = new HashSet<Node>();
                LinkedList<Node> nodesToCheck = new LinkedList<Node>(Arrays.asList(neighbor));
                while ((cNode = (Node)nodesToCheck.poll()) != null) {
                    if (!connectedNodes.add(cNode) || cNode.nodeType != NodeType.Conductor) continue;
                    for (NodeLink link : cNode.links) {
                        Node nNode = link.getNeighbor(cNode);
                        if (connectedNodes.contains(nNode)) continue;
                        nodesToCheck.add(nNode);
                    }
                }
                nodeTable.add(connectedNodes);
            }
            assert (nodeTable.size() == node.links.size());
            for (i = 1; i < node.links.size(); ++i) {
                if (EnergyNetGlobal.debugGrid) {
                    IC2.log.debug(LogCategory.EnergyNet, "%d Checking net %d with %d nodes.", this.uid, i, ((Set)nodeTable.get(i)).size());
                }
                Set connectedNodes = (Set)nodeTable.get(i);
                Node neighbor2 = node.links.get(i).getNeighbor(node);
                assert (connectedNodes.contains(neighbor2));
                boolean split = true;
                for (int j = 0; j < i; ++j) {
                    Set cmpList = (Set)nodeTable.get(j);
                    if (!cmpList.contains(neighbor2)) continue;
                    if (EnergyNetGlobal.debugGrid) {
                        IC2.log.debug(LogCategory.EnergyNet, "%d Same as %d.", this.uid, j);
                    }
                    split = false;
                    break;
                }
                if (!split) continue;
                if (EnergyNetGlobal.debugGrid) {
                    IC2.log.debug(LogCategory.EnergyNet, "%d Moving nodes %s.", this.uid, connectedNodes);
                }
                Grid grid = new Grid(this.energyNet);
                for (Node cNode : connectedNodes) {
                    boolean needsExtraNode = false;
                    if (!cNode.links.isEmpty() && cNode.nodeType != NodeType.Conductor) {
                        for (int j = 0; j < i; ++j) {
                            Set cmpList = (Set)nodeTable.get(j);
                            if (!cmpList.contains(cNode)) continue;
                            needsExtraNode = true;
                            break;
                        }
                    }
                    if (needsExtraNode) {
                        if (EnergyNetGlobal.debugGrid) {
                            IC2.log.debug(LogCategory.EnergyNet, "%s Create extra Node for %s.", this.uid, cNode);
                        }
                        Node extraNode = new Node(this.energyNet, cNode.tile, cNode.nodeType);
                        cNode.tile.addExtraNode(extraNode);
                        Iterator<NodeLink> it2 = cNode.links.iterator();
                        while (it2.hasNext()) {
                            NodeLink link = it2.next();
                            if (!connectedNodes.contains(link.getNeighbor(cNode))) continue;
                            link.replaceNode(cNode, extraNode);
                            extraNode.links.add(link);
                            it2.remove();
                        }
                        assert (!extraNode.links.isEmpty());
                        grid.add(extraNode);
                        assert (extraNode.getGrid() != null);
                        continue;
                    }
                    if (EnergyNetGlobal.debugGrid) {
                        IC2.log.debug(LogCategory.EnergyNet, "%d Move Node %s.", this.uid, cNode);
                    }
                    assert (this.nodes.containsKey(cNode.uid));
                    this.nodes.remove(cNode.uid);
                    cNode.clearGrid();
                    grid.add(cNode);
                    assert (cNode.getGrid() != null);
                }
            }
        }
    }

    void merge(Grid grid, Map<Node, Node> nodeReplacements) {
        if (EnergyNetGlobal.debugGrid) {
            IC2.log.debug(LogCategory.EnergyNet, "%d Merge %s -> %s.", this.uid, grid, this);
        }
        assert (this.energyNet.grids.contains(grid));
        this.invalidate();
        for (Node node : grid.nodes.values()) {
            boolean found = false;
            if (node.nodeType != NodeType.Conductor) {
                for (Node node2 : this.nodes.values()) {
                    if (node2.tile != node.tile || node2.nodeType != node.nodeType) continue;
                    if (EnergyNetGlobal.debugGrid) {
                        IC2.log.debug(LogCategory.EnergyNet, "%d Merge Node %s -> %s.", this.uid, node, node2);
                    }
                    found = true;
                    for (NodeLink link : node.links) {
                        link.replaceNode(node, node2);
                        node2.links.add(link);
                    }
                    node2.tile.removeExtraNode(node);
                    nodeReplacements.put(node, node2);
                    break;
                }
            }
            if (found) continue;
            if (EnergyNetGlobal.debugGrid) {
                IC2.log.debug(LogCategory.EnergyNet, "%d Add Node %s.", this.uid, node);
            }
            node.clearGrid();
            this.add(node);
            assert (node.getGrid() != null);
        }
        if (EnergyNetGlobal.debugGrid) {
            IC2.log.debug(LogCategory.EnergyNet, "Remove %s.", grid);
        }
        this.energyNet.grids.remove(grid);
    }

    void prepareCalculation() {
        assert (this.calculation == null);
        if (!this.activeSources.isEmpty()) {
            this.activeSources.clear();
        }
        if (!this.activeSinks.isEmpty()) {
            this.activeSinks.clear();
        }
        ArrayList<Node> dynamicTierNodes = new ArrayList<Node>();
        int maxSourceTier = 0;
        for (Node node : this.nodes.values()) {
            assert (node.getGrid() == this);
            switch (node.nodeType) {
                case Source: {
                    IEnergySource source = (IEnergySource)node.tile.entity;
                    node.setTier(source.getSourceTier());
                    node.setAmount(source.getOfferedEnergy());
                    if (node.getAmount() > 0.0) {
                        this.activeSources.add(node.uid);
                        maxSourceTier = Math.max(node.getTier(), maxSourceTier);
                        break;
                    }
                    node.setAmount(0.0);
                    break;
                }
                case Sink: {
                    IEnergySink sink = (IEnergySink)node.tile.entity;
                    node.setTier(sink.getSinkTier());
                    node.setAmount(sink.getDemandedEnergy());
                    if (node.getAmount() > 0.0) {
                        this.activeSinks.add(node.uid);
                        if (node.getTier() != Integer.MAX_VALUE) break;
                        dynamicTierNodes.add(node);
                        break;
                    }
                    node.setAmount(0.0);
                    break;
                }
                case Conductor: {
                    node.setAmount(0.0);
                }
            }
            assert (node.getAmount() >= 0.0);
        }
        for (Node node : dynamicTierNodes) {
            node.setTier(maxSourceTier);
        }
    }

    Runnable startCalculation() {
        assert (this.calculation == null);
        if (this.failed) {
            IC2.log.warn(LogCategory.EnergyNet, "Calculation failed previously, skipping calculation.");
            return null;
        }
        boolean run = this.hasNonZeroVoltages;
        if (!this.activeSinks.isEmpty() && !this.activeSources.isEmpty()) {
            run = true;
            for (int nodeId : this.activeSources) {
                Node node = this.nodes.get(nodeId);
                int shareCount = 1;
                for (Node shared : node.tile.nodes) {
                    if (shared.uid == nodeId || shared.nodeType != NodeType.Source || shared.getGrid().activeSinks.isEmpty()) continue;
                    assert (shared.getGrid().activeSources.contains(shared.uid));
                    assert (shared.getGrid() != this);
                    ++shareCount;
                }
                node.setAmount(node.getAmount() / (double)shareCount);
                IEnergySource source = (IEnergySource)node.tile.entity;
                source.drawEnergy(node.getAmount());
                if (!EnergyNetGlobal.debugGrid) continue;
                IC2.log.debug(LogCategory.EnergyNet, "%d %s %f EU", this.uid, node, -node.getAmount());
            }
        }
        if (run) {
            RunnableFuture<Iterable<Node>> task = IC2.getInstance().threadPool.makeTask(new GridCalculation(this));
            this.calculation = task;
            return task;
        }
        return null;
    }

    void finishCalculation() {
        if (this.calculation == null) {
            return;
        }
        try {
            Iterable<Node> result = this.calculation.get();
            for (Node node : result) {
                ForgeDirection dir;
                if (!node.links.isEmpty()) {
                    dir = node.links.get(0).getDirFrom(node);
                } else {
                    dir = ForgeDirection.UNKNOWN;
                    if (EnergyNetGlobal.debugGrid) {
                        IC2.log.warn(LogCategory.EnergyNet, "Can't determine direction for %s.", node);
                        this.dumpNodeInfo(IC2.log.getPrintStream(LogCategory.EnergyNet, Level.DEBUG), false, node);
                        this.dumpGraph(false);
                    }
                }
                this.energyNet.addChange(node, dir, node.getAmount(), node.getVoltage());
            }
        }
        catch (InterruptedException e) {
            IC2.log.debug(LogCategory.EnergyNet, e, "Calculation interrupted.");
        }
        catch (ExecutionException e) {
            IC2.log.warn(LogCategory.EnergyNet, e, "Calculation failed.");
            PrintStream ps = IC2.log.getPrintStream(LogCategory.EnergyNet, Level.WARN);
            this.dumpStats(ps, false);
            this.dumpMatrix(ps, false, true, true);
            this.dumpGraph(false);
            this.failed = true;
        }
        this.calculation = null;
    }

    void updateStats() {
        if (this.lastVoltagesNeedUpdate) {
            this.lastVoltagesNeedUpdate = false;
            for (Node node : this.nodes.values()) {
                node.updateStats();
            }
        }
    }

    Iterable<Node> calculate() {
        this.lastVoltagesNeedUpdate = true;
        if (this.activeSources.isEmpty() || this.activeSinks.isEmpty()) {
            for (Node node : this.nodes.values()) {
                node.setVoltage(0.0);
                node.resetCurrents();
            }
            if (!this.activeSources.isEmpty()) {
                this.activeSources.clear();
            }
            if (!this.activeSinks.isEmpty()) {
                this.activeSinks.clear();
            }
            this.hasNonZeroVoltages = false;
            return new ArrayList<Node>();
        }
        StructureCache.Data data = this.calculateDistribution();
        this.calculateEffects(data);
        this.activeSources.clear();
        this.activeSinks.clear();
        ArrayList<Node> ret = new ArrayList<Node>();
        for (Node node : data.activeNodes) {
            if (node.nodeType != NodeType.Sink && node.nodeType != NodeType.Source) continue;
            ret.add(node.getTop());
        }
        this.hasNonZeroVoltages = true;
        return ret;
    }

    private void add(Node node) {
        node.setGrid(this);
        Node prev = this.nodes.put(node.uid, node);
        if (prev != null) {
            throw new IllegalStateException("duplicate node uid, new " + node + ", old " + prev);
        }
    }

    private void invalidate() {
        this.finishCalculation();
        this.cache.clear();
    }

    private StructureCache.Data calculateDistribution() {
        StructureCache.Data data;
        long time = System.nanoTime();
        this.lastData = data = this.cache.get(this.activeSources, this.activeSinks);
        if (!data.isInitialized) {
            this.copyForOptimize(data);
            this.optimize(data);
            Grid.determineEmittingNodes(data);
            int size = data.activeNodes.size();
            data.networkMatrix = new DenseMatrix64F(size, size);
            data.sourceMatrix = new DenseMatrix64F(size, 1);
            data.resultMatrix = new DenseMatrix64F(size, 1);
            data.solver = LinearSolverFactory.symmPosDef((int)size);
            if (!EnergyNetLocal.useLinearTransferModel) {
                Grid.populateNetworkMatrix(data);
                Grid.initializeSolver(data);
                if (data.solver instanceof LinearSolver_B64_to_D64) {
                    data.networkMatrix = null;
                }
            }
            data.isInitialized = true;
        }
        if (EnergyNetLocal.useLinearTransferModel) {
            Grid.populateNetworkMatrix(data);
            Grid.initializeSolver(data);
        }
        this.populateSourceMatrix(data);
        if (EnergyNetGlobal.debugGridVerbose) {
            this.dumpMatrix(IC2.log.getPrintStream(LogCategory.EnergyNet, Level.TRACE), false, true, false);
        }
        data.solver.solve((Matrix64F)data.sourceMatrix, (Matrix64F)data.resultMatrix);
        assert (!data.solver.modifiesB());
        if (EnergyNetGlobal.debugGridVerbose) {
            this.dumpMatrix(IC2.log.getPrintStream(LogCategory.EnergyNet, Level.TRACE), false, false, true);
        }
        if (EnergyNetGlobal.debugGrid) {
            time = System.nanoTime() - time;
            IC2.log.debug(LogCategory.EnergyNet, "%d The distribution calculation took %d us.", this.uid, time / 1000L);
        }
        return data;
    }

    private static void initializeSolver(StructureCache.Data data) {
        if (!data.solver.setA((Matrix64F)data.networkMatrix)) {
            int size = data.networkMatrix.numCols;
            if (data.solver.modifiesA()) {
                Grid.populateNetworkMatrix(data);
            }
            data.solver = LinearSolverFactory.linear((int)size);
            if (!data.solver.setA((Matrix64F)data.networkMatrix)) {
                EigenDecomposition ed;
                if (data.solver.modifiesA()) {
                    Grid.populateNetworkMatrix(data);
                }
                if ((ed = DecompositionFactory.eig((int)size, (boolean)false)).decompose((Matrix64F)data.networkMatrix)) {
                    int complex = size;
                    int nonPositive = size;
                    StringBuilder sb = new StringBuilder("Eigen values: ");
                    for (int i = 0; i < size; ++i) {
                        Complex64F ev = ed.getEigenvalue(i);
                        if (ev.isReal()) {
                            --complex;
                        }
                        if (ev.real > 0.0) {
                            --nonPositive;
                        }
                        if (i != 0) {
                            sb.append(", ");
                        }
                        sb.append(ev);
                    }
                    IC2.log.info(LogCategory.EnergyNet, sb.toString());
                    IC2.log.info(LogCategory.EnergyNet, "Total: %d, complex: %d, non positive: %d", size, complex, nonPositive);
                } else {
                    IC2.log.info(LogCategory.EnergyNet, "Unable to compute the eigen values.");
                }
                if (ed.inputModified()) {
                    Grid.populateNetworkMatrix(data);
                }
                throw new RuntimeException("Can't decompose network matrix.");
            }
        }
    }

    private void calculateEffects(StructureCache.Data data) {
        long time = System.nanoTime();
        for (Node node : this.nodes.values()) {
            node.setVoltage(Double.NaN);
            node.resetCurrents();
        }
        block5: for (int row = 0; row < data.activeNodes.size(); ++row) {
            Node node;
            node = data.activeNodes.get(row);
            node.setVoltage(data.resultMatrix.get(row));
            switch (node.nodeType) {
                case Source: {
                    double current;
                    if (EnergyNetLocal.useLinearTransferModel) {
                        current = data.sourceMatrix.get(row) - node.getVoltage() / node.getResistance();
                        double actualAmount = current * node.getVoltage();
                        assert (actualAmount >= 0.0) : actualAmount + " (u=" + node.getVoltage() + ")";
                        assert (actualAmount <= node.getAmount()) : actualAmount + " <= " + node.getAmount() + " (u=" + node.getVoltage() + ")";
                        node.setAmount(actualAmount - node.getAmount());
                    } else {
                        current = node.getAmount();
                        node.setAmount(0.0);
                    }
                    assert (node.getAmount() <= 0.0);
                    if (!EnergyNetGlobal.debugGrid) continue block5;
                    IC2.log.debug(LogCategory.EnergyNet, "%d %s %f EU, %f V, %f A.", this.uid, node, -node.getAmount(), node.getVoltage(), -current);
                    continue block5;
                }
                case Sink: {
                    double current;
                    if (EnergyNetLocal.useLinearTransferModel) {
                        current = node.getVoltage() / node.getResistance();
                        node.setAmount(node.getVoltage() * current);
                    } else {
                        current = node.getVoltage();
                        node.setAmount(current);
                    }
                    assert (node.getAmount() >= 0.0);
                    if (!EnergyNetGlobal.debugGrid) continue block5;
                    IC2.log.debug(LogCategory.EnergyNet, "%d %s %f EU, %f V, %f A.", this.uid, node, node.getAmount(), node.getVoltage(), current);
                    continue block5;
                }
            }
        }
        HashSet<NodeLink> visitedLinks = EnergyNetGlobal.verifyGrid() ? new HashSet<NodeLink>() : null;
        for (Node node : data.activeNodes) {
            for (NodeLink link : node.links) {
                if (link.nodeA != node) continue;
                Node nodeA = link.nodeA.getTop();
                Node nodeB = link.nodeB.getTop();
                double totalLoss = link.loss;
                for (Node skipped : link.skippedNodes) {
                    assert (skipped.nodeType == NodeType.Conductor);
                    if (!Double.isNaN((skipped = skipped.getTop()).getVoltage())) {
                        assert (false);
                        break;
                    }
                    NodeLink link2 = nodeA.getConnectionTo(skipped);
                    assert (link2 != null);
                    if (EnergyNetGlobal.verifyGrid()) assert (visitedLinks.add(link2));
                    skipped.setVoltage(Util.lerp(nodeA.getVoltage(), nodeB.getVoltage(), link2.loss / totalLoss));
                    link2.updateCurrent();
                    nodeA = skipped;
                    totalLoss -= link2.loss;
                }
                nodeA.getConnectionTo(nodeB).updateCurrent();
            }
        }
        time = System.nanoTime() - time;
        if (EnergyNetGlobal.debugGrid) {
            IC2.log.debug(LogCategory.EnergyNet, "%d The effect calculation took %d us.", this.uid, time / 1000L);
        }
    }

    private void copyForOptimize(StructureCache.Data data) {
        data.optimizedNodes = new HashMap<Integer, Node>();
        for (Node node : this.nodes.values()) {
            assert (!node.links.isEmpty());
            if (!(node.getAmount() > 0.0) && node.nodeType != NodeType.Conductor) continue;
            assert (node.nodeType != NodeType.Sink || this.activeSinks.contains(node.uid));
            assert (node.nodeType != NodeType.Source || this.activeSources.contains(node.uid));
            assert (node.getGrid() != null);
            data.optimizedNodes.put(node.uid, new Node(node));
        }
        for (Node node : data.optimizedNodes.values()) {
            assert (!node.links.isEmpty());
            assert (node.getGrid() == this);
            ListIterator<NodeLink> it = node.links.listIterator();
            while (it.hasNext()) {
                NodeLink link = it.next();
                Node neighbor = link.getNeighbor(node.uid);
                assert (neighbor.getGrid() == this);
                if ((neighbor.nodeType == NodeType.Sink || neighbor.nodeType == NodeType.Source) && neighbor.getAmount() <= 0.0) {
                    it.remove();
                    continue;
                }
                if (link.nodeA.uid == node.uid) {
                    link.nodeA = data.optimizedNodes.get(link.nodeA.uid);
                    link.nodeB = data.optimizedNodes.get(link.nodeB.uid);
                    assert (link.nodeA != null && link.nodeB != null);
                    ArrayList<Node> newSkippedNodes = new ArrayList<Node>();
                    for (Node skippedNode : link.skippedNodes) {
                        newSkippedNodes.add(data.optimizedNodes.get(skippedNode.uid));
                    }
                    link.skippedNodes = newSkippedNodes;
                    continue;
                }
                assert (link.nodeB.uid == node.uid);
                boolean foundReverseLink = false;
                for (NodeLink reverseLink : data.optimizedNodes.get((Object)Integer.valueOf((int)link.nodeA.uid)).links) {
                    assert (reverseLink.nodeA.uid != node.uid);
                    if (reverseLink.nodeB.uid != node.uid || node.links.contains(reverseLink)) continue;
                    assert (reverseLink.nodeA.uid == link.nodeA.uid);
                    foundReverseLink = true;
                    it.set(reverseLink);
                    break;
                }
                assert (foundReverseLink);
            }
        }
        if (EnergyNetGlobal.verifyGrid()) {
            for (Node node : data.optimizedNodes.values()) {
                assert (!node.links.isEmpty());
                for (NodeLink link : node.links) {
                    if (!data.optimizedNodes.containsValue(link.nodeA)) {
                        IC2.log.debug(LogCategory.EnergyNet, "%d Link %s is broken.", this.uid, link);
                    }
                    assert (data.optimizedNodes.containsValue(link.nodeA));
                    assert (data.optimizedNodes.containsValue(link.nodeB));
                    assert (link.nodeA != link.nodeB);
                    assert (link.getNeighbor((Node)node).links.contains(link));
                }
            }
            Iterator<Object> iterator = this.activeSources.iterator();
            while (iterator.hasNext()) {
                int uid = (Integer)iterator.next();
                assert (data.optimizedNodes.containsKey(uid));
            }
            iterator = this.activeSinks.iterator();
            while (iterator.hasNext()) {
                int uid = (Integer)iterator.next();
                assert (data.optimizedNodes.containsKey(uid));
            }
        }
    }

    private void optimize(StructureCache.Data data) {
        int removed;
        do {
            removed = 0;
            Iterator<Node> it = data.optimizedNodes.values().iterator();
            while (it.hasNext()) {
                Node node = it.next();
                if (node.nodeType != NodeType.Conductor) continue;
                if (node.links.size() < 2) {
                    it.remove();
                    ++removed;
                    for (NodeLink link : node.links) {
                        boolean found = false;
                        Iterator<NodeLink> it2 = link.getNeighbor((Node)node).links.iterator();
                        while (it2.hasNext()) {
                            if (it2.next() != link) continue;
                            found = true;
                            it2.remove();
                            break;
                        }
                        assert (found);
                    }
                    continue;
                }
                if (node.links.size() != 2) continue;
                it.remove();
                ++removed;
                NodeLink linkA = node.links.get(0);
                NodeLink linkB = node.links.get(1);
                Node neighborA = linkA.getNeighbor(node);
                Node neighborB = linkB.getNeighbor(node);
                if (neighborA == neighborB) {
                    neighborA.links.remove(linkA);
                    neighborB.links.remove(linkB);
                    continue;
                }
                linkA.loss += linkB.loss;
                if (linkA.nodeA == node) {
                    linkA.nodeA = neighborB;
                    linkA.dirFromA = linkB.getDirFrom(neighborB);
                    if (linkB.nodeA == node) {
                        assert (linkB.nodeB == neighborB);
                        Collections.reverse(linkB.skippedNodes);
                    } else assert (linkB.nodeB == node && linkB.nodeA == neighborB);
                    linkB.skippedNodes.add(node);
                    linkB.skippedNodes.addAll(linkA.skippedNodes);
                    linkA.skippedNodes = linkB.skippedNodes;
                } else {
                    linkA.nodeB = neighborB;
                    linkA.dirFromB = linkB.getDirFrom(neighborB);
                    if (linkB.nodeB == node) {
                        assert (linkB.nodeA == neighborB);
                        Collections.reverse(linkB.skippedNodes);
                    } else assert (linkB.nodeA == node && linkB.nodeB == neighborB);
                    linkA.skippedNodes.add(node);
                    linkA.skippedNodes.addAll(linkB.skippedNodes);
                }
                assert (linkA.nodeA != linkA.nodeB);
                assert (linkA.nodeA == neighborA || linkA.nodeB == neighborA);
                assert (linkA.nodeA == neighborB || linkA.nodeB == neighborB);
                boolean found = false;
                ListIterator<NodeLink> it2 = neighborB.links.listIterator();
                while (it2.hasNext()) {
                    if (it2.next() != linkB) continue;
                    found = true;
                    it2.set(linkA);
                    break;
                }
                assert (found);
            }
        } while (removed > 0);
        if (EnergyNetGlobal.verifyGrid()) {
            for (Node node : data.optimizedNodes.values()) {
                assert (!node.links.isEmpty());
                for (NodeLink link : node.links) {
                    List<Node> skippedNodes;
                    if (!data.optimizedNodes.containsValue(link.nodeA)) {
                        IC2.log.debug(LogCategory.EnergyNet, "%d Link %s is broken.", this.uid, link);
                    }
                    assert (data.optimizedNodes.containsValue(link.nodeA));
                    assert (data.optimizedNodes.containsValue(link.nodeB));
                    assert (!this.nodes.containsValue(link.nodeA));
                    assert (!this.nodes.containsValue(link.nodeB));
                    assert (this.nodes.containsValue(link.nodeA.getTop()));
                    assert (this.nodes.containsValue(link.nodeB.getTop()));
                    assert (link.nodeA != link.nodeB);
                    assert (link.nodeA == node || link.nodeB == node);
                    assert (link.getNeighbor((Node)node).links.contains(link));
                    assert (!link.skippedNodes.contains(link.nodeA));
                    assert (!link.skippedNodes.contains(link.nodeB));
                    assert (Collections.disjoint(link.skippedNodes, data.optimizedNodes.values()));
                    assert (Collections.disjoint(link.skippedNodes, this.nodes.values()));
                    assert (new HashSet<Node>(link.skippedNodes).size() == link.skippedNodes.size());
                    Node start = node.getTop();
                    if (link.nodeA == node) {
                        skippedNodes = link.skippedNodes;
                    } else {
                        skippedNodes = new ArrayList<Node>(link.skippedNodes);
                        Collections.reverse(skippedNodes);
                    }
                    for (Node skipped : skippedNodes) {
                        assert (start.getConnectionTo(skipped.getTop()) != null) : start + " -> " + skipped.getTop() + " not in " + start.links + " (skipped " + skippedNodes + ")";
                        start = skipped.getTop();
                    }
                    assert (start.getConnectionTo(link.getNeighbor(node).getTop()) != null) : start + " -> " + link.getNeighbor(node).getTop() + " not in " + start.links;
                }
            }
            Iterator<Object> iterator = this.activeSources.iterator();
            while (iterator.hasNext()) {
                int uid = (Integer)iterator.next();
                assert (data.optimizedNodes.containsKey(uid));
            }
            iterator = this.activeSinks.iterator();
            while (iterator.hasNext()) {
                int uid = (Integer)iterator.next();
                assert (data.optimizedNodes.containsKey(uid));
            }
        }
    }

    private static void determineEmittingNodes(StructureCache.Data data) {
        data.activeNodes = new ArrayList<Node>();
        int index = 0;
        for (Node node : data.optimizedNodes.values()) {
            switch (node.nodeType) {
                case Source: {
                    if (EnergyNetGlobal.debugGrid) {
                        IC2.log.debug(LogCategory.EnergyNet, "%d %d %s.", node.getGrid().uid, index++, node);
                    }
                    data.activeNodes.add(node);
                    break;
                }
                case Sink: {
                    if (EnergyNetGlobal.debugGrid) {
                        IC2.log.debug(LogCategory.EnergyNet, "%d %d %s.", node.getGrid().uid, index++, node);
                    }
                    data.activeNodes.add(node);
                    break;
                }
                case Conductor: {
                    if (EnergyNetGlobal.debugGrid) {
                        IC2.log.debug(LogCategory.EnergyNet, "%d %d %s.", node.getGrid().uid, index++, node);
                    }
                    data.activeNodes.add(node);
                }
            }
        }
    }

    private static void populateNetworkMatrix(StructureCache.Data data) {
        for (int row = 0; row < data.activeNodes.size(); ++row) {
            Node node = data.activeNodes.get(row);
            for (int col = 0; col < data.activeNodes.size(); ++col) {
                double value;
                block11: {
                    block8: {
                        block9: {
                            block10: {
                                value = 0.0;
                                if (row != col) break block8;
                                for (NodeLink link : node.links) {
                                    if (link.getNeighbor(node) == node) continue;
                                    value += 1.0 / link.loss;
                                    assert (link.loss >= 0.0);
                                }
                                if (!EnergyNetLocal.useLinearTransferModel) break block9;
                                if (node.nodeType != NodeType.Source) break block10;
                                double openCircuitVoltage = EnergyNet.instance.getPowerFromTier(node.getTier());
                                double resistance = Util.square(openCircuitVoltage) / (node.getAmount() * 4.0);
                                assert (resistance > 0.0);
                                value += 1.0 / resistance;
                                node.setResistance(resistance);
                                break block11;
                            }
                            if (node.nodeType != NodeType.Sink) break block11;
                            double resistance = EnergyNet.instance.getPowerFromTier(node.getTier());
                            assert (resistance > 0.0);
                            value += 1.0 / resistance;
                            node.setResistance(resistance);
                            break block11;
                        }
                        if (node.nodeType != NodeType.Sink) break block11;
                        value += 1.0;
                        break block11;
                    }
                    Node possibleNeighbor = data.activeNodes.get(col);
                    for (NodeLink link : node.links) {
                        Node neighbor = link.getNeighbor(node);
                        if (neighbor == node || neighbor != possibleNeighbor) continue;
                        value -= 1.0 / link.loss;
                        assert (link.loss >= 0.0);
                    }
                }
                data.networkMatrix.set(row, col, value);
            }
        }
    }

    private void populateSourceMatrix(StructureCache.Data data) {
        for (int row = 0; row < data.activeNodes.size(); ++row) {
            Node node = data.activeNodes.get(row);
            double input = 0.0;
            if (node.nodeType == NodeType.Source) {
                if (EnergyNetLocal.useLinearTransferModel) {
                    double openCircuitVoltage = EnergyNet.instance.getPowerFromTier(node.getTier());
                    input = openCircuitVoltage / node.getResistance();
                } else {
                    input = node.getAmount();
                }
                assert (input > 0.0);
            }
            data.sourceMatrix.set(row, 0, input);
        }
    }

    void dumpNodeInfo(PrintStream ps, boolean waitForFinish, Node node) {
        if (waitForFinish) {
            this.finishCalculation();
        }
        ps.println("Node " + node + " info:");
        ps.println(" type: " + (Object)((Object)node.nodeType));
        switch (node.nodeType) {
            case Conductor: {
                break;
            }
            case Sink: {
                IEnergySink sink = (IEnergySink)node.tile.entity;
                ps.println(" demanded: " + sink.getDemandedEnergy());
                ps.println(" tier: " + sink.getSinkTier());
                break;
            }
            case Source: {
                IEnergySource source = (IEnergySource)node.tile.entity;
                ps.println(" offered: " + source.getOfferedEnergy());
                ps.println(" tier: " + source.getSourceTier());
                break;
            }
        }
        ps.println(node.links.size() + " neighbor links:");
        for (NodeLink link : node.links) {
            ps.println(" " + link.getNeighbor(node) + " " + link.loss + " " + link.skippedNodes);
        }
        StructureCache.Data data = this.lastData;
        if (data == null || !data.isInitialized || data.optimizedNodes == null) {
            ps.println("No optimized data");
        } else if (!data.optimizedNodes.containsKey(node.uid)) {
            ps.println("Optimized away");
        } else {
            Node optimizedNode = data.optimizedNodes.get(node.uid);
            ps.println(optimizedNode.links.size() + " optimized neighbor links:");
            for (NodeLink link : optimizedNode.links) {
                ps.println(" " + link.getNeighbor(optimizedNode) + " " + link.loss + " " + link.skippedNodes);
            }
        }
    }

    void dumpMatrix(PrintStream ps, boolean waitForFinish, boolean dumpNodesNetSrcMatrices, boolean dumpResultMatrix) {
        StructureCache.Data data;
        if (waitForFinish) {
            this.finishCalculation();
        }
        if (dumpNodesNetSrcMatrices) {
            ps.println("Dumping matrices for " + this + ".");
        }
        if ((data = this.lastData) == null) {
            ps.println("Matrices unavailable");
        } else if (dumpNodesNetSrcMatrices || dumpResultMatrix) {
            if (!data.isInitialized) {
                ps.println("Matrices potentially outdated");
            }
            if (dumpNodesNetSrcMatrices) {
                ps.println("Emitting node indizes:");
                for (int i = 0; i < data.activeNodes.size(); ++i) {
                    Node node = data.activeNodes.get(i);
                    ps.println(i + " " + node + " (amount=" + node.getAmount() + ", tier=" + node.getTier() + ")");
                }
                ps.println("Network matrix:");
                Grid.printMatrix(data.networkMatrix, ps);
                ps.println("Source matrix:");
                Grid.printMatrix(data.sourceMatrix, ps);
            }
            if (dumpResultMatrix) {
                ps.println("Result matrix:");
                Grid.printMatrix(data.resultMatrix, ps);
            }
        }
    }

    private static void printMatrix(DenseMatrix64F matrix, PrintStream ps) {
        if (matrix == null) {
            ps.println("null");
            return;
        }
        boolean isZero = true;
        block0: for (int i = 0; i < matrix.numRows; ++i) {
            for (int j = 0; j < matrix.numCols; ++j) {
                if (matrix.get(i, j) == 0.0) continue;
                isZero = false;
                continue block0;
            }
        }
        if (isZero) {
            ps.println(matrix.numRows + "x" + matrix.numCols + ", all zero");
        } else {
            MatrixIO.print((PrintStream)ps, (Matrix64F)matrix, (String)"%.6f");
        }
    }

    void dumpStats(PrintStream ps, boolean waitForFinish) {
        if (waitForFinish) {
            this.finishCalculation();
        }
        ps.println("Grid " + this.uid + " info:");
        ps.println(this.nodes.size() + " nodes");
        StructureCache.Data data = this.lastData;
        if (data != null && data.isInitialized) {
            if (data.activeNodes != null) {
                int srcCount = 0;
                int dstCount = 0;
                for (Node node : data.activeNodes) {
                    if (node.nodeType == NodeType.Source) {
                        ++srcCount;
                        continue;
                    }
                    if (node.nodeType != NodeType.Sink) continue;
                    ++dstCount;
                }
                ps.println("Active: " + srcCount + " sources -> " + dstCount + " sinks");
            }
            if (data.optimizedNodes != null) {
                ps.println(data.optimizedNodes.size() + " nodes after optimization");
            }
            if (data.activeNodes != null) {
                ps.println(data.activeNodes.size() + " emitting nodes");
            }
        }
        ps.printf("%d entries in cache, hitrate %.2f%%", this.cache.size(), 100.0 * (double)this.cache.hits / (double)(this.cache.hits + this.cache.misses));
        ps.println();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void dumpGraph(boolean waitForFinish) {
        if (waitForFinish) {
            this.finishCalculation();
        }
        StructureCache.Data data = this.lastData;
        for (int i = 0; i < 2 && (i != 1 || data != null && data.isInitialized && data.optimizedNodes != null); ++i) {
            OutputStreamWriter out = null;
            try {
                out = new FileWriter("graph_" + this.uid + "_" + (i == 0 ? "raw" : "optimized") + ".txt");
                out.write("graph nodes {\n  overlap=false;\n");
                Collection<Node> nodesToDump = (i == 0 ? this.nodes : data.optimizedNodes).values();
                HashSet<Node> dumpedConnections = new HashSet<Node>();
                for (Node node : nodesToDump) {
                    out.write("  \"" + node + "\";\n");
                    for (NodeLink link : node.links) {
                        Node neighbor = link.getNeighbor(node);
                        if (dumpedConnections.contains(neighbor)) continue;
                        out.write("  \"" + node + "\" -- \"" + neighbor + "\" [label=\"" + link.loss + "\"];\n");
                    }
                    dumpedConnections.add(node);
                }
                out.write("}\n");
                continue;
            }
            catch (IOException e) {
                IC2.log.debug(LogCategory.EnergyNet, e, "Graph saving failed.");
                continue;
            }
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                }
                catch (IOException iOException) {}
            }
        }
    }

    GridInfo getInfo() {
        int complexNodes = 0;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (Node node : this.nodes.values()) {
            if (node.links.size() > 2) {
                ++complexNodes;
            }
            for (TileEntity te : node.tile.positions) {
                if (te.xCoord < minX) {
                    minX = te.xCoord;
                }
                if (te.yCoord < minY) {
                    minY = te.yCoord;
                }
                if (te.zCoord < minZ) {
                    minZ = te.zCoord;
                }
                if (te.xCoord > maxX) {
                    maxX = te.xCoord;
                }
                if (te.yCoord > maxY) {
                    maxY = te.yCoord;
                }
                if (te.zCoord <= maxZ) continue;
                maxZ = te.zCoord;
            }
        }
        return new GridInfo(this.uid, this.nodes.size(), complexNodes, minX, minY, minZ, maxX, maxY, maxZ);
    }
}

