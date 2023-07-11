/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.heatgenerator.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.heatgenerator.container.ContainerElectricHeatGenerator;
import ic2.core.block.heatgenerator.gui.GuiElectricHeatGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityElectricHeatGenerator
extends TileEntityHeatSourceInventory
implements IHasGui {
    private boolean newActive;
    public final InvSlotDischarge dischargeSlot;
    public final InvSlotConsumable CoilSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "CoilSlot", 0, 10, Ic2Items.coil);
    protected final Energy energy;
    public static final double outputMultiplier = ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/electric");

    public TileEntityElectricHeatGenerator() {
        this.CoilSlot.setStackSizeLimit(1);
        this.dischargeSlot = new InvSlotDischarge(this, 6, InvSlot.Access.NONE, 4);
        this.energy = this.addComponent(Energy.asBasicSink(this, 10000.0, 4).addManagedSlot(this.dischargeSlot));
        this.newActive = false;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
    }

    @Override
    public String getInventoryName() {
        return "ElectricHeatGenerator";
    }

    public ContainerBase<TileEntityElectricHeatGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerElectricHeatGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiElectricHeatGenerator(new ContainerElectricHeatGenerator(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    protected int fillHeatBuffer(int maxAmount) {
        int amount = Math.min(maxAmount, (int)(this.energy.getEnergy() / outputMultiplier));
        if (amount > 0) {
            this.energy.useEnergy((double)amount / outputMultiplier);
            this.newActive = true;
        } else {
            this.newActive = false;
        }
        return amount;
    }

    @Override
    public int getMaxHeatEmittedPerTick() {
        int counter = 0;
        for (int i = 0; i < this.CoilSlot.size(); ++i) {
            if (this.CoilSlot.get(i) == null) continue;
            ++counter;
        }
        return counter * 10;
    }

    public final float getChargeLevel() {
        return (float)Math.min(1.0, this.energy.getFillRatio());
    }
}

