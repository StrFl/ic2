/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 */
package ic2.core.slot;

import ic2.api.item.ElectricItem;
import ic2.core.item.ItemBatterySU;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotDischarge
extends Slot {
    public int tier = Integer.MAX_VALUE;

    public SlotDischarge(IInventory par1iInventory, int tier1, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
        this.tier = tier1;
    }

    public SlotDischarge(IInventory par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
    }

    public boolean isItemValid(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getItem() == Items.redstone || stack.getItem() instanceof ItemBatterySU) {
            return true;
        }
        return ElectricItem.manager.discharge(stack, Double.POSITIVE_INFINITY, this.tier, true, true, true) > 0.0;
    }
}

