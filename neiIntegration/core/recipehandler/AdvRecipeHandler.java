/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.nei.NEIServerUtils
 *  codechicken.nei.recipe.ShapedRecipeHandler
 *  codechicken.nei.recipe.ShapedRecipeHandler$CachedShapedRecipe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.item.crafting.IRecipe
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.recipe.ShapedRecipeHandler;
import ic2.core.AdvRecipe;
import ic2.neiIntegration.core.PositionedStackIc2;
import java.util.Collection;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

public class AdvRecipeHandler
extends ShapedRecipeHandler {
    public ShapedRecipeHandler.CachedShapedRecipe createCachedRecipe(AdvRecipe recipe) {
        Object[] items = new List[9];
        int j = 0;
        for (int i = 0; i < 9; ++i) {
            if ((recipe.masks[0] & 1 << 8 - i) == 0) continue;
            List<ItemStack> inputs = AdvRecipe.expand(recipe.input[j]);
            if (inputs.isEmpty()) {
                return null;
            }
            items[i] = inputs;
            ++j;
        }
        CachedShapedRecipeIc2 ret = new CachedShapedRecipeIc2(3, 3, items, recipe.output);
        return ret;
    }

    public void loadCraftingRecipes(String outputId, Object ... results) {
        if (outputId.equals("crafting")) {
            for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
                ShapedRecipeHandler.CachedShapedRecipe cachedRecipe;
                AdvRecipe recipe;
                if (!(irecipe instanceof AdvRecipe) || !(recipe = (AdvRecipe)irecipe).canShow() || (cachedRecipe = this.createCachedRecipe(recipe)) == null) continue;
                this.arecipes.add(cachedRecipe);
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadUsageRecipes(ItemStack ingredient) {
        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
            ShapedRecipeHandler.CachedShapedRecipe cachedRecipe;
            AdvRecipe recipe;
            if (!(irecipe instanceof AdvRecipe) || !(recipe = (AdvRecipe)irecipe).canShow() || (cachedRecipe = this.createCachedRecipe(recipe)) == null || !cachedRecipe.contains((Collection)cachedRecipe.ingredients, ingredient)) continue;
            cachedRecipe.setIngredientPermutation((Collection)cachedRecipe.ingredients, ingredient);
            this.arecipes.add(cachedRecipe);
        }
    }

    public void loadCraftingRecipes(ItemStack result) {
        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
            ShapedRecipeHandler.CachedShapedRecipe cachedRecipe;
            AdvRecipe recipe;
            if (!(irecipe instanceof AdvRecipe) || !(recipe = (AdvRecipe)irecipe).canShow() || (cachedRecipe = this.createCachedRecipe(recipe)) == null || !NEIServerUtils.areStacksSameTypeCrafting((ItemStack)recipe.output, (ItemStack)result)) continue;
            this.arecipes.add(cachedRecipe);
        }
    }

    public String getRecipeName() {
        return "Shaped IC2 Crafting";
    }

    private class CachedShapedRecipeIc2
    extends ShapedRecipeHandler.CachedShapedRecipe {
        public CachedShapedRecipeIc2(int width, int height, Object[] items, ItemStack out) {
            super((ShapedRecipeHandler)AdvRecipeHandler.this, width, height, items, out);
        }

        public void setIngredients(int width, int height, Object[] items) {
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (items[y * width + x] == null) continue;
                    PositionedStackIc2 stack = new PositionedStackIc2(items[y * width + x], 25 + x * 18, 6 + y * 18, false);
                    stack.setMaxSize(1);
                    this.ingredients.add(stack);
                }
            }
        }
    }
}

