/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRubberSheet
extends BlockMetaData {
    public BlockRubberSheet(InternalName internalName1) {
        super(internalName1, Material.cloth, ItemBlockIC2.class);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
        this.setHardness(0.8f);
        this.setResistance(2.0f);
        this.setStepSound(soundTypeCloth);
        Ic2Items.rubberTrampoline = new ItemStack((Block)this);
    }

    @Override
    public int getTextureIndex(int meta) {
        return 0;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        return this.isBlockSupporter(world, i - 1, j, k) || this.isBlockSupporter(world, i + 1, j, k) || this.isBlockSupporter(world, i, j, k - 1) || this.isBlockSupporter(world, i, j, k + 1);
    }

    public boolean isBlockSupporter(World world, int i, int j, int k) {
        return world.isBlockNormalCubeDefault(i, j, k, false) || world.getBlock(i, j, k) == this;
    }

    public boolean canSupportWeight(World world, int i, int j, int k) {
        if (world.getBlockMetadata(i, j, k) == 1) {
            return true;
        }
        boolean xup = false;
        boolean xdown = false;
        boolean zup = false;
        boolean zdown = false;
        int x = i;
        while (true) {
            if (world.isBlockNormalCubeDefault(x, j, k, false)) {
                xdown = true;
                break;
            }
            if (world.getBlock(x, j, k) != this) break;
            if (world.isBlockNormalCubeDefault(x, j - 1, k, false)) {
                xdown = true;
                break;
            }
            --x;
        }
        x = i;
        while (true) {
            if (world.isBlockNormalCubeDefault(x, j, k, false)) {
                xup = true;
                break;
            }
            if (world.getBlock(x, j, k) != this) break;
            if (world.isBlockNormalCubeDefault(x, j - 1, k, false)) {
                xup = true;
                break;
            }
            ++x;
        }
        if (xup && xdown) {
            world.setBlockMetadataWithNotify(i, j, k, 1, 3);
            return true;
        }
        int z = k;
        while (true) {
            if (world.isBlockNormalCubeDefault(i, j, z, false)) {
                zdown = true;
                break;
            }
            if (world.getBlock(i, j, z) != this) break;
            if (world.isBlockNormalCubeDefault(i, j - 1, z, false)) {
                zdown = true;
                break;
            }
            --z;
        }
        z = k;
        while (true) {
            if (world.isBlockNormalCubeDefault(i, j, z, false)) {
                zup = true;
                break;
            }
            if (world.getBlock(i, j, z) != this) break;
            if (world.isBlockNormalCubeDefault(i, j - 1, z, false)) {
                zup = true;
                break;
            }
            ++z;
        }
        if (zup && zdown) {
            world.setBlockMetadataWithNotify(i, j, k, 1, 3);
            return true;
        }
        return false;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (world.getBlockMetadata(x, y, z) == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 0, 7);
        }
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        if (world.isBlockNormalCubeDefault(i, j - 1, k, false)) {
            return;
        }
        if (entity instanceof EntityLivingBase && !this.canSupportWeight(world, i, j, k)) {
            world.setBlockToAir(i, j, k);
            return;
        }
        if (entity.motionY <= (double)-0.4f) {
            entity.fallDistance = 0.0f;
            entity.motionX *= (double)1.1f;
            entity.motionY = entity instanceof EntityLivingBase ? (entity instanceof EntityPlayer && IC2.keyboard.isJumpKeyDown((EntityPlayer)entity) ? (entity.motionY *= (double)-1.3f) : (entity instanceof EntityPlayer && ((EntityPlayer)entity).isSneaking() ? (entity.motionY *= (double)-0.1f) : (entity.motionY *= (double)-0.8f))) : (entity.motionY *= (double)-0.8f);
            entity.motionZ *= (double)1.1f;
        }
    }
}

