/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.block.TileEntityLiquidTankStandardMaschine;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.machine.container.ContainerFluidBottler;
import ic2.core.block.machine.gui.GuiFluidBottler;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityFluidBottler
extends TileEntityLiquidTankStandardMaschine {
    public final InvSlotConsumableLiquid drainInputSlot;
    public final InvSlotConsumableLiquid fillInputSlot;

    public TileEntityFluidBottler() {
        super(2, 100, 1, 8);
        this.drainInputSlot = new InvSlotConsumableLiquidByTank(this, "drainInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, (IFluidTank)this.fluidTank);
        this.fillInputSlot = new InvSlotConsumableLiquidByTank(this, "fillInput", -1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.fluidTank);
    }

    @Override
    public RecipeOutput getOutput() {
        return this.processInput(true);
    }

    @Override
    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        this.processInput(false);
        this.outputSlot.add(processResult);
    }

    @Override
    public String getInventoryName() {
        return "Bottling Plant";
    }

    public ContainerBase<TileEntityFluidBottler> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerFluidBottler(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiFluidBottler(new ContainerFluidBottler(entityPlayer, this));
    }

    @Override
    public float getWrenchDropRate() {
        return 0.85f;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    private RecipeOutput processInput(boolean simulate) {
        MutableObject output;
        if (!this.drainInputSlot.isEmpty() && this.drainInputSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        if (!this.fillInputSlot.isEmpty() && this.fillInputSlot.transferFromTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, new UpgradableProperty[]{UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing});
    }
}

