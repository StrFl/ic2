/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 */
package ic2.core.block;

import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public abstract class TileEntityLiquidTankStandardMaschine
extends TileEntityStandardMachine
implements IFluidHandler {
    protected final FluidTank fluidTank;

    public TileEntityLiquidTankStandardMaschine(int energyPerTick, int length, int outputSlots, int tanksize) {
        super(energyPerTick, length, outputSlots);
        this.fluidTank = new FluidTank(1000 * tanksize);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.fluidTank.readFromNBT(nbttagcompound.getCompoundTag("fluidTank"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("fluidTank", (NBTBase)fluidTankTag);
    }

    public FluidTank getFluidTank() {
        return this.fluidTank;
    }

    public int getFluidTankCapacity() {
        return this.getFluidTank().getCapacity();
    }

    public FluidStack getFluidStackfromTank() {
        return this.getFluidTank().getFluid();
    }

    public Fluid getFluidfromTank() {
        return this.getFluidStackfromTank().getFluid();
    }

    public int getTankAmount() {
        return this.getFluidTank().getFluidAmount();
    }

    public int getTankFluidId() {
        return this.getFluidStackfromTank().fluidID;
    }

    public int gaugeLiquidScaled(int i) {
        if (this.getFluidTank().getFluidAmount() <= 0) {
            return 0;
        }
        return this.getFluidTank().getFluidAmount() * i / this.getFluidTank().getCapacity();
    }

    public boolean needsFluid() {
        return this.getFluidTank().getFluidAmount() <= this.getFluidTank().getCapacity();
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (this.canFill(from, resource.getFluid())) {
            return this.getFluidTank().fill(resource, doFill);
        }
        return 0;
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.getFluidTank().getFluid())) {
            return null;
        }
        if (!this.canDrain(from, resource.getFluid())) {
            return null;
        }
        return this.getFluidTank().drain(resource.amount, doDrain);
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (!this.canDrain(from, null)) {
            return null;
        }
        return this.getFluidTank().drain(maxDrain, doDrain);
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{this.getFluidTank().getInfo()};
    }

    public abstract boolean canFill(ForgeDirection var1, Fluid var2);

    public abstract boolean canDrain(ForgeDirection var1, Fluid var2);
}

