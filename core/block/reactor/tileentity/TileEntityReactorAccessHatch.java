/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.core.block.reactor.tileentity;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.api.tile.IWrenchable;
import ic2.core.Ic2Items;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileEntityReactorAccessHatch
extends TileEntity
implements IWrenchable,
ISidedInventory {
    public final boolean canUpdate() {
        return false;
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return false;
    }

    @Override
    public short getFacing() {
        return 0;
    }

    @Override
    public void setFacing(short facing) {
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public float getWrenchDropRate() {
        return 0.8f;
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        return Ic2Items.reactorAccessHatch.copy();
    }

    public int getSizeInventory() {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return 0;
        }
        return reactor.getSizeInventory();
    }

    public ItemStack getStackInSlot(int i) {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.getStackInSlot(i);
    }

    public ItemStack decrStackSize(int i, int j) {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.decrStackSize(i, j);
    }

    public void setInventorySlotContents(int i, ItemStack itemstack) {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return;
        }
        reactor.setInventorySlotContents(i, itemstack);
    }

    public String getInventoryName() {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return "Nuclear Reactor";
        }
        return reactor.getInventoryName();
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public int getInventoryStackLimit() {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return 64;
        }
        return reactor.getInventoryStackLimit();
    }

    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return false;
        }
        return reactor.isUseableByPlayer(entityplayer);
    }

    public void openInventory() {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return;
        }
        reactor.openInventory();
    }

    public void closeInventory() {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return;
        }
        reactor.closeInventory();
    }

    public ItemStack getStackInSlotOnClosing(int var1) {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.getStackInSlotOnClosing(var1);
    }

    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        IInventory reactor = this.getReactor();
        if (reactor == null) {
            return false;
        }
        return reactor.isItemValidForSlot(i, itemstack);
    }

    public IInventory getReactor() {
        for (int xoffset = -1; xoffset < 2; ++xoffset) {
            for (int yoffset = -1; yoffset < 2; ++yoffset) {
                for (int zoffset = -1; zoffset < 2; ++zoffset) {
                    TileEntity te = this.worldObj.getTileEntity(this.xCoord + xoffset, this.yCoord + yoffset, this.zCoord + zoffset);
                    if (!(te instanceof IReactorChamber) && !(te instanceof IReactor)) continue;
                    return (IInventory)te;
                }
            }
        }
        Block blk = this.getBlockType();
        if (blk != null) {
            blk.onNeighborBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, blk);
        }
        return null;
    }

    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        IInventory inv = this.getReactor();
        if (inv instanceof ISidedInventory) {
            return ((ISidedInventory)inv).getAccessibleSlotsFromSide(p_94128_1_);
        }
        int[] accessibleSlots = new int[this.getSizeInventory()];
        for (int i = 0; i < accessibleSlots.length; ++i) {
            accessibleSlots[i] = i;
        }
        return accessibleSlots;
    }

    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        IInventory reactor = this.getReactor();
        if (reactor instanceof ISidedInventory) {
            return ((ISidedInventory)reactor).canInsertItem(p_102007_1_, p_102007_2_, p_102007_3_);
        }
        return reactor.isItemValidForSlot(p_102007_1_, p_102007_2_);
    }

    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
        IInventory reactor = this.getReactor();
        if (reactor instanceof ISidedInventory) {
            return ((ISidedInventory)reactor).canExtractItem(p_102008_1_, p_102008_2_, p_102008_3_);
        }
        return true;
    }
}

