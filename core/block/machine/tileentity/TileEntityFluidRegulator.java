/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityLiquidTankElectricMachine;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.container.ContainerFluidRegulator;
import ic2.core.block.machine.gui.GuiFluidRegulator;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityFluidRegulator
extends TileEntityLiquidTankElectricMachine
implements IHasGui,
INetworkClientTileEntityEventListener {
    private int mode;
    private int updateTicker;
    private int outputmb;
    private boolean newActive;
    public final InvSlotOutput wasseroutputSlot;
    public final InvSlotConsumableLiquidByTank wasserinputSlot;

    public TileEntityFluidRegulator() {
        super(10000, 4, 1, 10);
        this.wasserinputSlot = new InvSlotConsumableLiquidByTank(this, "wasserinputSlot", 1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, (IFluidTank)this.fluidTank);
        this.wasseroutputSlot = new InvSlotOutput(this, "wasseroutputSlot", 2, 1);
        this.newActive = false;
        this.outputmb = 0;
        this.mode = 0;
        this.updateTicker = IC2.random.nextInt(this.getTickRate());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.outputmb = nbttagcompound.getInteger("outputmb");
        this.mode = nbttagcompound.getInteger("mode");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("outputmb", this.outputmb);
        nbttagcompound.setInteger("mode", this.mode);
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.wasserinputSlot.isEmpty() && this.wasserinputSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.wasseroutputSlot.canAdd((ItemStack)output.getValue()))) {
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
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.wasseroutputSlot.add(processResult);
        }
        if (this.updateTicker++ % this.getTickRate() != 0 && this.mode == 0) {
            return;
        }
        this.newActive = this.work();
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
    }

    private boolean work() {
        int transamount;
        if (this.outputmb == 0) {
            return false;
        }
        if (this.energy < 10.0) {
            return false;
        }
        ForgeDirection dir = ForgeDirection.getOrientation((int)this.getFacing());
        TileEntity te = this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
        if (te instanceof IFluidHandler && this.fluidTank.getFluidAmount() > 0 && (transamount = ((IFluidHandler)te).fill(dir.getOpposite(), this.getFluidTank().drain(this.outputmb, false), false)) > 0) {
            ((IFluidHandler)te).fill(dir.getOpposite(), this.getFluidTank().drain(transamount, true), true);
            this.energy -= 10.0;
            return true;
        }
        return false;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (event == 1001 || event == 1002) {
            if (event == 1001 && this.mode == 0) {
                this.mode = 1;
            }
            if (event == 1002 && this.mode == 1) {
                this.mode = 0;
            }
            return;
        }
        this.outputmb += event;
        if (this.outputmb > 1000) {
            this.outputmb = 1000;
        }
        if (this.outputmb < 0) {
            this.outputmb = 0;
        }
    }

    public int getTickRate() {
        return 20;
    }

    public ContainerBase<TileEntityFluidRegulator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerFluidRegulator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiFluidRegulator(new ContainerFluidRegulator(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "WaterHeater";
    }

    public int gaugeLiquidScaled(int i, int tank) {
        switch (tank) {
            case 0: {
                if (this.fluidTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.fluidTank.getFluidAmount() * i / this.fluidTank.getCapacity();
            }
        }
        return 0;
    }

    public boolean facingMatchesDirection(ForgeDirection direction) {
        return direction.ordinal() == this.getFacing();
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return this.getFacing() != side;
    }

    @Override
    public void setFacing(short side) {
        super.setFacing(side);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return !this.facingMatchesDirection(from);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    public int getoutputmb() {
        return this.outputmb;
    }

    public String getmodegui() {
        switch (this.mode) {
            case 0: {
                return StatCollector.translateToLocal((String)"ic2.generic.text.sec");
            }
            case 1: {
                return StatCollector.translateToLocal((String)"ic2.generic.text.tick");
            }
        }
        return "";
    }
}

