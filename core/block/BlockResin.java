/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.init.InternalName;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockResin
extends BlockMetaData {
    public BlockResin(InternalName internalName1) {
        super(internalName1, Material.circuits);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
        this.setHardness(1.6f);
        this.setResistance(0.5f);
        this.setStepSound(soundTypeSand);
        this.setCreativeTab(null);
        Ic2Items.resinSheet = new ItemStack((Block)this);
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

    public Item getItemDropped(int meta, Random random, int fortune) {
        return Ic2Items.resin.getItem();
    }

    public int quantityDropped(Random random) {
        return random.nextInt(5) == 0 ? 0 : 1;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        Block base = world.getBlock(x, y - 1, z);
        return !base.isAir((IBlockAccess)world, x, y - 1, z) && base.isOpaqueCube() && base.getMaterial().isSolid();
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        entity.fallDistance *= 0.75f;
        entity.motionX *= (double)0.6f;
        entity.motionY *= (double)0.85f;
        entity.motionZ *= (double)0.6f;
    }
}

