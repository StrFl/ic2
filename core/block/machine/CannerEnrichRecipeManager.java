/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.core.block.machine;

import ic2.api.recipe.ICannerEnrichRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.core.block.invslot.InvSlotProcessableCanner;
import ic2.core.util.LiquidUtil;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CannerEnrichRecipeManager
implements ICannerEnrichRecipeManager {
    private final Map<ICannerEnrichRecipeManager.Input, FluidStack> recipes = new HashMap<ICannerEnrichRecipeManager.Input, FluidStack>();

    @Override
    public void addRecipe(FluidStack fluid, IRecipeInput additive, FluidStack output) {
        if (fluid == null) {
            throw new NullPointerException("The fluid recipe input is null.");
        }
        if (additive == null) {
            throw new NullPointerException("The additive recipe input is null.");
        }
        if (output == null) {
            throw new NullPointerException("The recipe output is null.");
        }
        if (!LiquidUtil.check(fluid)) {
            throw new IllegalArgumentException("The fluid recipe input is invalid.");
        }
        if (!LiquidUtil.check(output)) {
            throw new IllegalArgumentException("The fluid recipe output is invalid.");
        }
        for (ICannerEnrichRecipeManager.Input input : this.recipes.keySet()) {
            for (ItemStack additiveStack : additive.getInputs()) {
                if (!input.matches(fluid, additiveStack)) continue;
                throw new RuntimeException("ambiguous recipe: [" + fluid + "+" + additive.getInputs() + " -> " + output + "], conflicts with [" + input.fluid + "+" + input.additive.getInputs() + " -> " + this.recipes.get(input) + "]");
            }
        }
        this.recipes.put(new ICannerEnrichRecipeManager.Input(fluid, additive), output);
    }

    @Override
    public RecipeOutput getOutputFor(FluidStack fluid, ItemStack additive, boolean adjustInput, boolean acceptTest) {
        if (!acceptTest && additive == null) {
            return null;
        }
        for (Map.Entry<ICannerEnrichRecipeManager.Input, FluidStack> entry : this.recipes.entrySet()) {
            ICannerEnrichRecipeManager.Input input = entry.getKey();
            if (acceptTest && fluid == null) {
                if (!input.additive.matches(additive)) continue;
                return InvSlotProcessableCanner.createOutput(entry.getValue(), new ItemStack[0]);
            }
            if (acceptTest && additive == null) {
                if (input.fluid != null && !input.fluid.isFluidEqual(fluid)) continue;
                return InvSlotProcessableCanner.createOutput(entry.getValue(), new ItemStack[0]);
            }
            if (!input.matches(fluid, additive)) continue;
            assert (fluid == null && input.fluid == null || fluid != null && input.fluid != null);
            if (!acceptTest && (fluid != null && fluid.amount < input.fluid.amount || additive.stackSize < input.additive.getAmount())) break;
            if (adjustInput) {
                if (fluid != null) {
                    fluid.amount -= input.fluid.amount;
                }
                additive.stackSize -= input.additive.getAmount();
            }
            return InvSlotProcessableCanner.createOutput(entry.getValue(), new ItemStack[0]);
        }
        return null;
    }

    @Override
    public Map<ICannerEnrichRecipeManager.Input, FluidStack> getRecipes() {
        return this.recipes;
    }
}

