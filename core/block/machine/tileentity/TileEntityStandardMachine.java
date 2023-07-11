/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.machine.tileentity;

import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityStandardMachine
extends TileEntityElectricMachine
implements IHasGui,
INetworkTileEntityEventListener,
IUpgradableBlock {
    protected short progress = 0;
    public final int defaultEnergyConsume;
    public final int defaultOperationLength;
    public final int defaultTier;
    public final int defaultEnergyStorage;
    public int energyConsume;
    public int operationLength;
    public int operationsPerTick;
    protected float guiProgress;
    public AudioSource audioSource;
    private static final int EventStart = 0;
    private static final int EventInterrupt = 1;
    private static final int EventStop = 2;
    public InvSlotProcessable inputSlot;
    public final InvSlotOutput outputSlot;
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityStandardMachine(int energyPerTick, int length, int outputSlots) {
        this(energyPerTick, length, outputSlots, 1);
    }

    public TileEntityStandardMachine(int energyPerTick, int length, int outputSlots, int aDefaultTier) {
        super(energyPerTick * length, 1, 1);
        this.defaultEnergyConsume = this.energyConsume = energyPerTick;
        this.defaultOperationLength = this.operationLength = length;
        this.defaultTier = aDefaultTier;
        this.defaultEnergyStorage = energyPerTick * length;
        this.outputSlot = new InvSlotOutput(this, "output", 2, outputSlots);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 3, 4);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.progress = nbttagcompound.getShort("progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("progress", this.progress);
    }

    public float getProgress() {
        return this.guiProgress;
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            this.setOverclockRates();
        }
    }

    @Override
    public void onUnloaded() {
        super.onUnloaded();
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.setOverclockRates();
        }
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        RecipeOutput output = this.getOutput();
        if (output != null && this.energy >= (double)this.energyConsume) {
            this.setActive(true);
            if (this.progress == 0) {
                IC2.network.get().initiateTileEntityEvent(this, 0, true);
            }
            this.progress = (short)(this.progress + 1);
            this.energy -= (double)this.energyConsume;
            if (this.progress >= this.operationLength) {
                this.operate(output);
                needsInvUpdate = true;
                this.progress = 0;
                IC2.network.get().initiateTileEntityEvent(this, 2, true);
            }
        } else {
            if (this.progress != 0 && this.getActive()) {
                IC2.network.get().initiateTileEntityEvent(this, 1, true);
            }
            if (output == null) {
                this.progress = 0;
            }
            this.setActive(false);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            needsInvUpdate = true;
        }
        this.guiProgress = (float)this.progress / (float)this.operationLength;
        if (needsInvUpdate) {
            super.markDirty();
        }
    }

    public void setOverclockRates() {
        this.upgradeSlot.onChanged();
        double previousProgress = (double)this.progress / (double)this.operationLength;
        double stackOpLen = ((double)this.defaultOperationLength + (double)this.upgradeSlot.extraProcessTime) * 64.0 * this.upgradeSlot.processTimeMultiplier;
        this.operationsPerTick = (int)Math.min(Math.ceil(64.0 / stackOpLen), 2.147483647E9);
        this.operationLength = (int)Math.round(stackOpLen * (double)this.operationsPerTick / 64.0);
        this.energyConsume = TileEntityStandardMachine.applyModifier(this.defaultEnergyConsume, this.upgradeSlot.extraEnergyDemand, this.upgradeSlot.energyDemandMultiplier);
        this.setTier(TileEntityStandardMachine.applyModifier(this.defaultTier, this.upgradeSlot.extraTier, 1.0));
        this.maxEnergy = TileEntityStandardMachine.applyModifier(this.defaultEnergyStorage, this.upgradeSlot.extraEnergyStorage + this.operationLength * this.energyConsume, this.upgradeSlot.energyStorageMultiplier);
        if (this.operationLength < 1) {
            this.operationLength = 1;
        }
        this.progress = (short)Math.floor(previousProgress * (double)this.operationLength + 0.1);
    }

    public void operate(RecipeOutput output) {
        for (int i = 0; i < this.operationsPerTick; ++i) {
            List<ItemStack> processResult = output.items;
            for (int j = 0; j < this.upgradeSlot.size(); ++j) {
                ItemStack stack = this.upgradeSlot.get(j);
                if (stack == null || !(stack.getItem() instanceof IUpgradeItem)) continue;
                ((IUpgradeItem)stack.getItem()).onProcessEnd(stack, this, processResult);
            }
            this.operateOnce(output, processResult);
            output = this.getOutput();
            if (output == null) break;
        }
    }

    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        this.inputSlot.consume();
        this.outputSlot.add(processResult);
    }

    public RecipeOutput getOutput() {
        if (this.inputSlot.isEmpty()) {
            return null;
        }
        RecipeOutput output = this.inputSlot.process();
        if (output == null) {
            return null;
        }
        if (this.outputSlot.canAdd(output.items)) {
            return output;
        }
        return null;
    }

    @Override
    public abstract String getInventoryName();

    public ContainerBase<? extends TileEntityStandardMachine> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerStandardMachine<TileEntityStandardMachine>(entityPlayer, this);
    }

    public String getStartSoundFile() {
        return null;
    }

    public String getInterruptSoundFile() {
        return null;
    }

    @Override
    public void onNetworkEvent(int event) {
        if (this.audioSource == null && this.getStartSoundFile() != null) {
            this.audioSource = IC2.audioManager.createSource(this, this.getStartSoundFile());
        }
        switch (event) {
            case 0: {
                if (this.audioSource == null) break;
                this.audioSource.play();
                break;
            }
            case 1: {
                if (this.audioSource == null) break;
                this.audioSource.stop();
                if (this.getInterruptSoundFile() == null) break;
                IC2.audioManager.playOnce(this, this.getInterruptSoundFile());
                break;
            }
            case 2: {
                if (this.audioSource == null) break;
                this.audioSource.stop();
            }
        }
    }

    public static int applyModifier(int base, int extra, double multiplier) {
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

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }
}

