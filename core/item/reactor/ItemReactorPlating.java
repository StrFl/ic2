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

public class ItemReactorPlating
extends ItemIC2
implements IReactorComponent {
    public final int maxHeatAdd;
    public final float effectModifier;

    public ItemReactorPlating(InternalName internalName, int maxheatadd, float effectmodifier) {
        super(internalName);
        this.maxHeatAdd = maxheatadd;
        this.effectModifier = effectmodifier;
    }

    @Override
    public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y, boolean heatrun) {
        if (heatrun) {
            reactor.setMaxHeat(reactor.getMaxHeat() + this.maxHeatAdd);
            reactor.setHeatEffectModifier(reactor.getHeatEffectModifier() * this.effectModifier);
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
        if (this.effectModifier >= 1.0f) {
            return 0.0f;
        }
        return this.effectModifier;
    }
}

