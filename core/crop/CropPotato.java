/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop;

import ic2.api.crops.ICropTile;
import ic2.core.crop.Ic2CropCard;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CropPotato
extends Ic2CropCard {
    @Override
    public String name() {
        return "potato";
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
                return 2;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return new String[]{"Yellow", "Food", "Potato"};
    }

    @Override
    public int maxSize() {
        return 4;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getSize() < 4 && crop.getLightLevel() >= 9;
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
        if (crop.getSize() >= 4) {
            return new ItemStack(Items.poisonous_potato);
        }
        if (crop.getSize() == 3) {
            return new ItemStack(Items.potato);
        }
        return null;
    }

    @Override
    public byte getSizeAfterHarvest(ICropTile crop) {
        return 1;
    }
}

