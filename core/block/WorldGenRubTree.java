/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.feature.WorldGenerator
 *  net.minecraftforge.common.IPlantable
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenRubTree
extends WorldGenerator {
    public static final int maxHeight = 8;

    public boolean generate(World world, Random random, int x, int count, int z) {
        while (count > 0) {
            int y;
            for (y = IC2.getWorldHeight(world) - 1; world.isAirBlock(x, y - 1, z) && y > 0; --y) {
            }
            if (!this.grow(world, x, y, z, random)) {
                count -= 3;
            }
            x += random.nextInt(15) - 7;
            z += random.nextInt(15) - 7;
            --count;
        }
        return true;
    }

    public boolean grow(World world, int x, int y, int z, Random random) {
        if (world == null || Ic2Items.rubberWood == null) {
            IC2.log.warn(LogCategory.General, "RubberTree did not spawn! w=%s r=%s.", world, Ic2Items.rubberWood);
            return false;
        }
        Block woodBlock = StackUtil.getBlock(Ic2Items.rubberWood);
        Block leavesBlock = StackUtil.getBlock(Ic2Items.rubberLeaves);
        int treeholechance = 25;
        int height = this.getGrowHeight(world, x, y, z);
        if (height < 2) {
            return false;
        }
        height -= random.nextInt(height / 2 + 1);
        for (int cHeight = 0; cHeight < height; ++cHeight) {
            world.setBlock(x, y + cHeight, z, woodBlock, 0, 3);
            if (random.nextInt(100) <= treeholechance) {
                treeholechance -= 10;
                world.setBlockMetadataWithNotify(x, y + cHeight, z, random.nextInt(4) + 2, 3);
            } else {
                world.setBlockMetadataWithNotify(x, y + cHeight, z, 1, 3);
            }
            if (height >= 4 && (height >= 7 || cHeight <= 1) && cHeight <= 2) continue;
            for (int cx = x - 2; cx <= x + 2; ++cx) {
                for (int cz = z - 2; cz <= z + 2; ++cz) {
                    boolean gen;
                    int c = Math.max(1, cHeight + 4 - height);
                    boolean bl = gen = cx > x - 2 && cx < x + 2 && cz > z - 2 && cz < z + 2 || cx > x - 2 && cx < x + 2 && random.nextInt(c) == 0 || cz > z - 2 && cz < z + 2 && random.nextInt(c) == 0;
                    if (!gen || !world.isAirBlock(cx, y + cHeight, cz)) continue;
                    world.setBlock(cx, y + cHeight, cz, leavesBlock, 0, 3);
                }
            }
        }
        for (int i = 0; i <= height / 4 + random.nextInt(2); ++i) {
            if (!world.isAirBlock(x, y + height + i, z)) continue;
            world.setBlock(x, y + height + i, z, leavesBlock, 0, 3);
        }
        return true;
    }

    public int getGrowHeight(World world, int x, int y, int z) {
        int height;
        Block base = world.getBlock(x, y - 1, z);
        if (base.isAir((IBlockAccess)world, x, y - 1, z) || !base.canSustainPlant((IBlockAccess)world, x, y - 1, z, ForgeDirection.UP, (IPlantable)StackUtil.getBlock(Ic2Items.rubberSapling)) || !world.isAirBlock(x, y, z) && !StackUtil.equals(world.getBlock(x, y, z), Ic2Items.rubberSapling)) {
            return 0;
        }
        for (height = 1; world.isAirBlock(x, y + 1, z) && height < 8; ++height) {
            ++y;
        }
        return height;
    }
}

