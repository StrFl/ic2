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

public class ItemPlate
extends ItemIC2 {
    public ItemPlate(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.platecopper = new ItemStack((Item)this, 1, 0);
        Ic2Items.platetin = new ItemStack((Item)this, 1, 1);
        Ic2Items.platebronze = new ItemStack((Item)this, 1, 2);
        Ic2Items.plategold = new ItemStack((Item)this, 1, 3);
        Ic2Items.plateiron = new ItemStack((Item)this, 1, 4);
        Ic2Items.plateadviron = new ItemStack((Item)this, 1, 5);
        Ic2Items.platelead = new ItemStack((Item)this, 1, 6);
        Ic2Items.plateobsidian = new ItemStack((Item)this, 1, 7);
        Ic2Items.platelapi = new ItemStack((Item)this, 1, 8);
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
                ret = InternalName.itemPlateCopper;
                break;
            }
            case 1: {
                ret = InternalName.itemPlateTin;
                break;
            }
            case 2: {
                ret = InternalName.itemPlateBronze;
                break;
            }
            case 3: {
                ret = InternalName.itemPlateGold;
                break;
            }
            case 4: {
                ret = InternalName.itemPlateIron;
                break;
            }
            case 5: {
                ret = InternalName.itemPlateAdvIron;
                break;
            }
            case 6: {
                ret = InternalName.itemPlateLead;
                break;
            }
            case 7: {
                ret = InternalName.itemPlateObsidian;
                break;
            }
            case 8: {
                ret = InternalName.itemPlateLapi;
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

