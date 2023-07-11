/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import java.util.ArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InvSlotUpgrade
extends InvSlot {
    public int augmentation;
    public int extraProcessTime;
    public double processTimeMultiplier;
    public int extraEnergyDemand;
    public double energyDemandMultiplier;
    public int extraEnergyStorage;
    public double energyStorageMultiplier;
    public int extraTier;

    public InvSlotUpgrade(TileEntityInventory base1, String name1, int oldStartIndex1, int count) {
        super(base1, name1, oldStartIndex1, InvSlot.Access.NONE, count);
        if (!(base1 instanceof IUpgradableBlock)) {
            throw new IllegalArgumentException("Base needs to be an IUpgradableBlock.");
        }
        this.resetRates();
    }

    @Override
    public boolean accepts(ItemStack stack) {
        Item rawItem = stack.getItem();
        if (!(rawItem instanceof IUpgradeItem)) {
            return false;
        }
        IUpgradeItem item = (IUpgradeItem)rawItem;
        return item.isSuitableFor(stack, ((IUpgradableBlock)((Object)this.base)).getUpgradableProperties());
    }

    @Override
    public void onChanged() {
        this.resetRates();
        final IUpgradableBlock block = (IUpgradableBlock)((Object)this.base);
        ArrayList<1> redstoneModifiers = null;
        for (int i = 0; i < this.size(); ++i) {
            final ItemStack stack = this.get(i);
            if (stack == null || !this.accepts(stack)) continue;
            final IUpgradeItem upgrade = (IUpgradeItem)stack.getItem();
            this.augmentation += upgrade.getAugmentation(stack, block) * stack.stackSize;
            this.extraProcessTime += upgrade.getExtraProcessTime(stack, block) * stack.stackSize;
            this.processTimeMultiplier *= Math.pow(upgrade.getProcessTimeMultiplier(stack, block), stack.stackSize);
            this.extraEnergyDemand += upgrade.getExtraEnergyDemand(stack, block) * stack.stackSize;
            this.energyDemandMultiplier *= Math.pow(upgrade.getEnergyDemandMultiplier(stack, block), stack.stackSize);
            this.extraEnergyStorage += upgrade.getExtraEnergyStorage(stack, block) * stack.stackSize;
            this.energyStorageMultiplier *= Math.pow(upgrade.getEnergyStorageMultiplier(stack, block), stack.stackSize);
            this.extraTier += upgrade.getExtraTier(stack, block) * stack.stackSize;
            if (!upgrade.modifiesRedstoneInput(stack, block)) continue;
            if (redstoneModifiers == null) {
                redstoneModifiers = new ArrayList<1>(this.size());
            }
            redstoneModifiers.add(new Redstone.IRedstoneModifier(){

                @Override
                public int getRedstoneInput(int redstoneInput) {
                    return upgrade.getRedstoneInput(stack, block, redstoneInput);
                }
            });
        }
        for (TileEntityComponent component : this.base.getComponents()) {
            if (!(component instanceof Redstone)) continue;
            ((Redstone)component).setRedstoneModifier(redstoneModifiers);
        }
    }

    private void resetRates() {
        this.augmentation = 0;
        this.extraProcessTime = 0;
        this.processTimeMultiplier = 1.0;
        this.extraEnergyDemand = 0;
        this.energyDemandMultiplier = 1.0;
        this.extraEnergyStorage = 0;
        this.energyStorageMultiplier = 1.0;
        this.extraTier = 0;
    }
}

