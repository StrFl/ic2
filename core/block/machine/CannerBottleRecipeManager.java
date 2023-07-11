/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.machine;

import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.core.util.StackUtil;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;

public class CannerBottleRecipeManager
implements ICannerBottleRecipeManager {
    private final Map<ICannerBottleRecipeManager.Input, RecipeOutput> recipes = new HashMap<ICannerBottleRecipeManager.Input, RecipeOutput>();

    @Override
    public void addRecipe(IRecipeInput container, IRecipeInput fill, ItemStack output) {
        if (container == null) {
            throw new NullPointerException("The container recipe input is null");
        }
        if (fill == null) {
            throw new NullPointerException("The fill recipe input is null");
        }
        if (output == null) {
            throw new NullPointerException("The recipe output is null");
        }
        if (!StackUtil.check(output)) {
            throw new IllegalArgumentException("The recipe output " + StackUtil.toStringSafe(output) + " is invalid");
        }
        for (ICannerBottleRecipeManager.Input input : this.recipes.keySet()) {
            for (ItemStack containerStack : container.getInputs()) {
                for (ItemStack fillStack : fill.getInputs()) {
                    if (!input.matches(containerStack, fillStack)) continue;
                    throw new RuntimeException("ambiguous recipe: [" + container.getInputs() + "+" + fill.getInputs() + " -> " + output + "], conflicts with [" + input.container.getInputs() + "+" + input.fill.getInputs() + " -> " + this.recipes.get(input) + "]");
                }
            }
        }
        this.recipes.put(new ICannerBottleRecipeManager.Input(container, fill), new RecipeOutput(null, output));
    }

    @Override
    public RecipeOutput getOutputFor(ItemStack container, ItemStack fill, boolean adjustInput, boolean acceptTest) {
        if (acceptTest ? container == null && fill == null : container == null || fill == null) {
            return null;
        }
        for (Map.Entry<ICannerBottleRecipeManager.Input, RecipeOutput> entry : this.recipes.entrySet()) {
            ICannerBottleRecipeManager.Input recipeInput = entry.getKey();
            if (acceptTest && container == null) {
                if (!recipeInput.fill.matches(fill)) continue;
                return entry.getValue();
            }
            if (acceptTest && fill == null) {
                if (!recipeInput.container.matches(container)) continue;
                return entry.getValue();
            }
            if (!recipeInput.matches(container, fill)) continue;
            if (!acceptTest && (container == null || container.stackSize < recipeInput.container.getAmount() || fill.stackSize < recipeInput.fill.getAmount())) break;
            if (adjustInput) {
                if (container != null) {
                    container.stackSize -= recipeInput.container.getAmount();
                }
                fill.stackSize -= recipeInput.fill.getAmount();
            }
            return entry.getValue();
        }
        return null;
    }

    @Override
    public Map<ICannerBottleRecipeManager.Input, RecipeOutput> getRecipes() {
        return this.recipes;
    }
}

