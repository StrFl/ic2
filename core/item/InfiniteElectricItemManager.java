/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item;

import ic2.api.item.IElectricItemManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class InfiniteElectricItemManager
implements IElectricItemManager {
    @Override
    public double charge(ItemStack itemStack, double amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
        return amount;
    }

    @Override
    public double discharge(ItemStack itemStack, double amount, int tier, boolean ignoreTransferLimit, boolean externally, boolean simulate) {
        return amount;
    }

    @Override
    public double getCharge(ItemStack itemStack) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean canUse(ItemStack itemStack, double amount) {
        return true;
    }

    @Override
    public boolean use(ItemStack itemStack, double amount, EntityLivingBase entity) {
        return true;
    }

    @Override
    public void chargeFromArmor(ItemStack itemStack, EntityLivingBase entity) {
    }

    @Override
    public String getToolTip(ItemStack itemStack) {
        return "infinite EU";
    }
}

