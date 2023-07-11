/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Loader
 *  cpw.mods.fml.common.LoaderState
 *  net.minecraft.potion.Potion
 *  net.minecraft.util.DamageSource
 */
package ic2.api.info;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import ic2.api.info.IEnergyValueProvider;
import ic2.api.info.IFuelValueProvider;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class Info {
    public static IEnergyValueProvider itemEnergy;
    public static IFuelValueProvider itemFuel;
    public static Object ic2ModInstance;
    public static DamageSource DMG_ELECTRIC;
    public static DamageSource DMG_NUKE_EXPLOSION;
    public static DamageSource DMG_RADIATION;
    public static Potion POTION_RADIATION;
    private static Boolean ic2Available;

    public static boolean isIc2Available() {
        if (ic2Available != null) {
            return ic2Available;
        }
        boolean loaded = Loader.isModLoaded((String)"IC2");
        if (Loader.instance().hasReachedState(LoaderState.CONSTRUCTING)) {
            ic2Available = loaded;
        }
        return loaded;
    }

    static {
        ic2Available = null;
    }
}

