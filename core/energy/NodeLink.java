/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.energy;

import ic2.core.energy.Node;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

class NodeLink {
    Node nodeA;
    Node nodeB;
    ForgeDirection dirFromA;
    ForgeDirection dirFromB;
    double loss;
    List<Node> skippedNodes = new ArrayList<Node>();

    NodeLink(Node nodeA1, Node nodeB1, double loss1) {
        this(nodeA1, nodeB1, loss1, null, null);
        this.calculateDirections();
    }

    NodeLink(NodeLink link) {
        this(link.nodeA, link.nodeB, link.loss, link.dirFromA, link.dirFromB);
        this.skippedNodes.addAll(link.skippedNodes);
    }

    private NodeLink(Node nodeA1, Node nodeB1, double loss1, ForgeDirection dirFromA, ForgeDirection dirFromB) {
        assert (nodeA1 != nodeB1);
        this.nodeA = nodeA1;
        this.nodeB = nodeB1;
        this.loss = loss1;
        this.dirFromA = dirFromA;
        this.dirFromB = dirFromB;
    }

    Node getNeighbor(Node node) {
        if (this.nodeA == node) {
            return this.nodeB;
        }
        return this.nodeA;
    }

    Node getNeighbor(int uid) {
        if (this.nodeA.uid == uid) {
            return this.nodeB;
        }
        return this.nodeA;
    }

    void replaceNode(Node oldNode, Node newNode) {
        if (this.nodeA == oldNode) {
            this.nodeA = newNode;
        } else if (this.nodeB == oldNode) {
            this.nodeB = newNode;
        } else {
            throw new IllegalArgumentException("Node " + oldNode + " isn't in " + this + ".");
        }
    }

    ForgeDirection getDirFrom(Node node) {
        if (this.nodeA == node) {
            return this.dirFromA;
        }
        if (this.nodeB == node) {
            return this.dirFromB;
        }
        return null;
    }

    void updateCurrent() {
        assert (!Double.isNaN(this.nodeA.getVoltage()));
        assert (!Double.isNaN(this.nodeB.getVoltage()));
        double currentAB = (this.nodeA.getVoltage() - this.nodeB.getVoltage()) / this.loss;
        this.nodeA.addCurrent(-currentAB);
        this.nodeB.addCurrent(currentAB);
    }

    public String toString() {
        return "NodeLink:" + this.nodeA + "@" + this.dirFromA + "->" + this.nodeB + "@" + this.dirFromB;
    }

    private void calculateDirections() {
        for (TileEntity posA : this.nodeA.tile.positions) {
            for (TileEntity posB : this.nodeB.tile.positions) {
                int deltaX = posB.xCoord - posA.xCoord;
                int deltaY = posB.yCoord - posA.yCoord;
                int deltaZ = posB.zCoord - posA.zCoord;
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    if (dir.offsetX != deltaX || dir.offsetY != deltaY || dir.offsetZ != deltaZ) continue;
                    this.dirFromA = dir;
                    this.dirFromB = dir.getOpposite();
                    return;
                }
            }
        }
        assert (false);
        this.dirFromA = ForgeDirection.UNKNOWN;
        this.dirFromB = ForgeDirection.UNKNOWN;
    }
}

