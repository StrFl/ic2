/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ChunkCoordinates
 */
package ic2.core.crop;

import ic2.api.crops.ICropTile;
import ic2.core.crop.Ic2CropCard;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

public class CropRedWheat
extends Ic2CropCard {
    @Override
    public String name() {
        return "redwheat";
    }

    @Override
    public String discoveredBy() {
        return "raa1337";
    }

    @Override
    public int tier() {
        return 6;
    }

    @Override
    public int stat(int n) {
        switch (n) {
            case 0: {
                return 3;
            }
            case 1: {
                return 0;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return 2;
            }
            case 4: {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return new String[]{"Red", "Redstone", "Wheat"};
    }

    @Override
    public int maxSize() {
        return 7;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getSize() < 7 && crop.getLightLevel() <= 10 && crop.getLightLevel() >= 5;
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getSize() == 7;
    }

    @Override
    public float dropGainChance() {
        return 0.5f;
    }

    @Override
    public int getOptimalHavestSize(ICropTile crop) {
        return 7;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        ChunkCoordinates coords = crop.getLocation();
        if (crop.getWorld().isBlockIndirectlyGettingPowered(coords.posX, coords.posY, coords.posZ) || crop.getWorld().rand.nextBoolean()) {
            return new ItemStack(Items.redstone, 1);
        }
        return new ItemStack(Items.wheat, 1);
    }

    @Override
    public int emitRedstone(ICropTile crop) {
        return crop.getSize() == 7 ? 15 : 0;
    }

    @Override
    public int getEmittedLight(ICropTile crop) {
        return crop.getSize() == 7 ? 7 : 0;
    }

    @Override
    public int growthDuration(ICropTile crop) {
        return 600;
    }

    @Override
    public byte getSizeAfterHarvest(ICropTile crop) {
        return 2;
    }
}

