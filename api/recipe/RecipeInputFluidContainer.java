/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidContainerRegistry
 *  net.minecraftforge.fluids.FluidContainerRegistry$FluidContainerData
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.api.recipe;

import ic2.api.recipe.IRecipeInput;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class RecipeInputFluidContainer
implements IRecipeInput {
    public final Fluid fluid;
    public final int amount;

    public RecipeInputFluidContainer(Fluid fluid) {
        this(fluid, 1000);
    }

    public RecipeInputFluidContainer(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    @Override
    public boolean matches(ItemStack subject) {
        FluidStack fs = FluidContainerRegistry.getFluidForFilledItem((ItemStack)subject);
        if (fs == null) {
            return false;
        }
        return fs.getFluid() == this.fluid;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public List<ItemStack> getInputs() {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            if (data.fluid.getFluid() != this.fluid) continue;
            ret.add(data.filledContainer);
        }
        return ret;
    }

    public String toString() {
        return "RInputFluidContainer<" + this.amount + "x" + this.fluid.getName() + ">";
    }
}

