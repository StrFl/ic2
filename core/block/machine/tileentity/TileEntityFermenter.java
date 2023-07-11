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
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.tile.IHeatSource;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerFermenter;
import ic2.core.block.machine.gui.GuiFermenter;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
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
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityFermenter
extends TileEntityInventory
implements IHasGui,
IFluidHandler,
IUpgradableBlock {
    private final int needamountbio_run;
    private final int outputamountgas_run;
    private final int maxheatbuffer;
    private int heatbuffer = 0;
    public int progress = 0;
    private final int maxprogress;
    private boolean newActive = false;
    public final FluidTank inputTank = new FluidTank(10000);
    public final FluidTank outputTank = new FluidTank(2000);
    public final InvSlotOutput biomassoutputSlot;
    public final InvSlotOutput biogassoutputSlot;
    public final InvSlotOutput outputSlot;
    public final InvSlotConsumableLiquidByList biomassinputSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "biomassinputSlot", 1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, BlocksItems.getFluid(InternalName.fluidBiomass));
    public final InvSlotConsumableLiquidByTank biogasinputSlot = new InvSlotConsumableLiquidByTank(this, "biogasinputSlot", 2, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityFermenter() {
        this.biomassoutputSlot = new InvSlotOutput(this, "biomassoutputSlot", 3, 1);
        this.biogassoutputSlot = new InvSlotOutput(this, "biogassoutputSlot", 4, 1);
        this.outputSlot = new InvSlotOutput(this, "outputSlot", 5, 1);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 6, 2);
        this.maxprogress = ConfigUtil.getInt(MainConfig.get(), "balance/fermenter/biomass_per_fertilizier");
        this.maxheatbuffer = ConfigUtil.getInt(MainConfig.get(), "balance/fermenter/hU_per_run");
        this.needamountbio_run = ConfigUtil.getInt(MainConfig.get(), "balance/fermenter/need_amount_biomass_per_run");
        this.outputamountgas_run = ConfigUtil.getInt(MainConfig.get(), "balance/fermenter/output_amount_biogas_per_run");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.inputTank.readFromNBT(nbttagcompound.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbttagcompound.getCompoundTag("outputTank"));
        this.progress = nbttagcompound.getInteger("progress");
        this.heatbuffer = nbttagcompound.getInteger("heatbuffer");
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
        nbttagcompound.setInteger("heatbuffer", this.heatbuffer);
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.biomassinputSlot.isEmpty() && this.biomassinputSlot.transferToTank((IFluidTank)this.inputTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.biomassoutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.biogasinputSlot.isEmpty() && this.biogasinputSlot.transferFromTank((IFluidTank)this.outputTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.biogassoutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    protected void updateEntityServer() {
        RecipeOutput outputoutputSlot;
        super.updateEntityServer();
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.biomassoutputSlot.add(processResult);
        }
        if ((outputoutputSlot = this.processOutputSlot(true)) != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.biogassoutputSlot.add(processResult);
        }
        this.newActive = this.work();
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    private boolean work() {
        if (this.progress >= this.maxprogress) {
            this.outputSlot.add(Ic2Items.fertilizer);
            this.progress = 0;
        }
        ForgeDirection dir = ForgeDirection.getOrientation((int)this.getFacing());
        TileEntity te = this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
        if (te instanceof IHeatSource && this.inputTank.getFluidAmount() >= this.needamountbio_run && this.outputamountgas_run <= this.outputTank.getCapacity() - this.outputTank.getFluidAmount()) {
            this.heatbuffer += ((IHeatSource)te).requestHeat(dir.getOpposite(), 100);
            if (this.heatbuffer >= this.maxheatbuffer) {
                this.heatbuffer = 0;
                this.getinputtank().drain(this.needamountbio_run, true);
                this.outputTank.fill(new FluidStack(BlocksItems.getFluid(InternalName.fluidBiogas), this.outputamountgas_run), true);
                this.progress += this.needamountbio_run;
            }
            return true;
        }
        return false;
    }

    public ContainerBase<TileEntityFermenter> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerFermenter(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiFermenter(new ContainerFermenter(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Fermenter";
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

    public int gaugeheatbuffer(int i) {
        return this.heatbuffer * i / this.maxheatbuffer;
    }

    public float getheatbuffer_proz() {
        return Math.round((float)this.heatbuffer / (float)this.maxheatbuffer * 100.0f);
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
        return fluid == BlocksItems.getFluid(InternalName.fluidBiomass);
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
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }
}

