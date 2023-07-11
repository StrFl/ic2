/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemResin
extends ItemIC2 {
    public ItemResin(InternalName internalName) {
        super(internalName);
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float a, float b, float c) {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.piston && world.getBlockMetadata(x, y, z) == side) {
            world.setBlock(x, y, z, (Block)Blocks.sticky_piston, side, 3);
            if (!entityplayer.capabilities.isCreativeMode) {
                --itemstack.stackSize;
            }
            return true;
        }
        if (side != 1) {
            return false;
        }
        if (!block.isAir((IBlockAccess)world, x, ++y, z) || !StackUtil.getBlock(Ic2Items.resinSheet).canPlaceBlockAt(world, x, y, z)) {
            return false;
        }
        world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.resinSheet), 0, 3);
        if (!entityplayer.capabilities.isCreativeMode) {
            --itemstack.stackSize;
        }
        return true;
    }
}

