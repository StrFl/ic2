/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item.block;

import ic2.core.Ic2Items;
import ic2.core.block.TileEntityBarrel;
import ic2.core.init.InternalName;
import ic2.core.item.ItemBooze;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBarrel
extends ItemIC2 {
    public ItemBarrel(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemstack) {
        int v = ItemBooze.getAmountOfValue(itemstack.getItemDamage());
        if (v > 0) {
            return "" + v + StatCollector.translateToLocal((String)"ic2.item.LBoozeBarrel");
        }
        return StatCollector.translateToLocal((String)"ic2.item.EmptyBoozeBarrel");
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float a, float b, float c) {
        if (StackUtil.equals(world.getBlock(x, y, z), Ic2Items.scaffold) && world.getBlockMetadata(x, y, z) < 5) {
            world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.blockBarrel));
            ((TileEntityBarrel)world.getTileEntity(x, y, z)).set(itemstack.getItemDamage());
            if (!entityplayer.capabilities.isCreativeMode) {
                --entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem].stackSize;
                if (entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem].stackSize == 0) {
                    entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
                }
            }
            return true;
        }
        return false;
    }
}

