/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 */
package ic2.core.slot;

import ic2.core.block.invslot.InvSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInvSlot
extends Slot {
    public final InvSlot invSlot;
    public final int index;

    public SlotInvSlot(InvSlot invSlot1, int index1, int xDisplayPosition1, int yDisplayPosition1) {
        super((IInventory)invSlot1.base, -1, xDisplayPosition1, yDisplayPosition1);
        this.invSlot = invSlot1;
        this.index = index1;
    }

    public boolean isItemValid(ItemStack itemStack) {
        return this.invSlot.accepts(itemStack);
    }

    public ItemStack getStack() {
        return this.invSlot.get(this.index);
    }

    public void putStack(ItemStack itemStack) {
        this.invSlot.put(this.index, itemStack);
        this.onSlotChanged();
    }

    public ItemStack decrStackSize(int amount) {
        ItemStack itemStack = this.invSlot.get(this.index);
        if (itemStack == null) {
            return null;
        }
        if (itemStack.stackSize <= amount) {
            this.invSlot.put(this.index, null);
            this.onSlotChanged();
            return itemStack;
        }
        ItemStack ret = itemStack.copy();
        ret.stackSize = amount;
        itemStack.stackSize -= amount;
        this.onSlotChanged();
        return ret;
    }

    public boolean isSlotInInventory(IInventory inventory1, int index1) {
        if (inventory1 != this.invSlot.base) {
            return false;
        }
        for (InvSlot invSlot1 : this.invSlot.base.invSlots) {
            if (index1 < invSlot1.size()) {
                return index1 == this.index;
            }
            index1 -= invSlot1.size();
        }
        return false;
    }

    public int getSlotStackLimit() {
        return this.invSlot.getStackSizeLimit();
    }
}

