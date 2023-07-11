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
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.BasicMachineRecipeManager;
import ic2.core.ContainerBase;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankStandardMaschine;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.container.ContainerOreWashing;
import ic2.core.block.machine.gui.GuiOreWashing;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityOreWashing
extends TileEntityLiquidTankStandardMaschine {
    public final InvSlotConsumableLiquid fluidSlot;
    public final InvSlotOutput cellSlot;
    public static List<Map.Entry<ItemStack, ItemStack>> recipes = new Vector<Map.Entry<ItemStack, ItemStack>>();

    public TileEntityOreWashing() {
        super(16, 500, 3, 8);
        this.inputSlot = new InvSlotProcessableGeneric((TileEntityInventory)this, "input", 0, 1, Recipes.oreWashing);
        this.fluidSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "fluid", 0, 1, FluidRegistry.WATER);
        this.cellSlot = new InvSlotOutput(this, "cell", 0, 1);
    }

    public static void init() {
        Recipes.oreWashing = new BasicMachineRecipeManager();
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.needsFluid()) {
            this.gainFluid();
        }
    }

    @Override
    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        super.operateOnce(output, processResult);
        this.fluidTank.drain(output.metadata.getInteger("amount"), true);
    }

    @Override
    public RecipeOutput getOutput() {
        RecipeOutput ret = super.getOutput();
        if (ret != null) {
            if (ret.metadata == null) {
                return null;
            }
            if (ret.metadata.getInteger("amount") > this.fluidTank.getFluidAmount()) {
                return null;
            }
        }
        return ret;
    }

    public boolean gainFluid() {
        boolean ret = false;
        MutableObject output = new MutableObject();
        if (this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)output, true) && (output.getValue() == null || this.cellSlot.canAdd((ItemStack)output.getValue()))) {
            ret = this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)output, false);
            if (output.getValue() != null) {
                this.cellSlot.add((ItemStack)output.getValue());
            }
        }
        return ret;
    }

    @Override
    public String getInventoryName() {
        return "OreWashing";
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiOreWashing(new ContainerOreWashing(entityPlayer, this));
    }

    public ContainerBase<TileEntityOreWashing> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerOreWashing(entityPlayer, this);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return FluidRegistry.WATER.getID() == fluid.getID();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, new UpgradableProperty[]{UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming});
    }

    @Override
    public float getWrenchDropRate() {
        return 0.85f;
    }
}

