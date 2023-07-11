/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 */
package ic2.core.block.invslot;

import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ic2.core.migration.ItemMigrate;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InvSlot {
    public final TileEntityInventory base;
    public final String name;
    public final int oldStartIndex;
    private final ItemStack[] contents;
    private ItemStack[] contentsBackup;
    protected final Access access;
    public final InvSide preferredSide;
    private int stackSizeLimit;

    public InvSlot(TileEntityInventory base1, String name1, int oldStartIndex1, Access access1, int count) {
        this(base1, name1, oldStartIndex1, access1, count, InvSide.ANY);
    }

    public InvSlot(TileEntityInventory base1, String name1, int oldStartIndex1, Access access1, int count, InvSide preferredSide1) {
        this.contents = new ItemStack[count];
        this.base = base1;
        this.name = name1;
        this.oldStartIndex = oldStartIndex1;
        this.access = access1;
        this.preferredSide = preferredSide1;
        this.stackSizeLimit = 64;
        base1.addInvSlot(this);
    }

    public InvSlot(int count) {
        this.contents = new ItemStack[count];
        this.base = null;
        this.name = null;
        this.oldStartIndex = -1;
        this.access = Access.NONE;
        this.preferredSide = InvSide.ANY;
    }

    public void readFromNbt(NBTTagCompound nbtTagCompound) {
        NBTTagList contentsTag = nbtTagCompound.getTagList("Contents", 10);
        for (int i = 0; i < contentsTag.tagCount(); ++i) {
            NBTTagCompound contentTag = contentsTag.getCompoundTagAt(i);
            int index = contentTag.getByte("Index") & 0xFF;
            if (index >= this.size()) {
                IC2.log.error(LogCategory.Block, "Can't load item stack for %s, slot %s, index %d is out of bounds.", Util.asString(this.base), this.name, index);
                continue;
            }
            ItemStack itemStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
            if (itemStack == null) {
                IC2.log.warn(LogCategory.Block, "Can't load item stack for %s, slot %s, index %d, no matching item for %d:%d.", Util.asString(this.base), this.name, index, contentTag.getShort("id"), contentTag.getShort("Damage"));
                continue;
            }
            Item item = itemStack.getItem();
            if (item instanceof ItemMigrate) {
                item.onUpdate(itemStack, null, null, 0, false);
            }
            if (this.get(index) != null) {
                IC2.log.error(LogCategory.Block, "Loading content to non-empty slot for %s, slot %s, index %d, replacing %s with %s.", Util.asString(this.base), this.name, index, this.get(index), itemStack);
            }
            this.put(index, itemStack);
        }
    }

    public void writeToNbt(NBTTagCompound nbtTagCompound) {
        NBTTagList contentsTag = new NBTTagList();
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] == null) continue;
            NBTTagCompound contentTag = new NBTTagCompound();
            contentTag.setByte("Index", (byte)i);
            this.contents[i].writeToNBT(contentTag);
            contentsTag.appendTag((NBTBase)contentTag);
        }
        nbtTagCompound.setTag("Contents", (NBTBase)contentsTag);
    }

    public int size() {
        return this.contents.length;
    }

    public ItemStack get() {
        return this.get(0);
    }

    public ItemStack get(int index) {
        return this.contents[index];
    }

    public void put(ItemStack content) {
        this.put(0, content);
    }

    public void put(int index, ItemStack content) {
        this.contents[index] = content;
        this.onChanged();
    }

    public void clear() {
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = null;
        }
    }

    public void onChanged() {
    }

    public boolean accepts(ItemStack itemStack) {
        return true;
    }

    public boolean canInput() {
        return this.access == Access.I || this.access == Access.IO;
    }

    public boolean canOutput() {
        return this.access == Access.O || this.access == Access.IO;
    }

    public boolean isEmpty() {
        for (ItemStack itemStack : this.contents) {
            if (itemStack == null) continue;
            return false;
        }
        return true;
    }

    public void organize() {
        block0: for (int dstIndex = 0; dstIndex < this.contents.length - 1; ++dstIndex) {
            ItemStack dst = this.contents[dstIndex];
            if (dst != null && dst.stackSize >= dst.getMaxStackSize()) continue;
            for (int srcIndex = dstIndex + 1; srcIndex < this.contents.length; ++srcIndex) {
                ItemStack src = this.contents[srcIndex];
                if (src == null) continue;
                if (dst == null) {
                    this.contents[srcIndex] = null;
                    this.contents[dstIndex] = dst = src;
                    continue;
                }
                if (!StackUtil.isStackEqualStrict(dst, src)) continue;
                int space = Math.min(this.getStackSizeLimit(), dst.getMaxStackSize() - dst.stackSize);
                if (src.stackSize <= space) {
                    this.contents[srcIndex] = null;
                    dst.stackSize += src.stackSize;
                    continue;
                }
                src.stackSize -= space;
                dst.stackSize += space;
                continue block0;
            }
        }
    }

    public int getStackSizeLimit() {
        return this.stackSizeLimit;
    }

    public void setStackSizeLimit(int stackSizeLimit) {
        this.stackSizeLimit = stackSizeLimit;
    }

    public String toString() {
        String ret = this.name + "[" + this.contents.length + "]: ";
        for (int i = 0; i < this.contents.length; ++i) {
            ret = ret + this.contents[i];
            if (i >= this.contents.length - 1) continue;
            ret = ret + ", ";
        }
        return ret;
    }

    protected void backup() {
        if (this.contentsBackup == null) {
            this.contentsBackup = new ItemStack[this.contents.length];
        }
        for (int i = 0; i < this.contents.length; ++i) {
            this.contentsBackup[i] = this.contents[i] == null ? null : this.contents[i].copy();
        }
    }

    protected void restore() {
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = this.contentsBackup[i];
        }
    }

    public static enum InvSide {
        ANY,
        TOP,
        BOTTOM,
        SIDE,
        NOTSIDE;


        public boolean matches(int side) {
            return this == ANY || side == 0 && this == BOTTOM || side == 1 && this == TOP || side >= 0 && side <= 1 && this == NOTSIDE || side >= 2 && side <= 5 && this == SIDE;
        }
    }

    public static enum Access {
        NONE,
        I,
        O,
        IO;

    }
}

