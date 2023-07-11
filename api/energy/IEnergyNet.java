/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.api.energy;

import ic2.api.energy.NodeStats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyNet {
    public TileEntity getTileEntity(World var1, int var2, int var3, int var4);

    public TileEntity getNeighbor(TileEntity var1, ForgeDirection var2);

    @Deprecated
    public double getTotalEnergyEmitted(TileEntity var1);

    @Deprecated
    public double getTotalEnergySunken(TileEntity var1);

    public NodeStats getNodeStats(TileEntity var1);

    public double getPowerFromTier(int var1);

    public int getTierFromPower(double var1);
}

