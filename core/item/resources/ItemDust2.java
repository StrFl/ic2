/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.resources;

import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemDust2
extends ItemIC2 {
    public ItemDust2(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.silicondioxideDust = new ItemStack((Item)this, 1, 0);
        Ic2Items.diamondDust = new ItemStack((Item)this, 1, 1);
        Ic2Items.energiumDust = new ItemStack((Item)this, 1, 2);
        Ic2Items.AshesDust = new ItemStack((Item)this, 1, 3);
    }

    @Override
    public String getTextureFolder() {
        return "resources";
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        InternalName ret;
        int meta = itemstack.getItemDamage();
        switch (meta) {
            case 0: {
                ret = InternalName.itemDustsilicondioxide;
                break;
            }
            case 1: {
                ret = InternalName.itemDustdiamond;
                break;
            }
            case 2: {
                ret = InternalName.itemDustenergium;
                break;
            }
            case 3: {
                ret = InternalName.itemAshes;
                break;
            }
            default: {
                return null;
            }
        }
        return "ic2." + ret.name();
    }

    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        ItemStack stack;
        for (int meta = 0; meta < Short.MAX_VALUE && this.getUnlocalizedName(stack = new ItemStack((Item)this, 1, meta)) != null; ++meta) {
            itemList.add(stack);
        }
    }
}

