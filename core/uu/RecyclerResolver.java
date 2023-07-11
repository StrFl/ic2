/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityRecycler;
import ic2.core.util.ItemStackWrapper;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.uu.ILateRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import net.minecraft.item.ItemStack;

public class RecyclerResolver
implements ILateRecipeResolver {
    private static final double transformCost = 55.0 * (double)TileEntityRecycler.recycleChance() / 4000.0 * 107.0;

    @Override
    public List<RecipeTransformation> getTransformations(Iterable<ItemStackWrapper> obtainableStacks) {
        HashSet<ItemStackWrapper> inputSet = new HashSet<ItemStackWrapper>();
        for (ItemStackWrapper itemStackWrapper : obtainableStacks) {
            ItemStack output = itemStackWrapper.stack;
            if (Recipes.recycler.getOutputFor((ItemStack)output, (boolean)false).items.isEmpty()) continue;
            inputSet.add(new ItemStackWrapper(StackUtil.copyWithSize(output, TileEntityRecycler.recycleChance())));
        }
        ArrayList<ItemStack> input = new ArrayList<ItemStack>(inputSet.size());
        for (ItemStackWrapper wrapper : inputSet) {
            if (!StackUtil.check(wrapper.stack)) {
                IC2.log.warn(LogCategory.Uu, "Invalid itemstack in recycler inputs detected.");
                continue;
            }
            input.add(wrapper.stack);
        }
        ArrayList<List<ItemStack>> arrayList = new ArrayList<List<ItemStack>>();
        arrayList.add(input);
        return Arrays.asList(new RecipeTransformation(transformCost, arrayList, Ic2Items.scrap));
    }
}

