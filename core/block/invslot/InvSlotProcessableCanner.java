/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.invslot;

import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class InvSlotProcessableCanner
extends InvSlotProcessableGeneric {
    public InvSlotProcessableCanner(TileEntityCanner base1, String name1, int oldStartIndex1, int count) {
        super((TileEntityInventory)base1, name1, oldStartIndex1, count, null);
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        switch (((TileEntityCanner)this.base).getMode()) {
            case BottleSolid: 
            case EnrichLiquid: {
                return super.accepts(itemStack);
            }
            case BottleLiquid: 
            case EmptyLiquid: {
                return false;
            }
        }
        assert (false);
        return false;
    }

    @Override
    public void consume() {
        FluidStack fluid;
        super.consume();
        ItemStack containerStack = ((TileEntityCanner)this.base).canInputSlot.get();
        if (containerStack != null && containerStack.stackSize <= 0) {
            ((TileEntityCanner)this.base).canInputSlot.put(null);
        }
        if ((fluid = ((TileEntityCanner)this.base).inputTank.getFluid()) != null && fluid.amount <= 0) {
            ((TileEntityCanner)this.base).inputTank.setFluid(null);
        }
    }

    @Override
    protected RecipeOutput getOutputFor(ItemStack input, boolean adjustInput, boolean forAccept) {
        return this.getOutput(((TileEntityCanner)this.base).canInputSlot.get(), input, adjustInput, forAccept);
    }

    @Override
    protected boolean allowEmptyInput() {
        return true;
    }

    protected RecipeOutput getOutput(ItemStack container, ItemStack fill, boolean adjustInput, boolean forAccept) {
        switch (((TileEntityCanner)this.base).getMode()) {
            case BottleSolid: {
                return Recipes.cannerBottle.getOutputFor(container, fill, adjustInput, forAccept);
            }
            case BottleLiquid: {
                return this.fillContainer(adjustInput);
            }
            case EmptyLiquid: {
                return this.drainContainer(adjustInput);
            }
            case EnrichLiquid: {
                return Recipes.cannerEnrich.getOutputFor(((TileEntityCanner)this.base).inputTank.getFluid(), fill, adjustInput, forAccept);
            }
        }
        assert (false);
        return null;
    }

    private RecipeOutput fillContainer(boolean adjustInput) {
        RecipeOutput ret = null;
        MutableObject output = new MutableObject();
        if (((TileEntityCanner)this.base).canInputSlot.transferFromTank((IFluidTank)((TileEntityCanner)this.base).inputTank, (MutableObject<ItemStack>)output, !adjustInput)) {
            ret = output.getValue() != null ? new RecipeOutput(null, (ItemStack)output.getValue()) : new RecipeOutput(null, new ItemStack[0]);
        }
        return ret;
    }

    private RecipeOutput drainContainer(boolean adjustInput) {
        int maxAmount = ((TileEntityCanner)this.base).outputTank.getCapacity() - ((TileEntityCanner)this.base).outputTank.getFluidAmount();
        if (maxAmount <= 0) {
            return null;
        }
        RecipeOutput ret = null;
        MutableObject output = new MutableObject();
        FluidStack fluid = ((TileEntityCanner)this.base).canInputSlot.drain(null, maxAmount, (MutableObject<ItemStack>)output, !adjustInput);
        if (fluid != null) {
            ret = output.getValue() != null ? InvSlotProcessableCanner.createOutput(fluid, (ItemStack)output.getValue()) : InvSlotProcessableCanner.createOutput(fluid, new ItemStack[0]);
        }
        return ret;
    }

    public static RecipeOutput createOutput(FluidStack fluid, ItemStack ... items) {
        NBTTagCompound metadata = new NBTTagCompound();
        NBTTagCompound output = new NBTTagCompound();
        fluid.writeToNBT(output);
        metadata.setTag("output", (NBTBase)output);
        return new RecipeOutput(metadata, items);
    }
}

