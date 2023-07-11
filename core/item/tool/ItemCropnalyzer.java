/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.crop.TileEntityCrop;
import ic2.core.init.InternalName;
import ic2.core.item.BaseElectricItem;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.tool.ContainerCropnalyzer;
import ic2.core.item.tool.HandHeldCropnalyzer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemCropnalyzer
extends BaseElectricItem
implements IHandHeldInventory {
    public ItemCropnalyzer(InternalName internalName) {
        super(internalName, 100000.0, 128.0, 2);
    }

    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (IC2.platform.isSimulating()) {
            IC2.platform.launchGui(entityPlayer, this.getInventory(entityPlayer, itemStack));
        }
        return itemStack;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public IHasGui getInventory(EntityPlayer entityPlayer, ItemStack itemStack) {
        return new HandHeldCropnalyzer(entityPlayer, itemStack);
    }

    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        HandHeldCropnalyzer cropnalyzer;
        if (player instanceof EntityPlayerMP && stack != null && player.openContainer instanceof ContainerCropnalyzer && (cropnalyzer = (HandHeldCropnalyzer)((ContainerCropnalyzer)player.openContainer).base).isThisContainer(stack)) {
            ((EntityPlayerMP)player).closeScreen();
        }
        return true;
    }

    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote || player.isSneaking()) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop)te;
            if (crop.getCrop() == null) {
                return false;
            }
            if (ElectricItem.manager.discharge(stack, HandHeldCropnalyzer.energyForLevel(2), 3, true, false, false) > 0.0) {
                IC2.platform.messagePlayer(player, "Crop name: " + StatCollector.translateToLocal((String)crop.getCrop().displayName()) + " (by " + crop.getCrop().discoveredBy() + ")", new Object[0]);
                IC2.platform.messagePlayer(player, "Crop size: " + crop.size + "/" + crop.getCrop().maxSize(), new Object[0]);
                IC2.platform.messagePlayer(player, "Nutrient storage: " + crop.nutrientStorage + "/100", new Object[0]);
                IC2.platform.messagePlayer(player, "Water storage: " + crop.waterStorage + "/100", new Object[0]);
                IC2.platform.messagePlayer(player, "Weed-Ex storage: " + crop.exStorage + "/100", new Object[0]);
                IC2.platform.messagePlayer(player, "GrowthPoints: " + crop.growthPoints + "/" + crop.getCrop().growthDuration(crop), new Object[0]);
                return true;
            }
        }
        return false;
    }
}

