/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.IPatternStorage;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.machine.container.ContainerPatternStorage;
import ic2.core.block.machine.gui.GuiPatternStorage;
import ic2.core.item.ItemCrystalMemory;
import ic2.core.util.StackUtil;
import ic2.core.uu.UuIndex;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityPatternStorage
extends TileEntityInventory
implements IHasGui,
INetworkClientTileEntityEventListener,
IPatternStorage {
    public final InvSlotConsumableId diskSlot;
    private final List<ItemStack> patterns = new ArrayList<ItemStack>();
    public int index = 0;
    public int maxIndex;
    public ItemStack pattern;
    public double patternUu;
    public double patternEu;

    public TileEntityPatternStorage() {
        this.diskSlot = new InvSlotConsumableId((TileEntityInventory)this, "SaveSlot", 1, InvSlot.Access.IO, 1, InvSlot.InvSide.ANY, Ic2Items.crystalmemory.getItem());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.readContents(nbttagcompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        this.writeContentsAsNbtList(nbttagcompound);
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        ItemStack ret = super.getWrenchDrop(entityPlayer);
        NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(ret);
        this.writeContentsAsNbtList(nbttagcompound);
        return ret;
    }

    public void readContents(NBTTagCompound nbt) {
        NBTTagList patternList = nbt.getTagList("patterns", 10);
        for (int i = 0; i < patternList.tagCount(); ++i) {
            NBTTagCompound contentTag = patternList.getCompoundTagAt(i);
            ItemStack Item2 = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
            this.addPattern(Item2);
        }
        this.refreshInfo();
    }

    private void writeContentsAsNbtList(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (ItemStack stack : this.patterns) {
            NBTTagCompound contentTag = new NBTTagCompound();
            stack.writeToNBT(contentTag);
            list.appendTag((NBTBase)contentTag);
        }
        nbt.setTag("patterns", (NBTBase)list);
    }

    public ContainerBase<TileEntityPatternStorage> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerPatternStorage(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiPatternStorage(new ContainerPatternStorage(entityPlayer, this));
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                if (this.patterns.isEmpty()) break;
                this.index = this.index <= 0 ? this.patterns.size() - 1 : --this.index;
                this.refreshInfo();
                break;
            }
            case 1: {
                if (this.patterns.isEmpty()) break;
                this.index = this.index >= this.patterns.size() - 1 ? 0 : ++this.index;
                this.refreshInfo();
                break;
            }
            case 2: {
                ItemStack crystalMemory;
                if (this.index < 0 || this.index >= this.patterns.size() || this.diskSlot.isEmpty() || !((crystalMemory = this.diskSlot.get()).getItem() instanceof ItemCrystalMemory)) break;
                ((ItemCrystalMemory)crystalMemory.getItem()).writecontentsTag(crystalMemory, this.patterns.get(this.index));
                break;
            }
            case 3: {
                ItemStack record;
                ItemStack crystalMemory;
                if (this.diskSlot.isEmpty() || !((crystalMemory = this.diskSlot.get()).getItem() instanceof ItemCrystalMemory) || (record = ((ItemCrystalMemory)crystalMemory.getItem()).readItemStack(crystalMemory)) == null) break;
                this.addPattern(record);
            }
        }
    }

    public void refreshInfo() {
        if (this.index < 0 || this.index >= this.patterns.size()) {
            this.index = 0;
        }
        this.maxIndex = this.patterns.size();
        if (this.patterns.isEmpty()) {
            this.pattern = null;
        } else {
            this.pattern = this.patterns.get(this.index);
            this.patternUu = UuIndex.instance.getInBuckets(this.pattern);
        }
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "PatternStorage";
    }

    @Override
    public boolean addPattern(ItemStack itemstack) {
        for (ItemStack pattern : this.patterns) {
            if (!StackUtil.isStackEqual(pattern, itemstack)) continue;
            return false;
        }
        this.patterns.add(itemstack);
        this.refreshInfo();
        return true;
    }

    @Override
    public List<ItemStack> getPatterns() {
        return this.patterns;
    }
}

