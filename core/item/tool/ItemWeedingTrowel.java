/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.crop.IC2Crops;
import ic2.core.crop.TileEntityCrop;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemWeedingTrowel
extends ItemIC2 {
    public ItemWeedingTrowel(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
    }

    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop)te;
            if (crop.getCrop() == IC2Crops.weed) {
                StackUtil.dropAsEntity(world, x, y, z, new ItemStack(Ic2Items.weed.getItem(), crop.size));
                crop.reset();
                return true;
            }
            if (crop.weedlevel > 0) {
                StackUtil.dropAsEntity(world, x, y, z, new ItemStack(Ic2Items.weed.getItem(), crop.weedlevel));
                crop.weedlevel = 0;
                crop.updateState();
                return true;
            }
        }
        return false;
    }
}

