/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.item.ItemStack;

public class InvSlotOutput
extends InvSlot {
    public InvSlotOutput(TileEntityInventory base1, String name1, int oldStartIndex1, int count) {
        super(base1, name1, oldStartIndex1, InvSlot.Access.O, count, InvSlot.InvSide.BOTTOM);
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        return false;
    }

    public int add(List<ItemStack> itemStacks) {
        return this.add(itemStacks.toArray(new ItemStack[0]), false);
    }

    public int add(ItemStack itemStack) {
        if (itemStack == null) {
            throw new NullPointerException("null ItemStack");
        }
        return this.add(new ItemStack[]{itemStack}, false);
    }

    public boolean canAdd(List<ItemStack> itemStacks) {
        return this.add(itemStacks.toArray(new ItemStack[0]), true) == 0;
    }

    public boolean canAdd(ItemStack itemStack) {
        if (itemStack == null) {
            throw new NullPointerException("null ItemStack");
        }
        return this.add(new ItemStack[]{itemStack}, true) == 0;
    }

    private int add(ItemStack[] itemStacks, boolean simulate) {
        if (itemStacks == null) {
            return 0;
        }
        if (simulate) {
            this.backup();
        }
        int totalAmount = 0;
        for (ItemStack stack : itemStacks) {
            int amount = stack.stackSize;
            for (int pass = 0; pass < 2; ++pass) {
                for (int i = 0; i < this.size(); ++i) {
                    ItemStack existingItemStack = this.get(i);
                    if (pass == 0 && existingItemStack != null) {
                        int space = Math.min(this.getStackSizeLimit(), existingItemStack.getMaxStackSize()) - existingItemStack.stackSize;
                        if (space > 0 && StackUtil.isStackEqualStrict(stack, existingItemStack)) {
                            if (space >= amount) {
                                existingItemStack.stackSize += amount;
                                amount = 0;
                            } else {
                                amount -= space;
                                existingItemStack.stackSize += space;
                            }
                        }
                    } else if (pass == 1 && existingItemStack == null) {
                        this.put(i, StackUtil.copyWithSize(stack, amount));
                        amount = 0;
                    }
                    if (amount == 0) break;
                }
                if (amount == 0) break;
            }
            totalAmount += amount;
        }
        if (simulate) {
            this.restore();
        }
        return totalAmount;
    }
}

