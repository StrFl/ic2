/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ICrafting
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.core;

import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.slot.SlotHologramSlot;
import ic2.core.slot.SlotInvSlot;
import ic2.core.slot.SlotInvSlotReadOnly;
import ic2.core.util.ReflectionUtil;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class ContainerBase<T extends IInventory>
extends Container {
    protected static final int windowBorder = 8;
    protected static final int slotSize = 16;
    protected static final int slotDistance = 2;
    protected static final int slotSeparator = 4;
    protected static final int hotbarYOffset = -24;
    protected static final int inventoryYOffset = -82;
    public final T base;

    public ContainerBase(T base1) {
        this.base = base1;
    }

    protected void addPlayerInventorySlots(EntityPlayer player, int height) {
        this.addPlayerInventorySlots(player, 178, height);
    }

    protected void addPlayerInventorySlots(EntityPlayer player, int width, int height) {
        int xStart = (width - 162) / 2;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot((IInventory)player.inventory, col + row * 9 + 9, xStart + col * 18, height + -82 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot((IInventory)player.inventory, col, xStart + col * 18, height + -24));
        }
    }

    public final ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex) {
        Slot sourceSlot = (Slot)this.inventorySlots.get(sourceSlotIndex);
        if (sourceSlot != null && sourceSlot.getHasStack()) {
            ItemStack sourceItemStack = sourceSlot.getStack();
            int oldSourceItemStackSize = sourceItemStack.stackSize;
            if (sourceSlot.inventory == player.inventory) {
                block0: for (int run = 0; run < 4 && sourceItemStack.stackSize > 0; ++run) {
                    for (Slot targetSlot : this.inventorySlots) {
                        if (targetSlot.inventory == player.inventory || !ContainerBase.isValidTargetSlot(targetSlot, sourceItemStack, run % 2 == 1, run < 2)) continue;
                        this.transfer(sourceItemStack, targetSlot);
                        if (sourceItemStack.stackSize != 0) continue;
                        continue block0;
                    }
                }
            } else {
                block2: for (int run = 0; run < 2 && sourceItemStack.stackSize > 0; ++run) {
                    ListIterator it = this.inventorySlots.listIterator(this.inventorySlots.size());
                    while (it.hasPrevious()) {
                        Slot targetSlot = (Slot)it.previous();
                        if (targetSlot.inventory != player.inventory || !ContainerBase.isValidTargetSlot(targetSlot, sourceItemStack, run == 1, false)) continue;
                        this.transfer(sourceItemStack, targetSlot);
                        if (sourceItemStack.stackSize != 0) continue;
                        continue block2;
                    }
                }
            }
            if (sourceItemStack.stackSize != oldSourceItemStackSize) {
                if (sourceItemStack.stackSize == 0) {
                    sourceSlot.putStack(null);
                } else {
                    sourceSlot.onPickupFromSlot(player, sourceItemStack);
                }
                if (IC2.platform.isSimulating()) {
                    this.detectAndSendChanges();
                }
            }
        }
        return null;
    }

    private static final boolean isValidTargetSlot(Slot slot, ItemStack stack, boolean allowEmpty, boolean requireInputOnly) {
        if (slot instanceof SlotInvSlotReadOnly || slot instanceof SlotHologramSlot) {
            return false;
        }
        if (!slot.isItemValid(stack)) {
            return false;
        }
        if (!allowEmpty && !slot.getHasStack()) {
            return false;
        }
        if (requireInputOnly) {
            return slot instanceof SlotInvSlot && ((SlotInvSlot)slot).invSlot.canInput();
        }
        return true;
    }

    public boolean canInteractWith(EntityPlayer entityplayer) {
        return this.base.isUseableByPlayer(entityplayer);
    }

    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (this.base instanceof TileEntity) {
            for (String string : this.getNetworkedFields()) {
                for (Object crafter : this.crafters) {
                    if (!(crafter instanceof EntityPlayerMP)) continue;
                    IC2.network.get().updateTileEntityFieldTo((TileEntity)this.base, string, (EntityPlayerMP)crafter);
                }
            }
            if (this.base instanceof TileEntityBlock) {
                for (Map.Entry entry : ((TileEntityBlock)this.base).getNamedComponents()) {
                    for (Object crafter : this.crafters) {
                        if (!(crafter instanceof EntityPlayerMP)) continue;
                        ((TileEntityComponent)entry.getValue()).onContainerUpdate((String)entry.getKey(), (EntityPlayerMP)crafter);
                    }
                }
            }
        }
    }

    public List<String> getNetworkedFields() {
        return new ArrayList<String>();
    }

    public List<ICrafting> getCrafters() {
        return this.crafters;
    }

    public void setField(String fieldName, Object value) {
        ReflectionUtil.setValueRecursive((Object)this, fieldName, value);
        IC2.network.get().sendContainerField(this, fieldName);
    }

    public void onContainerEvent(String event) {
    }

    private void transfer(ItemStack stack, Slot dst) {
        int amount = this.getTransferAmount(stack, dst);
        if (amount <= 0) {
            return;
        }
        ItemStack dstStack = dst.getStack();
        if (dstStack == null) {
            dst.putStack(StackUtil.copyWithSize(stack, amount));
        } else {
            dstStack.stackSize += amount;
        }
        stack.stackSize -= amount;
        dst.onSlotChanged();
    }

    private int getTransferAmount(ItemStack stack, Slot dst) {
        int amount = Math.min(dst.inventory.getInventoryStackLimit(), dst.getSlotStackLimit());
        amount = Math.min(amount, stack.isStackable() ? stack.getMaxStackSize() : 1);
        ItemStack dstStack = dst.getStack();
        if (dstStack != null && !StackUtil.isStackEqualStrict(stack, dstStack)) {
            return 0;
        }
        if (dstStack != null) {
            amount -= dstStack.stackSize;
        }
        amount = Math.min(amount, stack.stackSize);
        return amount;
    }
}

