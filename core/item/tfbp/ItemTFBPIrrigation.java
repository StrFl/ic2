/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.init.Blocks
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.item.tfbp;

import ic2.core.Ic2Items;
import ic2.core.block.BlockRubSapling;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.init.InternalName;
import ic2.core.item.tfbp.ItemTFBP;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemTFBPIrrigation
extends ItemTFBP {
    public ItemTFBPIrrigation(InternalName internalName) {
        super(internalName);
    }

    @Override
    public int getConsume() {
        return 3000;
    }

    @Override
    public int getRange() {
        return 60;
    }

    @Override
    public boolean terraform(World world, int x, int z, int yCoord) {
        if (world.rand.nextInt(48000) == 0) {
            world.getWorldInfo().setRaining(true);
            return true;
        }
        int y = TileEntityTerra.getFirstBlockFrom(world, x, z, yCoord + 10);
        if (y == -1) {
            return false;
        }
        if (TileEntityTerra.switchGround(world, (Block)Blocks.sand, Blocks.dirt, x, y, z, true)) {
            TileEntityTerra.switchGround(world, (Block)Blocks.sand, Blocks.dirt, x, y, z, true);
            return true;
        }
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.tallgrass) {
            return this.spreadGrass(world, x + 1, y, z) || this.spreadGrass(world, x - 1, y, z) || this.spreadGrass(world, x, y, z + 1) || this.spreadGrass(world, x, y, z - 1);
        }
        if (block == Blocks.sapling) {
            ((BlockSapling)Blocks.sapling).func_149878_d(world, x, y, z, world.rand);
            return true;
        }
        if (StackUtil.equals(block, Ic2Items.rubberSapling)) {
            ((BlockRubSapling)StackUtil.getBlock(Ic2Items.rubberSapling)).func_149878_d(world, x, y, z, world.rand);
            return true;
        }
        if (block == Blocks.log) {
            int meta = world.getBlockMetadata(x, y, z);
            world.setBlock(x, y + 1, z, Blocks.log, meta, 7);
            this.createLeaves(world, x, y + 2, z, block, meta);
            this.createLeaves(world, x + 1, y + 1, z, block, meta);
            this.createLeaves(world, x - 1, y + 1, z, block, meta);
            this.createLeaves(world, x, y + 1, z + 1, block, meta);
            this.createLeaves(world, x, y + 1, z - 1, block, meta);
            return true;
        }
        if (block == Blocks.wheat) {
            world.setBlockMetadataWithNotify(x, y, z, 7, 7);
            return true;
        }
        if (block == Blocks.fire) {
            world.setBlockToAir(x, y, z);
            return true;
        }
        return false;
    }

    public void createLeaves(World world, int x, int y, int z, Block oldBlock, int meta) {
        if (oldBlock.isAir((IBlockAccess)world, x, y, z)) {
            world.setBlock(x, y, z, (Block)Blocks.leaves, meta, 7);
        }
    }

    public boolean spreadGrass(World world, int x, int y, int z) {
        if (world.rand.nextBoolean()) {
            return false;
        }
        Block block = world.getBlock(x, y = TileEntityTerra.getFirstBlockFrom(world, x, z, y), z);
        if (block == Blocks.dirt) {
            world.setBlock(x, y, z, (Block)Blocks.grass, 0, 7);
            return true;
        }
        if (block == Blocks.grass) {
            world.setBlock(x, y + 1, z, (Block)Blocks.tallgrass, 1, 7);
            return true;
        }
        return false;
    }
}

