/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.wiring;

import ic2.api.energy.EnergyNet;
import ic2.core.block.wiring.TileEntityCable;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityCableDetector
extends TileEntityCable {
    public static int tickRate = 20;
    public int ticker = 0;

    public TileEntityCableDetector(short meta) {
        super(meta);
    }

    public TileEntityCableDetector() {
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("active", this.getActive());
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.ticker++ % tickRate == 0) {
            double energy = EnergyNet.instance.getNodeStats(this).getEnergyIn();
            if (energy > 0.0) {
                if (!this.getActive()) {
                    this.setActive(true);
                    this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
                }
            } else if (this.getActive()) {
                this.setActive(false);
                this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
            }
        }
    }
}

