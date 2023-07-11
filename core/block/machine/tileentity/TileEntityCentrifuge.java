/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.BasicMachineRecipeManager;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.container.ContainerCentrifuge;
import ic2.core.block.machine.gui.GuiCentrifuge;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityCentrifuge
extends TileEntityStandardMachine {
    public short maxHeat = (short)5000;
    public short heat = 0;
    public short workheat = this.maxHeat;
    protected final Redstone redstone;

    public TileEntityCentrifuge() {
        super(48, 500, 3, 2);
        this.inputSlot = new InvSlotProcessableGeneric((TileEntityInventory)this, "input", 0, 1, Recipes.centrifuge);
        this.redstone = this.addComponent(new Redstone(this));
    }

    public static void init() {
        Recipes.centrifuge = new BasicMachineRecipeManager();
    }

    @Override
    public String getInventoryName() {
        if (IC2.platform.isRendering()) {
            return "Thermal  Centrifuge";
        }
        return "ThermalCentrifuge";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.heat = nbttagcompound.getShort("heat");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("heat", this.heat);
    }

    public int gaugeHeatScaled(int i) {
        return i * this.heat / this.workheat;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean energyPerHeat = true;
        boolean coolingPerTick = true;
        boolean heating = false;
        if (this.energy >= 1.0) {
            int heatRequested = Integer.MIN_VALUE;
            RecipeOutput output = super.getOutput();
            if (output != null) {
                heatRequested = Math.min(this.maxHeat, output.metadata.getInteger("minHeat"));
                this.workheat = (short)heatRequested;
                if (this.heat > heatRequested) {
                    this.heat = (short)heatRequested;
                }
            } else if (this.heat < this.maxHeat && this.redstone.hasRedstoneInput()) {
                heatRequested = this.maxHeat;
                this.workheat = (short)heatRequested;
            }
            if (this.heat - 1 < heatRequested) {
                this.energy -= 1.0;
                heating = true;
            }
        }
        this.heat = heating ? (short)(this.heat + 1) : (short)(this.heat - Math.min(this.heat, 1));
    }

    @Override
    public RecipeOutput getOutput() {
        RecipeOutput ret = super.getOutput();
        if (ret != null) {
            if (ret.metadata == null) {
                return null;
            }
            if (ret.metadata.getInteger("minHeat") > this.heat) {
                return null;
            }
        }
        return ret;
    }

    public ContainerBase<TileEntityCentrifuge> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerCentrifuge(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiCentrifuge(new ContainerCentrifuge(entityPlayer, this));
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, new UpgradableProperty[]{UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing});
    }

    @Override
    public float getWrenchDropRate() {
        return 0.8f;
    }
}

