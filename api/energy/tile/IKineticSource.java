/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.api.energy.tile;

import net.minecraftforge.common.util.ForgeDirection;

public interface IKineticSource {
    public int maxrequestkineticenergyTick(ForgeDirection var1);

    public int requestkineticenergy(ForgeDirection var1, int var2);
}

