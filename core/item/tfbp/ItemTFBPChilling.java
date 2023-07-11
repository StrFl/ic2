/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.world.World
 */
package ic2.core.item.tfbp;

import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.init.InternalName;
import ic2.core.item.tfbp.ItemTFBP;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemTFBPChilling
extends ItemTFBP {
    public ItemTFBPChilling(InternalName internalName) {
        super(internalName);
    }

    @Override
    public int getConsume() {
        return 2000;
    }

    @Override
    public int getRange() {
        return 50;
    }

    @Override
    public boolean terraform(World world, int x, int z, int yCoord) {
        int y = TileEntityTerra.getFirstBlockFrom(world, x, z, yCoord + 10);
        if (y == -1) {
            return false;
        }
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.water || block == Blocks.flowing_water) {
            world.setBlock(x, y, z, Blocks.ice, 0, 7);
            return true;
        }
        if (block == Blocks.ice) {
            Block blockBelow = world.getBlock(x, y - 1, z);
            if (blockBelow == Blocks.water || blockBelow == Blocks.flowing_water) {
                world.setBlock(x, y - 1, z, Blocks.ice, 0, 7);
                return true;
            }
        } else if (block == Blocks.snow_layer && this.isSurroundedBySnow(world, x, y, z)) {
            world.setBlock(x, y, z, Blocks.snow, 0, 7);
            return true;
        }
        if (Blocks.snow_layer.canPlaceBlockAt(world, x, y + 1, z) || block == Blocks.ice) {
            world.setBlock(x, y + 1, z, Blocks.snow_layer, 0, 7);
            return true;
        }
        return false;
    }

    public boolean isSurroundedBySnow(World world, int x, int y, int z) {
        return this.isSnowHere(world, x + 1, y, z) && this.isSnowHere(world, x - 1, y, z) && this.isSnowHere(world, x, y, z + 1) && this.isSnowHere(world, x, y, z - 1);
    }

    public boolean isSnowHere(World world, int x, int y, int z) {
        int saveY = y;
        if (saveY > (y = TileEntityTerra.getFirstBlockFrom(world, x, z, y + 16))) {
            return false;
        }
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.snow || block == Blocks.snow_layer) {
            return true;
        }
        if (Blocks.snow_layer.canPlaceBlockAt(world, x, y + 1, z) || block == Blocks.ice) {
            world.setBlock(x, y + 1, z, Blocks.snow_layer, 0, 7);
        }
        return false;
    }
}

