/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fluids.Fluid
 */
package ic2.api.recipe;

import ic2.api.recipe.ILiquidAcceptManager;
import java.util.Map;
import net.minecraftforge.fluids.Fluid;

public interface ISemiFluidFuelManager
extends ILiquidAcceptManager {
    public void addFluid(String var1, int var2, double var3);

    public BurnProperty getBurnProperty(Fluid var1);

    public Map<String, BurnProperty> getBurnProperties();

    public static class BurnProperty {
        public final int amount;
        public final double power;

        public BurnProperty(int amount1, double power1) {
            this.amount = amount1;
            this.power = power1;
        }
    }
}

