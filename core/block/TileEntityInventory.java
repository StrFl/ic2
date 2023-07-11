/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 */
package ic2.core.block;

import ic2.core.block.TileEntityBlock;
import ic2.core.block.invslot.InvSlot;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class TileEntityInventory
extends TileEntityBlock
implements ISidedInventory {
    public final List<InvSlot> invSlots = new ArrayList<InvSlot>();

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey("Items")) {
            NBTTagList nbtTagList = nbtTagCompound.getTagList("Items", 10);
            for (int i = 0; i < nbtTagList.tagCount(); ++i) {
                NBTTagCompound nbtTagCompoundSlot = nbtTagList.getCompoundTagAt(i);
                byte slot = nbtTagCompoundSlot.getByte("Slot");
                int maxOldStartIndex = -1;
                InvSlot maxSlot = null;
                for (InvSlot invSlot : this.invSlots) {
                    if (invSlot.oldStartIndex > slot || invSlot.oldStartIndex <= maxOldStartIndex) continue;
                    maxOldStartIndex = invSlot.oldStartIndex;
                    maxSlot = invSlot;
                }
                if (maxSlot == null) continue;
                int index = Math.min(slot - maxOldStartIndex, maxSlot.size() - 1);
                maxSlot.put(index, ItemStack.loadItemStackFromNBT((NBTTagCompound)nbtTagCompoundSlot));
            }
        }
        NBTTagCompound invSlotsTag = nbtTagCompound.getCompoundTag("InvSlots");
        for (InvSlot invSlot : this.invSlots) {
            invSlot.readFromNbt(invSlotsTag.getCompoundTag(invSlot.name));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        NBTTagCompound invSlotsTag = new NBTTagCompound();
        for (InvSlot invSlot : this.invSlots) {
            NBTTagCompound invSlotTag = new NBTTagCompound();
            invSlot.writeToNbt(invSlotTag);
            invSlotsTag.setTag(invSlot.name, (NBTBase)invSlotTag);
        }
        nbtTagCompound.setTag("InvSlots", (NBTBase)invSlotsTag);
    }

    public int getSizeInventory() {
        int ret = 0;
        for (InvSlot invSlot : this.invSlots) {
            ret += invSlot.size();
        }
        return ret;
    }

    public ItemStack getStackInSlot(int index) {
        for (InvSlot invSlot : this.invSlots) {
            if (index < invSlot.size()) {
                return invSlot.get(index);
            }
            index -= invSlot.size();
        }
        return null;
    }

    public ItemStack decrStackSize(int index, int amount) {
        ItemStack stack = this.getStackInSlot(index);
        if (stack == null) {
            return null;
        }
        if (amount >= stack.stackSize) {
            this.setInventorySlotContents(index, null);
            return stack;
        }
        if (amount != 0) {
            if (amount < 0) {
                int space = Math.min(this.getInvSlot(index).getStackSizeLimit(), stack.getMaxStackSize()) - stack.stackSize;
                amount = Math.max(amount, -space);
            }
            stack.stackSize -= amount;
            this.getInvSlot(index).onChanged();
        }
        ItemStack ret = stack.copy();
        ret.stackSize = amount;
        return ret;
    }

    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack ret = this.getStackInSlot(index);
        if (ret != null) {
            this.setInventorySlotContents(index, null);
        }
        return ret;
    }

    public void setInventorySlotContents(int index, ItemStack itemStack) {
        for (InvSlot invSlot : this.invSlots) {
            if (index < invSlot.size()) {
                invSlot.put(index, itemStack);
                break;
            }
            index -= invSlot.size();
        }
    }

    public void markDirty() {
        super.markDirty();
        for (InvSlot invSlot : this.invSlots) {
            invSlot.onChanged();
        }
    }

    public abstract String getInventoryName();

    public boolean hasCustomInventoryName() {
        return false;
    }

    public int getInventoryStackLimit() {
        int max = 0;
        for (InvSlot slot : this.invSlots) {
            max = Math.max(max, slot.getStackSizeLimit());
        }
        return max;
    }

    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return !this.isInvalid() && entityPlayer.getDistance((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    public boolean isItemValidForSlot(int index, ItemStack itemStack) {
        InvSlot invSlot = this.getInvSlot(index);
        return invSlot != null && invSlot.canInput() && invSlot.accepts(itemStack);
    }

    public int[] getAccessibleSlotsFromSide(int var1) {
        int[] ret = new int[this.getSizeInventory()];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = i;
        }
        return ret;
    }

    public boolean canInsertItem(int index, ItemStack itemStack, int side) {
        InvSlot targetSlot = this.getInvSlot(index);
        if (targetSlot == null) {
            return false;
        }
        if (!targetSlot.canInput() || !targetSlot.accepts(itemStack)) {
            return false;
        }
        if (targetSlot.preferredSide != InvSlot.InvSide.ANY && targetSlot.preferredSide.matches(side)) {
            return true;
        }
        for (InvSlot invSlot : this.invSlots) {
            if (invSlot == targetSlot || invSlot.preferredSide == InvSlot.InvSide.ANY || !invSlot.preferredSide.matches(side) || !invSlot.canInput() || !invSlot.accepts(itemStack)) continue;
            return false;
        }
        return true;
    }

    public boolean canExtractItem(int index, ItemStack itemStack, int side) {
        InvSlot targetSlot = this.getInvSlot(index);
        if (targetSlot == null) {
            return false;
        }
        if (!targetSlot.canOutput()) {
            return false;
        }
        boolean correctSide = targetSlot.preferredSide.matches(side);
        if (targetSlot.preferredSide != InvSlot.InvSide.ANY && correctSide) {
            return true;
        }
        for (InvSlot invSlot : this.invSlots) {
            if (invSlot == targetSlot || invSlot.preferredSide == InvSlot.InvSide.ANY && correctSide || !invSlot.preferredSide.matches(side) || !invSlot.canOutput()) continue;
            return false;
        }
        return true;
    }

    public void addInvSlot(InvSlot invSlot) {
        this.invSlots.add(invSlot);
    }

    private InvSlot getInvSlot(int index) {
        for (InvSlot invSlot : this.invSlots) {
            if (index < invSlot.size()) {
                return invSlot;
            }
            index -= invSlot.size();
        }
        return null;
    }
}

