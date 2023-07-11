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

public class ItemsmallDust
extends ItemIC2 {
    public ItemsmallDust(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.smallIronDust = new ItemStack((Item)this, 1, 0);
        Ic2Items.smallCopperDust = new ItemStack((Item)this, 1, 1);
        Ic2Items.smallGoldDust = new ItemStack((Item)this, 1, 2);
        Ic2Items.smallTinDust = new ItemStack((Item)this, 1, 3);
        Ic2Items.smallSilverDust = new ItemStack((Item)this, 1, 4);
        Ic2Items.smallLeadDust = new ItemStack((Item)this, 1, 5);
        Ic2Items.smallSulfurDust = new ItemStack((Item)this, 1, 6);
        Ic2Items.smallLithiumDust = new ItemStack((Item)this, 1, 7);
        Ic2Items.smallBronzeDust = new ItemStack((Item)this, 1, 8);
        Ic2Items.smallLapiDust = new ItemStack((Item)this, 1, 9);
        Ic2Items.smallObsidianDust = new ItemStack((Item)this, 1, 10);
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
                ret = InternalName.itemDustIronSmall;
                break;
            }
            case 1: {
                ret = InternalName.itemDustCopperSmall;
                break;
            }
            case 2: {
                ret = InternalName.itemDustGoldSmall;
                break;
            }
            case 3: {
                ret = InternalName.itemDustTinSmall;
                break;
            }
            case 4: {
                ret = InternalName.itemDustSilverSmall;
                break;
            }
            case 5: {
                ret = InternalName.itemDustLeadSmall;
                break;
            }
            case 6: {
                ret = InternalName.itemDustSulfurSmall;
                break;
            }
            case 7: {
                ret = InternalName.itemDustLithiumSmall;
                break;
            }
            case 8: {
                ret = InternalName.itemDustBronzeSmall;
                break;
            }
            case 9: {
                ret = InternalName.itemDustLapiSmall;
                break;
            }
            case 10: {
                ret = InternalName.itemDustObsidianSmall;
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

