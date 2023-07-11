/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.api.recipe;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import java.util.Map;
import net.minecraft.item.ItemStack;

public interface ICannerBottleRecipeManager {
    public void addRecipe(IRecipeInput var1, IRecipeInput var2, ItemStack var3);

    public RecipeOutput getOutputFor(ItemStack var1, ItemStack var2, boolean var3, boolean var4);

    public Map<Input, RecipeOutput> getRecipes();

    public static class Input {
        public final IRecipeInput container;
        public final IRecipeInput fill;

        public Input(IRecipeInput container1, IRecipeInput fill1) {
            this.container = container1;
            this.fill = fill1;
        }

        public boolean matches(ItemStack container1, ItemStack fill1) {
            return this.container.matches(container1) && this.fill.matches(fill1);
        }
    }
}

