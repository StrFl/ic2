/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.core.IC2;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.uu.IRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;

public class MachineRecipeResolver
implements IRecipeResolver {
    private static final double transformCost = 14.0;
    private final IMachineRecipeManager manager;

    public MachineRecipeResolver(IMachineRecipeManager manager) {
        this.manager = manager;
    }

    @Override
    public List<RecipeTransformation> getTransformations() {
        ArrayList<RecipeTransformation> ret = new ArrayList<RecipeTransformation>();
        Map<IRecipeInput, RecipeOutput> recipes = this.manager.getRecipes();
        for (Map.Entry<IRecipeInput, RecipeOutput> entry : recipes.entrySet()) {
            List<List<ItemStack>> inputs = MachineRecipeResolver.fixSizeExpand(entry.getKey().getInputs(), entry.getKey().getAmount());
            ItemStack[] output = entry.getValue().items.toArray(new ItemStack[0]);
            if (!StackUtil.check2(inputs) || !StackUtil.check(output)) {
                IC2.log.warn(LogCategory.Uu, "Invalid itemstack detected, shaped vanilla crafting recipe %s -> %s", StackUtil.toStringSafe2(inputs), StackUtil.toStringSafe(output));
                continue;
            }
            ret.add(new RecipeTransformation(14.0, inputs, output));
        }
        return ret;
    }

    private static List<List<ItemStack>> fixSizeExpand(List<ItemStack> x, int size) {
        ArrayList<ItemStack> expanded = new ArrayList<ItemStack>(x.size());
        for (ItemStack stack : x) {
            if (stack.stackSize == size) {
                expanded.add(stack);
                continue;
            }
            expanded.add(StackUtil.copyWithSize(stack, size));
        }
        ArrayList<List<ItemStack>> ret = new ArrayList<List<ItemStack>>(1);
        ret.add(expanded);
        return ret;
    }
}

