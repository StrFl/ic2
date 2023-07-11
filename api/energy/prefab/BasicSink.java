/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.api.energy.prefab;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class BasicSink
extends TileEntity
implements IEnergySink {
    public final TileEntity parent;
    protected int capacity;
    protected int tier;
    protected double energyStored;
    protected boolean addedToEnet;

    public BasicSink(TileEntity parent1, int capacity1, int tier1) {
        this.parent = parent1;
        this.capacity = capacity1;
        this.tier = tier1;
    }

    public void updateEntity() {
        if (!this.addedToEnet) {
            this.onLoaded();
        }
    }

    public void onLoaded() {
        if (!this.addedToEnet && !FMLCommonHandler.instance().getEffectiveSide().isClient() && Info.isIc2Available()) {
            this.worldObj = this.parent.getWorldObj();
            this.xCoord = this.parent.xCoord;
            this.yCoord = this.parent.yCoord;
            this.zCoord = this.parent.zCoord;
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnet = true;
        }
    }

    public void invalidate() {
        super.invalidate();
        this.onChunkUnload();
    }

    public void onChunkUnload() {
        if (this.addedToEnet && Info.isIc2Available()) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnet = false;
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagCompound data = tag.getCompoundTag("IC2BasicSink");
        this.energyStored = data.getDouble("energy");
    }

    public void writeToNBT(NBTTagCompound tag) {
        try {
            super.writeToNBT(tag);
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        NBTTagCompound data = new NBTTagCompound();
        data.setDouble("energy", this.energyStored);
        tag.setTag("IC2BasicSink", (NBTBase)data);
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity1) {
        this.capacity = capacity1;
    }

    public int getTier() {
        return this.tier;
    }

    public void setTier(int tier1) {
        this.tier = tier1;
    }

    public double getEnergyStored() {
        return this.energyStored;
    }

    public void setEnergyStored(double amount) {
        this.energyStored = amount;
    }

    public boolean canUseEnergy(double amount) {
        return this.energyStored >= amount;
    }

    public boolean useEnergy(double amount) {
        if (this.canUseEnergy(amount) && !FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.energyStored -= amount;
            return true;
        }
        return false;
    }

    public boolean discharge(ItemStack stack, int limit) {
        if (stack == null || !Info.isIc2Available()) {
            return false;
        }
        double amount = (double)this.capacity - this.energyStored;
        if (amount <= 0.0) {
            return false;
        }
        if (limit > 0 && (double)limit < amount) {
            amount = limit;
        }
        amount = ElectricItem.manager.discharge(stack, amount, this.tier, limit > 0, true, false);
        this.energyStored += amount;
        return amount > 0.0;
    }

    @Deprecated
    public void onUpdateEntity() {
        this.updateEntity();
    }

    @Deprecated
    public void onInvalidate() {
        this.invalidate();
    }

    @Deprecated
    public void onOnChunkUnload() {
        this.onChunkUnload();
    }

    @Deprecated
    public void onReadFromNbt(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Deprecated
    public void onWriteToNbt(NBTTagCompound tag) {
        this.writeToNBT(tag);
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return true;
    }

    @Override
    public double getDemandedEnergy() {
        return Math.max(0.0, (double)this.capacity - this.energyStored);
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        this.energyStored += amount;
        return 0.0;
    }

    @Override
    public int getSinkTier() {
        return this.tier;
    }
}

