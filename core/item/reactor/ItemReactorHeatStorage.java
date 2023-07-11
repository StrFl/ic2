/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.init.InternalName;
import ic2.core.item.ItemGradualInt;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemReactorHeatStorage
extends ItemGradualInt
implements IReactorComponent {
    public ItemReactorHeatStorage(InternalName internalName, int heatStorage1) {
        super(internalName, heatStorage1);
    }

    @Override
    public void processChamber(IReactor reactor, ItemStack yourStack, int x, int y, boolean heatrun) {
    }

    @Override
    public boolean acceptUraniumPulse(IReactor reactor, ItemStack yourStack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
        return false;
    }

    @Override
    public boolean canStoreHeat(IReactor reactor, ItemStack yourStack, int x, int y) {
        return true;
    }

    @Override
    public int getMaxHeat(IReactor reactor, ItemStack stack, int x, int y) {
        return this.getMaxCustomDamage(stack);
    }

    @Override
    public int getCurrentHeat(IReactor reactor, ItemStack stack, int x, int y) {
        return this.getCustomDamage(stack);
    }

    @Override
    public int alterHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
        int myHeat = this.getCurrentHeat(reactor, stack, x, y);
        int max = this.getMaxCustomDamage(stack);
        if ((myHeat += heat) > max) {
            reactor.setItemAt(x, y, null);
            heat = max - myHeat + 1;
        } else {
            if (myHeat < 0) {
                heat = myHeat;
                myHeat = 0;
            } else {
                heat = 0;
            }
            this.setCustomDamage(stack, myHeat);
        }
        return heat;
    }

    @Override
    public float influenceExplosion(IReactor reactor, ItemStack yourStack) {
        return 0.0f;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean b) {
        super.addInformation(stack, player, info, b);
        if (this.getCustomDamage(stack) > 0) {
            info.add(StatCollector.translateToLocal((String)"ic2.reactoritem.heatwarning.line1"));
            info.add(StatCollector.translateToLocal((String)"ic2.reactoritem.heatwarning.line2"));
        }
    }
}

