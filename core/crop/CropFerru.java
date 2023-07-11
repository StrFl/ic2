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

public class CropFerru
extends Ic2CropCard {
    public ItemStack mDrop;

    @Override
    public String name() {
        return "ferru";
    }

    @Override
    public int tier() {
        return 6;
    }

    @Override
    public int stat(int n) {
        switch (n) {
            case 0: {
                return 2;
            }
            case 1: {
                return 0;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return 1;
            }
            case 4: {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return new String[]{"Gray", "Leaves", "Metal"};
    }

    @Override
    public int maxSize() {
        return 4;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        if (crop.getSize() < 3) {
            return true;
        }
        return crop.getSize() == 3 && (crop.isBlockBelow("oreIron") || crop.isBlockBelow("blockIron"));
    }

    @Override
    public int getrootslength(ICropTile crop) {
        return 5;
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
        if (this.mDrop == null) {
            this.mDrop = Ic2Items.smallIronDust.copy();
        }
        return this.mDrop.copy();
    }

    @Override
    public float dropGainChance() {
        return super.dropGainChance() / 2.0f;
    }

    @Override
    public int growthDuration(ICropTile crop) {
        if (crop.getSize() == 3) {
            return 2000;
        }
        return 800;
    }

    @Override
    public byte getSizeAfterHarvest(ICropTile crop) {
        return 2;
    }
}

