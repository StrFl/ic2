/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.world.World
 */
package ic2.core.item.tfbp;

import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.init.InternalName;
import ic2.core.item.tfbp.ItemTFBP;
import ic2.core.item.tfbp.ItemTFBPCultivation;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemTFBPDesertification
extends ItemTFBP {
    public ItemTFBPDesertification(InternalName internalName) {
        super(internalName);
    }

    @Override
    public int getConsume() {
        return 2500;
    }

    @Override
    public int getRange() {
        return 40;
    }

    @Override
    public boolean terraform(World world, int x, int z, int yCoord) {
        int y = TileEntityTerra.getFirstBlockFrom(world, x, z, yCoord + 10);
        if (y == -1) {
            return false;
        }
        if (TileEntityTerra.switchGround(world, Blocks.dirt, (Block)Blocks.sand, x, y, z, false) || TileEntityTerra.switchGround(world, (Block)Blocks.grass, (Block)Blocks.sand, x, y, z, false) || TileEntityTerra.switchGround(world, Blocks.farmland, (Block)Blocks.sand, x, y, z, false)) {
            TileEntityTerra.switchGround(world, Blocks.dirt, (Block)Blocks.sand, x, y, z, false);
            return true;
        }
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.water || block == Blocks.flowing_water || block == Blocks.snow_layer || block == Blocks.leaves || StackUtil.equals(block, Ic2Items.rubberLeaves) || this.isPlant(block)) {
            world.setBlockToAir(x, y, z);
            return true;
        }
        if (block == Blocks.ice || block == Blocks.snow) {
            world.setBlock(x, y, z, (Block)Blocks.flowing_water, 0, 7);
            return true;
        }
        if ((block == Blocks.planks || block == Blocks.log || StackUtil.equals(block, Ic2Items.rubberWood)) && world.rand.nextInt(15) == 0) {
            world.setBlock(x, y, z, (Block)Blocks.fire, 0, 7);
            return true;
        }
        return false;
    }

    public boolean isPlant(Block block) {
        return ItemTFBPCultivation.plants.contains(block);
    }
}

