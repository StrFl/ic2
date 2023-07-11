/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import net.minecraft.item.ItemStack;

public class ItemReactorVentSpread
extends ItemIC2
implements IReactorComponent {
    public final int sideVent;

    public ItemReactorVentSpread(InternalName internalName, int sidevent) {
        super(internalName);
        this.setMaxStackSize(1);
        this.sideVent = sidevent;
    }

    @Override
    public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y, boolean heatrun) {
        if (heatrun) {
            this.cool(reactor, x - 1, y);
            this.cool(reactor, x + 1, y);
            this.cool(reactor, x, y - 1);
            this.cool(reactor, x, y + 1);
        }
    }

    private void cool(IReactor reactor, int x, int y) {
        int self;
        IReactorComponent comp;
        ItemStack stack = reactor.getItemAt(x, y);
        if (stack != null && stack.getItem() instanceof IReactorComponent && (comp = (IReactorComponent)stack.getItem()).canStoreHeat(reactor, stack, x, y) && (self = comp.alterHeat(reactor, stack, x, y, -this.sideVent)) <= 0) {
            reactor.addEmitHeat(self + this.sideVent);
        }
    }

    @Override
    public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
        return false;
    }

    @Override
    public boolean canStoreHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
        return false;
    }

    @Override
    public int getMaxHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
        return 0;
    }

    @Override
    public int getCurrentHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
        return 0;
    }

    @Override
    public int alterHeat(IReactor reactor, ItemStack yourStack, int x, int y, int heat) {
        return heat;
    }

    @Override
    public float influenceExplosion(IReactor reactor, ItemStack yourStack) {
        return 0.0f;
    }
}

