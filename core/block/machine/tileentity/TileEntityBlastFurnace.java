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
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.tile.IHeatSource;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.BasicMachineRecipeManager;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerBlastFurnace;
import ic2.core.block.machine.gui.GuiBlastFurnace;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityBlastFurnace
extends TileEntityInventory
implements IHasGui,
IUpgradableBlock {
    public boolean outOfAir = false;
    public int progress = 0;
    public final int maxprogress;
    private boolean newActive = false;
    public int heat = 0;
    public static int maxHeat = 50000;
    public final InvSlotConsumable inputSlot = new InvSlotProcessableGeneric((TileEntityInventory)this, "InputSlot", 0, 1, Recipes.blastfurance);
    public final InvSlotConsumable airSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "AirSlot", 1, 1, Ic2Items.airCell);
    public final InvSlotOutput outputSlot = new InvSlotOutput(this, "OuputSlot", 2, 1);
    public final InvSlotOutput slagOutputSlot;
    public final InvSlotOutput airOutputSlot = new InvSlotOutput(this, "AirOutputSlot", 3, 1);
    public final InvSlotUpgrade upgradeSlot;
    protected final Redstone redstone;

    public TileEntityBlastFurnace() {
        this.slagOutputSlot = new InvSlotOutput(this, "OuputslagSlot", 4, 1);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 5, 2);
        this.maxprogress = 6000;
        this.redstone = this.addComponent(new Redstone(this));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.heat = nbttagcompound.getInteger("heat");
        this.progress = nbttagcompound.getInteger("progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("heat", this.heat);
        nbttagcompound.setInteger("progress", this.progress);
    }

    public static void init() {
        Recipes.blastfurance = new BasicMachineRecipeManager();
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.heatup();
        if (this.isHot()) {
            this.newActive = this.work();
        }
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    private RecipeOutput getRecipeOutput() {
        RecipeOutput ro = Recipes.blastfurance.getOutputFor(this.inputSlot.get(), false);
        return ro;
    }

    private boolean work() {
        if (this.inputSlot.isEmpty()) {
            return false;
        }
        RecipeOutput ro = this.getRecipeOutput();
        if (ro == null) {
            return false;
        }
        ItemStack firstResult = ro.items.get(0);
        ItemStack secondResult = null;
        if (ro.items.size() > 1) {
            secondResult = ro.items.get(1);
        }
        if (!this.outputSlot.canAdd(firstResult) || secondResult != null && !this.outputSlot.canAdd(secondResult)) {
            return false;
        }
        if (this.progress == 0) {
            ++this.progress;
            return true;
        }
        if ((this.progress == 1 || this.progress == 1000 || this.progress == 2000 || this.progress == 3000 || this.progress == 4000 || this.progress == 5000) && !this.airSlot.isEmpty() && this.airOutputSlot.canAdd(Ic2Items.cell)) {
            this.airSlot.consume(1);
            this.airOutputSlot.add(Ic2Items.cell);
            ++this.progress;
            if (this.outOfAir) {
                this.outOfAir = false;
            }
            return true;
        }
        if (this.progress >= this.maxprogress) {
            this.outputSlot.add(firstResult);
            if (secondResult != null) {
                this.slagOutputSlot.add(secondResult);
            }
            this.inputSlot.consume(1);
            this.progress = 0;
            return false;
        }
        if (this.progress != 0 && this.progress != 1 && this.progress != 1000 && this.progress != 2000 && this.progress != 3000 && this.progress != 4000 && this.progress != 5000) {
            ++this.progress;
            return true;
        }
        if (this.progress != 0 && !this.outOfAir) {
            this.outOfAir = true;
        }
        return false;
    }

    private void heatup() {
        boolean coolingPerTick = true;
        int heatRequested = 0;
        int gainhU = 0;
        if (!(this.inputSlot.isEmpty() && this.progress < 1 || this.heat > maxHeat)) {
            heatRequested = maxHeat - this.heat + 100;
        } else if (this.redstone.hasRedstoneInput() && this.heat <= maxHeat) {
            heatRequested = maxHeat - this.heat + 100;
        }
        if (heatRequested > 0) {
            ForgeDirection dir = ForgeDirection.getOrientation((int)this.getFacing());
            TileEntity te = this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
            if (te instanceof IHeatSource) {
                gainhU = ((IHeatSource)te).requestHeat(dir.getOpposite(), heatRequested);
                this.heat += gainhU;
            }
            if (gainhU == 0) {
                this.heat -= Math.min(this.heat, 1);
            }
        } else {
            this.heat -= Math.min(this.heat, 1);
        }
    }

    public boolean isHot() {
        return this.heat >= maxHeat;
    }

    public int gaugeHeatScaled(int i) {
        return i * this.heat / maxHeat;
    }

    public int gaugeprogress(int i) {
        return this.progress * i / this.maxprogress;
    }

    public int getprogresspercent() {
        return (int)((float)this.progress / (float)this.maxprogress * 100.0f);
    }

    public int getheatpercent() {
        return (int)((float)this.heat / (float)maxHeat * 100.0f);
    }

    public ContainerBase<TileEntityBlastFurnace> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerBlastFurnace(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiBlastFurnace(new ContainerBlastFurnace(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Blast Furnace";
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
    public double getEnergy() {
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.RedstoneSensitive, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

