/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.uu.IRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;

public class CannerBottleSolidResolver
implements IRecipeResolver {
    private static final double transformCost = 0.0;

    @Override
    public List<RecipeTransformation> getTransformations() {
        ArrayList<RecipeTransformation> ret = new ArrayList<RecipeTransformation>();
        for (Map.Entry<ICannerBottleRecipeManager.Input, RecipeOutput> entry : Recipes.cannerBottle.getRecipes().entrySet()) {
            List<ItemStack> container = entry.getKey().container.getInputs();
            List<ItemStack> fill = entry.getKey().fill.getInputs();
            if (container.isEmpty() || fill.isEmpty()) continue;
            if (!StackUtil.check(container) || !StackUtil.check(fill)) {
                IC2.log.warn(LogCategory.Uu, "Invalid itemstack detected, cannerBottle recipe %s -> %s", StackUtil.toStringSafe(container), StackUtil.toStringSafe(fill));
                continue;
            }
            ArrayList<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>(2);
            inputs.add(container);
            inputs.add(fill);
            ret.add(new RecipeTransformation(0.0, inputs, entry.getValue().items));
        }
        return ret;
    }
}

