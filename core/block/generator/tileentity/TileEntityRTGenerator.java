/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.generator.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.generator.container.ContainerRTGenerator;
import ic2.core.block.generator.gui.GuiRTGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityRTGenerator
extends TileEntityBaseGenerator {
    public final InvSlotConsumable fuelSlot = new InvSlotConsumableId((TileEntityInventory)this, "fuelSlot", 0, 6, Ic2Items.RTGPellets.getItem());
    private static final float efficiency = ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/radioisotope");

    public TileEntityRTGenerator() {
        super(Math.round(16.0f * efficiency), 1, 20000);
    }

    @Override
    public int gaugeFuelScaled(int i) {
        return i;
    }

    @Override
    public boolean gainEnergy() {
        int counter = 0;
        for (int i = 0; i < this.fuelSlot.size(); ++i) {
            if (this.fuelSlot.get(i) == null) continue;
            ++counter;
        }
        if (counter == 0) {
            return false;
        }
        this.storage += (double)((int)(Math.pow(2.0, counter - 1) * (double)efficiency));
        return true;
    }

    @Override
    public boolean gainFuel() {
        return false;
    }

    @Override
    public boolean needsFuel() {
        return true;
    }

    @Override
    public String getInventoryName() {
        return "RTGenerator";
    }

    public ContainerBase<TileEntityRTGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerRTGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiRTGenerator(new ContainerRTGenerator(entityPlayer, this));
    }

    @Override
    public boolean delayActiveUpdate() {
        return true;
    }
}

