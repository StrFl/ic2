/*
 * Decompiled with CFR 0.152.
 */
package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityElectricBlock;

public class TileEntityElectricMFSU
extends TileEntityElectricBlock {
    public TileEntityElectricMFSU() {
        super(4, 2048, 40000000);
    }

    @Override
    public String getInventoryName() {
        return "MFSU";
    }
}

