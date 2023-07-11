/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class InvSlotConsumableClass
extends InvSlotConsumable {
    private final Class<?> clazz;

    public InvSlotConsumableClass(TileEntityInventory base1, String name1, int oldStartIndex1, InvSlot.Access access1, int count, InvSlot.InvSide preferredSide1, Class<?> clazz) {
        super(base1, name1, oldStartIndex1, access1, count, preferredSide1);
        this.clazz = clazz;
    }

    public InvSlotConsumableClass(TileEntityInventory base1, String name1, int oldStartIndex1, int count, Class<?> clazz) {
        super(base1, name1, oldStartIndex1, count);
        this.clazz = clazz;
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (itemStack.getItem() instanceof ItemBlock) {
            return this.clazz.isInstance(Block.getBlockFromItem((Item)itemStack.getItem()));
        }
        return this.clazz.isInstance(itemStack.getItem());
    }
}

