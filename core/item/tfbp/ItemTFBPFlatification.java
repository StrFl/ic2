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
import ic2.core.util.StackUtil;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemTFBPFlatification
extends ItemTFBP {
    public static Set<Block> removable = new HashSet<Block>();

    public ItemTFBPFlatification(InternalName internalName) {
        super(internalName);
    }

    public static void init() {
        removable.add(Blocks.snow);
        removable.add(Blocks.ice);
        removable.add((Block)Blocks.grass);
        removable.add(Blocks.stone);
        removable.add(Blocks.gravel);
        removable.add((Block)Blocks.sand);
        removable.add(Blocks.dirt);
        removable.add((Block)Blocks.leaves);
        removable.add(Blocks.log);
        removable.add((Block)Blocks.tallgrass);
        removable.add((Block)Blocks.red_flower);
        removable.add((Block)Blocks.yellow_flower);
        removable.add(Blocks.sapling);
        removable.add(Blocks.wheat);
        removable.add(Blocks.red_mushroom_block);
        removable.add((Block)Blocks.brown_mushroom);
        removable.add(Blocks.pumpkin);
        if (Ic2Items.rubberLeaves != null) {
            removable.add(StackUtil.getBlock(Ic2Items.rubberLeaves));
        }
        if (Ic2Items.rubberSapling != null) {
            removable.add(StackUtil.getBlock(Ic2Items.rubberSapling));
        }
        if (Ic2Items.rubberWood != null) {
            removable.add(StackUtil.getBlock(Ic2Items.rubberWood));
        }
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
        int y = TileEntityTerra.getFirstBlockFrom(world, x, z, yCoord + 20);
        if (y == -1) {
            return false;
        }
        if (world.getBlock(x, y, z) == Blocks.snow_layer) {
            --y;
        }
        if (y == yCoord) {
            return false;
        }
        if (y < yCoord) {
            world.setBlock(x, y + 1, z, Blocks.dirt, 0, 7);
            return true;
        }
        if (this.canRemove(world.getBlock(x, y, z))) {
            world.setBlockToAir(x, y, z);
            return true;
        }
        return false;
    }

    public boolean canRemove(Block block) {
        return removable.contains(block);
    }
}

