/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.api.item.ElectricItem;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.item.ItemStack;

public class InvSlotCharge
extends InvSlot {
    public int tier;

    public InvSlotCharge(TileEntityInventory base1, int oldStartIndex1, int tier1) {
        super(base1, "charge", oldStartIndex1, InvSlot.Access.IO, 1, InvSlot.InvSide.TOP);
        this.tier = tier1;
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        return ElectricItem.manager.charge(itemStack, Double.POSITIVE_INFINITY, this.tier, true, true) > 0.0;
    }

    public double charge(double amount) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Amount must be > 0.");
        }
        ItemStack itemStack = this.get(0);
        if (itemStack == null) {
            return 0.0;
        }
        return ElectricItem.manager.charge(itemStack, amount, this.tier, false, false);
    }

    public void setTier(int tier1) {
        this.tier = tier1;
    }
}

