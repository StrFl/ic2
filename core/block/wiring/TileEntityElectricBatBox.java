/*
 * Decompiled with CFR 0.152.
 */
package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityElectricBlock;

public class TileEntityElectricBatBox
extends TileEntityElectricBlock {
    public TileEntityElectricBatBox() {
        super(1, 32, 40000);
    }

    @Override
    public String getInventoryName() {
        return "BatBox";
    }
}

