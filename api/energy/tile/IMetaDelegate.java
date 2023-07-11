/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.api.energy.tile;

import ic2.api.energy.tile.IEnergyTile;
import java.util.List;
import net.minecraft.tileentity.TileEntity;

public interface IMetaDelegate
extends IEnergyTile {
    public List<TileEntity> getSubTiles();
}

