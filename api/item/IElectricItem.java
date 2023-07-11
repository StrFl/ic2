/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.api.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IElectricItem {
    public boolean canProvideEnergy(ItemStack var1);

    public Item getChargedItem(ItemStack var1);

    public Item getEmptyItem(ItemStack var1);

    public double getMaxCharge(ItemStack var1);

    public int getTier(ItemStack var1);

    public double getTransferLimit(ItemStack var1);
}

