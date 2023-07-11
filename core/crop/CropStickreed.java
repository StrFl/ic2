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
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.crop.Ic2CropCard;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CropStickreed
extends Ic2CropCard {
    @Override
    public String name() {
        return "stickreed";
    }

    @Override
    public String discoveredBy() {
        return "raa1337";
    }

    @Override
    public int tier() {
        return 4;
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
                return 1;
            }
            case 3: {
                return 0;
            }
            case 4: {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return new String[]{"Reed", "Resin"};
    }

    @Override
    public int maxSize() {
        return 4;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getSize() < 4;
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
        return 4;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        if (crop.getSize() <= 3) {
            return new ItemStack(Items.reeds, crop.getSize() - 1);
        }
        return new ItemStack(Ic2Items.resin.getItem());
    }

    @Override
    public byte getSizeAfterHarvest(ICropTile crop) {
        if (crop.getSize() == 4) {
            return (byte)(3 - IC2.random.nextInt(3));
        }
        return 1;
    }

    @Override
    public boolean onEntityCollision(ICropTile crop, Entity entity) {
        return false;
    }

    @Override
    public int growthDuration(ICropTile crop) {
        if (crop.getSize() == 4) {
            return 400;
        }
        return 100;
    }
}

