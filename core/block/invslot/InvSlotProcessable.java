/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.api.recipe.RecipeOutput;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumable;
import net.minecraft.item.ItemStack;

public abstract class InvSlotProcessable
extends InvSlotConsumable {
    public InvSlotProcessable(TileEntityInventory base1, String name1, int oldStartIndex1, int count) {
        super(base1, name1, oldStartIndex1, count);
    }

    @Override
    public abstract boolean accepts(ItemStack var1);

    public abstract RecipeOutput process();

    public abstract void consume();
}

