/*
 * Decompiled with CFR 0.152.
 */
package ic2.api.crops;

import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;

public class BaseSeed {
    public final CropCard crop;
    @Deprecated
    public int id;
    public int size;
    public int statGrowth;
    public int statGain;
    public int statResistance;
    public int stackSize;

    public BaseSeed(CropCard crop, int size, int statGrowth, int statGain, int statResistance, int stackSize) {
        this.crop = crop;
        this.id = Crops.instance.getIdFor(crop);
        this.size = size;
        this.statGrowth = statGrowth;
        this.statGain = statGain;
        this.statResistance = statResistance;
        this.stackSize = stackSize;
    }

    @Deprecated
    public BaseSeed(int id, int size, int statGrowth, int statGain, int statResistance, int stackSize) {
        this(BaseSeed.getCropFromId(id), size, statGrowth, statGain, statResistance, stackSize);
    }

    private static CropCard getCropFromId(int id) {
        CropCard[] crops = Crops.instance.getCropList();
        if (id < 0 || id >= crops.length) {
            return null;
        }
        return crops[id];
    }
}

