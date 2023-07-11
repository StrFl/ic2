/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.event.world.WorldEvent
 */
package ic2.api.energy.event;

import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.WorldEvent;

public class EnergyTileEvent
extends WorldEvent {
    public final IEnergyTile energyTile;

    public EnergyTileEvent(IEnergyTile energyTile1) {
        super(((TileEntity)energyTile1).getWorldObj());
        if (this.world == null) {
            throw new NullPointerException("world is null");
        }
        this.energyTile = energyTile1;
    }
}

