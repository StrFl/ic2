/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.reactor.ItemReactorUranium;
import net.minecraft.item.ItemStack;

public class ItemReactorMOX
extends ItemReactorUranium {
    public ItemReactorMOX(InternalName internalName, int cells) {
        super(internalName, cells, 10000);
    }

    @Override
    protected int getFinalHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
        float breedereffectiveness;
        if (reactor.isFluidCooled() && (double)(breedereffectiveness = (float)reactor.getHeat() / (float)reactor.getMaxHeat()) > 0.5) {
            heat *= 2;
        }
        return heat;
    }

    @Override
    protected ItemStack getDepletedStack(IReactor reactor, ItemStack stack) {
        ItemStack ret;
        switch (this.numberOfCells) {
            case 1: {
                ret = Ic2Items.reactorDepletedMOXSimple;
                break;
            }
            case 2: {
                ret = Ic2Items.reactorDepletedMOXDual;
                break;
            }
            case 4: {
                ret = Ic2Items.reactorDepletedMOXQuad;
                break;
            }
            default: {
                throw new RuntimeException("invalid cell count: " + this.numberOfCells);
            }
        }
        return new ItemStack(ret.getItem(), 1);
    }

    @Override
    public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
        if (!heatrun) {
            float breedereffectiveness = (float)reactor.getHeat() / (float)reactor.getMaxHeat();
            float ReaktorOutput = 4.0f * breedereffectiveness + 1.0f;
            reactor.addOutput(ReaktorOutput);
        }
        return true;
    }
}

