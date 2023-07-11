/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.nei.NEIServerUtils
 *  codechicken.nei.recipe.ShapelessRecipeHandler
 *  codechicken.nei.recipe.ShapelessRecipeHandler$CachedShapelessRecipe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.item.crafting.IRecipe
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.recipe.ShapelessRecipeHandler;
import ic2.core.AdvRecipe;
import ic2.core.AdvShapelessRecipe;
import ic2.neiIntegration.core.PositionedStackIc2;
import java.util.Collection;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

public class AdvShapelessRecipeHandler
extends ShapelessRecipeHandler {
    public ShapelessRecipeHandler.CachedShapelessRecipe createCachedRecipe(AdvShapelessRecipe recipe) {
        Object[] items;
        for (List<ItemStack> list : items = AdvRecipe.expandArray(recipe.input)) {
            if (list == null || !list.isEmpty()) continue;
            return null;
        }
        return new CachedShapelessRecipeIc2(items, recipe.output);
    }

    public void loadCraftingRecipes(String outputId, Object ... results) {
        if (outputId.equals("crafting") && ((Object)((Object)this)).getClass() == AdvShapelessRecipeHandler.class) {
            for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
                ShapelessRecipeHandler.CachedShapelessRecipe cachedRecipe;
                AdvShapelessRecipe recipe;
                if (!(irecipe instanceof AdvShapelessRecipe) || !(recipe = (AdvShapelessRecipe)irecipe).canShow() || (cachedRecipe = this.createCachedRecipe(recipe)) == null) continue;
                this.arecipes.add(cachedRecipe);
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadUsageRecipes(ItemStack ingredient) {
        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
            ShapelessRecipeHandler.CachedShapelessRecipe cachedRecipe;
            AdvShapelessRecipe recipe;
            if (!(irecipe instanceof AdvShapelessRecipe) || !(recipe = (AdvShapelessRecipe)irecipe).canShow() || (cachedRecipe = this.createCachedRecipe(recipe)) == null || !cachedRecipe.contains((Collection)cachedRecipe.ingredients, ingredient)) continue;
            cachedRecipe.setIngredientPermutation((Collection)cachedRecipe.ingredients, ingredient);
            this.arecipes.add(cachedRecipe);
        }
    }

    public void loadCraftingRecipes(ItemStack result) {
        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
            ShapelessRecipeHandler.CachedShapelessRecipe cachedRecipe;
            AdvShapelessRecipe recipe;
            if (!(irecipe instanceof AdvShapelessRecipe) || !(recipe = (AdvShapelessRecipe)irecipe).canShow() || (cachedRecipe = this.createCachedRecipe(recipe)) == null || !NEIServerUtils.areStacksSameTypeCrafting((ItemStack)recipe.output, (ItemStack)result)) continue;
            this.arecipes.add(cachedRecipe);
        }
    }

    public String getRecipeName() {
        return "Shapeless IC2 Crafting";
    }

    private class CachedShapelessRecipeIc2
    extends ShapelessRecipeHandler.CachedShapelessRecipe {
        public CachedShapelessRecipeIc2(Object[] input, ItemStack output) {
            super((ShapelessRecipeHandler)AdvShapelessRecipeHandler.this, input, output);
        }

        public void setIngredients(List<?> items) {
            this.ingredients.clear();
            for (int ingred = 0; ingred < items.size(); ++ingred) {
                PositionedStackIc2 stack = new PositionedStackIc2(items.get(ingred), 25 + AdvShapelessRecipeHandler.this.stackorder[ingred][0] * 18, 6 + AdvShapelessRecipeHandler.this.stackorder[ingred][1] * 18);
                stack.setMaxSize(1);
                this.ingredients.add(stack);
            }
        }
    }
}

