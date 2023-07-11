/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotDischarge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityElectricMachine
extends TileEntityInventory
implements IEnergySink {
    public double energy = 0.0;
    public int maxEnergy;
    private boolean addedToEnergyNet = false;
    private int tier;
    private float guiChargeLevel;
    public final InvSlotDischarge dischargeSlot;

    public TileEntityElectricMachine(int maxenergy, int tier1, int oldDischargeIndex) {
        this.maxEnergy = maxenergy;
        this.tier = tier1;
        this.dischargeSlot = new InvSlotDischarge(this, oldDischargeIndex, InvSlot.Access.NONE, tier1);
    }

    public TileEntityElectricMachine(int maxenergy, int tier1, int oldDischargeIndex, boolean allowRedstone) {
        this.maxEnergy = maxenergy;
        this.tier = tier1;
        this.dischargeSlot = new InvSlotDischarge((TileEntityInventory)this, oldDischargeIndex, InvSlot.Access.NONE, tier1, allowRedstone, InvSlot.InvSide.ANY);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.energy = nbttagcompound.getDouble("energy");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.energy);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
    }

    @Override
    public void onUnloaded() {
        if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        super.onUnloaded();
    }

    @Override
    protected void updateEntityServer() {
        double amount;
        super.updateEntityServer();
        if ((double)this.maxEnergy - this.energy >= 1.0 && (amount = this.dischargeSlot.discharge((double)this.maxEnergy - this.energy, false)) > 0.0) {
            this.energy += amount;
            this.markDirty();
        }
        this.guiChargeLevel = Math.min(1.0f, (float)this.energy / (float)this.maxEnergy);
    }

    @Override
    public double getDemandedEnergy() {
        return (double)this.maxEnergy - this.energy;
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        if (this.energy >= (double)this.maxEnergy) {
            return amount;
        }
        this.energy += amount;
        return 0.0;
    }

    @Override
    public int getSinkTier() {
        return this.tier;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return true;
    }

    @Override
    public void onNetworkUpdate(String field) {
        super.onNetworkUpdate(field);
        if (field.equals("tier")) {
            this.setTier(this.tier);
        }
    }

    public final float getChargeLevel() {
        return this.guiChargeLevel;
    }

    public void setTier(int tier1) {
        if (this.tier == tier1) {
            return;
        }
        boolean addedToENet = this.addedToEnergyNet;
        if (addedToENet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        this.tier = tier1;
        this.dischargeSlot.setTier(tier1);
        if (addedToENet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
    }
}

