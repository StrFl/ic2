/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityBarrel;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMug
extends ItemIC2 {
    public ItemMug(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
        if (StackUtil.equals(world.getBlock(i, j, k), Ic2Items.blockBarrel)) {
            TileEntityBarrel barrel = (TileEntityBarrel)world.getTileEntity(i, j, k);
            if (barrel.treetapSide < 2 || barrel.treetapSide != side) {
                return false;
            }
            int value = barrel.calculateMetaValue();
            if (barrel.drainLiquid(1) && IC2.platform.isSimulating()) {
                ItemStack is = new ItemStack(Ic2Items.mugBooze.getItem(), 1, value);
                if (entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem].stackSize > 1) {
                    --entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem].stackSize;
                    if (!entityplayer.inventory.addItemStackToInventory(is)) {
                        entityplayer.dropPlayerItemWithRandomChoice(is, false);
                    }
                } else {
                    entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = is;
                }
                return true;
            }
            return false;
        }
        return false;
    }
}

