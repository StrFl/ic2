/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop;

import ic2.api.crops.ICropTile;
import ic2.core.crop.Ic2CropCard;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CropBrownMushroom
extends Ic2CropCard {
    @Override
    public String name() {
        return "brownMushroom";
    }

    @Override
    public int tier() {
        return 2;
    }

    @Override
    public int stat(int n) {
        switch (n) {
            case 0: {
                return 0;
            }
            case 1: {
                return 4;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return 0;
            }
            case 4: {
                return 4;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return new String[]{"Brown", "Food", "Mushroom"};
    }

    @Override
    public int maxSize() {
        return 3;
    }

    @Override
    public int growthDuration(ICropTile crop) {
        return 200;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getSize() < this.maxSize() && crop.getHydrationStorage() > 0;
    }

    @Override
    public int getOptimalHavestSize(ICropTile crop) {
        return 3;
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getSize() >= 3;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        if (crop.getSize() == 3) {
            return new ItemStack((Block)Blocks.brown_mushroom, 1, Short.MAX_VALUE);
        }
        return null;
    }
}

