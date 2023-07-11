/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.container.ContainerFluidDistributor;
import ic2.core.block.machine.gui.GuiFluidDistributor;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityFluidDistributor
extends TileEntityLiquidTankInventory
implements IHasGui,
INetworkClientTileEntityEventListener {
    public final InvSlotConsumableLiquidByTank inputSlot = new InvSlotConsumableLiquidByTank(this, "inputSlot", 2, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.getFluidTank());
    public final InvSlotOutput OutputSlot = new InvSlotOutput(this, "OutputSlot", 2, 1);

    public TileEntityFluidDistributor() {
        super(1);
    }

    @Override
    protected void updateEntityServer() {
        block8: {
            int transamount;
            TileEntity target;
            block9: {
                Direction[] outputinputSlot;
                super.updateEntityServer();
                if (this.getFluidTank().getFluidAmount() > 0 && (outputinputSlot = this.processInputSlot(true)) != null) {
                    this.processInputSlot(false);
                    List<ItemStack> processResult = outputinputSlot.items;
                    this.OutputSlot.add(processResult);
                }
                if (this.getFluidTank().getFluidAmount() <= 0) break block8;
                if (!this.getActive()) break block9;
                for (Direction direction : Direction.directions) {
                    int transamount2;
                    TileEntity target2;
                    if (!this.facingMatchesDirection(direction.toForgeDirection()) || !((target2 = direction.applyToTileEntity(this)) instanceof IFluidHandler) || (transamount2 = ((IFluidHandler)target2).fill(direction.toForgeDirection().getOpposite(), new FluidStack(this.getFluidTank().getFluid().getFluid(), this.getFluidTank().getFluidAmount()), false)) <= 0) continue;
                    ((IFluidHandler)target2).fill(direction.toForgeDirection().getOpposite(), new FluidStack(this.getFluidTank().getFluid().getFluid(), transamount2), true);
                    this.getFluidTank().drain(transamount2, true);
                }
                break block8;
            }
            int countnode = 0;
            int countvolume = 0;
            for (Direction direction : Direction.directions) {
                if (this.facingMatchesDirection(direction.toForgeDirection()) || !((target = direction.applyToTileEntity(this)) instanceof IFluidHandler) || (transamount = ((IFluidHandler)target).fill(direction.toForgeDirection().getOpposite(), new FluidStack(this.getFluidTank().getFluid().getFluid(), this.getFluidTank().getFluidAmount()), false)) <= 0) continue;
                ++countnode;
                countvolume += transamount;
            }
            if (countnode > 0) {
                for (Direction direction : Direction.directions) {
                    if (this.facingMatchesDirection(direction.toForgeDirection()) || !((target = direction.applyToTileEntity(this)) instanceof IFluidHandler)) continue;
                    if (this.getFluidTank().getFluidAmount() < countvolume / countnode || this.getFluidTank().getFluidAmount() == 0) break;
                    transamount = ((IFluidHandler)target).fill(direction.toForgeDirection().getOpposite(), new FluidStack(this.getFluidTank().getFluid().getFluid(), this.getFluidTank().getFluidAmount() / countnode), true);
                    this.getFluidTank().drain(transamount, true);
                    countvolume -= transamount;
                }
            }
            if (countvolume <= 0) break block8;
            for (Direction direction : Direction.directions) {
                if (this.facingMatchesDirection(direction.toForgeDirection()) || !((target = direction.applyToTileEntity(this)) instanceof IFluidHandler) || this.getFluidTank().getFluidAmount() < countvolume) continue;
                transamount = ((IFluidHandler)target).fill(direction.toForgeDirection().getOpposite(), new FluidStack(this.getFluidTank().getFluid().getFluid(), countvolume), true);
                if (transamount > 0) {
                    this.getFluidTank().drain(transamount, true);
                    countvolume -= transamount;
                }
                if (countvolume != 0) {
                    continue;
                }
                break;
            }
        }
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        if (!this.inputSlot.isEmpty()) {
            MutableObject output = new MutableObject();
            if (this.inputSlot.transferFromTank((IFluidTank)this.getFluidTank(), (MutableObject<ItemStack>)output, simulate) && (output.getValue() == null || this.OutputSlot.canAdd((ItemStack)output.getValue()))) {
                if (output.getValue() == null) {
                    return new RecipeOutput(null, new ItemStack[0]);
                }
                return new RecipeOutput(null, (ItemStack)output.getValue());
            }
        }
        return null;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        this.setActive(!this.getActive());
    }

    public ContainerBase<TileEntityFluidDistributor> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerFluidDistributor(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiFluidDistributor(new ContainerFluidDistributor(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Fluid Distributor";
    }

    public boolean facingMatchesDirection(ForgeDirection direction) {
        return direction.ordinal() == this.getFacing();
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return this.getFacing() != side;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (this.getActive() ? this.facingMatchesDirection(from) : !this.facingMatchesDirection(from)) {
            return false;
        }
        if (this.getFluidTank().getFluidAmount() == 0) {
            return true;
        }
        return this.getFluidTank().getFluid().getFluid().equals(fluid);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!this.canFill(from, resource.getFluid())) {
            return 0;
        }
        return this.getFluidTank().fill(resource, doFill);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }
}

