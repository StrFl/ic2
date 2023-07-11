/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.inventory.InventoryLargeChest
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.world.World
 */
package ic2.core.util;

import ic2.api.Direction;
import ic2.core.IC2;
import ic2.core.block.personal.IPersonalBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public final class StackUtil {
    private static final int[] emptySlotArray = new int[0];

    public static AdjacentInv getAdjacentInventory(TileEntity source, Direction direction) {
        TileEntity target = direction.applyToTileEntity(source);
        if (!(target instanceof IInventory)) {
            return null;
        }
        IInventory inventory = (IInventory)target;
        if (target instanceof TileEntityChest) {
            for (Direction direction2 : Direction.directions) {
                TileEntity target2;
                if (direction2 == Direction.YN || direction2 == Direction.YP || !((target2 = direction2.applyToTileEntity(target)) instanceof TileEntityChest)) continue;
                inventory = new InventoryLargeChest("", inventory, (IInventory)target2);
                break;
            }
        }
        if (target instanceof IPersonalBlock) {
            if (source instanceof IPersonalBlock) {
                if (!((IPersonalBlock)target).permitsAccess(((IPersonalBlock)source).getOwner())) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return new AdjacentInv(inventory, direction);
    }

    public static List<AdjacentInv> getAdjacentInventories(TileEntity source) {
        ArrayList<AdjacentInv> inventories = new ArrayList<AdjacentInv>();
        for (Direction direction : Direction.directions) {
            AdjacentInv inventory = StackUtil.getAdjacentInventory(source, direction);
            if (inventory == null) continue;
            inventories.add(inventory);
        }
        Collections.sort(inventories, new Comparator<AdjacentInv>(){

            @Override
            public int compare(AdjacentInv a, AdjacentInv b) {
                if (a.inv instanceof IPersonalBlock || !(b.inv instanceof IPersonalBlock)) {
                    return -1;
                }
                if (b.inv instanceof IPersonalBlock || !(a.inv instanceof IPersonalBlock)) {
                    return 1;
                }
                return b.inv.getSizeInventory() - a.inv.getSizeInventory();
            }
        });
        return inventories;
    }

    public static int distribute(TileEntity source, ItemStack itemStack, boolean simulate) {
        int transferred = 0;
        for (AdjacentInv inventory : StackUtil.getAdjacentInventories(source)) {
            int amount = StackUtil.putInInventory(inventory.inv, inventory.dir.getInverse(), itemStack, simulate);
            transferred += amount;
            itemStack.stackSize -= amount;
            if (itemStack.stackSize != 0) continue;
            break;
        }
        itemStack.stackSize += transferred;
        return transferred;
    }

    public static ItemStack fetch(TileEntity source, ItemStack itemStack, boolean simulate) {
        ItemStack ret = null;
        int oldStackSize = itemStack.stackSize;
        for (AdjacentInv inventory : StackUtil.getAdjacentInventories(source)) {
            ItemStack transferred = StackUtil.getFromInventory(inventory.inv, inventory.dir.getInverse(), itemStack, itemStack.stackSize, true, simulate);
            if (transferred == null) continue;
            if (ret == null) {
                ret = transferred;
            } else {
                ret.stackSize += transferred.stackSize;
                itemStack.stackSize -= transferred.stackSize;
            }
            if (itemStack.stackSize > 0) continue;
            break;
        }
        itemStack.stackSize = oldStackSize;
        return ret;
    }

    public static int transfer(IInventory src, IInventory dst, Direction dir, int amount) {
        int[] srcSlots = StackUtil.getInventorySlots(src, dir, false, true);
        int[] dstSlots = StackUtil.getInventorySlots(dst, dir.getInverse(), true, false);
        ISidedInventory dstSided = dst instanceof ISidedInventory ? (ISidedInventory)dst : null;
        int dstVanillaSide = dir.getInverse().toSideValue();
        int total = amount;
        block0: for (int srcSlot : srcSlots) {
            ItemStack srcStack = src.getStackInSlot(srcSlot);
            if (srcStack == null) continue;
            int srcTransfer = Math.min(amount, srcStack.stackSize);
            assert (srcTransfer > 0);
            for (int pass = 0; pass < 2; ++pass) {
                for (int i = 0; i < dstSlots.length; ++i) {
                    int transfer;
                    int dstSlot = dstSlots[i];
                    if (dstSlot < 0) continue;
                    ItemStack dstStack = dst.getStackInSlot(dstSlot);
                    if (pass == 0 && (dstStack == null || !StackUtil.isStackEqualStrict(srcStack, dstStack)) || pass == 1 && dstStack != null || !dst.isItemValidForSlot(dstSlot, srcStack) || dstSided != null && !dstSided.canInsertItem(dstSlot, srcStack, dstVanillaSide)) continue;
                    assert (srcTransfer > 0);
                    if (dstStack == null) {
                        transfer = Math.min(srcTransfer, dst.getInventoryStackLimit());
                        dst.setInventorySlotContents(dstSlot, StackUtil.copyWithSize(srcStack, transfer));
                    } else {
                        transfer = Math.min(srcTransfer, Math.min(dstStack.getMaxStackSize(), dst.getInventoryStackLimit()) - dstStack.stackSize);
                        if (transfer <= 0) {
                            dstSlots[i] = -1;
                            continue;
                        }
                        dstStack.stackSize += transfer;
                    }
                    assert (transfer > 0);
                    srcStack.stackSize -= transfer;
                    amount -= transfer;
                    if ((srcTransfer -= transfer) <= 0) {
                        if (srcStack.stackSize <= 0) {
                            src.setInventorySlotContents(srcSlot, null);
                        }
                        if (amount > 0) continue block0;
                        break block0;
                    }
                    assert (srcStack.stackSize > 0);
                    assert (amount > 0);
                }
            }
        }
        amount = total - amount;
        assert (amount >= 0);
        if (amount > 0) {
            src.markDirty();
            dst.markDirty();
        }
        return amount;
    }

    public static void distributeDrop(TileEntity source, List<ItemStack> itemStacks) {
        Iterator<ItemStack> it = itemStacks.iterator();
        while (it.hasNext()) {
            ItemStack itemStack = it.next();
            int amount = StackUtil.distribute(source, itemStack, false);
            if (amount == itemStack.stackSize) {
                it.remove();
                continue;
            }
            itemStack.stackSize -= amount;
        }
        for (ItemStack itemStack : itemStacks) {
            StackUtil.dropAsEntity(source.getWorldObj(), source.xCoord, source.yCoord, source.zCoord, itemStack);
        }
        itemStacks.clear();
    }

    public static ItemStack getFromInventory(IInventory inv, Direction side, ItemStack itemStackDestination, int max, boolean ignoreMaxStackSize, boolean simulate) {
        if (itemStackDestination != null && !ignoreMaxStackSize) {
            max = Math.min(max, itemStackDestination.getMaxStackSize() - itemStackDestination.stackSize);
        }
        ItemStack ret = null;
        for (int i : StackUtil.getInventorySlots(inv, side, false, true)) {
            if (max <= 0) break;
            ItemStack stack = inv.getStackInSlot(i);
            assert (stack != null);
            if (itemStackDestination != null && !StackUtil.isStackEqualStrict(stack, itemStackDestination)) continue;
            if (ret == null) {
                ret = StackUtil.copyWithSize(stack, 0);
                if (itemStackDestination == null) {
                    if (!ignoreMaxStackSize) {
                        max = Math.min(max, ret.getMaxStackSize());
                    }
                    itemStackDestination = ret;
                }
            }
            int transfer = Math.min(max, stack.stackSize);
            if (!simulate) {
                stack.stackSize -= transfer;
                if (stack.stackSize == 0) {
                    inv.setInventorySlotContents(i, null);
                }
            }
            max -= transfer;
            ret.stackSize += transfer;
        }
        if (!simulate && ret != null) {
            inv.markDirty();
        }
        return ret;
    }

    public static int putInInventory(IInventory inv, Direction side, ItemStack itemStackSource, boolean simulate) {
        int transfer;
        ItemStack itemStack;
        int[] slots;
        if (itemStackSource == null) {
            return 0;
        }
        int toTransfer = itemStackSource.stackSize;
        int vanillaSide = side.toSideValue();
        for (int i : slots = StackUtil.getInventorySlots(inv, side, true, false)) {
            if (toTransfer <= 0) break;
            if (!inv.isItemValidForSlot(i, itemStackSource) || inv instanceof ISidedInventory && !((ISidedInventory)inv).canInsertItem(i, itemStackSource, vanillaSide) || (itemStack = inv.getStackInSlot(i)) == null || !StackUtil.isStackEqualStrict(itemStack, itemStackSource)) continue;
            transfer = Math.min(toTransfer, Math.min(inv.getInventoryStackLimit(), itemStack.getMaxStackSize()) - itemStack.stackSize);
            if (!simulate) {
                itemStack.stackSize += transfer;
            }
            toTransfer -= transfer;
        }
        for (int i : slots) {
            if (toTransfer <= 0) break;
            if (!inv.isItemValidForSlot(i, itemStackSource) || inv instanceof ISidedInventory && !((ISidedInventory)inv).canInsertItem(i, itemStackSource, vanillaSide) || (itemStack = inv.getStackInSlot(i)) != null) continue;
            transfer = Math.min(toTransfer, Math.min(inv.getInventoryStackLimit(), itemStackSource.getMaxStackSize()));
            if (!simulate) {
                ItemStack dest = StackUtil.copyWithSize(itemStackSource, transfer);
                inv.setInventorySlotContents(i, dest);
            }
            toTransfer -= transfer;
        }
        if (!simulate && toTransfer != itemStackSource.stackSize) {
            inv.markDirty();
        }
        return itemStackSource.stackSize - toTransfer;
    }

    public static int[] getInventorySlots(IInventory inv, Direction side, boolean checkInsert, boolean checkExtract) {
        int[] ret;
        ISidedInventory sidedInv;
        if (inv.getInventoryStackLimit() <= 0) {
            return emptySlotArray;
        }
        if (inv instanceof ISidedInventory) {
            sidedInv = (ISidedInventory)inv;
            ret = sidedInv.getAccessibleSlotsFromSide(side.toSideValue());
            if (ret.length == 0) {
                return emptySlotArray;
            }
            ret = Arrays.copyOf(ret, ret.length);
        } else {
            int size = inv.getSizeInventory();
            if (size <= 0) {
                return emptySlotArray;
            }
            sidedInv = null;
            ret = new int[size];
            for (int i = 0; i < ret.length; ++i) {
                ret[i] = i;
            }
        }
        if (checkInsert || checkExtract) {
            int writeIdx = 0;
            int vanillaSide = side.toSideValue();
            for (int readIdx = 0; readIdx < ret.length; ++readIdx) {
                int slot = ret[readIdx];
                ItemStack stack = inv.getStackInSlot(slot);
                if (checkExtract && (stack == null || stack.stackSize <= 0 || sidedInv != null && !sidedInv.canExtractItem(slot, stack, vanillaSide)) || checkInsert && stack != null && (stack.stackSize >= stack.getMaxStackSize() || stack.stackSize >= inv.getInventoryStackLimit() || sidedInv != null && !sidedInv.canInsertItem(slot, stack, vanillaSide))) continue;
                ret[writeIdx] = slot;
                ++writeIdx;
            }
            if (writeIdx != ret.length) {
                ret = Arrays.copyOf(ret, writeIdx);
            }
        }
        return ret;
    }

    public static void dropAsEntity(World world, int x, int y, int z, ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }
        double f = 0.7;
        double dx = (double)world.rand.nextFloat() * f + (1.0 - f) * 0.5;
        double dy = (double)world.rand.nextFloat() * f + (1.0 - f) * 0.5;
        double dz = (double)world.rand.nextFloat() * f + (1.0 - f) * 0.5;
        EntityItem entityItem = new EntityItem(world, (double)x + dx, (double)y + dy, (double)z + dz, itemStack.copy());
        entityItem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld((Entity)entityItem);
    }

    public static ItemStack copyWithSize(ItemStack itemStack, int newSize) {
        ItemStack ret = itemStack.copy();
        ret.stackSize = newSize;
        return ret;
    }

    public static ItemStack copyWithWildCard(ItemStack itemStack) {
        ItemStack ret = itemStack.copy();
        Items.dye.setDamage(ret, Short.MAX_VALUE);
        return ret;
    }

    public static NBTTagCompound getOrCreateNbtData(ItemStack itemStack) {
        NBTTagCompound ret = itemStack.getTagCompound();
        if (ret == null) {
            ret = new NBTTagCompound();
            itemStack.setTagCompound(ret);
        }
        return ret;
    }

    public static boolean isStackEqual(ItemStack stack1, ItemStack stack2) {
        return stack1 == null && stack2 == null || stack1 != null && stack2 != null && stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() && !stack1.isItemStackDamageable() || stack1.getItemDamage() == stack2.getItemDamage());
    }

    public static boolean isStackEqualStrict(ItemStack stack1, ItemStack stack2) {
        return StackUtil.isStackEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual((ItemStack)stack1, (ItemStack)stack2);
    }

    public static boolean isTagEqual(ItemStack a, ItemStack b) {
        boolean bEmpty;
        boolean aEmpty = !a.hasTagCompound() || a.getTagCompound().hasNoTags();
        boolean bl = bEmpty = !b.hasTagCompound() || b.getTagCompound().hasNoTags();
        if (aEmpty != bEmpty) {
            return false;
        }
        if (aEmpty) {
            return true;
        }
        return a.getTagCompound().equals((Object)b.getTagCompound());
    }

    public static Block getBlock(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            return ((ItemBlock)item).field_150939_a;
        }
        return null;
    }

    public static boolean equals(Block block, ItemStack stack) {
        return block == StackUtil.getBlock(stack);
    }

    public static boolean damageItemStack(ItemStack itemStack, int amount) {
        if (itemStack.attemptDamageItem(amount, IC2.random)) {
            --itemStack.stackSize;
            itemStack.setItemDamage(0);
            return itemStack.stackSize <= 0;
        }
        return false;
    }

    public static boolean check2(Iterable<List<ItemStack>> list) {
        for (List<ItemStack> list2 : list) {
            if (StackUtil.check(list2)) continue;
            return false;
        }
        return true;
    }

    public static boolean check(ItemStack[] array) {
        return StackUtil.check(Arrays.asList(array));
    }

    public static boolean check(Iterable<ItemStack> list) {
        for (ItemStack stack : list) {
            if (StackUtil.check(stack)) continue;
            return false;
        }
        return true;
    }

    public static boolean check(ItemStack stack) {
        return stack.getItem() != null;
    }

    public static String toStringSafe2(Iterable<List<ItemStack>> list) {
        String ret = "[";
        for (List<ItemStack> list2 : list) {
            if (ret.length() > 1) {
                ret = ret + ", ";
            }
            ret = ret + StackUtil.toStringSafe(list2);
        }
        ret = ret + "]";
        return ret;
    }

    public static String toStringSafe(ItemStack[] array) {
        return StackUtil.toStringSafe(Arrays.asList(array));
    }

    public static String toStringSafe(Iterable<ItemStack> list) {
        String ret = "[";
        for (ItemStack stack : list) {
            if (ret.length() > 1) {
                ret = ret + ", ";
            }
            ret = ret + StackUtil.toStringSafe(stack);
        }
        ret = ret + "]";
        return ret;
    }

    public static String toStringSafe(ItemStack stack) {
        if (stack.getItem() == null) {
            return stack.stackSize + "x(null)@(unknown)";
        }
        return stack.toString();
    }

    public static void consumeInventoryItem(EntityPlayer player, ItemStack itemStack) {
        for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
            if (player.inventory.mainInventory[i] == null || !player.inventory.mainInventory[i].isItemEqual(itemStack)) continue;
            player.inventory.decrStackSize(i, 1);
            return;
        }
    }

    public static boolean storeInventoryItem(ItemStack stack, EntityPlayer player, boolean simulate) {
        if (simulate) {
            for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
                ItemStack invStack = player.inventory.mainInventory[i];
                if (invStack != null && (!StackUtil.isStackEqualStrict(stack, invStack) || invStack.stackSize + stack.stackSize > Math.min(player.inventory.getInventoryStackLimit(), invStack.getMaxStackSize()))) continue;
                return true;
            }
        } else if (player.inventory.addItemStackToInventory(stack)) {
            if (!IC2.platform.isRendering()) {
                player.openContainer.detectAndSendChanges();
            }
            return true;
        }
        return false;
    }

    public static class AdjacentInv {
        public final IInventory inv;
        public final Direction dir;

        private AdjacentInv(IInventory inv, Direction dir) {
            this.inv = inv;
            this.dir = dir;
        }
    }
}

