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
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.IPatternStorage;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotScannable;
import ic2.core.block.machine.container.ContainerScanner;
import ic2.core.block.machine.gui.GuiScanner;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.item.ItemCrystalMemory;
import ic2.core.util.StackUtil;
import ic2.core.uu.UuGraph;
import ic2.core.uu.UuIndex;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityScanner
extends TileEntityElectricMachine
implements IHasGui,
INetworkClientTileEntityEventListener {
    private ItemStack currentStack = null;
    private ItemStack pattern = null;
    private final int energyusecycle = 256;
    public int progress = 0;
    public final int duration = 3300;
    public final InvSlotConsumable inputSlot;
    public final InvSlot diskSlot;
    private State state = State.IDLE;
    public double patternUu;
    public double patternEu;

    public TileEntityScanner() {
        super(512000, 3, 0);
        this.inputSlot = new InvSlotScannable(this, "input", 0, 1);
        this.diskSlot = new InvSlotConsumableId((TileEntityInventory)this, "disk", 0, InvSlot.Access.IO, 1, InvSlot.InvSide.ANY, Ic2Items.crystalmemory.getItem());
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean newActive = false;
        if (this.progress < 3300) {
            if (this.inputSlot.isEmpty() || this.currentStack != null && !StackUtil.isStackEqual(this.currentStack, this.inputSlot.get())) {
                this.state = State.IDLE;
                this.reset();
            } else if (this.getPatternStorage() == null && this.diskSlot.isEmpty()) {
                this.state = State.NO_STORAGE;
                this.reset();
            } else if (this.energy >= 256.0) {
                if (this.currentStack == null) {
                    this.currentStack = StackUtil.copyWithSize(this.inputSlot.get(), 1);
                }
                this.pattern = UuGraph.find(this.currentStack);
                if (this.pattern == null) {
                    this.state = State.FAILED;
                } else if (this.isPatternRecorded(this.pattern)) {
                    this.state = State.ALREADY_RECORDED;
                    this.reset();
                } else {
                    newActive = true;
                    this.state = State.SCANNING;
                    this.energy -= 256.0;
                    ++this.progress;
                    if (this.progress >= 3300) {
                        this.refreshInfo();
                        if (this.patternUu != Double.POSITIVE_INFINITY) {
                            this.state = State.COMPLETED;
                            this.inputSlot.consume(1, false, true);
                            this.markDirty();
                        } else {
                            this.state = State.FAILED;
                        }
                    }
                }
            } else {
                this.state = State.NO_ENERGY;
            }
        } else if (this.pattern == null) {
            this.state = State.IDLE;
            this.progress = 0;
        }
        this.setActive(newActive);
    }

    public void reset() {
        this.progress = 0;
        this.currentStack = null;
        this.pattern = null;
    }

    private boolean isPatternRecorded(ItemStack stack) {
        ItemStack crystalMemory;
        if (!this.diskSlot.isEmpty() && this.diskSlot.get().getItem() instanceof ItemCrystalMemory && StackUtil.isStackEqual(((ItemCrystalMemory)(crystalMemory = this.diskSlot.get()).getItem()).readItemStack(crystalMemory), stack)) {
            return true;
        }
        IPatternStorage storage = this.getPatternStorage();
        if (storage == null) {
            return false;
        }
        for (ItemStack stored : storage.getPatterns()) {
            if (!StackUtil.isStackEqual(stored, stack)) continue;
            return true;
        }
        return false;
    }

    private void record() {
        if (this.pattern == null || this.patternUu == Double.POSITIVE_INFINITY) {
            this.reset();
            return;
        }
        if (!this.savetoDisk(this.pattern)) {
            IPatternStorage storage = this.getPatternStorage();
            if (storage != null) {
                if (!storage.addPattern(this.pattern)) {
                    this.state = State.TRANSFER_ERROR;
                    return;
                }
            } else {
                this.state = State.TRANSFER_ERROR;
                return;
            }
        }
        this.reset();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.progress = nbttagcompound.getInteger("progress");
        NBTTagCompound contentTag = nbttagcompound.getCompoundTag("currentStack");
        this.currentStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
        contentTag = nbttagcompound.getCompoundTag("pattern");
        this.pattern = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
        int stateIdx = nbttagcompound.getInteger("state");
        this.state = stateIdx < State.values().length ? State.values()[stateIdx] : State.IDLE;
        this.refreshInfo();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        NBTTagCompound contentTag;
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("progress", this.progress);
        if (this.currentStack != null) {
            contentTag = new NBTTagCompound();
            this.currentStack.writeToNBT(contentTag);
            nbttagcompound.setTag("currentStack", (NBTBase)contentTag);
        }
        if (this.pattern != null) {
            contentTag = new NBTTagCompound();
            this.pattern.writeToNBT(contentTag);
            nbttagcompound.setTag("pattern", (NBTBase)contentTag);
        }
        nbttagcompound.setInteger("state", this.state.ordinal());
    }

    public ContainerBase<TileEntityScanner> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerScanner(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiScanner(new ContainerScanner(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "scanner";
    }

    public IPatternStorage getPatternStorage() {
        for (Direction direction : Direction.directions) {
            TileEntity target = direction.applyToTileEntity(this);
            if (!(target instanceof IPatternStorage)) continue;
            return (IPatternStorage)target;
        }
        return null;
    }

    public boolean savetoDisk(ItemStack stack) {
        if (this.diskSlot.isEmpty() || stack == null) {
            return false;
        }
        if (this.diskSlot.get().getItem() instanceof ItemCrystalMemory) {
            ItemStack crystalMemory = this.diskSlot.get();
            ((ItemCrystalMemory)crystalMemory.getItem()).writecontentsTag(crystalMemory, stack);
            return true;
        }
        return false;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                this.reset();
                break;
            }
            case 1: {
                if (this.progress < 3300) break;
                this.record();
            }
        }
    }

    private void refreshInfo() {
        if (this.pattern != null) {
            this.patternUu = UuIndex.instance.getInBuckets(this.pattern);
        }
    }

    public int getPercentageDone() {
        return 100 * this.progress / 3300;
    }

    public int getSubPercentageDoneScaled(int width) {
        return width * (100 * this.progress % 3300) / 3300;
    }

    public boolean isDone() {
        return this.progress >= 3300;
    }

    public State getState() {
        return this.state;
    }

    public static enum State {
        IDLE,
        SCANNING,
        COMPLETED,
        FAILED,
        NO_STORAGE,
        NO_ENERGY,
        TRANSFER_ERROR,
        ALREADY_RECORDED;

    }
}

