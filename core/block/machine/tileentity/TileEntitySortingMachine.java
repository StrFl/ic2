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
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerSortingMachine;
import ic2.core.block.machine.gui.GuiSortingMachine;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntitySortingMachine
extends TileEntityElectricMachine
implements IHasGui,
INetworkClientTileEntityEventListener,
IUpgradableBlock {
    public final int defaultTier;
    public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, "upgrade", 1, 3);
    public final InvSlot buffer = new InvSlot(this, "Buffer", 4, InvSlot.Access.I, 11);
    private final ItemStack[][] filters = new ItemStack[6][];
    private int amount = 0;
    public int defaultRoute = 0;

    public TileEntitySortingMachine() {
        super(100000, 2, 1, false);
        this.defaultTier = 1;
        for (int i = 0; i < this.filters.length; ++i) {
            this.filters[i] = new ItemStack[7];
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList filtersTag = nbt.getTagList("filters", 10);
        for (int i = 0; i < filtersTag.tagCount(); ++i) {
            ItemStack stack;
            NBTTagCompound filterTag = filtersTag.getCompoundTagAt(i);
            int index = filterTag.getByte("index") & 0xFF;
            this.filters[index / 7][index % 7] = stack = ItemStack.loadItemStackFromNBT((NBTTagCompound)filterTag);
        }
        this.defaultRoute = nbt.getInteger("defaultroute");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList filtersTag = new NBTTagList();
        for (int i = 0; i < 42; ++i) {
            ItemStack stack = this.filters[i / 7][i % 7];
            if (stack == null) continue;
            NBTTagCompound contentTag = new NBTTagCompound();
            contentTag.setByte("index", (byte)i);
            stack.writeToNBT(contentTag);
            filtersTag.appendTag((NBTBase)contentTag);
        }
        nbt.setTag("filters", (NBTBase)filtersTag);
        nbt.setInteger("defaultroute", this.defaultRoute);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        block0: for (int index = 0; index < this.buffer.size(); ++index) {
            if (this.energy < 20.0) {
                return;
            }
            ItemStack stack = this.buffer.get(index);
            if (stack == null) continue;
            if (stack.stackSize <= 0) {
                this.buffer.put(index, null);
                continue;
            }
            block1: for (StackUtil.AdjacentInv inv : StackUtil.getAdjacentInventories(this)) {
                if (inv.dir.toSideValue() != this.defaultRoute) {
                    for (ItemStack filterStack : this.getFilterSlots(inv.dir.toForgeDirection())) {
                        if (filterStack == null || !StackUtil.isStackEqualStrict(filterStack, stack)) continue;
                        this.amount = StackUtil.putInInventory(inv.inv, inv.dir, StackUtil.copyWithSize(stack, 1), false);
                        if (this.amount <= 0) continue block1;
                        stack.stackSize -= this.amount;
                        this.energy -= 20.0;
                        if (stack.stackSize > 0) continue block1;
                        this.buffer.put(index, null);
                        continue block0;
                    }
                    continue;
                }
                boolean inFilter = false;
                ItemStack[][] itemStackArray = this.filters;
                int n = itemStackArray.length;
                for (int i = 0; i < n; ++i) {
                    ItemStack[] sideFilters;
                    for (ItemStack filter : sideFilters = itemStackArray[i]) {
                        if (!StackUtil.isStackEqualStrict(filter, stack)) continue;
                        inFilter = true;
                    }
                }
                if (inFilter) continue;
                this.amount = StackUtil.putInInventory(inv.inv, inv.dir, StackUtil.copyWithSize(stack, 1), false);
                if (this.amount <= 0) continue block0;
                stack.stackSize -= this.amount;
                this.energy -= 20.0;
                if (stack.stackSize > 0) continue block0;
                this.buffer.put(index, null);
                continue block0;
            }
        }
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (event >= 0 && event <= 5) {
            this.defaultRoute = event;
        }
    }

    @Override
    public String getInventoryName() {
        return "SortingMachine";
    }

    public ContainerBase<TileEntitySortingMachine> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerSortingMachine(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiSortingMachine(new ContainerSortingMachine(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Transformer);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.setUpgradableBlock();
        }
    }

    public void setUpgradableBlock() {
        int extraTier = 0;
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem)) continue;
            IUpgradeItem upgrade = (IUpgradeItem)stack.getItem();
            extraTier += upgrade.getExtraTier(stack, this) * stack.stackSize;
        }
        this.setTier(TileEntitySortingMachine.applyModifier(this.defaultTier, extraTier, 1.0));
    }

    private static int applyModifier(int base, int extra, double multiplier) {
        double ret = Math.round(((double)base + (double)extra) * multiplier);
        return ret > 2.147483647E9 ? Integer.MAX_VALUE : (int)ret;
    }

    @Override
    public double getEnergy() {
        return this.energy;
    }

    @Override
    public boolean useEnergy(double amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
            return true;
        }
        return false;
    }

    public ItemStack[] getFilterSlots(ForgeDirection side) {
        return this.filters[side.ordinal()];
    }
}

