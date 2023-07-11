/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.oredict.OreDictionary
 */
package ic2.api.recipe;

import ic2.api.recipe.IRecipeInput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeInputOreDict
implements IRecipeInput {
    public final String input;
    public final int amount;
    public final Integer meta;
    private List<ItemStack> ores;

    public RecipeInputOreDict(String input1) {
        this(input1, 1);
    }

    public RecipeInputOreDict(String input1, int amount1) {
        this(input1, amount1, null);
    }

    public RecipeInputOreDict(String input1, int amount1, Integer meta) {
        this.input = input1;
        this.amount = amount1;
        this.meta = meta;
    }

    @Override
    public boolean matches(ItemStack subject) {
        List<ItemStack> inputs = this.getOres();
        boolean useOreStackMeta = this.meta == null;
        Item subjectItem = subject.getItem();
        int subjectMeta = subject.getItemDamage();
        for (ItemStack oreStack : inputs) {
            int metaRequired;
            Item oreItem = oreStack.getItem();
            if (oreItem == null) continue;
            int n = metaRequired = useOreStackMeta ? oreStack.getItemDamage() : this.meta.intValue();
            if (subjectItem != oreItem || subjectMeta != metaRequired && metaRequired != Short.MAX_VALUE) continue;
            return true;
        }
        return false;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public List<ItemStack> getInputs() {
        List<ItemStack> ores = this.getOres();
        boolean hasInvalidEntries = false;
        for (ItemStack stack : ores) {
            if (stack.getItem() != null) continue;
            hasInvalidEntries = true;
            break;
        }
        if (!hasInvalidEntries) {
            return ores;
        }
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>(ores.size());
        for (ItemStack stack : ores) {
            if (stack.getItem() == null) continue;
            ret.add(stack);
        }
        return Collections.unmodifiableList(ret);
    }

    public String toString() {
        if (this.meta == null) {
            return "RInputOreDict<" + this.amount + "x" + this.input + ">";
        }
        return "RInputOreDict<" + this.amount + "x" + this.input + "@" + this.meta + ">";
    }

    private List<ItemStack> getOres() {
        if (this.ores != null) {
            return this.ores;
        }
        ArrayList ret = OreDictionary.getOres((String)this.input);
        if (ret != OreDictionary.EMPTY_LIST) {
            this.ores = ret;
        }
        return ret;
    }
}

