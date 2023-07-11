/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemBattery;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBatteryDischarged
extends ItemBattery {
    public ItemBatteryDischarged(InternalName internalName, int maxCharge1, int transferLimit1, int tier1) {
        super(internalName, maxCharge1, transferLimit1, tier1);
        this.setMaxDamage(0);
        this.setMaxStackSize(16);
    }

    @Override
    public String getTextureName(int index) {
        return null;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return Ic2Items.chargedReBattery.getItem().getIconFromDamage(Ic2Items.chargedReBattery.getItem().getMaxDamage());
    }

    @Override
    public Item getChargedItem(ItemStack itemstack) {
        return Ic2Items.chargedReBattery.getItem();
    }
}

