/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 */
package ic2.core.block.reactor.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.reactor.IReactorChamber;
import ic2.api.tile.IWrenchable;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.ITickCallback;
import ic2.core.Ic2Items;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityReactorChamberElectric
extends TileEntity
implements IHasGui,
IWrenchable,
IInventory,
IReactorChamber,
IEnergyEmitter,
IFluidHandler {
    public boolean redpowert = false;
    private short ticker = 0;
    private boolean loaded = false;

    public void validate() {
        super.validate();
        IC2.tickHandler.addSingleTickCallback(this.worldObj, new ITickCallback(){

            @Override
            public void tickCallback(World world) {
                if (TileEntityReactorChamberElectric.this.isInvalid() || !world.blockExists(TileEntityReactorChamberElectric.this.xCoord, TileEntityReactorChamberElectric.this.yCoord, TileEntityReactorChamberElectric.this.zCoord)) {
                    return;
                }
                TileEntityReactorChamberElectric.this.onLoaded();
                if (TileEntityReactorChamberElectric.this.enableUpdateEntity()) {
                    world.loadedTileEntityList.add(TileEntityReactorChamberElectric.this);
                }
            }
        });
    }

    public void onLoaded() {
        TileEntityNuclearReactorElectric te;
        if (IC2.platform.isSimulating() && (te = this.getReactor()) != null) {
            TileEntityNuclearReactorElectric reactor = te;
            reactor.refreshChambers();
        }
        this.loaded = true;
    }

    public void onUnloaded() {
        TileEntityNuclearReactorElectric te;
        if (IC2.platform.isSimulating() && this.worldObj.blockExists(this.xCoord, this.yCoord, this.zCoord) && (te = this.getReactor()) != null) {
            TileEntityNuclearReactorElectric reactor = te;
            reactor.refreshChambers();
        }
        this.loaded = false;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return true;
    }

    public void invalidate() {
        super.invalidate();
        if (this.loaded) {
            this.onUnloaded();
        }
    }

    public void onChunkUnload() {
        super.onChunkUnload();
        if (this.loaded) {
            this.onUnloaded();
        }
    }

    public void updateEntity() {
        super.updateEntity();
        if (this.ticker == 19) {
            if (this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord)) {
                if (!this.redpowert) {
                    this.redpowert = true;
                    this.setRedstoneSignal(this.redpowert);
                }
            } else if (this.redpowert) {
                this.redpowert = false;
                this.setRedstoneSignal(this.redpowert);
            }
            this.ticker = 0;
        }
        this.ticker = (short)(this.ticker + 1);
    }

    public final boolean canUpdate() {
        return true;
    }

    public boolean enableUpdateEntity() {
        return true;
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
        return Ic2Items.reactorChamber.copy();
    }

    public int getSizeInventory() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return 0;
        }
        return reactor.getSizeInventory();
    }

    public ItemStack getStackInSlot(int i) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.getStackInSlot(i);
    }

    public ItemStack decrStackSize(int i, int j) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.decrStackSize(i, j);
    }

    public void setInventorySlotContents(int i, ItemStack itemstack) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return;
        }
        reactor.setInventorySlotContents(i, itemstack);
    }

    public String getInventoryName() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return "Nuclear Reactor";
        }
        return reactor.getInventoryName();
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public int getInventoryStackLimit() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return 64;
        }
        return reactor.getInventoryStackLimit();
    }

    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return false;
        }
        return reactor.isUseableByPlayer(entityplayer);
    }

    public void openInventory() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return;
        }
        reactor.openInventory();
    }

    public void closeInventory() {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return;
        }
        reactor.closeInventory();
    }

    public ItemStack getStackInSlotOnClosing(int var1) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.getStackInSlotOnClosing(var1);
    }

    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return false;
        }
        return reactor.isItemValidForSlot(i, itemstack);
    }

    @Override
    public TileEntityNuclearReactorElectric getReactor() {
        for (Direction value : Direction.directions) {
            TileEntity te = value.applyToTileEntity(this);
            if (!(te instanceof TileEntityNuclearReactorElectric)) continue;
            return (TileEntityNuclearReactorElectric)te;
        }
        Block blk = this.getBlockType();
        if (blk != null) {
            blk.onNeighborBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, blk);
        }
        return null;
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return 0;
        }
        return reactor.fill(from, resource, doFill);
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.drain(from, resource, doDrain);
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.drain(from, maxDrain, doDrain);
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return false;
        }
        return reactor.canFill(from, fluid);
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return false;
        }
        return reactor.canDrain(from, fluid);
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.getTankInfo(from);
    }

    @Override
    public ContainerBase<?> getGuiContainer(EntityPlayer entityPlayer) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.getGuiContainer(entityPlayer);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return null;
        }
        return reactor.getGui(entityPlayer, isAdmin);
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return;
        }
        reactor.onGuiClosed(entityPlayer);
    }

    @Override
    public void setRedstoneSignal(boolean redstone) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        if (reactor == null) {
            return;
        }
        reactor.setRedstoneSignal(redstone);
    }
}

