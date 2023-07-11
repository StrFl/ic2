/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.util;

import ic2.core.util.StackUtil;
import net.minecraft.item.ItemStack;

public class ItemStackWrapper {
    public final ItemStack stack;
    private final int hashCode;

    public ItemStackWrapper(ItemStack stack) {
        if (stack == null) {
            throw new NullPointerException("null stack supplied");
        }
        this.stack = stack;
        this.hashCode = ItemStackWrapper.calculateHashCode(stack);
    }

    public boolean equals(Object o) {
        if (o instanceof ItemStackWrapper) {
            ItemStackWrapper isw = (ItemStackWrapper)o;
            if (isw.hashCode != this.hashCode) {
                return false;
            }
            if (isw == this) {
                return true;
            }
            ItemStack stack2 = isw.stack;
            return this.stack.getItem() == stack2.getItem() && (!this.stack.getHasSubtypes() && !this.stack.isItemStackDamageable() || this.stack.getItemDamage() == stack2.getItemDamage()) && StackUtil.isTagEqual(this.stack, stack2);
        }
        return false;
    }

    public int hashCode() {
        return this.hashCode;
    }

    private static int calculateHashCode(ItemStack stack) {
        int ret = 0;
        if (stack.hasTagCompound() && !stack.getTagCompound().hasNoTags()) {
            ret = stack.getTagCompound().hashCode() * 257;
        }
        ret ^= stack.getItem().hashCode();
        if (stack.getHasSubtypes() || stack.isItemStackDamageable()) {
            ret ^= stack.getItemDamage();
        }
        return ret;
    }
}

