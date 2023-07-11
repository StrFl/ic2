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
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.BiomeGenBase
 *  net.minecraft.world.biome.BiomeGenDesert
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
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
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerSolarDestiller;
import ic2.core.block.machine.gui.GuiSolarDestiller;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntitySolarDestiller
extends TileEntityInventory
implements IHasGui,
IFluidHandler,
IUpgradableBlock {
    public final FluidTank inputTank = new FluidTank(10000);
    public final FluidTank outputTank = new FluidTank(10000);
    private int tickrate;
    private int updateTicker;
    private boolean sunIsVisible = false;
    public final InvSlotOutput wateroutputSlot;
    public final InvSlotOutput destiwateroutputSlott;
    public final InvSlotConsumableLiquidByList waterinputSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "waterinputSlot", 1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, FluidRegistry.WATER);
    public final InvSlotConsumableLiquidByTank destiwaterinputSlot = new InvSlotConsumableLiquidByTank(this, "destiwaterinputSlot", 2, InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
    public final InvSlotUpgrade upgradeSlot;

    public TileEntitySolarDestiller() {
        this.wateroutputSlot = new InvSlotOutput(this, "wateroutputSlot", 3, 1);
        this.destiwateroutputSlott = new InvSlotOutput(this, "destiwateroutputSlott", 4, 1);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 7, 3);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        this.tickrate = this.getTickRate();
        this.updateTicker = IC2.random.nextInt(this.tickrate);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.inputTank.readFromNBT(nbttagcompound.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbttagcompound.getCompoundTag("outputTank"));
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
    }

    @Override
    protected void updateEntityServer() {
        RecipeOutput outputoutputSlot;
        super.updateEntityServer();
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.wateroutputSlot.add(processResult);
        }
        this.updateSunVisibility();
        if (this.updateTicker++ >= this.tickrate - 1) {
            if (this.sunIsVisible && this.inputTank.getFluidAmount() > 0 && this.outputTank.getFluidAmount() < 10000) {
                this.inputTank.drain(1, true);
                this.outputTank.fill(new FluidStack(BlocksItems.getFluid(InternalName.fluidDistilledWater), 1), true);
            }
            this.updateTicker = 0;
        }
        if ((outputoutputSlot = this.processOutputSlot(true)) != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.destiwateroutputSlott.add(processResult);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.waterinputSlot.isEmpty() && this.waterinputSlot.transferToTank((IFluidTank)this.inputTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.wateroutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.destiwaterinputSlot.isEmpty() && this.destiwaterinputSlot.transferFromTank((IFluidTank)this.outputTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.destiwateroutputSlott.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private static int getskylight(World world, int x, int z) {
        return world.getBlockLightValue(x, 255, z);
    }

    public void updateSunVisibility() {
        this.sunIsVisible = TileEntitySolarDestiller.isSunVisible(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord);
    }

    public static boolean isSunVisible(World world, int x, int y, int z) {
        return TileEntitySolarDestiller.getskylight(world, x, z) > 4 && !world.provider.hasNoSky && world.canBlockSeeTheSky(x, y, z) && (world.getWorldChunkManager().getBiomeGenAt(x, z) instanceof BiomeGenDesert || !world.isRaining() && !world.isThundering());
    }

    @Override
    public String getInventoryName() {
        return "Solar Destiller";
    }

    public ContainerBase<TileEntitySolarDestiller> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerSolarDestiller(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiSolarDestiller(new ContainerSolarDestiller(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    public int getTickRate() {
        if (BiomeDictionary.isBiomeOfType((BiomeGenBase)this.worldObj.getWorldChunkManager().getBiomeGenAt(this.xCoord, this.zCoord), (BiomeDictionary.Type)BiomeDictionary.Type.HOT)) {
            return 36;
        }
        if (BiomeDictionary.isBiomeOfType((BiomeGenBase)this.worldObj.getWorldChunkManager().getBiomeGenAt(this.xCoord, this.zCoord), (BiomeDictionary.Type)BiomeDictionary.Type.COLD)) {
            return 144;
        }
        return 72;
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

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{this.inputTank.getInfo(), this.outputTank.getInfo()};
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid.getID() == FluidRegistry.WATER.getID();
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

    public boolean work() {
        return this.inputTank.getFluidAmount() > 0 && this.outputTank.getFluidAmount() < 1000 && this.sunIsVisible;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidProducing);
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

