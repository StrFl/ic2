/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.uu;

import ic2.core.Ic2Items;
import ic2.core.uu.IRecipeResolver;
import ic2.core.uu.RecipeTransformation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;

public class ManualRecipeResolver
implements IRecipeResolver {
    private static final double transformCost = 0.0;

    @Override
    public List<RecipeTransformation> getTransformations() {
        ArrayList<RecipeTransformation> ret = new ArrayList<RecipeTransformation>();
        ret.add(new RecipeTransformation(0.0, this.toList(new ItemStack(Ic2Items.reactorUraniumSimple.getItem(), 1, 1)), Ic2Items.reactorDepletedUraniumSimple));
        ret.add(new RecipeTransformation(0.0, this.toList(new ItemStack(Ic2Items.reactorUraniumDual.getItem(), 1, 1)), Ic2Items.reactorDepletedUraniumDual));
        ret.add(new RecipeTransformation(0.0, this.toList(new ItemStack(Ic2Items.reactorUraniumQuad.getItem(), 1, 1)), Ic2Items.reactorDepletedUraniumQuad));
        ret.add(new RecipeTransformation(0.0, this.toList(new ItemStack(Ic2Items.reactorMOXSimple.getItem(), 1, 1)), Ic2Items.reactorDepletedMOXSimple));
        ret.add(new RecipeTransformation(0.0, this.toList(new ItemStack(Ic2Items.reactorMOXDual.getItem(), 1, 1)), Ic2Items.reactorDepletedMOXDual));
        ret.add(new RecipeTransformation(0.0, this.toList(new ItemStack(Ic2Items.reactorMOXQuad.getItem(), 1, 1)), Ic2Items.reactorDepletedMOXQuad));
        return ret;
    }

    private List<List<ItemStack>> toList(ItemStack ... stacks) {
        ArrayList<List<ItemStack>> ret = new ArrayList<List<ItemStack>>(stacks.length);
        for (ItemStack stack : stacks) {
            ret.add(Arrays.asList(stack));
        }
        return ret;
    }
}

