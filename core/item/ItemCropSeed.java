/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.Ic2Items;
import ic2.core.crop.TileEntityCrop;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.Util;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemCropSeed
extends ItemIC2 {
    public ItemCropSeed(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
        if (!Util.inDev()) {
            this.setCreativeTab(null);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        if (itemstack == null) {
            return "ic2.crop.unknown";
        }
        CropCard cropCard = Crops.instance.getCropCard(itemstack);
        byte level = ItemCropSeed.getScannedFromStack(itemstack);
        if (level == 0) {
            return "ic2.crop.unknown";
        }
        if (level < 0 || cropCard == null) {
            return "ic2.crop.invalid";
        }
        return cropCard.displayName();
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        return StatCollector.translateToLocalFormatted((String)"ic2.crop.seeds", (Object[])new Object[]{super.getItemStackDisplayName(itemStack)});
    }

    public boolean isDamageable() {
        return true;
    }

    public boolean isRepairable() {
        return false;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debugTooltips) {
        if (ItemCropSeed.getScannedFromStack(stack) >= 4) {
            info.add("\u00a72Gr\u00a77 " + ItemCropSeed.getGrowthFromStack(stack));
            info.add("\u00a76Ga\u00a77 " + ItemCropSeed.getGainFromStack(stack));
            info.add("\u00a73Re\u00a77 " + ItemCropSeed.getResistanceFromStack(stack));
        }
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float a, float b, float c) {
        TileEntityCrop crop;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCrop && (crop = (TileEntityCrop)te).tryPlantIn(Crops.instance.getCropCard(itemstack), 1, ItemCropSeed.getGrowthFromStack(itemstack), ItemCropSeed.getGainFromStack(itemstack), ItemCropSeed.getResistanceFromStack(itemstack), ItemCropSeed.getScannedFromStack(itemstack))) {
            if (!entityplayer.capabilities.isCreativeMode) {
                entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
            }
            return true;
        }
        return false;
    }

    public void getSubItems(Item item, CreativeTabs tabs, List items) {
        for (CropCard crop : Crops.instance.getCrops()) {
            items.add(ItemCropSeed.generateItemStackFromValues(crop, (byte)1, (byte)1, (byte)1, (byte)4));
        }
    }

    public static ItemStack generateItemStackFromValues(CropCard crop, byte statGrowth, byte statGain, byte statResistance, byte scan) {
        ItemStack stack = new ItemStack(Ic2Items.cropSeed.getItem());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("owner", crop.owner());
        tag.setString("name", crop.name());
        tag.setByte("growth", statGrowth);
        tag.setByte("gain", statGain);
        tag.setByte("resistance", statResistance);
        tag.setByte("scan", scan);
        stack.setTagCompound(tag);
        return stack;
    }

    public static byte getGrowthFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return -1;
        }
        return is.getTagCompound().getByte("growth");
    }

    public static byte getGainFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return -1;
        }
        return is.getTagCompound().getByte("gain");
    }

    public static byte getResistanceFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return -1;
        }
        return is.getTagCompound().getByte("resistance");
    }

    public static byte getScannedFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return -1;
        }
        return is.getTagCompound().getByte("scan");
    }

    public static void incrementScannedOfStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return;
        }
        is.getTagCompound().setByte("scan", (byte)(ItemCropSeed.getScannedFromStack(is) + 1));
    }
}

