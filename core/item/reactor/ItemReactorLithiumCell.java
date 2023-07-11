/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemGradual;
import net.minecraft.item.ItemStack;

public class ItemReactorLithiumCell
extends ItemGradual
implements IReactorComponent {
    public ItemReactorLithiumCell(InternalName internalName) {
        super(internalName);
    }

    @Override
    public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y, boolean heatrun) {
    }

    @Override
    public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
        if (heatrun) {
            int myLevel = yourStack.getItemDamage() + 1 + reactor.getHeat() / 3000;
            if (myLevel >= this.getMaxDamage()) {
                reactor.setItemAt(youX, youY, new ItemStack(Ic2Items.TritiumCell.getItem()));
            } else {
                yourStack.setItemDamage(myLevel);
            }
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
        return 0.0f;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - (double)stack.getItemDamageForDisplay() / (double)stack.getMaxDamage();
    }
}

