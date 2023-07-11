/*
 * Decompiled with CFR 0.152.
 */
package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityElectricBlock;

public class TileEntityElectricCESU
extends TileEntityElectricBlock {
    public TileEntityElectricCESU() {
        super(2, 128, 300000);
    }

    @Override
    public String getInventoryName() {
        return "CESU";
    }
}

