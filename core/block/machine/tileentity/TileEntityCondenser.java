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
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerCondenser;
import ic2.core.block.machine.gui.GuiCondenser;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityCondenser
extends TileEntityElectricMachine
implements IHasGui,
IFluidHandler,
IUpgradableBlock {
    private final short passivecolling;
    private final short activecollingperVent;
    public final short euperVent;
    public int progress = 0;
    public final int maxprogress;
    private boolean newActive = false;
    public final FluidTank inputTank = new FluidTank(100000);
    public final FluidTank outputTank = new FluidTank(1000);
    public final InvSlotOutput wateroutputSlot;
    public final InvSlotConsumableLiquidByTank waterinputSlot = new InvSlotConsumableLiquidByTank(this, "biogasinputSlot", 1, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
    public final InvSlotConsumableId ventslots;
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityCondenser() {
        super(100000, 3, 0);
        this.wateroutputSlot = new InvSlotOutput(this, "biogassoutputSlot", 2, 1);
        this.ventslots = new InvSlotConsumableId((TileEntityInventory)this, "ventslots", 3, 4, Ic2Items.reactorVent.getItem());
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 7, 1);
        this.maxprogress = 10000;
        this.euperVent = (short)2;
        this.passivecolling = (short)100;
        this.activecollingperVent = (short)100;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.inputTank.readFromNBT(nbttagcompound.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbttagcompound.getCompoundTag("outputTank"));
        this.progress = nbttagcompound.getInteger("progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound inputTankTag = new NBTTagCompound();
        this.inputTank.writeToNBT(inputTankTag);
        nbttagcompound.setTag("inputTank", (NBTBase)inputTankTag);
        NBTTagCompound outputTankTag = new NBTTagCompound();
        this.outputTank.writeToNBT(outputTankTag);
        nbttagcompound.setTag("outputTank", (NBTBase)outputTankTag);
        nbttagcompound.setInteger("progress", this.progress);
    }

    public int getVents() {
        int Vents = 0;
        for (int i = 0; i < this.ventslots.size(); ++i) {
            if (this.ventslots.get(i) == null) continue;
            ++Vents;
        }
        return Vents;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.waterinputSlot.isEmpty() && this.waterinputSlot.transferFromTank((IFluidTank)this.outputTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.wateroutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        RecipeOutput outputoutputSlot = this.processOutputSlot(true);
        if (outputoutputSlot != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.wateroutputSlot.add(processResult);
        }
        this.newActive = this.inputTank.getFluidAmount() > 0;
        this.work();
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    private void work() {
        if (this.getinputtank().getFluidAmount() > 0 && this.getoutputtank().getCapacity() - this.getoutputtank().getFluidAmount() >= this.maxprogress / 100) {
            if (this.progress >= this.maxprogress) {
                this.outputTank.fill(new FluidStack(BlocksItems.getFluid(InternalName.fluidDistilledWater), this.maxprogress / 100), true);
                this.progress = 0;
            } else {
                int drain = 0;
                double energyuse = 0.0;
                if (this.getVents() == 0) {
                    drain = this.passivecolling;
                } else {
                    drain = this.getVents() * this.activecollingperVent;
                    energyuse = this.getVents() * this.euperVent;
                }
                if (this.energy >= energyuse) {
                    this.energy -= energyuse;
                    this.progress += this.getinputtank().drain((int)drain, (boolean)true).amount;
                }
            }
        }
    }

    public ContainerBase<TileEntityCondenser> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerCondenser(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiCondenser(new ContainerCondenser(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Condenser";
    }

    public int gaugeLiquidScaled(int i, int tank) {
        switch (tank) {
            case 0: {
                if (this.inputTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.inputTank.getFluidAmount() * i / this.inputTank.getCapacity();
            }
            case 1: {
                if (this.outputTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.outputTank.getFluidAmount() * i / this.outputTank.getCapacity();
            }
        }
        return 0;
    }

    public int gaugeprogress(int i) {
        return this.progress * i / this.maxprogress;
    }

    public FluidTank getinputtank() {
        return this.inputTank;
    }

    public FluidTank getoutputtank() {
        return this.outputTank;
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{this.inputTank.getInfo(), this.outputTank.getInfo()};
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid == BlocksItems.getFluid(InternalName.fluidSteam) || fluid == BlocksItems.getFluid(InternalName.fluidSuperheatedSteam);
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        FluidStack fluidStack = this.outputTank.getFluid();
        if (fluidStack == null) {
            return false;
        }
        return fluidStack.isFluidEqual(new FluidStack(fluid, 1));
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!this.canFill(from, resource.getFluid())) {
            return 0;
        }
        return this.inputTank.fill(resource, doFill);
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.outputTank.getFluid())) {
            return null;
        }
        if (!this.canDrain(from, resource.getFluid())) {
            return null;
        }
        return this.outputTank.drain(resource.amount, doDrain);
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return this.outputTank.drain(maxDrain, doDrain);
    }

    @Override
    public double getEnergy() {
        return 0.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }
}

