/*
 * Decompiled with CFR 0.152.
 */
package ic2.core.block.beam;

import ic2.core.block.machine.tileentity.TileEntityElectricMachine;

public class TileAccelerator
extends TileEntityElectricMachine {
    public TileAccelerator() {
        super(5000, 2, -1);
    }

    @Override
    public String getInventoryName() {
        return "Particle Accelerator";
    }
}

