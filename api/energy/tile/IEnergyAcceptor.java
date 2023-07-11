/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.api.energy.tile;

import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyAcceptor
extends IEnergyTile {
    public boolean acceptsEnergyFrom(TileEntity var1, ForgeDirection var2);
}

