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

public class ItemReactorCondensator
extends ItemGradualInt
implements IReactorComponent {
    public ItemReactorCondensator(InternalName internalName, int maxdmg) {
        super(internalName, maxdmg + 1);
    }

    @Override
    public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatrun) {
    }

    @Override
    public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
        return false;
    }

    @Override
    public boolean canStoreHeat(IReactor reactor, ItemStack stack, int x, int y) {
        return this.getCustomDamage(stack) + 1 < this.getMaxCustomDamage(stack);
    }

    @Override
    public int getMaxHeat(IReactor reactor, ItemStack stack, int x, int y) {
        return this.getMaxCustomDamage(stack);
    }

    @Override
    public int getCurrentHeat(IReactor reactor, ItemStack stack, int x, int y) {
        return 0;
    }

    @Override
    public int alterHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
        if (heat < 0) {
            return heat;
        }
        int can = this.getMaxCustomDamage(stack) - (this.getCustomDamage(stack) + 1);
        if (can > heat) {
            can = heat;
        }
        this.setCustomDamage(stack, this.getCustomDamage(stack) + can);
        return heat -= can;
    }

    @Override
    public float influenceExplosion(IReactor reactor, ItemStack yourStack) {
        return 0.0f;
    }
}

