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

public class ItemDust
extends ItemIC2 {
    public ItemDust(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.bronzeDust = new ItemStack((Item)this, 1, 0);
        Ic2Items.clayDust = new ItemStack((Item)this, 1, 1);
        Ic2Items.coalDust = new ItemStack((Item)this, 1, 2);
        Ic2Items.copperDust = new ItemStack((Item)this, 1, 3);
        Ic2Items.goldDust = new ItemStack((Item)this, 1, 4);
        Ic2Items.ironDust = new ItemStack((Item)this, 1, 5);
        Ic2Items.silverDust = new ItemStack((Item)this, 1, 6);
        Ic2Items.tinDust = new ItemStack((Item)this, 1, 7);
        Ic2Items.hydratedCoalDust = new ItemStack((Item)this, 1, 8);
        Ic2Items.stoneDust = new ItemStack((Item)this, 1, 9);
        Ic2Items.leadDust = new ItemStack((Item)this, 1, 10);
        Ic2Items.obsidianDust = new ItemStack((Item)this, 1, 11);
        Ic2Items.lapiDust = new ItemStack((Item)this, 1, 12);
        Ic2Items.sulfurDust = new ItemStack((Item)this, 1, 13);
        Ic2Items.lithiumDust = new ItemStack((Item)this, 1, 14);
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
                ret = InternalName.itemDustBronze;
                break;
            }
            case 1: {
                ret = InternalName.itemDustClay;
                break;
            }
            case 2: {
                ret = InternalName.itemDustCoal;
                break;
            }
            case 3: {
                ret = InternalName.itemDustCopper;
                break;
            }
            case 4: {
                ret = InternalName.itemDustGold;
                break;
            }
            case 5: {
                ret = InternalName.itemDustIron;
                break;
            }
            case 6: {
                ret = InternalName.itemDustSilver;
                break;
            }
            case 7: {
                ret = InternalName.itemDustTin;
                break;
            }
            case 8: {
                ret = InternalName.itemFuelCoalDust;
                break;
            }
            case 9: {
                ret = InternalName.itemDustStone;
                break;
            }
            case 10: {
                ret = InternalName.itemDustLead;
                break;
            }
            case 11: {
                ret = InternalName.itemDustObsidian;
                break;
            }
            case 12: {
                ret = InternalName.itemDustLapi;
                break;
            }
            case 13: {
                ret = InternalName.itemDustSulfur;
                break;
            }
            case 14: {
                ret = InternalName.itemDustLithium;
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

