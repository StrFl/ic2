/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraftforge.common.DimensionManager
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.energy;

import ic2.api.Direction;
import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IMetaDelegate;
import ic2.core.IC2;
import ic2.core.TickHandler;
import ic2.core.energy.Change;
import ic2.core.energy.EnergyNetGlobal;
import ic2.core.energy.Grid;
import ic2.core.energy.GridInfo;
import ic2.core.energy.Node;
import ic2.core.energy.NodeType;
import ic2.core.energy.Tile;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

public final class EnergyNetLocal {
    public static final boolean useLinearTransferModel = ConfigUtil.getBool(MainConfig.get(), "misc/useLinearTransferModel");
    public static final double nonConductorResistance = 0.2;
    public static final double sourceResistanceFactor = 0.0625;
    public static final double sinkResistanceFactor = 1.0;
    public static final double sourceCurrent = 17.0;
    public static final boolean enableCache = true;
    private static int nextGridUid = 0;
    private static int nextNodeUid = 0;
    protected final Set<Grid> grids = new HashSet<Grid>();
    protected List<Change> changes = new ArrayList<Change>();
    private final Map<ChunkCoordinates, Tile> registeredTiles = new HashMap<ChunkCoordinates, Tile>();
    private Map<TileEntity, Integer> pendingAdds = new WeakHashMap<TileEntity, Integer>();
    private final Set<Tile> removedTiles = new HashSet<Tile>();
    private boolean locked = false;
    private static final long logSuppressionTimeout = 300000000000L;
    private final Map<String, Long> recentLogs = new HashMap<String, Long>();

    protected void addTileEntity(TileEntity te) {
        this.addTileEntity(te, 0);
    }

