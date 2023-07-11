/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.armor;

import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorElectric;
import net.minecraft.item.ItemStack;

public class ItemArmorAdvBatpack
extends ItemArmorElectric {
    public ItemArmorAdvBatpack(InternalName internalName) {
        super(internalName, InternalName.advbatpack, 1, 600000.0, 1000.0, 2);
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return true;
    }

    @Override
    public double getDamageAbsorptionRatio() {
        return 0.0;
    }

    @Override
    public int getEnergyPerDamage() {
        return 0;
    }
}

