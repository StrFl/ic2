/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 */
package ic2.core.uu;

import ic2.core.AdvRecipe;
import ic2.core.IC2;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.uu.IRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class VanillaSmeltingResolver
implements IRecipeResolver {
    private static final double transformCost = 14.0;

    @Override
    public List<RecipeTransformation> getTransformations() {
        ArrayList<RecipeTransformation> ret = new ArrayList<RecipeTransformation>();
        for (Map.Entry entry : FurnaceRecipes.smelting().getSmeltingList().entrySet()) {
            List<List<ItemStack>> inputs = Arrays.asList(AdvRecipe.expand(entry.getKey()));
            ItemStack output = (ItemStack)entry.getValue();
            if (!StackUtil.check2(inputs) || !StackUtil.check(output)) {
                IC2.log.warn(LogCategory.Uu, "Invalid itemstack detected, vanilla smelting recipe %s -> %s", StackUtil.toStringSafe2(inputs), StackUtil.toStringSafe(output));
                continue;
            }
            ret.add(new RecipeTransformation(14.0, inputs, output));
        }
        return ret;
    }
}

