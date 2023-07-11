/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.api.energy.tile;

import ic2.api.energy.tile.IEnergyAcceptor;
import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergySink
extends IEnergyAcceptor {
    public double getDemandedEnergy();

    public int getSinkTier();

    public double injectEnergy(ForgeDirection var1, double var2, double var4);
}

