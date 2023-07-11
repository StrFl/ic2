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

public class ItemPurifiedCrushedOre
extends ItemIC2 {
    public ItemPurifiedCrushedOre(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.purifiedCrushedIronOre = new ItemStack((Item)this, 1, 0);
        Ic2Items.purifiedCrushedCopperOre = new ItemStack((Item)this, 1, 1);
        Ic2Items.purifiedCrushedGoldOre = new ItemStack((Item)this, 1, 2);
        Ic2Items.purifiedCrushedTinOre = new ItemStack((Item)this, 1, 3);
        Ic2Items.purifiedCrushedUraniumOre = new ItemStack((Item)this, 1, 4);
        Ic2Items.purifiedCrushedSilverOre = new ItemStack((Item)this, 1, 5);
        Ic2Items.purifiedCrushedLeadOre = new ItemStack((Item)this, 1, 6);
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
                ret = InternalName.itemPurifiedCrushedIronOre;
                break;
            }
            case 1: {
                ret = InternalName.itemPurifiedCrushedCopperOre;
                break;
            }
            case 2: {
                ret = InternalName.itemPurifiedCrushedGoldOre;
                break;
            }
            case 3: {
                ret = InternalName.itemPurifiedCrushedTinOre;
                break;
            }
            case 4: {
                ret = InternalName.itemPurifiedCrushedUranOre;
                break;
            }
            case 5: {
                ret = InternalName.itemPurifiedCrushedSilverOre;
                break;
            }
            case 6: {
                ret = InternalName.itemPurifiedCrushedLeadOre;
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

