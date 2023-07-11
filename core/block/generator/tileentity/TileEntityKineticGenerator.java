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
import ic2.api.energy.tile.IKineticSource;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.generator.container.ContainerKineticGenerator;
import ic2.core.block.generator.gui.GuiKineticGenerator;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityKineticGenerator
extends TileEntityInventory
implements IEnergySource,
IHasGui {
    public int updateTicker;
    private double guiproduction = 0.0;
    private double production = 0.0;
    private int receivedkinetic = 0;
    public double EUstorage = 0.0;
    private final int maxEUStorage;
    private final double productionpeerkineticunit = 0.25 * (double)ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/Kinetic");
    public boolean addedToEnergyNet = false;

    public TileEntityKineticGenerator() {
        this.maxEUStorage = 200000;
        this.updateTicker = IC2.random.nextInt(this.getTickRate());
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        boolean newActive = this.gainEnergy();
        if (this.updateTicker++ >= this.getTickRate()) {
            this.guiproduction = this.production;
            this.updateTicker = 0;
        }
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
        ForgeDirection dir = ForgeDirection.getOrientation((int)this.getFacing());
        TileEntity te = this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
        if (te instanceof IKineticSource) {
            int kineticbandwith = ((IKineticSource)te).maxrequestkineticenergyTick(dir.getOpposite());
            double freeEUstorage = (double)this.maxEUStorage - this.EUstorage;
            if (freeEUstorage > 0.0 && freeEUstorage < this.productionpeerkineticunit * (double)kineticbandwith) {
                freeEUstorage = this.productionpeerkineticunit * (double)kineticbandwith;
            }
            if (freeEUstorage >= this.productionpeerkineticunit * (double)kineticbandwith) {
                this.receivedkinetic = ((IKineticSource)te).requestkineticenergy(dir.getOpposite(), kineticbandwith);
                if (this.receivedkinetic != 0) {
                    this.production = (double)this.receivedkinetic * this.productionpeerkineticunit;
                    this.EUstorage += this.production;
                    return true;
                }
            }
        }
        this.production = 0.0;
        this.receivedkinetic = 0;
        return false;
    }

    public ContainerBase<TileEntityKineticGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerKineticGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiKineticGenerator(new ContainerKineticGenerator(entityPlayer, this));
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
        return 3;
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

    public double getproduction() {
        return this.guiproduction;
    }

    public int getTickRate() {
        return 20;
    }
}

