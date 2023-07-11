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
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class BasicSource
extends TileEntity
implements IEnergySource {
    public final TileEntity parent;
    protected double capacity;
    protected int tier;
    protected double power;
    protected double energyStored;
    protected boolean addedToEnet;

    public BasicSource(TileEntity parent1, double capacity1, int tier1) {
        double power = EnergyNet.instance.getPowerFromTier(tier1);
        this.parent = parent1;
        this.capacity = capacity1 < power ? power : capacity1;
        this.tier = tier1;
        this.power = power;
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
        NBTTagCompound data = tag.getCompoundTag("IC2BasicSource");
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
        tag.setTag("IC2BasicSource", (NBTBase)data);
    }

    public double getCapacity() {
        return this.capacity;
    }

    public void setCapacity(double capacity1) {
        if (capacity1 < this.power) {
            capacity1 = this.power;
        }
        this.capacity = capacity1;
    }

    public int getTier() {
        return this.tier;
    }

    public void setTier(int tier1) {
        double power = EnergyNet.instance.getPowerFromTier(tier1);
        if (this.capacity < power) {
            this.capacity = power;
        }
        this.tier = tier1;
        this.power = power;
    }

    public double getEnergyStored() {
        return this.energyStored;
    }

    public void setEnergyStored(double amount) {
        this.energyStored = amount;
    }

    public double getFreeCapacity() {
        return this.capacity - this.energyStored;
    }

    public double addEnergy(double amount) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return 0.0;
        }
        if (amount > this.capacity - this.energyStored) {
            amount = this.capacity - this.energyStored;
        }
        this.energyStored += amount;
        return amount;
    }

    public boolean charge(ItemStack stack) {
        if (stack == null || !Info.isIc2Available()) {
            return false;
        }
        double amount = ElectricItem.manager.charge(stack, this.energyStored, this.tier, false, false);
        this.energyStored -= amount;
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
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return true;
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(this.energyStored, this.power);
    }

    @Override
    public void drawEnergy(double amount) {
        this.energyStored -= amount;
    }

    @Override
    public int getSourceTier() {
        return this.tier;
    }
}

