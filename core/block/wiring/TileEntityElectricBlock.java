/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.wiring;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.tile.IEnergyStorage;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.wiring.ContainerElectricBlock;
import ic2.core.block.wiring.GuiElectricBlock;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityElectricBlock
extends TileEntityInventory
implements IEnergySink,
IEnergySource,
IHasGui,
INetworkClientTileEntityEventListener,
IEnergyStorage {
    public final int tier;
    public final int output;
    public final int maxStorage;
    public double energy = 0.0;
    public boolean hasRedstone = false;
    public byte redstoneMode = 0;
    public static byte redstoneModes = (byte)7;
    private boolean isEmittingRedstone = false;
    private int redstoneUpdateInhibit = 5;
    public boolean addedToEnergyNet = false;
    public final InvSlotCharge chargeSlot;
    public final InvSlotDischarge dischargeSlot;

    public TileEntityElectricBlock(int tier1, int output1, int maxStorage1) {
        this.tier = tier1;
        this.output = output1;
        this.maxStorage = maxStorage1;
        this.chargeSlot = new InvSlotCharge(this, 0, tier1);
        this.dischargeSlot = new InvSlotDischarge((TileEntityInventory)this, 1, InvSlot.Access.IO, tier1, InvSlot.InvSide.BOTTOM);
    }

    public float getChargeLevel() {
        float ret = (float)this.energy / (float)this.maxStorage;
        if (ret > 1.0f) {
            ret = 1.0f;
        }
        return ret;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.energy = Util.limit(nbttagcompound.getDouble("energy"), 0.0, (double)this.maxStorage + EnergyNet.instance.getPowerFromTier(this.tier));
        this.redstoneMode = nbttagcompound.getByte("redstoneMode");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.energy);
        nbttagcompound.setBoolean("active", this.getActive());
        nbttagcompound.setByte("redstoneMode", this.redstoneMode);
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
        boolean shouldEmitRedstone;
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.energy >= 1.0) {
            double sent = this.chargeSlot.charge(this.energy);
            this.energy -= sent;
            boolean bl = needsInvUpdate = sent > 0.0;
        }
        if (this.getDemandedEnergy() > 0.0 && !this.dischargeSlot.isEmpty()) {
            double gain = this.dischargeSlot.discharge((double)this.maxStorage - this.energy, false);
            this.energy += gain;
            boolean bl = needsInvUpdate = gain > 0.0;
        }
        if (this.redstoneMode == 5 || this.redstoneMode == 6) {
            this.hasRedstone = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
        }
        if ((shouldEmitRedstone = this.shouldEmitRedstone()) != this.isEmittingRedstone) {
            this.isEmittingRedstone = shouldEmitRedstone;
            this.setActive(this.isEmittingRedstone);
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return !this.facingMatchesDirection(direction);
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return this.facingMatchesDirection(direction);
    }

    public boolean facingMatchesDirection(ForgeDirection direction) {
        return direction.ordinal() == this.getFacing();
    }

    @Override
    public double getOfferedEnergy() {
        if (!(!(this.energy >= (double)this.output) || this.redstoneMode == 5 && this.hasRedstone || this.redstoneMode == 6 && this.hasRedstone && !(this.energy >= (double)this.maxStorage))) {
            return Math.min(this.energy, (double)this.output);
        }
        return 0.0;
    }

    @Override
    public void drawEnergy(double amount) {
        this.energy -= amount;
    }

    @Override
    public double getDemandedEnergy() {
        return (double)this.maxStorage - this.energy;
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        if (this.energy >= (double)this.maxStorage) {
            return amount;
        }
        this.energy += amount;
        return 0.0;
    }

    @Override
    public int getSourceTier() {
        return this.tier;
    }

    @Override
    public int getSinkTier() {
        return this.tier;
    }

    public ContainerBase<? extends TileEntityElectricBlock> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerElectricBlock(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiElectricBlock(new ContainerElectricBlock(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return this.getFacing() != side;
    }

    @Override
    public void setFacing(short facing) {
        if (this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
        }
        super.setFacing(facing);
        if (this.addedToEnergyNet) {
            this.addedToEnergyNet = false;
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
    }

    public boolean isEmittingRedstone() {
        return this.isEmittingRedstone;
    }

    public boolean shouldEmitRedstone() {
        boolean shouldEmitRedstone = false;
        switch (this.redstoneMode) {
            case 1: {
                shouldEmitRedstone = this.energy >= (double)(this.maxStorage - this.output * 20);
                break;
            }
            case 2: {
                shouldEmitRedstone = this.energy > (double)this.output && this.energy < (double)this.maxStorage;
                break;
            }
            case 3: {
                shouldEmitRedstone = this.energy > (double)this.output && this.energy < (double)this.maxStorage || this.energy < (double)this.output;
                break;
            }
            case 4: {
                boolean bl = shouldEmitRedstone = this.energy < (double)this.output;
            }
        }
        if (this.isEmittingRedstone == shouldEmitRedstone || this.redstoneUpdateInhibit == 0) {
            this.redstoneUpdateInhibit = 5;
            return shouldEmitRedstone;
        }
        --this.redstoneUpdateInhibit;
        return this.isEmittingRedstone;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        this.redstoneMode = (byte)(this.redstoneMode + 1);
        if (this.redstoneMode >= redstoneModes) {
            this.redstoneMode = 0;
        }
        IC2.platform.messagePlayer(player, this.getredstoneMode(), new Object[0]);
    }

    public String getredstoneMode() {
        if (this.redstoneMode > 6 || this.redstoneMode < 0) {
            return "";
        }
        return StatCollector.translateToLocal((String)("ic2.EUStorage.gui.mod.redstone" + this.redstoneMode));
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        ItemStack ret = super.getWrenchDrop(entityPlayer);
        float energyRetainedInStorageBlockDrops = ConfigUtil.getFloat(MainConfig.get(), "balance/energyRetainedInStorageBlockDrops");
        if (energyRetainedInStorageBlockDrops > 0.0f) {
            NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(ret);
            nbttagcompound.setDouble("energy", this.energy * (double)energyRetainedInStorageBlockDrops);
        }
        return ret;
    }

    @Override
    public int getStored() {
        return (int)this.energy;
    }

    @Override
    public int getCapacity() {
        return this.maxStorage;
    }

    @Override
    public int getOutput() {
        return this.output;
    }

    @Override
    public double getOutputEnergyUnitsPerTick() {
        return this.output;
    }

    @Override
    public void setStored(int energy1) {
        this.energy = energy1;
    }

    @Override
    public int addEnergy(int amount) {
        this.energy += (double)amount;
        return amount;
    }

    @Override
    public boolean isTeleporterCompatible(ForgeDirection side) {
        return true;
    }
}

