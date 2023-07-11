/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package ic2.core.util;

import net.minecraft.world.World;

public class PumpUtil {
    public static int[] searchFluidSource(World world, int startx, int starty, int startz) {
        int currentFlowDecay = PumpUtil.getFlowDecay(world, startx, starty, startz);
        for (int i = 0; i < 64; ++i) {
            if (PumpUtil.getFlowDecay(world, startx, starty + 1, startz) >= 0) {
                currentFlowDecay = PumpUtil.getFlowDecay(world, startx, ++starty, startz);
                continue;
            }
            if (PumpUtil.getFlowDecay(world, startx + 1, starty + 1, startz) >= 0) {
                currentFlowDecay = PumpUtil.getFlowDecay(world, ++startx, ++starty, startz);
                continue;
            }
            if (PumpUtil.getFlowDecay(world, startx - 1, starty + 1, startz) >= 0) {
                currentFlowDecay = PumpUtil.getFlowDecay(world, --startx, ++starty, startz);
                continue;
            }
            if (PumpUtil.getFlowDecay(world, startx, starty + 1, startz + 1) >= 0) {
                currentFlowDecay = PumpUtil.getFlowDecay(world, startx, ++starty, ++startz);
                continue;
            }
            if (PumpUtil.getFlowDecay(world, startx, starty + 1, startz - 1) >= 0) {
                currentFlowDecay = PumpUtil.getFlowDecay(world, startx, ++starty, --startz);
                continue;
            }
            if (PumpUtil.getFlowDecay(world, startx - 1, starty, startz) < currentFlowDecay && PumpUtil.getFlowDecay(world, startx - 1, starty, startz) != -1) {
                currentFlowDecay = PumpUtil.getFlowDecay(world, --startx, starty, startz);
                continue;
            }
            if (PumpUtil.getFlowDecay(world, startx, starty, startz + 1) < currentFlowDecay && PumpUtil.getFlowDecay(world, startx, starty, startz + 1) != -1) {
                currentFlowDecay = PumpUtil.getFlowDecay(world, startx, starty, ++startz);
                continue;
            }
            if (PumpUtil.getFlowDecay(world, startx, starty, startz - 1) < currentFlowDecay && PumpUtil.getFlowDecay(world, startx, starty, startz - 1) != -1) {
                currentFlowDecay = PumpUtil.getFlowDecay(world, startx, starty, --startz);
                continue;
            }
            if (PumpUtil.getFlowDecay(world, startx + 1, starty, startz) >= currentFlowDecay || PumpUtil.getFlowDecay(world, startx + 1, starty, startz) == -1) break;
            currentFlowDecay = PumpUtil.getFlowDecay(world, ++startx, starty, startz);
        }
        int[][] xyz = new int[65][3];
        for (int i = 0; i < 64; ++i) {
            xyz[i][0] = startx;
            xyz[i][1] = starty;
            xyz[i][2] = startz;
            if (PumpUtil.getFlowDecay(world, startx - 1, starty, startz) != -1 && !PumpUtil.isExistInArray(startx - 1, starty, startz, xyz, i)) {
                if (PumpUtil.getFlowDecay(world, --startx, starty, startz) != 0) continue;
                return new int[]{startx, starty, startz};
            }
            if (PumpUtil.getFlowDecay(world, startx, starty, startz + 1) != -1 && !PumpUtil.isExistInArray(startx, starty, startz + 1, xyz, i)) {
                if (PumpUtil.getFlowDecay(world, startx, starty, ++startz) != 0) continue;
                return new int[]{startx, starty, startz};
            }
            if (PumpUtil.getFlowDecay(world, startx, starty, startz - 1) != -1 && !PumpUtil.isExistInArray(startx, starty, startz - 1, xyz, i)) {
                if (PumpUtil.getFlowDecay(world, startx, starty, --startz) != 0) continue;
                return new int[]{startx, starty, startz};
            }
            if (PumpUtil.getFlowDecay(world, startx + 1, starty, startz) == -1 || PumpUtil.isExistInArray(startx + 1, starty, startz, xyz, i)) break;
            if (PumpUtil.getFlowDecay(world, ++startx, starty, startz) != 0) continue;
            return new int[]{startx, starty, startz};
        }
        for (int ix = 0; ix <= 4; ++ix) {
            for (int iz = 0; iz <= 4; ++iz) {
                int metadata = PumpUtil.getFlowDecay(world, startx + ix - 2, starty, startz + iz - 2);
                if (metadata == 0) {
                    return new int[]{startx + ix - 2, starty, startz + iz - 2};
                }
                if (metadata >= 1 && metadata < 7) {
                    world.setBlockMetadataWithNotify(startx + ix - 2, starty, startz + iz - 2, metadata + 1, 3);
                    continue;
                }
                if (metadata < 7) continue;
                world.setBlockToAir(startx + ix - 2, starty, startz + iz - 2);
            }
        }
        return new int[0];
    }

    protected static int getFlowDecay(World par1World, int par2, int par3, int par4) {
        return par1World.getBlock(par2, par3, par4).getMaterial().isLiquid() ? par1World.getBlockMetadata(par2, par3, par4) : -1;
    }

    protected static boolean isExistInArray(int x, int y, int z, int[][] xyz, int end_i) {
        for (int i = 0; i <= end_i; ++i) {
            if (xyz[i][0] != x || xyz[i][1] != y || xyz[i][2] != z) continue;
            return true;
        }
        return false;
    }
}

