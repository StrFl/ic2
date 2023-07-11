/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block;

import ic2.api.energy.tile.IHeatSource;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityHeatSourceInventory
extends TileEntityInventory
implements IHeatSource {
    protected int transmitHeat;
    protected int maxHeatEmitpeerTick;
    protected int HeatBuffer;

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        int amount = this.getMaxHeatEmittedPerTick() - this.HeatBuffer;
        if (amount > 0) {
            this.addtoHeatBuffer(this.fillHeatBuffer(amount));
        }
    }

    @Override
    public int maxrequestHeatTick(ForgeDirection directionFrom) {
        if (this.facingMatchesDirection(directionFrom)) {
            return this.getMaxHeatEmittedPerTick();
        }
        return 0;
    }

    @Override
    public int requestHeat(ForgeDirection directionFrom, int requestheat) {
        if (this.facingMatchesDirection(directionFrom)) {
            int heatbuffertemp = this.getHeatBuffer();
            if (this.getHeatBuffer() >= requestheat) {
                this.setHeatBuffer(this.getHeatBuffer() - requestheat);
                this.transmitHeat = requestheat;
                return requestheat;
            }
            this.transmitHeat = heatbuffertemp;
            this.setHeatBuffer(0);
            return heatbuffertemp;
        }
        return 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.HeatBuffer = nbtTagCompound.getInteger("HeatBuffer");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("HeatBuffer", this.HeatBuffer);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.maxHeatEmitpeerTick = this.getMaxHeatEmittedPerTick();
        }
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            this.maxHeatEmitpeerTick = this.getMaxHeatEmittedPerTick();
        }
    }

    public boolean facingMatchesDirection(ForgeDirection direction) {
        return direction.ordinal() == this.getFacing();
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return this.getFacing() != side;
    }

    @Override
    public void setFacing(short side) {
        super.setFacing(side);
    }

    public int getHeatBuffer() {
        return this.HeatBuffer;
    }

    public void setHeatBuffer(int HeatBuffer) {
        this.HeatBuffer = HeatBuffer;
    }

    public void addtoHeatBuffer(int heat) {
        this.setHeatBuffer(this.getHeatBuffer() + heat);
    }

    public int gettransmitHeat() {
        return this.transmitHeat;
    }

    protected abstract int fillHeatBuffer(int var1);

    public abstract int getMaxHeatEmittedPerTick();
}

