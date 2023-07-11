/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ChunkCoordinates
 */
package ic2.core.energy;

import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IMetaDelegate;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.energy.Node;
import ic2.core.energy.NodeType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

class Tile {
    final TileEntity entity;
    final List<TileEntity> positions;
    final List<Node> nodes = new ArrayList<Node>();
    final double maxCurrent;

    Tile(EnergyNetLocal energyNet, TileEntity te) {
        this.entity = te;
        if (te instanceof IMetaDelegate) {
            this.positions = new ArrayList<TileEntity>(((IMetaDelegate)te).getSubTiles());
            if (this.positions.isEmpty()) {
                throw new RuntimeException("Tile entity " + te + " must return at least 1 sub tile for IMetaDelegate.getSubTiles().");
            }
        } else {
            this.positions = Arrays.asList(te);
        }
        if (te instanceof IEnergySource) {
            this.nodes.add(new Node(energyNet, this, NodeType.Source));
        }
        if (te instanceof IEnergySink) {
            this.nodes.add(new Node(energyNet, this, NodeType.Sink));
        }
        if (te instanceof IEnergyConductor) {
            this.nodes.add(new Node(energyNet, this, NodeType.Conductor));
            this.maxCurrent = ((IEnergyConductor)te).getConductorBreakdownEnergy();
        } else {
            this.maxCurrent = Double.MAX_VALUE;
        }
    }

    void addExtraNode(Node node) {
        node.setExtraNode(true);
        this.nodes.add(node);
    }

    boolean removeExtraNode(Node node) {
        boolean canBeRemoved = false;
        if (node.isExtraNode()) {
            canBeRemoved = true;
        } else {
            for (Node otherNode : this.nodes) {
                if (otherNode == node || otherNode.nodeType != node.nodeType || !otherNode.isExtraNode()) continue;
                otherNode.setExtraNode(false);
                canBeRemoved = true;
                break;
            }
        }
        if (canBeRemoved) {
            this.nodes.remove(node);
            return true;
        }
        return false;
    }

    TileEntity getSubEntityAt(ChunkCoordinates coords) {
        for (TileEntity te : this.positions) {
            if (te.xCoord != coords.posX || te.yCoord != coords.posY || te.zCoord != coords.posZ) continue;
            return te;
        }
        return null;
    }

    Iterable<NodeStats> getStats() {
        ArrayList<NodeStats> ret = new ArrayList<NodeStats>(this.nodes.size());
        for (Node node : this.nodes) {
            ret.add(node.getStats());
        }
        return ret;
    }
}

