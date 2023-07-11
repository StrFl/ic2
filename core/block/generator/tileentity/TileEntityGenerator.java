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
import ic2.core.block.TileEntityInventory;
import ic2.core.block.generator.container.ContainerGenerator;
import ic2.core.block.generator.gui.GuiGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityGenerator
extends TileEntityBaseGenerator {
    public int itemFuelTime = 0;
    public final InvSlotConsumableFuel fuelSlot = new InvSlotConsumableFuel((TileEntityInventory)this, "fuel", 1, 1, false);

    public TileEntityGenerator() {
        super(Math.round(10.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/generator")), 1, 4000);
    }

    @Override
    public int gaugeFuelScaled(int i) {
        if (this.fuel <= 0) {
            return 0;
        }
        if (this.itemFuelTime <= 0) {
            this.itemFuelTime = this.fuel;
        }
        return Math.min(this.fuel * i / this.itemFuelTime, i);
    }

    @Override
    public boolean gainFuel() {
        int fuelValue = this.fuelSlot.consumeFuel() / 4;
        if (fuelValue == 0) {
            return false;
        }
        this.fuel += fuelValue;
        this.itemFuelTime = fuelValue;
        return true;
    }

    @Override
    public String getInventoryName() {
        return "Generator";
    }

    @Override
    public boolean isConverting() {
        return this.fuel > 0;
    }

    @Override
    public String getOperationSoundFile() {
        return "Generators/GeneratorLoop.ogg";
    }

    public ContainerBase<TileEntityGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiGenerator(new ContainerGenerator(entityPlayer, this));
    }
}

