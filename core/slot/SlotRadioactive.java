/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 */
package ic2.core.slot;

import ic2.core.item.ItemRadioactive;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorUranium;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotRadioactive
extends Slot {
    public SlotRadioactive(IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }

    public boolean isItemValid(ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        if (itemstack.getItem() instanceof ItemRadioactive) {
            return true;
        }
        if (itemstack.getItem() instanceof ItemReactorMOX) {
            return true;
        }
        return itemstack.getItem() instanceof ItemReactorUranium;
    }
}

