/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class InvSlotDischarge
extends InvSlot {
    public int tier;
    public boolean allowRedstoneDust = true;

    public InvSlotDischarge(TileEntityInventory base, int oldStartIndex, InvSlot.Access access, int tier) {
        this(base, oldStartIndex, access, tier, InvSlot.InvSide.ANY);
    }

    public InvSlotDischarge(TileEntityInventory base, int oldStartIndex, InvSlot.Access access, int tier, InvSlot.InvSide preferredSide) {
        this(base, oldStartIndex, access, tier, true, preferredSide);
    }

    public InvSlotDischarge(TileEntityInventory base, int oldStartIndex, InvSlot.Access access, int tier, boolean allowRedstoneDust, InvSlot.InvSide preferredSide) {
        super(base, "discharge", oldStartIndex, access, 1, preferredSide);
        this.tier = tier;
        this.allowRedstoneDust = allowRedstoneDust;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getItem() == Items.redstone && !this.allowRedstoneDust) {
            return false;
        }
        return Info.itemEnergy.getEnergyValue(stack) > 0.0 || ElectricItem.manager.discharge(stack, Double.POSITIVE_INFINITY, this.tier, true, true, true) > 0.0;
    }

    public double discharge(double amount, boolean ignoreLimit) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Amount must be > 0.");
        }
        ItemStack stack = this.get(0);
        if (stack == null) {
            return 0.0;
        }
        double realAmount = ElectricItem.manager.discharge(stack, amount, this.tier, ignoreLimit, true, false);
        if (realAmount <= 0.0) {
            realAmount = Info.itemEnergy.getEnergyValue(stack);
            if (realAmount <= 0.0) {
                return 0.0;
            }
            --stack.stackSize;
            if (stack.stackSize <= 0) {
                this.put(0, null);
            }
        }
        return realAmount;
    }

    public void setTier(int tier1) {
        this.tier = tier1;
    }
}

