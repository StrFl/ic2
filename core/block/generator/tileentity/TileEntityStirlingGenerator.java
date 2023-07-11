/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.generator.tileentity;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IHeatSource;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.generator.container.ContainerStirlingGenerator;
import ic2.core.block.generator.gui.GuiStirlingGenerator;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityStirlingGenerator
extends TileEntityInventory
implements IEnergySource,
IHasGui {
    public double production = 0.0;
    public int receivedheat = 0;
    public double EUstorage = 0.0;
    public final short maxEUStorage;
    public final double productionpeerheat = 0.5f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/Stirling");
    public boolean addedToEnergyNet = false;

    public TileEntityStirlingGenerator() {
        this.maxEUStorage = (short)30000;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        boolean newActive = this.gainEnergy();
        if (this.EUstorage > (double)this.maxEUStorage) {
            this.EUstorage = this.maxEUStorage;
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (this.getActive() != newActive) {
            this.setActive(newActive);
        }
    }

    protected boolean gainEnergy() {
        int heatbandwith;
        double freeEUstorage;
        ForgeDirection dir = ForgeDirection.getOrientation((int)this.getFacing());
        TileEntity te = this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
        if (te instanceof IHeatSource && (freeEUstorage = (double)this.maxEUStorage - this.EUstorage) >= this.productionpeerheat * (double)(heatbandwith = ((IHeatSource)te).maxrequestHeatTick(dir.getOpposite()))) {
            this.receivedheat = ((IHeatSource)te).requestHeat(dir.getOpposite(), heatbandwith);
            if (this.receivedheat != 0) {
                this.production = (double)this.receivedheat * this.productionpeerheat;
                this.EUstorage += this.production;
                return true;
            }
        }
        this.production = 0.0;
        this.receivedheat = 0;
        return false;
    }

    public ContainerBase<TileEntityStirlingGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerStirlingGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiStirlingGenerator(new ContainerStirlingGenerator(entityPlayer, this));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.EUstorage = nbttagcompound.getDouble("EUstorage");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("EUstorage", this.EUstorage);
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return true;
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(this.EUstorage, EnergyNet.instance.getPowerFromTier(this.getSourceTier()));
    }

    @Override
    public int getSourceTier() {
        return 2;
    }

    @Override
    public void drawEnergy(double amount) {
        this.EUstorage -= amount;
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    public int gaugeEUStorageScaled(int i) {
        return (int)(this.EUstorage * (double)i / (double)this.maxEUStorage);
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return this.getFacing() != side;
    }

    @Override
    public void setFacing(short side) {
        super.setFacing(side);
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
    public float getWrenchDropRate() {
        return 0.9f;
    }

    @Override
    public String getInventoryName() {
        return "StirlingGenerator";
    }
}

