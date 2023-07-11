/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 */
package ic2.core.slot;

import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotHologramSlot
extends Slot {
    protected final ItemStack[] stacks;
    protected final int index;
    protected final int stackSizeLimit;
    protected final ChangeCallback changeCallback;

    public SlotHologramSlot(ItemStack[] stacks, int index, int x, int y, int stackSizeLimit, ChangeCallback changeCallback) {
        super(null, 0, x, y);
        if (index >= stacks.length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        this.stacks = stacks;
        this.index = index;
        this.stackSizeLimit = stackSizeLimit;
        this.changeCallback = changeCallback;
    }

    public boolean canTakeStack(EntityPlayer player) {
        this.stacks[this.index] = null;
        this.onSlotChanged();
        return false;
    }

    public int getSlotStackLimit() {
        return this.stackSizeLimit;
    }

    public boolean isItemValid(ItemStack stack) {
        this.stacks[this.index] = StackUtil.copyWithSize(stack, Math.min(stack.stackSize, this.stackSizeLimit));
        this.onSlotChanged();
        return false;
    }

    public ItemStack getStack() {
        return this.stacks[this.index];
    }

    public void putStack(ItemStack stack) {
        this.stacks[this.index] = stack;
    }

    public void onSlotChanged() {
        if (this.changeCallback != null) {
            this.changeCallback.onChanged(this.index);
        }
    }

    public ItemStack decrStackSize(int amount) {
        return null;
    }

    public boolean isSlotInInventory(IInventory inventory, int index) {
        return false;
    }

    public static interface ChangeCallback {
        public void onChanged(int var1);
    }
}

