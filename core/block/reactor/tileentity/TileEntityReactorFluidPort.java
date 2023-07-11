/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 */
package ic2.core.block.reactor.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.api.tile.IWrenchable;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.reactor.container.ContainerReactorFluidPort;
import ic2.core.block.reactor.gui.GuiReactorFluidPort;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityReactorFluidPort
extends TileEntityInventory
implements IWrenchable,
IFluidHandler,
IHasGui,
IUpgradableBlock {
    public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, "upgrade", 1, 1);

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    public ContainerBase<TileEntityReactorFluidPort> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerReactorFluidPort(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiReactorFluidPort(new ContainerReactorFluidPort(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Reactor Fluid Port";
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return false;
    }

    @Override
    public short getFacing() {
        return 0;
    }

    @Override
    public void setFacing(short facing) {
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public float getWrenchDropRate() {
        return 0.8f;
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        return Ic2Items.reactorFluidPort.copy();
    }

    public IFluidHandler getIFluidHandler() {
        for (int xoffset = -1; xoffset < 2; ++xoffset) {
            for (int yoffset = -1; yoffset < 2; ++yoffset) {
                for (int zoffset = -1; zoffset < 2; ++zoffset) {
                    TileEntity te = this.worldObj.getTileEntity(this.xCoord + xoffset, this.yCoord + yoffset, this.zCoord + zoffset);
                    if (!(te instanceof IReactorChamber) && !(te instanceof IReactor)) continue;
                    return (IFluidHandler)te;
                }
            }
        }
        Block blk = this.getBlockType();
        if (blk != null) {
            blk.onNeighborBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, blk);
        }
        return null;
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        IFluidHandler reactor = this.getIFluidHandler();
        if (reactor == null) {
            return 0;
        }
        return reactor.fill(from, resource, doFill);
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        IFluidHandler reactor = this.getIFluidHandler();
        if (reactor == null) {
            return null;
        }
        return reactor.drain(from, resource, doDrain);
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        IFluidHandler reactor = this.getIFluidHandler();
        if (reactor == null) {
            return null;
        }
        return reactor.drain(from, maxDrain, doDrain);
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        IFluidHandler reactor = this.getIFluidHandler();
        if (reactor == null) {
            return false;
        }
        return reactor.canFill(from, fluid);
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        IFluidHandler reactor = this.getIFluidHandler();
        if (reactor == null) {
            return false;
        }
        return reactor.canDrain(from, fluid);
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        IFluidHandler reactor = this.getIFluidHandler();
        if (reactor == null) {
            return null;
        }
        return reactor.getTankInfo(from);
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }

    @Override
    public double getEnergy() {
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
    }
}

