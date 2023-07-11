/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.upgrade;

import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.UpgradableProperty;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;

public interface IUpgradeItem {
    public boolean isSuitableFor(ItemStack var1, Set<UpgradableProperty> var2);

    public int getAugmentation(ItemStack var1, IUpgradableBlock var2);

    public int getExtraProcessTime(ItemStack var1, IUpgradableBlock var2);

    public double getProcessTimeMultiplier(ItemStack var1, IUpgradableBlock var2);

    public int getExtraEnergyDemand(ItemStack var1, IUpgradableBlock var2);

    public double getEnergyDemandMultiplier(ItemStack var1, IUpgradableBlock var2);

    public int getExtraEnergyStorage(ItemStack var1, IUpgradableBlock var2);

    public double getEnergyStorageMultiplier(ItemStack var1, IUpgradableBlock var2);

    public int getExtraTier(ItemStack var1, IUpgradableBlock var2);

    public boolean modifiesRedstoneInput(ItemStack var1, IUpgradableBlock var2);

    public int getRedstoneInput(ItemStack var1, IUpgradableBlock var2, int var3);

    public boolean onTick(ItemStack var1, IUpgradableBlock var2);

    public void onProcessEnd(ItemStack var1, IUpgradableBlock var2, List<ItemStack> var3);
}

