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

public class ItemRecipePart
extends ItemIC2 {
    public ItemRecipePart(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.coil = new ItemStack((Item)this, 1, 0);
        Ic2Items.elemotor = new ItemStack((Item)this, 1, 1);
        Ic2Items.powerunit = new ItemStack((Item)this, 1, 2);
        Ic2Items.powerunitsmall = new ItemStack((Item)this, 1, 3);
        Ic2Items.rawcrystalmemory = new ItemStack((Item)this, 1, 4);
        Ic2Items.heatconductor = new ItemStack((Item)this, 1, 5);
        Ic2Items.copperboiler = new ItemStack((Item)this, 1, 6);
        Ic2Items.woodrotorblade = new ItemStack((Item)this, 1, 7);
        Ic2Items.ironrotorblade = new ItemStack((Item)this, 1, 8);
        Ic2Items.carbonrotorblade = new ItemStack((Item)this, 1, 9);
        Ic2Items.steelrotorblade = new ItemStack((Item)this, 1, 10);
        Ic2Items.ironshaft = new ItemStack((Item)this, 1, 11);
        Ic2Items.steelshaft = new ItemStack((Item)this, 1, 12);
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
                ret = InternalName.itemPartCoil;
                break;
            }
            case 1: {
                ret = InternalName.itemPartEleMotor;
                break;
            }
            case 2: {
                ret = InternalName.itempowerunit;
                break;
            }
            case 3: {
                ret = InternalName.itempowerunitsmall;
                break;
            }
            case 4: {
                ret = InternalName.itemrawcrystalmemory;
                break;
            }
            case 5: {
                ret = InternalName.itemheatconductor;
                break;
            }
            case 6: {
                ret = InternalName.itemcopperboiler;
                break;
            }
            case 7: {
                ret = InternalName.itemwoodrotorblade;
                break;
            }
            case 8: {
                ret = InternalName.itemironrotorblade;
                break;
            }
            case 9: {
                ret = InternalName.itemcarbonrotorblade;
                break;
            }
            case 10: {
                ret = InternalName.itemsteelrotorblade;
                break;
            }
            case 11: {
                ret = InternalName.itemironshaft;
                break;
            }
            case 12: {
                ret = InternalName.itemsteelshaft;
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

