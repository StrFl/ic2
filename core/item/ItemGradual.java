/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemGradual
extends ItemIC2 {
    public ItemGradual(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
        this.setMaxDamage(10000);
        this.setNoRepair();
    }

    public boolean isDamaged(ItemStack stack) {
        return this.getDamage(stack) > 1;
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        itemList.add(new ItemStack((Item)this, 1, 1));
    }
}

