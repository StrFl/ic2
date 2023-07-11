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

public class ItemDensePlate
extends ItemIC2 {
    public ItemDensePlate(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.denseplatecopper = new ItemStack((Item)this, 1, 0);
        Ic2Items.denseplatetin = new ItemStack((Item)this, 1, 1);
        Ic2Items.denseplatebronze = new ItemStack((Item)this, 1, 2);
        Ic2Items.denseplategold = new ItemStack((Item)this, 1, 3);
        Ic2Items.denseplateiron = new ItemStack((Item)this, 1, 4);
        Ic2Items.denseplateadviron = new ItemStack((Item)this, 1, 5);
        Ic2Items.denseplatelead = new ItemStack((Item)this, 1, 6);
        Ic2Items.denseplateobsidian = new ItemStack((Item)this, 1, 7);
        Ic2Items.denseplatelapi = new ItemStack((Item)this, 1, 8);
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
                ret = InternalName.itemDensePlateCopper;
                break;
            }
            case 1: {
                ret = InternalName.itemDensePlateTin;
                break;
            }
            case 2: {
                ret = InternalName.itemDensePlateBronze;
                break;
            }
            case 3: {
                ret = InternalName.itemDensePlateGold;
                break;
            }
            case 4: {
                ret = InternalName.itemDensePlateIron;
                break;
            }
            case 5: {
                ret = InternalName.itemDensePlateAdvIron;
                break;
            }
            case 6: {
                ret = InternalName.itemDensePlateLead;
                break;
            }
            case 7: {
                ret = InternalName.itemDensePlateObsidian;
                break;
            }
            case 8: {
                ret = InternalName.itemDensePlateLapi;
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

