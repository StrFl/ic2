/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.IC2Potion;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemGradualInt;
import ic2.core.item.armor.ItemArmorHazmat;
import java.util.ArrayDeque;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemReactorUranium
extends ItemGradualInt
implements IReactorComponent {
    public final int numberOfCells;

    public ItemReactorUranium(InternalName internalName, int cells) {
        this(internalName, cells, 20000);
    }

    protected ItemReactorUranium(InternalName internalName, int cells, int duration) {
        super(internalName, duration);
        this.setMaxStackSize(64);
        this.numberOfCells = cells;
    }

    @Override
    public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatRun) {
        if (!reactor.produceEnergy()) {
            return;
        }
        int basePulses = 1 + this.numberOfCells / 2;
        for (int iteration = 0; iteration < this.numberOfCells; ++iteration) {
            int dheat;
            int pulses = basePulses;
            if (!heatRun) {
                for (int i = 0; i < pulses; ++i) {
                    this.acceptUraniumPulse(reactor, stack, stack, x, y, x, y, heatRun);
                }
                pulses += ItemReactorUranium.checkPulseable(reactor, x - 1, y, stack, x, y, heatRun) + ItemReactorUranium.checkPulseable(reactor, x + 1, y, stack, x, y, heatRun) + ItemReactorUranium.checkPulseable(reactor, x, y - 1, stack, x, y, heatRun) + ItemReactorUranium.checkPulseable(reactor, x, y + 1, stack, x, y, heatRun);
                continue;
            }
            int heat = ItemReactorUranium.triangularNumber(pulses += ItemReactorUranium.checkPulseable(reactor, x - 1, y, stack, x, y, heatRun) + ItemReactorUranium.checkPulseable(reactor, x + 1, y, stack, x, y, heatRun) + ItemReactorUranium.checkPulseable(reactor, x, y - 1, stack, x, y, heatRun) + ItemReactorUranium.checkPulseable(reactor, x, y + 1, stack, x, y, heatRun)) * 4;
            ArrayDeque<ItemStackCoord> heatAcceptors = new ArrayDeque<ItemStackCoord>();
            this.checkHeatAcceptor(reactor, x - 1, y, heatAcceptors);
            this.checkHeatAcceptor(reactor, x + 1, y, heatAcceptors);
            this.checkHeatAcceptor(reactor, x, y - 1, heatAcceptors);
            this.checkHeatAcceptor(reactor, x, y + 1, heatAcceptors);
            for (heat = this.getFinalHeat(reactor, stack, x, y, heat); !heatAcceptors.isEmpty() && heat > 0; heat += dheat) {
                dheat = heat / heatAcceptors.size();
                heat -= dheat;
                ItemStackCoord acceptor = (ItemStackCoord)heatAcceptors.remove();
                IReactorComponent acceptorComp = (IReactorComponent)acceptor.stack.getItem();
                dheat = acceptorComp.alterHeat(reactor, acceptor.stack, acceptor.x, acceptor.y, dheat);
            }
            if (heat <= 0) continue;
            reactor.addHeat(heat);
        }
        if (this.getCustomDamage(stack) >= this.getMaxCustomDamage(stack) - 1) {
            reactor.setItemAt(x, y, this.getDepletedStack(reactor, stack));
        } else if (heatRun) {
            this.applyCustomDamage(stack, 1, null);
        }
    }

    protected int getFinalHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
        return heat;
    }

    protected ItemStack getDepletedStack(IReactor reactor, ItemStack stack) {
        ItemStack ret;
        switch (this.numberOfCells) {
            case 1: {
                ret = Ic2Items.reactorDepletedUraniumSimple;
                break;
            }
            case 2: {
                ret = Ic2Items.reactorDepletedUraniumDual;
                break;
            }
            case 4: {
                ret = Ic2Items.reactorDepletedUraniumQuad;
                break;
            }
            default: {
                throw new RuntimeException("invalid cell count: " + this.numberOfCells);
            }
        }
        return new ItemStack(ret.getItem(), 1);
    }

    protected static int checkPulseable(IReactor reactor, int x, int y, ItemStack me, int mex, int mey, boolean heatrun) {
        ItemStack other = reactor.getItemAt(x, y);
        if (other != null && other.getItem() instanceof IReactorComponent && ((IReactorComponent)other.getItem()).acceptUraniumPulse(reactor, other, me, x, y, mex, mey, heatrun)) {
            return 1;
        }
        return 0;
    }

    protected static int triangularNumber(int x) {
        return (x * x + x) / 2;
    }

    protected void checkHeatAcceptor(IReactor reactor, int x, int y, Collection<ItemStackCoord> heatAcceptors) {
        ItemStack thing = reactor.getItemAt(x, y);
        if (thing != null && thing.getItem() instanceof IReactorComponent && ((IReactorComponent)thing.getItem()).canStoreHeat(reactor, thing, x, y)) {
            heatAcceptors.add(new ItemStackCoord(thing, x, y));
        }
    }

    @Override
    public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
        if (!heatrun) {
            reactor.addOutput(1.0f);
        }
        return true;
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
        return 2 * this.numberOfCells;
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
        EntityLivingBase entityLiving;
        if (entity instanceof EntityLivingBase && !ItemArmorHazmat.hasCompleteHazmat(entityLiving = (EntityLivingBase)entity)) {
            IC2Potion.radiation.applyTo(entityLiving, 200, 100);
        }
    }

    private static class ItemStackCoord {
        public final ItemStack stack;
        public final int x;
        public final int y;

        public ItemStackCoord(ItemStack stack, int x, int y) {
            this.stack = stack;
            this.x = x;
            this.y = y;
        }
    }
}

