/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidEvent
 *  net.minecraftforge.fluids.FluidEvent$FluidSpilledEvent
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.generator.tileentity;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.ElectricItem;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.generator.container.ContainerGeoGenerator;
import ic2.core.block.generator.gui.GuiGeoGenerator;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityGeoGenerator
extends TileEntityLiquidTankInventory
implements IEnergySource,
IHasGui {
    public final InvSlotCharge chargeSlot;
    public final InvSlotConsumableLiquid fluidSlot;
    public final InvSlotOutput outputSlot;
    public double storage = 0.0;
    public final short maxStorage;
    public final int production = Math.round(20.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/geothermal"));
    public boolean addedToEnergyNet = false;
    public AudioSource audioSource;

    public TileEntityGeoGenerator() {
        super(8);
        this.maxStorage = (short)24000;
        this.chargeSlot = new InvSlotCharge(this, 0, 1);
        this.fluidSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "fluidSlot", 1, 1, FluidRegistry.LAVA);
        this.outputSlot = new InvSlotOutput(this, "output", 2, 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        try {
            this.storage = nbttagcompound.getDouble("storage");
        }
        catch (Exception e) {
            this.storage = nbttagcompound.getShort("storage");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("storage", this.storage);
    }

    @Override
    protected void updateEntityServer() {
        MutableObject output;
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFluid() && this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)(output = new MutableObject()), true) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
            needsInvUpdate = this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)output, false);
            if (output.getValue() != null) {
                this.outputSlot.add((ItemStack)output.getValue());
            }
        }
        boolean newActive = this.gainEnergy();
        if (this.storage > (double)this.maxStorage) {
            this.storage = this.maxStorage;
        }
        if (this.storage >= 1.0 && this.chargeSlot.get() != null) {
            double used = ElectricItem.manager.charge(this.chargeSlot.get(), this.storage, 1, false, false);
            this.storage -= used;
            if (used > 0.0) {
                needsInvUpdate = true;
            }
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (this.getActive() != newActive) {
            this.setActive(newActive);
        }
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
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return true;
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(this.storage, EnergyNet.instance.getPowerFromTier(this.getSourceTier()));
    }

    @Override
    public int getSourceTier() {
        return 1;
    }

    @Override
    public void drawEnergy(double amount) {
        this.storage -= amount;
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active") && this.prevActive != this.getActive()) {
            if (this.audioSource == null && this.getOperationSoundFile() != null) {
                this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, this.getOperationSoundFile(), true, false, IC2.audioManager.getDefaultVolume());
            }
            if (this.getActive()) {
                if (this.audioSource != null) {
                    this.audioSource.play();
                }
            } else if (this.audioSource != null) {
                this.audioSource.stop();
            }
        }
        super.onNetworkUpdate(field);
    }

    @Override
    public float getWrenchDropRate() {
        return 0.9f;
    }

    public boolean gainEnergy() {
        if (this.isConverting()) {
            this.storage += (double)this.production;
            this.getFluidTank().drain(2, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid == FluidRegistry.LAVA;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    public boolean isConverting() {
        return this.getTankAmount() > 0 && this.storage + (double)this.production <= (double)this.maxStorage;
    }

    public int gaugeStorageScaled(int i) {
        return (int)(this.storage * (double)i / (double)this.maxStorage);
    }

    @Override
    public String getInventoryName() {
        if (IC2.platform.isRendering()) {
            return "Geothermal Generator";
        }
        return "Geoth. Generator";
    }

    public String getOperationSoundFile() {
        return "Generators/GeothermalLoop.ogg";
    }

    public ContainerBase<TileEntityGeoGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerGeoGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiGeoGenerator(new ContainerGeoGenerator(entityPlayer, this));
    }

    @Override
    public void onBlockBreak(Block block, int meta) {
        FluidEvent.fireEvent((FluidEvent)new FluidEvent.FluidSpilledEvent(new FluidStack(FluidRegistry.LAVA, 1000), this.worldObj, this.xCoord, this.yCoord, this.zCoord));
    }
}

