/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockMushroom
 *  net.minecraft.init.Blocks
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.BiomeGenBase
 */
package ic2.core.item.tfbp;

import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.init.InternalName;
import ic2.core.item.tfbp.ItemTFBP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockMushroom;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class ItemTFBPMushroom
extends ItemTFBP {
    public ItemTFBPMushroom(InternalName internalName) {
        super(internalName);
    }

    @Override
    public int getConsume() {
        return 8000;
    }

    @Override
    public int getRange() {
        return 25;
    }

    @Override
    public boolean terraform(World world, int x, int z, int yCoord) {
        int y = TileEntityTerra.getFirstSolidBlockFrom(world, x, z, yCoord + 20);
        if (y == -1) {
            return false;
        }
        return this.growBlockWithDependancy(world, x, y, z, Blocks.brown_mushroom_block, (Block)Blocks.brown_mushroom);
    }

    public boolean growBlockWithDependancy(World world, int x, int y, int z, Block target, Block dependancy) {
        Block block;
        for (int xm = x - 1; dependancy != null && xm < x + 1; ++xm) {
            block1: for (int zm = z - 1; zm < z + 1; ++zm) {
                for (int ym = y + 5; ym > y - 2; --ym) {
                    block = world.getBlock(xm, ym, zm);
                    if (dependancy == Blocks.mycelium) {
                        if (block == dependancy || block == Blocks.brown_mushroom_block || block == Blocks.red_mushroom_block) continue block1;
                        if (block.isAir((IBlockAccess)world, xm, ym, zm) || block != Blocks.dirt && block != Blocks.grass) continue;
                        world.setBlock(xm, ym, zm, dependancy, 0, 7);
                        TileEntityTerra.setBiomeAt(world, x, z, BiomeGenBase.mushroomIsland);
                        return true;
                    }
                    if (dependancy != Blocks.brown_mushroom) continue;
                    if (block == Blocks.brown_mushroom || block == Blocks.red_mushroom) continue block1;
                    if (block.isAir((IBlockAccess)world, xm, ym, zm) || !this.growBlockWithDependancy(world, xm, ym, zm, (Block)Blocks.brown_mushroom, (Block)Blocks.mycelium)) continue;
                    return true;
                }
            }
        }
        if (target == Blocks.brown_mushroom) {
            Block above;
            Block base = world.getBlock(x, y, z);
            if (base != Blocks.mycelium) {
                if (base == Blocks.brown_mushroom_block || base == Blocks.red_mushroom_block) {
                    world.setBlock(x, y, z, (Block)Blocks.mycelium, 0, 7);
                } else {
                    return false;
                }
            }
            if (!(above = world.getBlock(x, y + 1, z)).isAir((IBlockAccess)world, x, y + 1, z) && above != Blocks.tallgrass) {
                return false;
            }
            BlockBush shroom = world.rand.nextBoolean() ? Blocks.brown_mushroom : Blocks.red_mushroom;
            world.setBlock(x, y + 1, z, (Block)shroom, 0, 7);
            return true;
        }
        if (target == Blocks.brown_mushroom_block) {
            Block base = world.getBlock(x, y + 1, z);
            if (base != Blocks.brown_mushroom && base != Blocks.red_mushroom) {
                return false;
            }
            if (((BlockMushroom)base).func_149884_c(world, x, y + 1, z, world.rand)) {
                for (int xm = x - 1; xm < x + 1; ++xm) {
                    for (int zm = z - 1; zm < z + 1; ++zm) {
                        block = world.getBlock(xm, y + 1, zm);
                        if (block != Blocks.brown_mushroom && block != Blocks.red_mushroom) continue;
                        world.setBlockToAir(xm, y + 1, zm);
                    }
                }
                return true;
            }
        }
        return false;
    }
}

