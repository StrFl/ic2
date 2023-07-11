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
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.IPatternStorage;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankElectricMachine;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerReplicator;
import ic2.core.block.machine.gui.GuiReplicator;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import ic2.core.uu.UuIndex;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityReplicator
extends TileEntityLiquidTankElectricMachine
implements IHasGui,
IUpgradableBlock,
INetworkClientTileEntityEventListener {
    private static final double uuPerTickBase = 1.0E-4;
    private static final double euPerTickBase = 512.0;
    private static final int defaultTier = 4;
    private static final int defaultEnergyStorage = 2000000;
    private double uuPerTick = 1.0E-4;
    private double euPerTick = 512.0;
    private double extraUuStored = 0.0;
    public double uuProcessed = 0.0;
    public ItemStack pattern;
    private Mode mode = Mode.STOPPED;
    public int index;
    public int maxIndex;
    public double patternUu;
    public double patternEu;
    public final InvSlotConsumableLiquid fluidSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "fluid", 0, 1, BlocksItems.getFluid(InternalName.fluidUuMatter));
    public final InvSlotOutput cellSlot = new InvSlotOutput(this, "cell", 0, 1);
    public final InvSlotOutput outputSlot = new InvSlotOutput(this, "output", 0, 1);
    public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, "upgrade", 3, 4);

    public TileEntityReplicator() {
        super(2000000, 4, 0, 16);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFluid()) {
            needsInvUpdate = this.gainFluid();
        }
        boolean newActive = false;
        if (this.mode != Mode.STOPPED && this.energy >= this.euPerTick && this.pattern != null && this.outputSlot.canAdd(this.pattern)) {
            boolean finish;
            double uuRemaining = this.patternUu - this.uuProcessed;
            if (uuRemaining <= this.uuPerTick) {
                finish = true;
            } else {
                uuRemaining = this.uuPerTick;
                finish = false;
            }
            if (this.consumeUu(uuRemaining)) {
                newActive = true;
                this.energy -= this.euPerTick;
                this.uuProcessed += uuRemaining;
                if (finish) {
                    this.uuProcessed = 0.0;
                    if (this.mode == Mode.SINGLE) {
                        this.mode = Mode.STOPPED;
                    } else {
                        this.refreshInfo();
                    }
                    if (this.pattern != null) {
                        this.outputSlot.add(this.pattern);
                        needsInvUpdate = true;
                    }
                }
            }
        }
        this.setActive(newActive);
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    private boolean consumeUu(double amount) {
        if (amount <= this.extraUuStored) {
            this.extraUuStored -= amount;
            return true;
        }
        int toDrain = (int)Math.ceil((amount -= this.extraUuStored) * 1000.0);
        FluidStack drained = this.fluidTank.drain(toDrain, false);
        if (drained != null && drained.getFluid() == BlocksItems.getFluid(InternalName.fluidUuMatter) && drained.amount == toDrain) {
            this.fluidTank.drain(toDrain, true);
            this.extraUuStored = amount < 0.0 ? -(amount -= (double)drained.amount / 1000.0) : 0.0;
            return true;
        }
        return false;
    }

    public void refreshInfo() {
        IPatternStorage storage = this.getPatternStorage();
        ItemStack oldPattern = this.pattern;
        if (storage == null) {
            this.pattern = null;
        } else {
            List<ItemStack> patterns = storage.getPatterns();
            if (this.index < 0 || this.index >= patterns.size()) {
                this.index = 0;
            }
            this.maxIndex = patterns.size();
            if (patterns.isEmpty()) {
                this.pattern = null;
            } else {
                this.pattern = patterns.get(this.index);
                this.patternUu = UuIndex.instance.getInBuckets(this.pattern);
                if (!StackUtil.isStackEqual(this.pattern, oldPattern)) {
                    this.uuProcessed = 0.0;
                    this.mode = Mode.STOPPED;
                }
            }
        }
        if (this.pattern == null) {
            this.uuProcessed = 0.0;
            this.mode = Mode.STOPPED;
        }
    }

    public IPatternStorage getPatternStorage() {
        for (Direction direction : Direction.directions) {
            TileEntity target = direction.applyToTileEntity(this);
            if (!(target instanceof IPatternStorage)) continue;
            return (IPatternStorage)target;
        }
        return null;
    }

    public void setOverclockRates() {
        this.upgradeSlot.onChanged();
        this.uuPerTick = 1.0E-4 / this.upgradeSlot.processTimeMultiplier;
        this.euPerTick = (512.0 + (double)this.upgradeSlot.extraEnergyDemand) * this.upgradeSlot.energyDemandMultiplier;
        this.setTier(TileEntityReplicator.applyModifier(4, this.upgradeSlot.extraTier, 1.0));
        this.maxEnergy = TileEntityReplicator.applyModifier(2000000, this.upgradeSlot.extraEnergyStorage, this.upgradeSlot.energyStorageMultiplier);
    }

    private static int applyModifier(int base, int extra, double multiplier) {
        double ret = Math.round(((double)base + (double)extra) * multiplier);
        return ret > 2.147483647E9 ? Integer.MAX_VALUE : (int)ret;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiReplicator(new ContainerReplicator(entityPlayer, this));
    }

    public ContainerBase<TileEntityReplicator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerReplicator(entityPlayer, this);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            this.setOverclockRates();
            this.refreshInfo();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.setOverclockRates();
        }
    }

    public boolean gainFluid() {
        boolean ret = false;
        MutableObject output = new MutableObject();
        if (this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)output, true) && (output.getValue() == null || this.cellSlot.canAdd((ItemStack)output.getValue()))) {
            ret = this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)output, false);
            if (output.getValue() != null) {
                this.cellSlot.add((ItemStack)output.getValue());
            }
        }
        return ret;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.extraUuStored = nbttagcompound.getDouble("extraUuStored");
        this.uuProcessed = nbttagcompound.getDouble("uuProcessed");
        this.index = nbttagcompound.getInteger("index");
        int modeIdx = nbttagcompound.getInteger("mode");
        this.mode = modeIdx < Mode.values().length ? Mode.values()[modeIdx] : Mode.STOPPED;
        NBTTagCompound contentTag = nbttagcompound.getCompoundTag("pattern");
        this.pattern = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("extraUuStored", this.extraUuStored);
        nbttagcompound.setDouble("uuProcessed", this.uuProcessed);
        nbttagcompound.setInteger("index", this.index);
        nbttagcompound.setInteger("mode", this.mode.ordinal());
        if (this.pattern != null) {
            NBTTagCompound contentTag = new NBTTagCompound();
            this.pattern.writeToNBT(contentTag);
            nbttagcompound.setTag("pattern", (NBTBase)contentTag);
        }
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: 
            case 1: {
                List<ItemStack> patterns;
                IPatternStorage storage;
                if (this.mode != Mode.STOPPED || (storage = this.getPatternStorage()) == null || (patterns = storage.getPatterns()).isEmpty()) break;
                this.index = event == 0 ? (this.index <= 0 ? patterns.size() - 1 : --this.index) : (this.index >= patterns.size() - 1 ? 0 : ++this.index);
                this.refreshInfo();
                break;
            }
            case 3: {
                if (this.mode == Mode.STOPPED) break;
                this.uuProcessed = 0.0;
                this.mode = Mode.STOPPED;
                break;
            }
            case 4: {
                if (this.pattern == null) break;
                this.mode = Mode.SINGLE;
                break;
            }
            case 5: {
                if (this.pattern == null) break;
                this.mode = Mode.CONTINUOUS;
            }
        }
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return BlocksItems.getFluid(InternalName.fluidUuMatter) == fluid;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Replicator";
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

    public Mode getMode() {
        return this.mode;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, new UpgradableProperty[]{UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming});
    }

    public static enum Mode {
        STOPPED,
        SINGLE,
        CONTINUOUS;

    }
}

