/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.core.init.InternalName;
import ic2.core.item.reactor.ItemReactorHeatStorage;
import net.minecraft.item.ItemStack;

public class ItemReactorVent
extends ItemReactorHeatStorage {
    public final int selfVent;
    public final int reactorVent;

    public ItemReactorVent(InternalName internalName, int heatStorage1, int selfvent, int reactorvent) {
        super(internalName, heatStorage1);
        this.selfVent = selfvent;
        this.reactorVent = reactorvent;
    }

    @Override
    public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y, boolean heatrun) {
        if (heatrun) {
            int self;
            if (this.reactorVent > 0) {
                int rheat = reactor.getHeat();
                int reactorDrain = rheat;
                if (reactorDrain > this.reactorVent) {
                    reactorDrain = this.reactorVent;
                }
                rheat -= reactorDrain;
                if ((reactorDrain = this.alterHeat(reactor, yourStack, x, y, reactorDrain)) > 0) {
                    return;
                }
                reactor.setHeat(rheat);
            }
            if ((self = this.alterHeat(reactor, yourStack, x, y, -this.selfVent)) <= 0) {
                reactor.addEmitHeat(self + this.selfVent);
            }
        }
    }
}

