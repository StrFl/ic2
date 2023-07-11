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
import ic2.core.item.reactor.ItemReactorHeatStorage;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;

public class ItemReactorHeatSwitch
extends ItemReactorHeatStorage {
    public final int switchSide;
    public final int switchReactor;

    public ItemReactorHeatSwitch(InternalName internalName, int heatStorage1, int switchside, int switchreactor) {
        super(internalName, heatStorage1);
        this.switchSide = switchside;
        this.switchReactor = switchreactor;
    }

    @Override
    public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y, boolean heatrun) {
        if (heatrun) {
            int myHeat = 0;
            ArrayList<ItemStackCoord> heatAcceptors = new ArrayList<ItemStackCoord>();
            if (this.switchSide > 0) {
                this.checkHeatAcceptor(reactor, x - 1, y, heatAcceptors);
                this.checkHeatAcceptor(reactor, x + 1, y, heatAcceptors);
                this.checkHeatAcceptor(reactor, x, y - 1, heatAcceptors);
                this.checkHeatAcceptor(reactor, x, y + 1, heatAcceptors);
            }
            if (this.switchSide > 0) {
                for (ItemStackCoord stackcoord : heatAcceptors) {
                    IReactorComponent heatable = (IReactorComponent)stackcoord.stack.getItem();
                    double mymed = (double)this.getCurrentHeat(reactor, yourStack, x, y) * 100.0 / (double)this.getMaxHeat(reactor, yourStack, x, y);
                    double heatablemed = (double)heatable.getCurrentHeat(reactor, stackcoord.stack, stackcoord.x, stackcoord.y) * 100.0 / (double)heatable.getMaxHeat(reactor, stackcoord.stack, stackcoord.x, stackcoord.y);
                    int add = (int)((double)heatable.getMaxHeat(reactor, stackcoord.stack, stackcoord.x, stackcoord.y) / 100.0 * (heatablemed + mymed / 2.0));
                    if (add > this.switchSide) {
                        add = this.switchSide;
                    }
                    if (heatablemed + mymed / 2.0 < 1.0) {
                        add = this.switchSide / 2;
                    }
                    if (heatablemed + mymed / 2.0 < 0.75) {
                        add = this.switchSide / 4;
                    }
                    if (heatablemed + mymed / 2.0 < 0.5) {
                        add = this.switchSide / 8;
                    }
                    if (heatablemed + mymed / 2.0 < 0.25) {
                        add = 1;
                    }
                    if ((double)Math.round(heatablemed * 10.0) / 10.0 > (double)Math.round(mymed * 10.0) / 10.0) {
                        add -= 2 * add;
                    } else if ((double)Math.round(heatablemed * 10.0) / 10.0 == (double)Math.round(mymed * 10.0) / 10.0) {
                        add = 0;
                    }
                    myHeat -= add;
                    add = heatable.alterHeat(reactor, stackcoord.stack, stackcoord.x, stackcoord.y, add);
                    myHeat += add;
                }
            }
            if (this.switchReactor > 0) {
                double mymed = (double)this.getCurrentHeat(reactor, yourStack, x, y) * 100.0 / (double)this.getMaxHeat(reactor, yourStack, x, y);
                double Reactormed = (double)reactor.getHeat() * 100.0 / (double)reactor.getMaxHeat();
                int add = (int)Math.round((double)reactor.getMaxHeat() / 100.0 * (Reactormed + mymed / 2.0));
                if (add > this.switchReactor) {
                    add = this.switchReactor;
                }
                if (Reactormed + mymed / 2.0 < 1.0) {
                    add = this.switchSide / 2;
                }
                if (Reactormed + mymed / 2.0 < 0.75) {
                    add = this.switchSide / 4;
                }
                if (Reactormed + mymed / 2.0 < 0.5) {
                    add = this.switchSide / 8;
                }
                if (Reactormed + mymed / 2.0 < 0.25) {
                    add = 1;
                }
                if ((double)Math.round(Reactormed * 10.0) / 10.0 > (double)Math.round(mymed * 10.0) / 10.0) {
                    add -= 2 * add;
                } else if ((double)Math.round(Reactormed * 10.0) / 10.0 == (double)Math.round(mymed * 10.0) / 10.0) {
                    add = 0;
                }
                myHeat -= add;
                reactor.setHeat(reactor.getHeat() + add);
            }
            this.alterHeat(reactor, yourStack, x, y, myHeat);
        }
    }

    private void checkHeatAcceptor(IReactor reactor, int x, int y, ArrayList<ItemStackCoord> heatAcceptors) {
        IReactorComponent comp;
        ItemStack thing = reactor.getItemAt(x, y);
        if (thing != null && thing.getItem() instanceof IReactorComponent && (comp = (IReactorComponent)thing.getItem()).canStoreHeat(reactor, thing, x, y)) {
            heatAcceptors.add(new ItemStackCoord(thing, x, y));
        }
    }

    private class ItemStackCoord {
        public ItemStack stack;
        public int x;
        public int y;

        public ItemStackCoord(ItemStack stack1, int x1, int y1) {
            this.stack = stack1;
            this.x = x1;
            this.y = y1;
        }
    }
}

