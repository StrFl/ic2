/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.ILiquidHeatExchangerManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.LiquidHeatExchangerManager;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByManager;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerLiquidHeatExchanger;
import ic2.core.block.machine.gui.GuiLiquidHeatExchanger;
import ic2.core.init.MainConfig;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityLiquidHeatExchanger
extends TileEntityHeatSourceInventory
implements IHasGui,
IFluidHandler,
IUpgradableBlock {
    private boolean newActive;
    public final FluidTank inputTank = new FluidTank(2000);
    public final FluidTank outputTank = new FluidTank(2000);
    public final InvSlotConsumable heatexchangerslots = new InvSlotConsumableItemStack((TileEntityInventory)this, "heatexchangerslots", 0, 10, Ic2Items.heatconductor);
    public final InvSlotOutput hotoutputSlot;
    public final InvSlotOutput cooloutputSlot;
    public final InvSlotConsumableLiquid hotfluidinputSlot;
    public final InvSlotConsumableLiquid coolfluidinputSlot;
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityLiquidHeatExchanger() {
        this.heatexchangerslots.setStackSizeLimit(1);
        this.hotoutputSlot = new InvSlotOutput(this, "outputSlot", 3, 1);
        this.cooloutputSlot = new InvSlotOutput(this, "outputSlot", 6, 1);
        this.hotfluidinputSlot = new InvSlotConsumableLiquidByManager(this, "hotfluidinputSlot", 5, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, Recipes.liquidCooldownManager);
        this.coolfluidinputSlot = new InvSlotConsumableLiquidByTank(this, "coolfluidoutputSlot", 4, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 7, 3);
        this.newActive = false;
    }

    public static void init() {
        Recipes.liquidCooldownManager = new LiquidHeatExchangerManager(false);
        Recipes.liquidHeatupManager = new LiquidHeatExchangerManager(true);
        TileEntityLiquidHeatExchanger.addCooldownRecipe("lava", "ic2pahoehoelava", Math.round(20.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/fluidconversion/heatExchangerLava")));
        TileEntityLiquidHeatExchanger.addBiDiRecipe("ic2hotcoolant", "ic2coolant", Math.round(20.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/fluidconversion/heatExchangerHotCoolant")));
        TileEntityLiquidHeatExchanger.addHeatupRecipe("ic2hotwater", "water", Math.round(1.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/fluidconversion/heatExchangerWater")));
    }

    public static void addBiDiRecipe(String hotFluid, String coldFluid, int huPerMB) {
        TileEntityLiquidHeatExchanger.addHeatupRecipe(hotFluid, coldFluid, huPerMB);
        TileEntityLiquidHeatExchanger.addCooldownRecipe(hotFluid, coldFluid, huPerMB);
    }

    public static void addHeatupRecipe(String hotFluid, String coldFluid, int huPerMB) {
        Recipes.liquidHeatupManager.addFluid(coldFluid, hotFluid, huPerMB);
    }

    public static void addCooldownRecipe(String hotFluid, String coldFluid, int huPerMB) {
        Recipes.liquidCooldownManager.addFluid(hotFluid, coldFluid, huPerMB);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.inputTank.readFromNBT(nbt.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbt.getCompoundTag("outputTank"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound inputTankTag = new NBTTagCompound();
        this.inputTank.writeToNBT(inputTankTag);
        nbt.setTag("inputTank", (NBTBase)inputTankTag);
        NBTTagCompound outputTankTag = new NBTTagCompound();
        this.outputTank.writeToNBT(outputTankTag);
        nbt.setTag("outputTank", (NBTBase)outputTankTag);
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.hotfluidinputSlot.isEmpty() && this.hotfluidinputSlot.transferToTank((IFluidTank)this.inputTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.hotoutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.coolfluidinputSlot.isEmpty() && this.coolfluidinputSlot.transferFromTank((IFluidTank)this.outputTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.cooloutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    protected void updateEntityServer() {
        RecipeOutput outputoutputSlot;
        super.updateEntityServer();
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.hotoutputSlot.add(processResult);
        }
        if ((outputoutputSlot = this.processOutputSlot(true)) != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.cooloutputSlot.add(processResult);
        }
        boolean bl = this.newActive = this.HeatBuffer > 0;
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    public ContainerBase<TileEntityLiquidHeatExchanger> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerLiquidHeatExchanger(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiLiquidHeatExchanger(new ContainerLiquidHeatExchanger(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "HeatSourceFluid";
    }

    public int gaugeLiquidScaled(int i, int tank) {
        switch (tank) {
            case 0: {
                if (this.inputTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.inputTank.getFluidAmount() * i / this.inputTank.getCapacity();
            }
            case 1: {
                if (this.outputTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.outputTank.getFluidAmount() * i / this.outputTank.getCapacity();
            }
        }
        return 0;
    }

    @Override
    public int getMaxHeatEmittedPerTick() {
        int count = 0;
        for (int i = 0; i < this.heatexchangerslots.size(); ++i) {
            if (this.heatexchangerslots.get(i) == null) continue;
            count += 10;
        }
        return count;
    }

    @Override
    protected int fillHeatBuffer(int bufferspace) {
        if (bufferspace > 0) {
            int AmountHotCoolant = this.inputTank.getFluidAmount();
            int OutputTankFreeCap = this.outputTank.getCapacity() - this.outputTank.getFluidAmount();
            FluidStack draincoolant = null;
            if (OutputTankFreeCap == 0 || AmountHotCoolant == 0) {
                return 0;
            }
            Fluid fluidInputTank = this.inputTank.getFluid().getFluid();
            Fluid fluidOutput = null;
            int hUper1mb = 0;
            if (Recipes.liquidCooldownManager.acceptsFluid(fluidInputTank)) {
                ILiquidHeatExchangerManager.HeatExchangeProperty hep = Recipes.liquidCooldownManager.getHeatExchangeProperty(fluidInputTank);
                fluidOutput = hep.outputFluid;
                hUper1mb = hep.huPerMB;
            }
            if (fluidOutput == null) {
                return 0;
            }
            if (this.outputTank.getFluidAmount() > 0 && !this.outputTank.getFluid().getFluid().equals(fluidOutput)) {
                return 0;
            }
            int mbtofillheatbuffer = bufferspace / hUper1mb;
            draincoolant = OutputTankFreeCap >= AmountHotCoolant ? (mbtofillheatbuffer <= AmountHotCoolant ? this.inputTank.drain(mbtofillheatbuffer, false) : this.inputTank.drain(AmountHotCoolant, false)) : (mbtofillheatbuffer <= OutputTankFreeCap ? this.inputTank.drain(mbtofillheatbuffer, false) : this.inputTank.drain(OutputTankFreeCap * 20, false));
            if (draincoolant != null) {
                this.inputTank.drain(draincoolant.amount, true);
                this.outputTank.fill(new FluidStack(fluidOutput, draincoolant.amount), true);
            }
            return draincoolant.amount * hUper1mb;
        }
        return 0;
    }

    public FluidTank getinputtank() {
        return this.inputTank;
    }

    public FluidTank getoutputtank() {
        return this.outputTank;
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{this.inputTank.getInfo(), this.outputTank.getInfo()};
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return Recipes.liquidCooldownManager.acceptsFluid(fluid);
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        FluidStack fluidStack = this.outputTank.getFluid();
        if (fluidStack == null) {
            return false;
        }
        return fluidStack.isFluidEqual(new FluidStack(fluid, 1));
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!this.canFill(from, resource.getFluid())) {
            return 0;
        }
        return this.inputTank.fill(resource, doFill);
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.outputTank.getFluid())) {
            return null;
        }
        if (!this.canDrain(from, resource.getFluid())) {
            return null;
        }
        return this.outputTank.drain(resource.amount, doDrain);
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return this.outputTank.drain(maxDrain, doDrain);
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }

    @Override
    public double getEnergy() {
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
    }
}

