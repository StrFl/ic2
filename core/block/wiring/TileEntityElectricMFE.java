/*
 * Decompiled with CFR 0.152.
 */
package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityElectricBlock;

public class TileEntityElectricMFE
extends TileEntityElectricBlock {
    public TileEntityElectricMFE() {
        super(3, 512, 4000000);
    }

    @Override
    public String getInventoryName() {
        return "MFE";
    }
}

