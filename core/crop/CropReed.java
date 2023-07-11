/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop;

import ic2.api.crops.ICropTile;
import ic2.core.crop.Ic2CropCard;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CropReed
extends Ic2CropCard {
    @Override
    public String name() {
        return "reed";
    }

    @Override
    public String discoveredBy() {
        return "Notch";
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
                return 0;
            }
            case 2: {
                return 1;
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
        return new String[]{"Reed"};
    }

    @Override
    public int maxSize() {
        return 3;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getSize() < 3;
    }

    @Override
    public int weightInfluences(ICropTile crop, float humidity, float nutrients, float air) {
        return (int)((double)humidity * 1.2 + (double)nutrients + (double)air * 0.8);
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getSize() > 1;
    }

    @Override
    public int getOptimalHavestSize(ICropTile crop) {
        return 3;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return new ItemStack(Items.reeds, crop.getSize() - 1);
    }

    @Override
    public boolean onEntityCollision(ICropTile crop, Entity entity) {
        return false;
    }

    @Override
    public int growthDuration(ICropTile crop) {
        return 200;
    }
}

