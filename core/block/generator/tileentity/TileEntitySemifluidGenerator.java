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
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
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
import ic2.api.recipe.ISemiFluidFuelManager;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.SemiFluidFuelManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.generator.container.ContainerSemifluidGenerator;
import ic2.core.block.generator.gui.GuiSemifluidGenerator;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByManager;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntitySemifluidGenerator
extends TileEntityLiquidTankInventory
implements IEnergySource,
IHasGui {
    public final InvSlotCharge chargeSlot = new InvSlotCharge(this, 0, 1);
    public final InvSlotConsumableLiquid fluidSlot = new InvSlotConsumableLiquidByManager((TileEntityInventory)this, "fluidSlot", 1, 1, Recipes.semiFluidGenerator);
    public final InvSlotOutput outputSlot = new InvSlotOutput(this, "output", 2, 1);
    private short ticker = 0;
    protected int burnAmount = 0;
    protected short maxStorage = (short)32000;
    public double storage = 0.0;
    protected double production = 0.0;
    protected boolean addedToEnergyNet = false;
    protected AudioSource audioSource;

    public TileEntitySemifluidGenerator() {
        super(10);
    }

    public static void init() {
        Recipes.semiFluidGenerator = new SemiFluidFuelManager();
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidOil") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("oil", 10, Math.round(8.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidOil")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidFuel") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("fuel", 5, Math.round(32.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidFuel")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiomass") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("biomass", 20, Math.round(8.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiomass")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBioethanol") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("bioethanol", 10, Math.round(16.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBioethanol")));
        }
        if (ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiogas") > 0.0f) {
            TileEntitySemifluidGenerator.addFuel("ic2biogas", 10, Math.round(16.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/semiFluidBiogas")));
        }
    }

    public static void addFuel(String fluidName, int amount, int eu) {
        Recipes.semiFluidGenerator.addFluid(fluidName, amount, eu);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.storage = nbttagcompound.getDouble("storage");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("storage", this.storage);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFluid()) {
            needsInvUpdate = this.gainFuel();
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

    protected boolean gainEnergy() {
        if (this.isConverting()) {
            this.storage += this.production;
            if (this.ticker >= 19) {
                this.getFluidTank().drain(this.burnAmount, true);
                this.ticker = 0;
            } else {
                this.ticker = (short)(this.ticker + 1);
            }
            return true;
        }
        return false;
    }

    public boolean isConverting() {
        return this.getTankAmount() > 0 && this.storage + this.production <= (double)this.maxStorage;
    }

    protected void calcProduction() {
        ISemiFluidFuelManager.BurnProperty property;
        if (this.getFluidfromTank() != null && (property = Recipes.semiFluidGenerator.getBurnProperty(this.getFluidfromTank())) != null) {
            this.production = property.power;
            return;
        }
        this.production = 0.0;
    }

    protected void calcBurnAmount() {
        ISemiFluidFuelManager.BurnProperty property;
        if (this.getFluidfromTank() != null && (property = Recipes.semiFluidGenerator.getBurnProperty(this.getFluidfromTank())) != null) {
            this.burnAmount = property.amount;
            return;
        }
        this.burnAmount = 0;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (!Recipes.semiFluidGenerator.acceptsFluid(fluid)) {
            return false;
        }
        if (this.getFluidTank().getFluid() == null) {
            return true;
        }
        return this.getFluidTank().getFluid().getFluid() == fluid && this.getTankAmount() < this.getFluidTank().getCapacity();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    protected boolean gainFuel() {
        if (this.fluidTank.getFluid() != null) {
            this.calcProduction();
            this.calcBurnAmount();
        }
        boolean ret = false;
        MutableObject output = new MutableObject();
        if (this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)output, true) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
            ret = this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)output, false);
            if (output.getValue() != null) {
                this.outputSlot.add((ItemStack)output.getValue());
            }
        }
        return ret;
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

    public String getOperationSoundFile() {
        return "Generators/GeothermalLoop.ogg";
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

    public int gaugeStorageScaled(int i) {
        return (int)(this.storage * (double)i / (double)this.maxStorage);
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    public ContainerBase<TileEntitySemifluidGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerSemifluidGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiSemifluidGenerator(new ContainerSemifluidGenerator(entityPlayer, this));
    }

    @Override
    public String getInventoryName() {
        if (IC2.platform.isRendering()) {
            return "Semifluid Generator";
        }
        return "SemifluidGenerator";
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
}

