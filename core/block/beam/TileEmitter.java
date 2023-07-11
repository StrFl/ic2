/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package ic2.core.block.beam;

import ic2.core.block.beam.EntityParticle;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import net.minecraft.entity.Entity;

public class TileEmitter
extends TileEntityElectricMachine {
    private int progress;

    public TileEmitter() {
        super(5000, 1, -1);
    }

    @Override
    public String getInventoryName() {
        return "Particle Emitter";
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.progress < 100) {
            ++this.progress;
        }
        if (this.progress == 100 && this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord)) {
            this.progress = 0;
            this.worldObj.spawnEntityInWorld((Entity)new EntityParticle(this));
        }
    }
}

