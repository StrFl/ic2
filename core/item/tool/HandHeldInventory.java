/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class HandHeldInventory
implements IHasGui {
    protected final ItemStack containerStack;
    protected final ItemStack[] inventory;
    protected final EntityPlayer player;
    private boolean cleared;

    public HandHeldInventory(EntityPlayer player, ItemStack itemStack1, int inventorySize) {
        this.containerStack = itemStack1;
        this.inventory = new ItemStack[inventorySize];
        this.player = player;
        if (IC2.platform.isSimulating()) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(itemStack1);
            nbt.setInteger("uid", IC2.random.nextInt());
            NBTTagList contentList = nbt.getTagList("Items", 10);
            for (int i = 0; i < contentList.tagCount(); ++i) {
                NBTTagCompound slotNbt = contentList.getCompoundTagAt(i);
                byte slot = slotNbt.getByte("Slot");
                if (slot < 0 || slot >= this.inventory.length) continue;
                this.inventory[slot] = ItemStack.loadItemStackFromNBT((NBTTagCompound)slotNbt);
            }
        }
    }

    public int getSizeInventory() {
        return this.inventory.length;
    }

    public ItemStack getStackInSlot(int i) {
        return this.inventory[i];
    }

    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack ret;
        if (this.inventory[slot] == null) {
            return null;
        }
        if (this.inventory[slot].stackSize <= amount) {
            ret = this.inventory[slot];
            this.inventory[slot] = null;
        } else {
            ret = this.inventory[slot].splitStack(amount);
            if (this.inventory[slot].stackSize <= 0) {
                this.inventory[slot] = null;
            }
        }
        this.save();
        return ret;
    }

    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.save();
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isItemValidForSlot(int slot, ItemStack itemStack1) {
        return false;
    }

    public void markDirty() {
        this.save();
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return player == this.player && this.getPlayerInventoryIndex() >= -1;
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    public ItemStack getStackInSlotOnClosing(int var1) {
        return null;
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
        this.save();
    }

    public boolean isThisContainer(ItemStack stack) {
        if (stack == null || stack.getItem() != this.containerStack.getItem()) {
            return false;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getInteger("uid") == this.getUid();
    }

    protected int getUid() {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(this.containerStack);
        return nbt.getInteger("uid");
    }

    protected int getPlayerInventoryIndex() {
        for (int i = -1; i < this.player.inventory.getSizeInventory(); ++i) {
            ItemStack stack;
            ItemStack itemStack = stack = i == -1 ? this.player.inventory.getItemStack() : this.player.inventory.getStackInSlot(i);
            if (!this.isThisContainer(stack)) continue;
            return i;
        }
        return Integer.MIN_VALUE;
    }

    protected void save() {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        if (this.cleared) {
            return;
        }
        boolean dropItself = false;
        for (int i = 0; i < this.inventory.length; ++i) {
            if (!this.isThisContainer(this.inventory[i])) continue;
            this.inventory[i] = null;
            dropItself = true;
        }
        NBTTagList contentList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] == null) continue;
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setByte("Slot", (byte)i);
            this.inventory[i].writeToNBT(nbt);
            contentList.appendTag((NBTBase)nbt);
        }
        StackUtil.getOrCreateNbtData(this.containerStack).setTag("Items", (NBTBase)contentList);
        this.containerStack.stackSize = 1;
        if (dropItself) {
            StackUtil.dropAsEntity(this.player.worldObj, (int)this.player.posX, (int)this.player.posY, (int)this.player.posZ, this.containerStack);
            this.clear();
        } else {
            int idx = this.getPlayerInventoryIndex();
            if (idx < -1) {
                IC2.log.warn(LogCategory.Item, "Handheld inventory saving failed for player " + this.player.getDisplayName() + ".");
                this.clear();
            } else if (idx == -1) {
                this.player.inventory.setItemStack(this.containerStack);
            } else {
                this.player.inventory.setInventorySlotContents(idx, this.containerStack);
            }
        }
    }

    private void clear() {
        for (int i = 0; i < this.inventory.length; ++i) {
            this.inventory[i] = null;
        }
        this.cleared = true;
    }
}

