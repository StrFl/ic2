/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.world.World
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemMugCoffee
extends ItemIC2 {
    public ItemMugCoffee(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage();
        if (meta < 3) {
            return "ic2.itemMugCoffee_" + meta;
        }
        return null;
    }

    public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player) {
        int meta = itemstack.getItemDamage();
        int highest = 0;
        int x = this.amplifyEffect(player, Potion.moveSpeed, meta);
        if (x > highest) {
            highest = x;
        }
        if ((x = this.amplifyEffect(player, Potion.digSpeed, meta)) > highest) {
            highest = x;
        }
        if (meta == 2) {
            highest -= 2;
        }
        if (highest >= 3) {
            player.addPotionEffect(new PotionEffect(Potion.confusion.id, (highest - 2) * 200, 0));
            if (highest >= 4) {
                player.addPotionEffect(new PotionEffect(Potion.harm.id, 1, highest - 3));
            }
        }
        return new ItemStack(Ic2Items.mugEmpty.getItem());
    }

    public int amplifyEffect(EntityPlayer player, Potion potion1, int meta) {
        PotionEffect eff = player.getActivePotionEffect(potion1);
        if (eff != null) {
            int max = 1;
            if (meta == 1) {
                max = 5;
            }
            if (meta == 2) {
                max = 6;
            }
            int newAmp = eff.getAmplifier();
            int newDur = eff.getDuration();
            if (newAmp < max) {
                ++newAmp;
            }
            newDur = meta == 0 ? (newDur += 600) : (newDur += 1200);
            eff.combine(new PotionEffect(eff.getPotionID(), newDur, newAmp));
            return newAmp;
        }
        player.addPotionEffect(new PotionEffect(potion1.id, 300, 0));
        return 1;
    }

    public int getMaxItemUseDuration(ItemStack itemstack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack itemstack) {
        return EnumAction.drink;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
        player.setItemInUse(itemstack, this.getMaxItemUseDuration(itemstack));
        return itemstack;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        for (int meta = 0; meta < 3; ++meta) {
            itemList.add(new ItemStack((Item)this, 1, meta));
        }
    }
}

