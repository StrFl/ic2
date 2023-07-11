/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
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
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.invslot.InvSlotConsumableCanner;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotProcessableCanner;
import ic2.core.block.machine.CannerBottleRecipeManager;
import ic2.core.block.machine.CannerEnrichRecipeManager;
import ic2.core.block.machine.container.ContainerCanner;
import ic2.core.block.machine.gui.GuiCanner;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityCanner
extends TileEntityStandardMachine
implements IFluidHandler,
INetworkClientTileEntityEventListener {
    private Mode mode = Mode.BottleSolid;
    private static final int EventSwitch = 0;
    private static final int EventTanks = 1;
    public final FluidTank inputTank;
    public final FluidTank outputTank;
    public final InvSlotConsumableCanner canInputSlot;

    public TileEntityCanner() {
        super(4, 200, 1);
        this.inputSlot = new InvSlotProcessableCanner(this, "input", 0, 1);
        this.canInputSlot = new InvSlotConsumableCanner(this, "canInput", 1, 1);
        this.inputTank = new FluidTank(8000);
        this.outputTank = new FluidTank(8000);
    }

    public static void init() {
        Recipes.cannerBottle = new CannerBottleRecipeManager();
        Recipes.cannerEnrich = new CannerEnrichRecipeManager();
        TileEntityCanner.addBottleRecipe(Ic2Items.fuelRod, Ic2Items.UranFuel, new ItemStack(Ic2Items.reactorUraniumSimple.getItem(), 1, 1));
        TileEntityCanner.addBottleRecipe(Ic2Items.fuelRod, Ic2Items.MOXFuel, new ItemStack(Ic2Items.reactorMOXSimple.getItem(), 1, 1));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, new ItemStack(Items.potato, 1), Ic2Items.filledTinCan);
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 2, new ItemStack(Items.cookie, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 2));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 2, new ItemStack(Items.melon, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 2));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 2, new ItemStack(Items.fish, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 2));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 2, new ItemStack(Items.chicken, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 2));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 3, new ItemStack(Items.porkchop, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 3));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 3, new ItemStack(Items.beef, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 3));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 4, new ItemStack(Items.apple, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 4));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 4, new ItemStack(Items.carrot, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 4));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 5, new ItemStack(Items.bread, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 5));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 5, new ItemStack(Items.cooked_fished, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 5));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 6, new ItemStack(Items.cooked_chicken, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 6));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 6, new ItemStack(Items.baked_potato, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 6));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 6, new ItemStack(Items.mushroom_stew, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 6));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 6, new ItemStack(Items.pumpkin_pie, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 6));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 8, new ItemStack(Items.cooked_porkchop, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 8));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 8, new ItemStack(Items.cooked_beef, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 8));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, 12, new ItemStack(Items.cake, 1), StackUtil.copyWithSize(Ic2Items.filledTinCan, 12));
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, new ItemStack(Items.poisonous_potato, 1), 2, Ic2Items.filledTinCan);
        TileEntityCanner.addBottleRecipe(Ic2Items.tinCan, new ItemStack(Items.rotten_flesh, 1), 2, Ic2Items.filledTinCan);
        TileEntityCanner.addEnrichRecipe(FluidRegistry.WATER, Ic2Items.constructionFoamPowder, BlocksItems.getFluid(InternalName.fluidConstructionFoam));
        TileEntityCanner.addEnrichRecipe(FluidRegistry.WATER, (IRecipeInput)new RecipeInputOreDict("dustLapis", 8), BlocksItems.getFluid(InternalName.fluidCoolant));
        TileEntityCanner.addEnrichRecipe(BlocksItems.getFluid(InternalName.fluidDistilledWater), (IRecipeInput)new RecipeInputOreDict("dustLapis", 1), BlocksItems.getFluid(InternalName.fluidCoolant));
        TileEntityCanner.addEnrichRecipe(FluidRegistry.WATER, Ic2Items.biochaff, BlocksItems.getFluid(InternalName.fluidBiomass));
        TileEntityCanner.addEnrichRecipe(new FluidStack(FluidRegistry.WATER, 6000), (IRecipeInput)new RecipeInputItemStack(new ItemStack(Items.stick)), new FluidStack(BlocksItems.getFluid(InternalName.fluidHotWater), 1000));
    }

    public static void addBottleRecipe(ItemStack container, int conamount, ItemStack fill, int fillamount, ItemStack output) {
        TileEntityCanner.addBottleRecipe(new RecipeInputItemStack(container, conamount), new RecipeInputItemStack(fill, fillamount), output);
    }

    public static void addBottleRecipe(ItemStack container, ItemStack fill, int fillamount, ItemStack output) {
        TileEntityCanner.addBottleRecipe(new RecipeInputItemStack(container, 1), new RecipeInputItemStack(fill, fillamount), output);
    }

    public static void addBottleRecipe(ItemStack container, int conamount, ItemStack fill, ItemStack output) {
        TileEntityCanner.addBottleRecipe(new RecipeInputItemStack(container, conamount), new RecipeInputItemStack(fill, 1), output);
    }

    public static void addBottleRecipe(ItemStack container, ItemStack fill, ItemStack output) {
        TileEntityCanner.addBottleRecipe(new RecipeInputItemStack(container, 1), new RecipeInputItemStack(fill, 1), output);
    }

    public static void addBottleRecipe(IRecipeInput container, IRecipeInput fill, ItemStack output) {
        Recipes.cannerBottle.addRecipe(container, fill, output);
    }

    public static void addEnrichRecipe(Fluid input, ItemStack additive, Fluid output) {
        TileEntityCanner.addEnrichRecipe(new FluidStack(input, 1000), (IRecipeInput)new RecipeInputItemStack(additive, 1), new FluidStack(output, 1000));
    }

    public static void addEnrichRecipe(Fluid input, IRecipeInput additive, Fluid output) {
        TileEntityCanner.addEnrichRecipe(new FluidStack(input, 1000), additive, new FluidStack(output, 1000));
    }

    public static void addEnrichRecipe(FluidStack input, IRecipeInput additive, FluidStack output) {
        Recipes.cannerEnrich.addRecipe(input, additive, output);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.inputTank.readFromNBT(nbtTagCompound.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbtTagCompound.getCompoundTag("outputTank"));
        this.setMode(Mode.values[nbtTagCompound.getInteger("mode")]);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        NBTTagCompound inputTankTag = new NBTTagCompound();
        this.inputTank.writeToNBT(inputTankTag);
        nbtTagCompound.setTag("inputTank", (NBTBase)inputTankTag);
        NBTTagCompound outputTankTag = new NBTTagCompound();
        this.outputTank.writeToNBT(outputTankTag);
        nbtTagCompound.setTag("outputTank", (NBTBase)outputTankTag);
        nbtTagCompound.setInteger("mode", this.mode.ordinal());
    }

    @Override
    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        MutableObject outputContainer;
        super.operateOnce(output, processResult);
        if (this.mode == Mode.EmptyLiquid || this.mode == Mode.EnrichLiquid) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT((NBTTagCompound)output.metadata.getCompoundTag("output"));
            int amount = this.outputTank.fill(fluid, true);
            assert (amount == fluid.amount);
        }
        if (this.mode == Mode.EnrichLiquid && this.canInputSlot.transferFromTank((IFluidTank)this.outputTank, (MutableObject<ItemStack>)(outputContainer = new MutableObject()), true) && (outputContainer.getValue() == null || this.outputSlot.canAdd((ItemStack)outputContainer.getValue()))) {
            this.canInputSlot.transferFromTank((IFluidTank)this.outputTank, (MutableObject<ItemStack>)outputContainer, false);
            if (outputContainer.getValue() != null) {
                this.outputSlot.add((ItemStack)outputContainer.getValue());
            }
        }
    }

    @Override
    public RecipeOutput getOutput() {
        FluidStack fluid;
        int amount;
        if (this.mode == Mode.EmptyLiquid || this.mode == Mode.BottleLiquid ? this.canInputSlot.isEmpty() : this.inputSlot.isEmpty()) {
            return null;
        }
        RecipeOutput output = this.inputSlot.process();
        if (output == null) {
            return null;
        }
        if (!this.outputSlot.canAdd(output.items)) {
            return null;
        }
        if ((this.mode == Mode.EmptyLiquid || this.mode == Mode.EnrichLiquid) && (amount = this.outputTank.fill(fluid = FluidStack.loadFluidStackFromNBT((NBTTagCompound)output.metadata.getCompoundTag("output")), false)) != fluid.amount) {
            return null;
        }
        return output;
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
        return this.inputTank.getFluid() == null || this.inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1));
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
    public List<String> getNetworkedFields() {
        Vector<String> ret = new Vector<String>(1);
        ret.add("canInputSlot");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    @Override
    public void onUnloaded() {
        if (this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    @Override
    public String getInventoryName() {
        return "Canning Machine";
    }

    @Override
    public String getStartSoundFile() {
        return null;
    }

    @Override
    public String getInterruptSoundFile() {
        return null;
    }

    public ContainerBase<TileEntityCanner> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerCanner(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiCanner(new ContainerCanner(entityPlayer, this));
    }

    @Override
    public float getWrenchDropRate() {
        return 0.85f;
    }

    @Override
    public void onNetworkUpdate(String field) {
        super.onNetworkUpdate(field);
        if (field.equals("mode")) {
            this.setMode(this.mode);
        }
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                this.cycleMode();
                break;
            }
            case 1: {
                this.switchTanks();
            }
        }
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode1) {
        this.mode = mode1;
        switch (mode1) {
            case BottleSolid: {
                this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.None);
                break;
            }
            case BottleLiquid: {
                this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.Fill);
                break;
            }
            case EmptyLiquid: {
                this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.Drain);
                break;
            }
            case EnrichLiquid: {
                this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.Both);
            }
        }
    }

    private void cycleMode() {
        this.setMode(Mode.values[(this.getMode().ordinal() + 1) % 4]);
    }

    private boolean switchTanks() {
        if (this.progress != 0) {
            return false;
        }
        FluidStack inputStack = this.inputTank.getFluid();
        FluidStack outputStack = this.outputTank.getFluid();
        this.inputTank.setFluid(outputStack);
        this.outputTank.setFluid(inputStack);
        return true;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, new UpgradableProperty[]{UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing});
    }

    public static enum Mode {
        BottleSolid,
        EmptyLiquid,
        BottleLiquid,
        EnrichLiquid;

        public static final Mode[] values;

        static {
            values = Mode.values();
        }
    }
}

