/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDispenser
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.item.block;

import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BehaviorDynamiteDispense;
import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityStickyDynamite;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemDynamite
extends ItemIC2
implements IBoxable {
    public boolean sticky;

    public ItemDynamite(InternalName internalName, boolean st) {
        super(internalName);
        this.sticky = st;
        this.setMaxStackSize(16);
        BlockDispenser.dispenseBehaviorRegistry.putObject((Object)this, (Object)new BehaviorDynamiteDispense(this.sticky));
    }

    public int getMetadata(int i) {
        return i;
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float a, float b, float c) {
        if (this.sticky) {
            return false;
        }
        if (l == 0) {
            --j;
        }
        if (l == 1) {
            ++j;
        }
        if (l == 2) {
            --k;
        }
        if (l == 3) {
            ++k;
        }
        if (l == 4) {
            --i;
        }
        if (l == 5) {
            ++i;
        }
        Block block = world.getBlock(i, j, k);
        Block dynamite = StackUtil.getBlock(Ic2Items.dynamiteStick);
        if (block.isAir((IBlockAccess)world, i, j, k) && dynamite.canPlaceBlockAt(world, i, j, k)) {
            world.setBlock(i, j, k, dynamite, 0, 3);
            --itemstack.stackSize;
            return true;
        }
        return true;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!entityplayer.capabilities.isCreativeMode) {
            --itemstack.stackSize;
        }
        world.playSoundAtEntity((Entity)entityplayer, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
        if (IC2.platform.isSimulating()) {
            if (this.sticky) {
                world.spawnEntityInWorld((Entity)new EntityStickyDynamite(world, (EntityLivingBase)entityplayer));
            } else {
                world.spawnEntityInWorld((Entity)new EntityDynamite(world, (EntityLivingBase)entityplayer));
            }
        }
        return itemstack;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

