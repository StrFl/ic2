/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.item.crafting.IRecipe
 */
package ic2.core.uu;

import ic2.core.AdvRecipe;
import ic2.core.AdvShapelessRecipe;
import ic2.core.IC2;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.uu.IRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

public class Ic2CraftingResolver
implements IRecipeResolver {
    private static final double transformCost = 1.0;

    @Override
    public List<RecipeTransformation> getTransformations() {
        ArrayList<RecipeTransformation> ret = new ArrayList<RecipeTransformation>();
        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
            ItemStack output;
            List<List<ItemStack>> inputs;
            Object recipe;
            if (irecipe instanceof AdvRecipe) {
                recipe = (AdvRecipe)irecipe;
                inputs = Arrays.asList(AdvRecipe.expandArray(((AdvRecipe)recipe).input));
                output = ((AdvRecipe)recipe).getRecipeOutput();
                if (output == null || output.stackSize == 0) continue;
                if (!StackUtil.check2(inputs) || !StackUtil.check(output)) {
                    IC2.log.warn(LogCategory.Uu, "Invalid itemstack detected, shaped ic2 crafting recipe %s -> %s", StackUtil.toStringSafe2(inputs), StackUtil.toStringSafe(output));
                    continue;
                }
                ret.add(new RecipeTransformation(1.0, inputs, output));
                continue;
            }
            if (!(irecipe instanceof AdvShapelessRecipe)) continue;
            recipe = (AdvShapelessRecipe)irecipe;
            inputs = Arrays.asList(AdvRecipe.expandArray(((AdvShapelessRecipe)recipe).input));
            output = ((AdvShapelessRecipe)recipe).getRecipeOutput();
            if (output == null || output.stackSize == 0) continue;
            if (!StackUtil.check2(inputs) || !StackUtil.check(output)) {
                IC2.log.warn(LogCategory.Uu, "Invalid itemstack detected, shapeless ic2 crafting recipe %s -> %s", StackUtil.toStringSafe2(inputs), StackUtil.toStringSafe(output));
                continue;
            }
            ret.add(new RecipeTransformation(1.0, inputs, output));
        }
        return ret;
    }
}

