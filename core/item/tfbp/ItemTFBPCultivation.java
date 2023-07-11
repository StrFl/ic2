/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.item.tfbp;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.init.InternalName;
import ic2.core.item.tfbp.ItemTFBP;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemTFBPCultivation
extends ItemTFBP {
    public static ArrayList<Block> plants = new ArrayList();

    public ItemTFBPCultivation(InternalName internalName) {
        super(internalName);
    }

    public static void init() {
        plants.add((Block)Blocks.tallgrass);
        plants.add((Block)Blocks.red_flower);
        plants.add((Block)Blocks.yellow_flower);
        plants.add(Blocks.sapling);
        plants.add(Blocks.wheat);
        plants.add(Blocks.red_mushroom_block);
        plants.add((Block)Blocks.brown_mushroom);
        plants.add(Blocks.pumpkin);
        if (Ic2Items.rubberSapling != null) {
            plants.add(StackUtil.getBlock(Ic2Items.rubberSapling));
        }
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float a, float b, float c) {
        if (super.onItemUse(itemstack, entityplayer, world, i, j, k, l, a, b, c)) {
            if (entityplayer.dimension == 1) {
                IC2.achievements.issueAchievement(entityplayer, "terraformEndCultivation");
            }
            return true;
        }
        return false;
    }

    @Override
    public int getConsume() {
        return 4000;
    }

    @Override
    public int getRange() {
        return 40;
    }

    @Override
    public boolean terraform(World world, int x, int z, int yCoord) {
        int y = TileEntityTerra.getFirstSolidBlockFrom(world, x, z, yCoord + 10);
        if (y == -1) {
            return false;
        }
        if (TileEntityTerra.switchGround(world, (Block)Blocks.sand, Blocks.dirt, x, y, z, true)) {
            return true;
        }
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.dirt) {
            world.setBlock(x, y, z, (Block)Blocks.grass, 0, 7);
            return true;
        }
        if (block == Blocks.grass) {
            return this.growPlantsOn(world, x, y + 1, z, block);
        }
        return false;
    }

    public boolean growPlantsOn(World world, int x, int y, int z, Block block) {
        if (block.isAir((IBlockAccess)world, x, y, z) || block == Blocks.tallgrass && world.rand.nextInt(4) == 0) {
            Block plant = this.pickRandomPlant(world.rand);
            if (plant == Blocks.wheat) {
                world.setBlock(x, y - 1, z, Blocks.farmland, 0, 7);
            }
            if (plant == Blocks.tallgrass) {
                world.setBlock(x, y, z, plant, 1, 7);
            } else {
                world.setBlock(x, y, z, plant, 0, 7);
            }
            return true;
        }
        return false;
    }

    public Block pickRandomPlant(Random random) {
        for (Block block : plants) {
            if (random.nextInt(5) > 1) continue;
            return block;
        }
        return Blocks.tallgrass;
    }
}

