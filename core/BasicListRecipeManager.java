/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core;

import ic2.api.recipe.IListRecipeManager;
import ic2.api.recipe.IRecipeInput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;

public class BasicListRecipeManager
implements IListRecipeManager {
    private final List<IRecipeInput> list = new ArrayList<IRecipeInput>();

    @Override
    public void add(IRecipeInput input) {
        if (input == null) {
            throw new NullPointerException("Input must not be null.");
        }
        this.list.add(input);
    }

    @Override
    public boolean contains(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        for (IRecipeInput input : this.list) {
            if (!input.matches(stack)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public List<IRecipeInput> getInputs() {
        return this.list;
    }

    @Override
    public Iterator<IRecipeInput> iterator() {
        return this.list.iterator();
    }
}

