/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemFoamPowder
extends ItemIC2 {
    public ItemFoamPowder(InternalName internalName) {
        super(internalName);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (position == null) {
            return stack;
        }
        if (position.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = position.blockX;
            int y = position.blockY;
            int z = position.blockZ;
            if (!world.canMineBlock(player, x, y, z)) {
                return stack;
            }
            if (world.getBlock(x, y, z) == Blocks.water) {
                player.inventory.consumeInventoryItem((Item)this);
                world.setBlock(x, y, z, BlocksItems.getFluidBlock(InternalName.fluidConstructionFoam));
            }
        }
        return stack;
    }
}

