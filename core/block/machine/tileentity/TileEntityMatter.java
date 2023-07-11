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
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.BasicMachineRecipeManager;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankElectricMachine;
import ic2.core.block.comp.Redstone;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerMatter;
import ic2.core.block.machine.gui.GuiMatter;
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
import java.util.Vector;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityMatter
extends TileEntityLiquidTankElectricMachine
implements IHasGui,
IUpgradableBlock {
    public final int defaultTier;
    public int soundTicker = IC2.random.nextInt(32);
    public int scrap = 0;
    private final int StateIdle = 0;
    private final int StateRunning = 1;
    private final int StateRunningScrap = 2;
    private int state = 0;
    private int prevState = 0;
    public boolean redstonePowered = false;
    private AudioSource audioSource;
    private AudioSource audioSourceScrap;
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlotProcessableGeneric amplifierSlot = new InvSlotProcessableGeneric((TileEntityInventory)this, "scrap", 0, 1, Recipes.matterAmplifier);
    public final InvSlotOutput outputSlot = new InvSlotOutput(this, "output", 1, 1);
    public final InvSlotConsumableLiquid containerslot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "containerslot", 2, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Fill, BlocksItems.getFluid(InternalName.fluidUuMatter));
    protected final Redstone redstone;

    public TileEntityMatter() {
        super(Math.round(1000000.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/uuEnergyFactor")), 3, -1, 8);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 3, 4);
        this.defaultTier = 3;
        this.redstone = this.addComponent(new Redstone(this));
    }

    public static void init() {
        Recipes.matterAmplifier = new BasicMachineRecipeManager();
        TileEntityMatter.addAmplifier(Ic2Items.scrap, 1, 5000);
        TileEntityMatter.addAmplifier(Ic2Items.scrapBox, 1, 45000);
    }

    public static void addAmplifier(ItemStack input, int amount, int amplification) {
        TileEntityMatter.addAmplifier(new RecipeInputItemStack(input, amount), amplification);
    }

    public static void addAmplifier(String input, int amount, int amplification) {
        TileEntityMatter.addAmplifier(new RecipeInputOreDict(input, amount), amplification);
    }

    public static void addAmplifier(IRecipeInput input, int amplification) {
        NBTTagCompound metadata = new NBTTagCompound();
        metadata.setInteger("amplification", amplification);
        Recipes.matterAmplifier.addRecipe(input, metadata, new ItemStack[0]);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        try {
            this.scrap = nbttagcompound.getInteger("scrap");
        }
        catch (Throwable e) {
            this.scrap = nbttagcompound.getShort("scrap");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("scrap", this.scrap);
    }

    @Override
    public String getInventoryName() {
        return "Mass Fabricator";
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.redstonePowered = false;
        boolean needsInvUpdate = false;
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            needsInvUpdate = true;
        }
        if (this.redstone.hasRedstoneInput() || this.energy <= 0.0) {
            this.setState(0);
            this.setActive(false);
        } else {
            MutableObject output;
            RecipeOutput amplifier;
            this.setState(this.scrap > 0 ? 2 : 1);
            this.setActive(true);
            if (this.scrap < 10000 && (amplifier = this.amplifierSlot.process()) != null) {
                this.amplifierSlot.consume();
                this.scrap += amplifier.metadata.getInteger("amplification");
            }
            if (this.energy >= (double)this.maxEnergy) {
                needsInvUpdate = this.attemptGeneration();
            }
            if (this.containerslot.transferFromTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)(output = new MutableObject()), true) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
                this.containerslot.transferFromTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)output, false);
                if (output.getValue() != null) {
                    this.outputSlot.add((ItemStack)output.getValue());
                }
            }
            if (needsInvUpdate) {
                this.markDirty();
            }
        }
    }

    @Override
    public void onUnloaded() {
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
            this.audioSourceScrap = null;
        }
        super.onUnloaded();
    }

    public boolean attemptGeneration() {
        if (this.fluidTank.getFluidAmount() + 1 > this.fluidTank.getCapacity()) {
            return false;
        }
        this.fill(null, new FluidStack(BlocksItems.getFluid(InternalName.fluidUuMatter), 1), true);
        this.energy -= (double)this.maxEnergy;
        return true;
    }

    @Override
    public double getDemandedEnergy() {
        if (this.redstone.hasRedstoneInput()) {
            return 0.0;
        }
        return (double)this.maxEnergy - this.energy;
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        if (this.energy >= (double)this.maxEnergy || this.redstone.hasRedstoneInput()) {
            return amount;
        }
        int bonus = Math.min((int)amount, this.scrap);
        this.scrap -= bonus;
        this.energy += amount + (double)(5 * bonus);
        return 0.0;
    }

    public String getProgressAsString() {
        int p = Math.min((int)(this.energy * 100.0 / (double)this.maxEnergy), 100);
        return "" + p + "%";
    }

    public ContainerBase<TileEntityMatter> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerMatter(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiMatter(new ContainerMatter(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    private void setState(int aState) {
        this.state = aState;
        if (this.prevState != this.state) {
            IC2.network.get().updateTileEntityField(this, "state");
        }
        this.prevState = this.state;
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> ret = new Vector<String>(1);
        ret.add("state");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("state") && this.prevState != this.state) {
            switch (this.state) {
                case 0: {
                    if (this.audioSource != null) {
                        this.audioSource.stop();
                    }
                    if (this.audioSourceScrap == null) break;
                    this.audioSourceScrap.stop();
                    break;
                }
                case 1: {
                    if (this.audioSource == null) {
                        this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    }
                    if (this.audioSource != null) {
                        this.audioSource.play();
                    }
                    if (this.audioSourceScrap == null) break;
                    this.audioSourceScrap.stop();
                    break;
                }
                case 2: {
                    if (this.audioSource == null) {
                        this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    }
                    if (this.audioSourceScrap == null) {
                        this.audioSourceScrap = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabScrapSolo.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    }
                    if (this.audioSource != null) {
                        this.audioSource.play();
                    }
                    if (this.audioSourceScrap == null) break;
                    this.audioSourceScrap.play();
                }
            }
            this.prevState = this.state;
        }
        super.onNetworkUpdate(field);
    }

    @Override
    public float getWrenchDropRate() {
        return 0.7f;
    }

    public boolean amplificationIsAvailable() {
        if (this.scrap > 0) {
            return true;
        }
        RecipeOutput amplifier = this.amplifierSlot.process();
        return amplifier != null && amplifier.metadata.getInteger("amplification") > 0;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid == BlocksItems.getFluid(InternalName.fluidUuMatter);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            this.setUpgradestat();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.setUpgradestat();
        }
    }

    public void setUpgradestat() {
        this.upgradeSlot.onChanged();
        this.setTier(TileEntityMatter.applyModifier(this.defaultTier, this.upgradeSlot.extraTier, 1.0));
    }

    private static int applyModifier(int base, int extra, double multiplier) {
        double ret = Math.round(((double)base + (double)extra) * multiplier);
        return ret > 2.147483647E9 ? Integer.MAX_VALUE : (int)ret;
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

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidProducing);
    }
}

