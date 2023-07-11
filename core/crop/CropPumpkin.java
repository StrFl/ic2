/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop;

import ic2.api.crops.ICropTile;
import ic2.core.IC2;
import ic2.core.crop.Ic2CropCard;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CropPumpkin
extends Ic2CropCard {
    @Override
    public String name() {
        return "pumpkin";
    }

    @Override
    public String discoveredBy() {
        return "Notch";
    }

    @Override
    public int tier() {
        return 1;
    }

    @Override
    public int stat(int n) {
        switch (n) {
            case 0: {
                return 0;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return 3;
            }
            case 4: {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return new String[]{"Orange", "Decoration", "Stem"};
    }

    @Override
    public int maxSize() {
        return 4;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getSize() <= 3;
    }

    @Override
    public int weightInfluences(ICropTile crop, float humidity, float nutrients, float air) {
        return (int)((double)humidity * 1.1 + (double)nutrients * 0.9 + (double)air);
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getSize() == 4;
    }

    @Override
    public int getOptimalHavestSize(ICropTile crop) {
        return 4;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return new ItemStack(Blocks.pumpkin);
    }

    @Override
    public ItemStack getSeeds(ICropTile crop) {
        if (crop.getGain() <= 1 && crop.getGrowth() <= 1 && crop.getResistance() <= 1) {
            return new ItemStack(Items.pumpkin_seeds, IC2.random.nextInt(3) + 1);
        }
        return super.getSeeds(crop);
    }

    @Override
    public int growthDuration(ICropTile crop) {
        if (crop.getSize() == 3) {
            return 600;
        }
        return 200;
    }

    @Override
    public byte getSizeAfterHarvest(ICropTile crop) {
        return 3;
    }
}

