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
import ic2.core.block.heatgenerator.container.ContainerRTHeatGenerator;
import ic2.core.block.heatgenerator.gui.GuiRTHeatGenerator;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityRTHeatGenerator
extends TileEntityHeatSourceInventory
implements IHasGui {
    private boolean newActive = false;
    public final InvSlotConsumable fuelSlot = new InvSlotConsumableId((TileEntityInventory)this, "fuelSlot", 0, 6, Ic2Items.RTGPellets.getItem());
    public static final float outputMultiplier = 2.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/radioisotope");

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.newActive = this.HeatBuffer > 0;
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
    }

    @Override
    protected int fillHeatBuffer(int maxAmount) {
        if (maxAmount >= this.getMaxHeatEmittedPerTick()) {
            return this.getMaxHeatEmittedPerTick();
        }
        return maxAmount;
    }

    @Override
    public int getMaxHeatEmittedPerTick() {
        int counter = 0;
        for (int i = 0; i < this.fuelSlot.size(); ++i) {
            if (this.fuelSlot.get(i) == null) continue;
            ++counter;
        }
        if (counter == 0) {
            return 0;
        }
        return (int)(Math.pow(2.0, counter - 1) * (double)outputMultiplier);
    }

    @Override
    public String getInventoryName() {
        return "RTHeatGenerator";
    }

    public ContainerBase<TileEntityRTHeatGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerRTHeatGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiRTHeatGenerator(new ContainerRTHeatGenerator(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }
}

