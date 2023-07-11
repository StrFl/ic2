/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package ic2.core.slot;

import ic2.core.block.invslot.InvSlot;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SlotInvSlotReadOnly
extends SlotInvSlot {
    public SlotInvSlotReadOnly(InvSlot invSlot1, int index1, int xDisplayPosition1, int yDisplayPosition1) {
        super(invSlot1, index1, xDisplayPosition1, yDisplayPosition1);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
    }

    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int par1) {
        return null;
    }
}

