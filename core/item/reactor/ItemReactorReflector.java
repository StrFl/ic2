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
import ic2.core.item.ItemGradualInt;
import net.minecraft.item.ItemStack;

public class ItemReactorReflector
extends ItemGradualInt
implements IReactorComponent {
    public ItemReactorReflector(InternalName internalName, int maxDamage) {
        super(internalName, maxDamage);
    }

    @Override
    public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y, boolean heatrun) {
    }

    @Override
    public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
        if (!heatrun) {
            IReactorComponent source = (IReactorComponent)pulsingStack.getItem();
            source.acceptUraniumPulse(reactor, pulsingStack, stack, pulseX, pulseY, youX, youY, heatrun);
        } else if (this.getCustomDamage(stack) + 1 >= this.getMaxCustomDamage(stack)) {
            reactor.setItemAt(youX, youY, null);
        } else {
            this.setCustomDamage(stack, this.getCustomDamage(stack) + 1);
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
        return -1.0f;
    }
}

