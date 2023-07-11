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

public class CropColorFlower
extends Ic2CropCard {
    public String name;
    public String[] attributes;
    public int color;

    public CropColorFlower(String n, String[] a, int c) {
        this.name = n;
        this.attributes = a;
        this.color = c;
    }

    @Override
    public String discoveredBy() {
        if (this.name.equals("dandelion") || this.name.equals("rose")) {
            return "Notch";
        }
        return "Alblaka";
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int tier() {
        return 2;
    }

    @Override
    public int stat(int n) {
        switch (n) {
            case 0: {
                return 1;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return 5;
            }
            case 4: {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return this.attributes;
    }

    @Override
    public int maxSize() {
        return 4;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getSize() <= 3 && crop.getLightLevel() >= 12;
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
        return new ItemStack(Items.dye, 1, this.color);
    }

    @Override
    public byte getSizeAfterHarvest(ICropTile crop) {
        return 3;
    }

    @Override
    public int growthDuration(ICropTile crop) {
        if (crop.getSize() == 3) {
            return 600;
        }
        return 400;
    }
}

