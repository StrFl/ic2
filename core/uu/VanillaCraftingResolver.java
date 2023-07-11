/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.item.crafting.ShapedRecipes
 *  net.minecraft.item.crafting.ShapelessRecipes
 */
package ic2.core.uu;

import ic2.core.AdvRecipe;
import ic2.core.IC2;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.uu.IRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

public class VanillaCraftingResolver
implements IRecipeResolver {
    private static final double transformCost = 1.0;

    @Override
    public List<RecipeTransformation> getTransformations() {
        ArrayList<RecipeTransformation> ret = new ArrayList<RecipeTransformation>();
        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
            ItemStack output;
            List<List<ItemStack>> inputs;
            ShapedRecipes recipe;
            if (irecipe instanceof ShapedRecipes) {
                recipe = (ShapedRecipes)irecipe;
                if (recipe.recipeItems == null) continue;
                inputs = VanillaCraftingResolver.fixSize(AdvRecipe.expandArray(recipe.recipeItems));
                output = recipe.getRecipeOutput();
                if (output == null || output.stackSize == 0) continue;
                if (!StackUtil.check2(inputs) || !StackUtil.check(output)) {
                    IC2.log.warn(LogCategory.Uu, "Invalid itemstack detected, shaped vanilla crafting recipe %s -> %s", StackUtil.toStringSafe2(inputs), StackUtil.toStringSafe(output));
                    continue;
                }
                ret.add(new RecipeTransformation(1.0, inputs, output));
                continue;
            }
            if (!(irecipe instanceof ShapelessRecipes)) continue;
            recipe = (ShapelessRecipes)irecipe;
            if (recipe.recipeItems == null) continue;
            inputs = VanillaCraftingResolver.fixSize(AdvRecipe.expandArray(recipe.recipeItems.toArray()));
            output = recipe.getRecipeOutput();
            if (output == null || output.stackSize == 0) continue;
            if (!StackUtil.check2(inputs) || !StackUtil.check(output)) {
                IC2.log.warn(LogCategory.Uu, "Invalid itemstack detected, shapeless vanilla crafting recipe %s -> %s", StackUtil.toStringSafe2(inputs), StackUtil.toStringSafe(output));
                continue;
            }
            ret.add(new RecipeTransformation(1.0, inputs, output));
        }
        return ret;
    }

    private static List<List<ItemStack>> fixSize(List<ItemStack>[] x) {
        ArrayList<List<ItemStack>> ret = new ArrayList<List<ItemStack>>(x.length);
        for (int i = 0; i < x.length; ++i) {
            if (x[i] == null) continue;
            ArrayList<ItemStack> list = new ArrayList<ItemStack>();
            for (ItemStack stack : x[i]) {
                if (stack.stackSize == 1) {
                    list.add(stack);
                    continue;
                }
                list.add(StackUtil.copyWithSize(stack, 1));
            }
            ret.add(list);
        }
        return ret;
    }
}

