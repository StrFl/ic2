/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityBarrel;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTreetap
extends ItemIC2
implements IBoxable {
    public ItemTreetap(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
        this.setMaxDamage(16);
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        Block block = world.getBlock(x, y, z);
        if (StackUtil.equals(block, Ic2Items.blockBarrel)) {
            return ((TileEntityBarrel)world.getTileEntity(x, y, z)).useTreetapOn(entityplayer, side);
        }
        if (Ic2Items.rubberWood != null && StackUtil.equals(block, Ic2Items.rubberWood)) {
            ItemTreetap.attemptExtract(entityplayer, world, x, y, z, side, null);
            if (IC2.platform.isSimulating()) {
                itemstack.damageItem(1, (EntityLivingBase)entityplayer);
            }
            return true;
        }
        return false;
    }

    public static void ejectHarz(World world, int x, int y, int z, int side, int quantity) {
        double ejectX = (double)x + 0.5;
        double ejectY = (double)y + 0.5;
        double ejectZ = (double)z + 0.5;
        if (side == 2) {
            ejectZ -= 0.3;
        } else if (side == 5) {
            ejectX += 0.3;
        } else if (side == 3) {
            ejectZ += 0.3;
        } else if (side == 4) {
            ejectX -= 0.3;
        }
        for (int i = 0; i < quantity; ++i) {
            EntityItem entityitem = new EntityItem(world, ejectX, ejectY, ejectZ, Ic2Items.resin.copy());
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld((Entity)entityitem);
        }
    }

    public static boolean attemptExtract(EntityPlayer entityplayer, World world, int x, int y, int z, int side, List<ItemStack> stacks) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 2 || meta % 6 != side) {
            return false;
        }
        if (meta < 6) {
            if (IC2.platform.isSimulating()) {
                world.setBlockMetadataWithNotify(x, y, z, meta + 6, 3);
                if (stacks != null) {
                    stacks.add(StackUtil.copyWithSize(Ic2Items.resin, world.rand.nextInt(3) + 1));
                } else {
                    ItemTreetap.ejectHarz(world, x, y, z, side, world.rand.nextInt(3) + 1);
                }
                if (entityplayer != null) {
                    IC2.achievements.issueAchievement(entityplayer, "acquireResin");
                }
                Block woodBlock = StackUtil.getBlock(Ic2Items.rubberWood);
                world.scheduleBlockUpdate(x, y, z, woodBlock, woodBlock.tickRate(world));
            }
            if (IC2.platform.isRendering() && entityplayer != null) {
                IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/Treetap.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return true;
        }
        if (IC2.platform.isSimulating() && world.rand.nextInt(5) == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 1, 3);
        }
        if (world.rand.nextInt(5) == 0) {
            if (IC2.platform.isSimulating()) {
                ItemTreetap.ejectHarz(world, x, y, z, side, 1);
                if (stacks != null) {
                    stacks.add(StackUtil.copyWithSize(Ic2Items.resin, 1));
                } else {
                    ItemTreetap.ejectHarz(world, x, y, z, side, 1);
                }
            }
            if (IC2.platform.isRendering() && entityplayer != null) {
                IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/Treetap.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

