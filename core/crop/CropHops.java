/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop;

import ic2.api.crops.ICropTile;
import ic2.core.Ic2Items;
import ic2.core.crop.Ic2CropCard;
import net.minecraft.item.ItemStack;

public class CropHops
extends Ic2CropCard {
    @Override
    public String name() {
        return "hops";
    }

    @Override
    public int tier() {
        return 5;
    }

    @Override
    public int stat(int n) {
        switch (n) {
            case 0: {
                return 2;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return 1;
            }
            case 4: {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return new String[]{"Green", "Ingredient", "Wheat"};
    }

    @Override
    public int maxSize() {
        return 7;
    }

    @Override
    public int growthDuration(ICropTile crop) {
        return 600;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getSize() < 7 && crop.getLightLevel() >= 9;
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getSize() == 7;
    }

    @Override
    public int getOptimalHavestSize(ICropTile crop) {
        return 7;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return new ItemStack(Ic2Items.hops.getItem(), 1);
    }

    @Override
    public byte getSizeAfterHarvest(ICropTile crop) {
        return 3;
    }
}

