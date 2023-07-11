/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fluids.Fluid
 */
package ic2.core;

import net.minecraftforge.fluids.Fluid;

public class Ic2Fluid
extends Fluid {
    public Ic2Fluid(String fluidName) {
        super(fluidName);
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(6);
    }
}

