/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.api.energy.tile;

import net.minecraftforge.common.util.ForgeDirection;

public interface IHeatSource {
    public int maxrequestHeatTick(ForgeDirection var1);

    public int requestHeat(ForgeDirection var1, int var2);
}

