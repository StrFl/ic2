/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.util.ItemStackWrapper;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.item.ItemStack;

public class InvSlotConsumableItemStack
extends InvSlotConsumable {
    private final Set<ItemStackWrapper> stacks = new HashSet<ItemStackWrapper>();

    public InvSlotConsumableItemStack(TileEntityInventory base1, String name1, int oldStartIndex1, int count, ItemStack ... stacks) {
        this(base1, name1, oldStartIndex1, InvSlot.Access.I, count, InvSlot.InvSide.TOP, stacks);
    }

    public InvSlotConsumableItemStack(TileEntityInventory base1, String name1, int oldStartIndex1, InvSlot.Access access1, int count, InvSlot.InvSide preferredSide1, ItemStack ... stacks) {
        super(base1, name1, oldStartIndex1, access1, count, preferredSide1);
        for (ItemStack stack : stacks) {
            this.stacks.add(new ItemStackWrapper(stack));
        }
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        return this.stacks.contains(new ItemStackWrapper(itemStack));
    }
}