    protected void addTileEntity(TileEntity te, int retry) {
        if (EnergyNetGlobal.debugTileManagement) {
            IC2.log.debug(LogCategory.EnergyNet, "EnergyNet.addTileEntity(%s, %d), world=%s, chunk=%s, this=%s", te, retry, te.getWorldObj(), te.getWorldObj().getChunkFromBlockCoords(te.xCoord, te.zCoord), this);
        }
        if (!(te instanceof IEnergyTile)) {
            this.logWarn("EnergyNet.addTileEntity: " + te + " doesn't implement IEnergyTile, aborting");
            return;
        }
        if (EnergyNetGlobal.checkApi && !Util.checkInterfaces(te.getClass())) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.addTileEntity: %s doesn't implement its advertised interfaces completely.", Util.asString(te));
        }
        if (te.isInvalid()) {
            this.logWarn("EnergyNet.addTileEntity: " + te + " is invalid (TileEntity.isInvalid()), aborting");
            return;
        }
        if (te.getWorldObj() != DimensionManager.getWorld((int)te.getWorldObj().provider.dimensionId)) {
            this.logDebug("EnergyNet.addTileEntity: " + te + " is in an unloaded world, aborting");
            return;
        }
        if (this.locked) {
            this.logDebug("EnergyNet.addTileEntity: adding " + te + " while locked, postponing.");
            this.pendingAdds.put(te, retry);
            return;
        }
        Tile tile = new Tile(this, te);
        if (EnergyNetGlobal.debugTileManagement) {
            ArrayList<String> posStrings = new ArrayList<String>(tile.positions.size());
            for (TileEntity pos : tile.positions) {
                posStrings.add(pos + " (" + pos.xCoord + "/" + pos.yCoord + "/" + pos.zCoord + ")");
            }
            IC2.log.debug(LogCategory.EnergyNet, "positions: %s", posStrings);
        }
        ListIterator<TileEntity> it = tile.positions.listIterator();
        while (it.hasNext()) {
            TileEntity pos = it.next();
            ChunkCoordinates coords = new ChunkCoordinates(pos.xCoord, pos.yCoord, pos.zCoord);
            Tile conflicting = this.registeredTiles.get(coords);
            if (conflicting != null) {
                if (te == conflicting.entity) {
                    this.logDebug("EnergyNet.addTileEntity: " + pos + " (" + te + ") is already added using the same position, aborting");
                } else if (retry < 2) {
                    this.pendingAdds.put(te, retry + 1);
                } else if (conflicting.entity.isInvalid() || EnergyNetGlobal.replaceConflicting) {
                    this.logDebug("EnergyNet.addTileEntity: " + pos + " (" + te + ") is conflicting with " + conflicting.entity + " (invalid=" + conflicting.entity.isInvalid() + ") using the same position, which is abandoned (prev. te not removed), replacing");
                    this.removeTileEntity(conflicting.entity);
                    conflicting = null;
                } else {
                    this.logWarn("EnergyNet.addTileEntity: " + pos + " (" + te + ") is still conflicting with " + conflicting.entity + " using the same position (overlapping), aborting");
                }
                if (conflicting != null) {
                    it.previous();
                    while (it.hasPrevious()) {
                        pos = it.previous();
                        coords = new ChunkCoordinates(pos.xCoord, pos.yCoord, pos.zCoord);
                        this.registeredTiles.remove(coords);
                    }
                    return;
                }
            }
            if (!te.getWorldObj().blockExists(pos.xCoord, pos.yCoord, pos.zCoord)) {
                if (retry < 1) {
                    this.logWarn("EnergyNet.addTileEntity: " + pos + " (" + te + ") was added too early, postponing");
                    this.pendingAdds.put(te, retry + 1);
                } else {
                    this.logWarn("EnergyNet.addTileEntity: " + pos + " (" + te + ") unloaded, aborting");
                }
                it.previous();
                while (it.hasPrevious()) {
                    pos = it.previous();
                    coords = new ChunkCoordinates(pos.xCoord, pos.yCoord, pos.zCoord);
                    this.registeredTiles.remove(coords);
                }
                return;
            }
            this.registeredTiles.put(coords, tile);
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                int x = pos.xCoord + dir.offsetX;
                int y = pos.yCoord + dir.offsetY;
                int z = pos.zCoord + dir.offsetZ;
                if (!te.getWorldObj().blockExists(x, y, z)) continue;
                te.getWorldObj().notifyBlockOfNeighborChange(x, y, z, Blocks.air);
            }
        }
        this.addTileToGrids(tile);
        if (EnergyNetGlobal.verifyGrid()) {
            for (Node node : tile.nodes) {
                assert (node.getGrid() != null);
            }
        }
    }

    protected void removeTileEntity(TileEntity te) {
        boolean wasPending;
        if (this.locked) {
            throw new IllegalStateException("removeTileEntity isn't allowed from this context");
        }
        if (EnergyNetGlobal.debugTileManagement) {
            IC2.log.debug(LogCategory.EnergyNet, "EnergyNet.removeTileEntity(%s), world=%s, chunk=%s, this=%s", te, te.getWorldObj(), te.getWorldObj().getChunkFromBlockCoords(te.xCoord, te.zCoord), this);
        }
        if (!(te instanceof IEnergyTile)) {
            this.logWarn("EnergyNet.removeTileEntity: " + te + " doesn't implement IEnergyTile, aborting");
            return;
        }
        List<TileEntity> positions = te instanceof IMetaDelegate ? ((IMetaDelegate)te).getSubTiles() : Arrays.asList(te);
        boolean bl = wasPending = this.pendingAdds.remove(te) != null;
        if (EnergyNetGlobal.debugTileManagement) {
            ArrayList<String> posStrings = new ArrayList<String>(positions.size());
            for (TileEntity pos : positions) {
                posStrings.add(pos + " (" + pos.xCoord + "/" + pos.yCoord + "/" + pos.zCoord + ")");
            }
            IC2.log.debug(LogCategory.EnergyNet, "positions: %s", posStrings);
        }
        boolean removed = false;
        for (TileEntity pos : positions) {
            ChunkCoordinates coords = new ChunkCoordinates(pos.xCoord, pos.yCoord, pos.zCoord);
            Tile tile = this.registeredTiles.get(coords);
            if (tile == null) {
                if (wasPending) continue;
                this.logDebug("EnergyNet.removeTileEntity: " + pos + " (" + te + ") wasn't found (added), skipping");
                continue;
            }
            if (tile.entity != te) {
                this.logWarn("EnergyNet.removeTileEntity: " + pos + " (" + te + ") doesn't match the registered te " + tile.entity + ", skipping");
                continue;
            }
            if (!removed) {
                assert (new HashSet<TileEntity>(positions).equals(new HashSet<TileEntity>(tile.positions)));
                this.removeTileFromGrids(tile);
                removed = true;
                this.removedTiles.add(tile);
            }
            this.registeredTiles.remove(coords);
            if (!te.getWorldObj().blockExists(pos.xCoord, pos.yCoord, pos.zCoord)) continue;
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                int x = pos.xCoord + dir.offsetX;
                int y = pos.yCoord + dir.offsetY;
                int z = pos.zCoord + dir.offsetZ;
                if (!te.getWorldObj().blockExists(x, y, z)) continue;
                te.getWorldObj().notifyBlockOfNeighborChange(x, y, z, Blocks.air);
            }
        }
    }

    protected double getTotalEnergyEmitted(TileEntity tileEntity) {
        ChunkCoordinates coords = new ChunkCoordinates(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        Tile tile = this.registeredTiles.get(coords);
        if (tile == null) {
            this.logWarn("EnergyNet.getTotalEnergyEmitted: " + tileEntity + " is not added to the enet, aborting");
            return 0.0;
        }
        double ret = 0.0;
        Iterable<NodeStats> stats = tile.getStats();
        for (NodeStats stat : stats) {
            ret += stat.getEnergyOut();
        }
        return ret;
    }

    protected double getTotalEnergySunken(TileEntity tileEntity) {
        ChunkCoordinates coords = new ChunkCoordinates(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        Tile tile = this.registeredTiles.get(coords);
        if (tile == null) {
            this.logWarn("EnergyNet.getTotalEnergySunken: " + tileEntity + " is not added to the enet, aborting");
            return 0.0;
        }
        double ret = 0.0;
        Iterable<NodeStats> stats = tile.getStats();
        for (NodeStats stat : stats) {
            ret += stat.getEnergyIn();
        }
        return ret;
    }

    protected NodeStats getNodeStats(TileEntity te) {
        ChunkCoordinates coords = new ChunkCoordinates(te.xCoord, te.yCoord, te.zCoord);
        Tile tile = this.registeredTiles.get(coords);
        if (tile == null) {
            this.logWarn("EnergyNet.getTotalEnergySunken: " + te + " is not added to the enet, aborting");
            return new NodeStats(0.0, 0.0, 0.0);
        }
        double in = 0.0;
        double out = 0.0;
        double voltage = 0.0;
        Iterable<NodeStats> stats = tile.getStats();
        for (NodeStats stat : stats) {
            in += stat.getEnergyIn();
            out += stat.getEnergyOut();
            voltage = Math.max(voltage, stat.getVoltage());
        }
        return new NodeStats(in, out, voltage);
    }

    protected TileEntity getTileEntity(int x, int y, int z) {
        Tile ret = this.registeredTiles.get(new ChunkCoordinates(x, y, z));
        if (ret == null) {
            return null;
        }
        return ret.entity;
    }

    protected TileEntity getNeighbor(TileEntity te, Direction dir) {
        switch (dir) {
            case XN: {
                return this.getTileEntity(te.xCoord - 1, te.yCoord, te.zCoord);
            }
            case XP: {
                return this.getTileEntity(te.xCoord + 1, te.yCoord, te.zCoord);
            }
            case YN: {
                return this.getTileEntity(te.xCoord, te.yCoord - 1, te.zCoord);
            }
            case YP: {
                return this.getTileEntity(te.xCoord, te.yCoord + 1, te.zCoord);
            }
            case ZN: {
                return this.getTileEntity(te.xCoord, te.yCoord, te.zCoord - 1);
            }
            case ZP: {
                return this.getTileEntity(te.xCoord, te.yCoord, te.zCoord + 1);
            }
        }
        return null;
    }

    public boolean dumpDebugInfo(PrintStream console, PrintStream chat, int x, int y, int z) {
        Tile tile = this.registeredTiles.get(new ChunkCoordinates(x, y, z));
        if (tile == null) {
            return false;
        }
        HashSet<Grid> processedGrids = new HashSet<Grid>();
        for (Node node : tile.nodes) {
            Grid grid = node.getGrid();
            if (!processedGrids.add(grid)) continue;
            grid.dumpNodeInfo(chat, true, node);
            grid.dumpStats(chat, true);
            grid.dumpMatrix(console, true, true, true);
            console.println("dumping graph for " + grid);
            grid.dumpGraph(true);
        }
        return true;
    }

    public List<GridInfo> getGridInfos() {
        ArrayList<GridInfo> ret = new ArrayList<GridInfo>();
        for (Grid grid : this.grids) {
            ret.add(grid.getInfo());
        }
        return ret;
    }

    protected void onTickEnd() {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        this.locked = true;
        for (Grid grid : this.grids) {
            grid.finishCalculation();
            grid.updateStats();
        }
        this.locked = false;
        this.processChanges();
        Map<TileEntity, Integer> currentPendingAdds = this.pendingAdds;
        this.pendingAdds = new WeakHashMap<TileEntity, Integer>();
        for (Map.Entry<TileEntity, Integer> entry : currentPendingAdds.entrySet()) {
            this.addTileEntity(entry.getKey(), entry.getValue());
        }
        this.locked = true;
        for (Grid grid : this.grids) {
            grid.prepareCalculation();
        }
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        for (Grid grid : this.grids) {
            Runnable task = grid.startCalculation();
            if (task == null) continue;
            arrayList.add(task);
        }
        IC2.getInstance().threadPool.executeAll(arrayList);
        this.locked = false;
    }

    protected void addChange(Node node, ForgeDirection dir, double amount, double voltage) {
        this.changes.add(new Change(node, dir, amount, voltage));
    }

    protected static int getNextGridUid() {
        return nextGridUid++;
    }

    protected static int getNextNodeUid() {
        return nextNodeUid++;
    }

    /*
     * Could not resolve type clashes
     */
    private void addTileToGrids(Tile tile) {
        ArrayList<Node> extraNodes = new ArrayList<Node>();
        block4: for (Node node : tile.nodes) {
            Grid grid;
            if (EnergyNetGlobal.debugGrid) {
                IC2.log.debug(LogCategory.EnergyNet, "Adding node %s.", node);
            }
            ArrayList<Node> neighbors = new ArrayList<Node>();
            for (Object pos : tile.positions) {
                for (Direction dir : Direction.directions) {
                    ForgeDirection fdir = dir.toForgeDirection();
                    ChunkCoordinates coords = new ChunkCoordinates(((TileEntity)pos).xCoord + fdir.offsetX, ((TileEntity)pos).yCoord + fdir.offsetY, ((TileEntity)pos).zCoord + fdir.offsetZ);
                    Tile neighborTile = this.registeredTiles.get(coords);
                    if (neighborTile == null || neighborTile == node.tile) continue;
                    for (Node neighbor : neighborTile.nodes) {
                        if (neighbor.isExtraNode()) continue;
                        boolean canEmit = false;
                        if ((node.nodeType == NodeType.Source || node.nodeType == NodeType.Conductor) && neighbor.nodeType != NodeType.Source) {
                            IEnergyEmitter emitter = (IEnergyEmitter)(pos instanceof IEnergyEmitter ? pos : node.tile.entity);
                            TileEntity neighborSubTe = neighborTile.getSubEntityAt(coords);
                            IEnergyAcceptor acceptor = (IEnergyAcceptor)(neighborSubTe instanceof IEnergyAcceptor ? neighborSubTe : neighbor.tile.entity);
                            canEmit = emitter.emitsEnergyTo(neighbor.tile.entity, dir.toForgeDirection()) && acceptor.acceptsEnergyFrom(node.tile.entity, dir.getInverse().toForgeDirection());
                        }
                        boolean canAccept = false;
                        if (!(canEmit || node.nodeType != NodeType.Sink && node.nodeType != NodeType.Conductor || neighbor.nodeType == NodeType.Sink)) {
                            IEnergyAcceptor acceptor = (IEnergyAcceptor)(pos instanceof IEnergyAcceptor ? pos : node.tile.entity);
                            TileEntity neighborSubTe = neighborTile.getSubEntityAt(coords);
                            IEnergyEmitter emitter = (IEnergyEmitter)(neighborSubTe instanceof IEnergyEmitter ? neighborSubTe : neighbor.tile.entity);
                            boolean bl = canAccept = acceptor.acceptsEnergyFrom(neighbor.tile.entity, dir.toForgeDirection()) && emitter.emitsEnergyTo(node.tile.entity, dir.getInverse().toForgeDirection());
                        }
                        if (!canEmit && !canAccept) continue;
                        neighbors.add(neighbor);
                    }
                }
            }
            if (neighbors.isEmpty()) {
                if (EnergyNetGlobal.debugGrid) {
                    IC2.log.debug(LogCategory.EnergyNet, "Creating new grid for %s.", node);
                }
                grid = new Grid(this);
                grid.add(node, neighbors);
                continue;
            }
            switch (node.nodeType) {
                case Conductor: {
                    Node neighbor2;
                    Object pos;
                    grid = null;
                    pos = neighbors.iterator();
                    while (pos.hasNext()) {
                        Node neighbor = (Node)pos.next();
                        if (neighbor.nodeType != NodeType.Conductor && !neighbor.links.isEmpty()) continue;
                        if (EnergyNetGlobal.debugGrid) {
                            IC2.log.debug(LogCategory.EnergyNet, "Using %s for %s with neighbors %s.", neighbor.getGrid(), node, neighbors);
                        }
                        grid = neighbor.getGrid();
                        break;
                    }
                    if (grid == null) {
                        if (EnergyNetGlobal.debugGrid) {
                            IC2.log.debug(LogCategory.EnergyNet, "Creating new grid for %s with neighbors %s.", node, neighbors);
                        }
                        grid = new Grid(this);
                    }
                    HashMap neighborReplacements = new HashMap();
                    ListIterator<Node> it = neighbors.listIterator();
                    while (it.hasNext()) {
                        Node neighbor = (Node)it.next();
                        if (neighbor.getGrid() == grid) continue;
                        if (neighbor.nodeType != NodeType.Conductor && !neighbor.links.isEmpty()) {
                            boolean found = false;
                            for (int i = 0; i < it.previousIndex(); ++i) {
                                neighbor2 = (Node)neighbors.get(i);
                                if (neighbor2.tile != neighbor.tile || neighbor2.nodeType != neighbor.nodeType || neighbor2.getGrid() != grid) continue;
                                if (EnergyNetGlobal.debugGrid) {
                                    IC2.log.debug(LogCategory.EnergyNet, "Using neighbor node %s instead of %s.", neighbor2, neighbors);
                                }
                                found = true;
                                it.set(neighbor2);
                                break;
                            }
                            if (found) continue;
                            if (EnergyNetGlobal.debugGrid) {
                                IC2.log.debug(LogCategory.EnergyNet, "Creating new extra node for neighbor %s.", neighbor);
                            }
                            neighbor = new Node(this, neighbor.tile, neighbor.nodeType);
                            neighbor.tile.addExtraNode(neighbor);
                            grid.add(neighbor, Collections.<Node>emptyList());
                            it.set(neighbor);
                            assert (neighbor.getGrid() != null);
                            continue;
                        }
                        grid.merge(neighbor.getGrid(), neighborReplacements);
                    }
                    it = neighbors.listIterator();
                    while (it.hasNext()) {
                        Node neighbor = (Node)it.next();
                        Node replacement = (Node)neighborReplacements.get(neighbor);
                        if (replacement != null) {
                            neighbor = replacement;
                            it.set(replacement);
                        }
                        assert (neighbor.getGrid() == grid);
                    }
                    grid.add(node, neighbors);
                    assert (node.getGrid() != null);
                    break;
                }
                case Sink: 
                case Source: {
                    Node neighbor2;
                    ArrayList neighborGroups = new ArrayList();
                    for (Node neighbor : neighbors) {
                        boolean found = false;
                        if (node.nodeType == NodeType.Conductor) {
                            for (List nodeList : neighborGroups) {
                                neighbor2 = (Node)nodeList.get(0);
                                if (neighbor2.nodeType != NodeType.Conductor || neighbor2.getGrid() != neighbor.getGrid()) continue;
                                nodeList.add(neighbor);
                                found = true;
                                break;
                            }
                        }
                        if (found) continue;
                        ArrayList<Node> nodeList = new ArrayList<Node>();
                        nodeList.add(neighbor);
                        neighborGroups.add(nodeList);
                    }
                    if (EnergyNetGlobal.debugGrid) {
                        IC2.log.debug(LogCategory.EnergyNet, "Neighbor groups detected for %s: %s.", node, neighborGroups);
                    }
                    assert (!neighborGroups.isEmpty());
                    for (int i = 0; i < neighborGroups.size(); ++i) {
                        Node currentNode;
                        List nodeList = (List)neighborGroups.get(i);
                        Node neighbor = (Node)nodeList.get(0);
                        if (neighbor.nodeType != NodeType.Conductor && !neighbor.links.isEmpty()) {
                            assert (nodeList.size() == 1);
                            if (EnergyNetGlobal.debugGrid) {
                                IC2.log.debug(LogCategory.EnergyNet, "Creating new extra node for neighbor %s.", neighbor);
                            }
                            neighbor = new Node(this, neighbor.tile, neighbor.nodeType);
                            neighbor.tile.addExtraNode(neighbor);
                            new Grid(this).add(neighbor, Collections.<Node>emptyList());
                            nodeList.set(0, neighbor);
                            assert (neighbor.getGrid() != null);
                        }
                        if (i == 0) {
                            currentNode = node;
                        } else {
                            if (EnergyNetGlobal.debugGrid) {
                                IC2.log.debug(LogCategory.EnergyNet, "Creating new extra node for %s.", node);
                            }
                            currentNode = new Node(this, tile, node.nodeType);
                            currentNode.setExtraNode(true);
                            extraNodes.add(currentNode);
                        }
                        neighbor.getGrid().add(currentNode, nodeList);
                        assert (currentNode.getGrid() != null);
                    }
                    continue block4;
                }
            }
        }
        for (Node node : extraNodes) {
            tile.addExtraNode(node);
        }
    }

    private void removeTileFromGrids(Tile tile) {
        for (Node node : tile.nodes) {
            node.getGrid().remove(node);
        }
    }

    private void processChanges() {
        for (Tile tile : this.removedTiles) {
            Iterator<Change> it = this.changes.iterator();
            while (it.hasNext()) {
                Change change = it.next();
                if (change.node.tile != tile) continue;
                Tile replacement = this.registeredTiles.get(new ChunkCoordinates(change.node.tile.entity.xCoord, change.node.tile.entity.yCoord, change.node.tile.entity.zCoord));
                boolean validReplacement = false;
                if (replacement != null) {
                    for (Node node : replacement.nodes) {
                        if (node.nodeType != change.node.nodeType || node.getGrid() != change.node.getGrid()) continue;
                        if (EnergyNetGlobal.debugGrid) {
                            IC2.log.debug(LogCategory.EnergyNet, "Redirecting change %s to replacement node %s.", change, node);
                        }
                        change.node = node;
                        validReplacement = true;
                        break;
                    }
                }
                if (validReplacement) continue;
                it.remove();
                ArrayList<Change> sameGridSourceChanges = new ArrayList<Change>();
                for (Change change2 : this.changes) {
                    if (change2.node.nodeType != NodeType.Source || change.node.getGrid() != change2.node.getGrid()) continue;
                    sameGridSourceChanges.add(change2);
                }
                if (EnergyNetGlobal.debugGrid) {
                    IC2.log.debug(LogCategory.EnergyNet, "Redistributing change %s to remaining source nodes %s.", change, sameGridSourceChanges);
                }
                for (Change change2 : sameGridSourceChanges) {
                    change2.setAmount(change2.getAmount() - Math.abs(change.getAmount()) / (double)sameGridSourceChanges.size());
                }
            }
        }
        this.removedTiles.clear();
        for (Change change : this.changes) {
            if (change.node.nodeType != NodeType.Sink) continue;
            assert (change.getAmount() > 0.0);
            IEnergySink sink = (IEnergySink)change.node.tile.entity;
            double returned = sink.injectEnergy(change.dir, change.getAmount(), change.getVoltage());
            if (EnergyNetGlobal.debugGrid) {
                IC2.log.debug(LogCategory.EnergyNet, "Applied change %s, %f EU returned.", change, returned);
            }
            if (!(returned > 0.0)) continue;
            ArrayList<Change> sameGridSourceChanges = new ArrayList<Change>();
            for (Change change2 : this.changes) {
                if (change2.node.nodeType != NodeType.Source || change.node.getGrid() != change2.node.getGrid()) continue;
                sameGridSourceChanges.add(change2);
            }
            if (EnergyNetGlobal.debugGrid) {
                IC2.log.debug(LogCategory.EnergyNet, "Redistributing returned amount to source nodes %s.", sameGridSourceChanges);
            }
            for (Change change3 : sameGridSourceChanges) {
                change3.setAmount(change3.getAmount() - returned / (double)sameGridSourceChanges.size());
            }
        }
        for (Change change : this.changes) {
            if (change.node.nodeType != NodeType.Source) continue;
            assert (change.getAmount() <= 0.0);
            if (change.getAmount() >= 0.0) continue;
            IEnergySource source = (IEnergySource)change.node.tile.entity;
            source.drawEnergy(change.getAmount());
            if (!EnergyNetGlobal.debugGrid) continue;
            IC2.log.debug(LogCategory.EnergyNet, "Applied change %s.", change);
        }
        this.changes.clear();
    }

    private void logDebug(String msg) {
        if (!this.shouldLog(msg)) {
            return;
        }
        IC2.log.debug(LogCategory.EnergyNet, msg);
        if (EnergyNetGlobal.debugTileManagement) {
            IC2.log.debug(LogCategory.EnergyNet, new Throwable(), "stack trace");
            if (TickHandler.getLastDebugTrace() != null) {
                IC2.log.debug(LogCategory.EnergyNet, TickHandler.getLastDebugTrace(), "parent stack trace");
            }
        }
    }

    private void logWarn(String msg) {
        if (!this.shouldLog(msg)) {
            return;
        }
        IC2.log.warn(LogCategory.EnergyNet, msg);
        if (EnergyNetGlobal.debugTileManagement) {
            IC2.log.debug(LogCategory.EnergyNet, new Throwable(), "stack trace");
            if (TickHandler.getLastDebugTrace() != null) {
                IC2.log.debug(LogCategory.EnergyNet, TickHandler.getLastDebugTrace(), "parent stack trace");
            }
        }
    }

    private boolean shouldLog(String msg) {
        if (EnergyNetGlobal.logAll) {
            return true;
        }
        this.cleanRecentLogs();
        msg = msg.replaceAll("@[0-9a-f]+", "@x");
        long time = System.nanoTime();
        Long lastLog = this.recentLogs.put(msg, time);
        return lastLog == null || lastLog < time - 300000000000L;
    }

    private void cleanRecentLogs() {
        if (this.recentLogs.size() < 100) {
            return;
        }
        long minTime = System.nanoTime() - 300000000000L;
        Iterator<Long> it = this.recentLogs.values().iterator();
        while (it.hasNext()) {
            long recTime = it.next();
            if (recTime >= minTime) continue;
            it.remove();
        }
    }
}

