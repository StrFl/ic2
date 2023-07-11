/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.block.EntityDynamite;
import ic2.core.block.MaterialIC2TNT;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockDynamite
extends BlockMetaData {
    public BlockDynamite(InternalName internalName1) {
        super(internalName1, MaterialIC2TNT.instance, ItemBlockIC2.class);
        this.setTickRandomly(true);
        this.setHardness(0.0f);
        this.setStepSound(soundTypeGrass);
        this.setCreativeTab(null);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public int getRenderType() {
        return 2;
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        return world.isBlockNormalCubeDefault(i - 1, j, k, false) || world.isBlockNormalCubeDefault(i + 1, j, k, false) || world.isBlockNormalCubeDefault(i, j, k - 1, false) || world.isBlockNormalCubeDefault(i, j, k + 1, false) || world.isBlockNormalCubeDefault(i, j - 1, k, false);
    }

    public void onPostBlockPlaced(World world, int x, int y, int z, int l) {
        int i1 = world.getBlockMetadata(x, y, z);
        if (l == 1 && world.isBlockNormalCubeDefault(x, y - 1, z, false)) {
            i1 = 5;
        } else if (l == 2 && world.isBlockNormalCubeDefault(x, y, z + 1, false)) {
            i1 = 4;
        } else if (l == 3 && world.isBlockNormalCubeDefault(x, y, z - 1, false)) {
            i1 = 3;
        } else if (l == 4 && world.isBlockNormalCubeDefault(x + 1, y, z, false)) {
            i1 = 2;
        } else if (l == 5 && world.isBlockNormalCubeDefault(x - 1, y, z, false)) {
            i1 = 1;
        }
        world.setBlockMetadataWithNotify(x, y, z, i1, 3);
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (world.getBlockMetadata(x, y, z) == 0) {
            this.onBlockAdded(world, x, y, z);
        }
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.removedByPlayer(world, null, x, y, z);
            return;
        }
        if (world.isBlockNormalCubeDefault(x, y - 1, z, false)) {
            world.setBlockMetadataWithNotify(x, y, z, 5, 3);
        } else if (world.isBlockNormalCubeDefault(x - 1, y, z, false)) {
            world.setBlockMetadataWithNotify(x, y, z, 1, 3);
        } else if (world.isBlockNormalCubeDefault(x + 1, y, z, false)) {
            world.setBlockMetadataWithNotify(x, y, z, 2, 3);
        } else if (world.isBlockNormalCubeDefault(x, y, z - 1, false)) {
            world.setBlockMetadataWithNotify(x, y, z, 3, 3);
        } else if (world.isBlockNormalCubeDefault(x, y, z + 1, false)) {
            world.setBlockMetadataWithNotify(x, y, z, 4, 3);
        }
        this.dropBlockIfCantStay(world, x, y, z);
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        return Ic2Items.dynamite.getItem();
    }

    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        EntityDynamite entitytntprimed = new EntityDynamite(world, (float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f);
        entitytntprimed.owner = explosion == null ? null : explosion.getExplosivePlacedBy();
        entitytntprimed.fuse = 5;
        world.spawnEntityInWorld((Entity)entitytntprimed);
    }

    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        world.setBlockToAir(x, y, z);
        EntityDynamite entitytntprimed = new EntityDynamite(world, (float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f);
        entitytntprimed.owner = player;
        entitytntprimed.fuse = 40;
        world.spawnEntityInWorld((Entity)entitytntprimed);
        world.playSoundAtEntity((Entity)entitytntprimed, "random.fuse", 1.0f, 1.0f);
        return true;
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, Block neighbor) {
        if (neighbor != null && neighbor.canProvidePower() && world.isBlockIndirectlyGettingPowered(i, j, k)) {
            this.removedByPlayer(world, null, i, j, k);
        } else if (this.dropBlockIfCantStay(world, i, j, k)) {
            int i1 = world.getBlockMetadata(i, j, k);
            if (!world.isBlockNormalCubeDefault(i - 1, j, k, false) && i1 == 1 || !world.isBlockNormalCubeDefault(i + 1, j, k, false) && i1 == 2 || !world.isBlockNormalCubeDefault(i, j, k - 1, false) && i1 == 3 || !world.isBlockNormalCubeDefault(i, j, k + 1, false) && i1 == 4 || !world.isBlockNormalCubeDefault(i, j - 1, k, false) && i1 == 5) {
                this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
                world.setBlockToAir(i, j, k);
            }
        }
    }

    public boolean dropBlockIfCantStay(World world, int i, int j, int k) {
        if (!this.canPlaceBlockAt(world, i, j, k)) {
            this.onBlockDestroyedByExplosion(world, i, j, k, null);
            world.setBlockToAir(i, j, k);
            return false;
        }
        return true;
    }

    public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 Vec32, Vec3 Vec31) {
        int l = world.getBlockMetadata(i, j, k) & 7;
        float f = 0.15f;
        if (l == 1) {
            this.setBlockBounds(0.0f, 0.2f, 0.5f - f, f * 2.0f, 0.8f, 0.5f + f);
        } else if (l == 2) {
            this.setBlockBounds(1.0f - f * 2.0f, 0.2f, 0.5f - f, 1.0f, 0.8f, 0.5f + f);
        } else if (l == 3) {
            this.setBlockBounds(0.5f - f, 0.2f, 0.0f, 0.5f + f, 0.8f, f * 2.0f);
        } else if (l == 4) {
            this.setBlockBounds(0.5f - f, 0.2f, 1.0f - f * 2.0f, 0.5f + f, 0.8f, 1.0f);
        } else {
            float f1 = 0.1f;
            this.setBlockBounds(0.5f - f1, 0.0f, 0.5f - f1, 0.5f + f1, 0.6f, 0.5f + f1);
        }
        return super.collisionRayTrace(world, i, j, k, Vec32, Vec31);
    }
}

