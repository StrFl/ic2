/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.api.recipe;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ICannerEnrichRecipeManager {
    public void addRecipe(FluidStack var1, IRecipeInput var2, FluidStack var3);

    public RecipeOutput getOutputFor(FluidStack var1, ItemStack var2, boolean var3, boolean var4);

    public Map<Input, FluidStack> getRecipes();

    public static class Input {
        public final FluidStack fluid;
        public final IRecipeInput additive;

        public Input(FluidStack fluid1, IRecipeInput additive1) {
            this.fluid = fluid1;
            this.additive = additive1;
        }

        public boolean matches(FluidStack fluid1, ItemStack additive1) {
            return (this.fluid == null || this.fluid.isFluidEqual(fluid1)) && this.additive.matches(additive1);
        }
    }
}

