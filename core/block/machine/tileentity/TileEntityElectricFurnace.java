/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.invslot.InvSlotProcessableSmelting;
import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.gui.GuiElecFurnace;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityElectricFurnace
extends TileEntityStandardMachine {
    public TileEntityElectricFurnace() {
        super(3, 100, 1);
        this.inputSlot = new InvSlotProcessableSmelting(this, "input", 0, 1);
    }

    @Override
    public String getInventoryName() {
        return "Electric Furnace";
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiElecFurnace(new ContainerStandardMachine<TileEntityElectricFurnace>(entityPlayer, this));
    }

    @Override
    public String getStartSoundFile() {
        return "Machines/Electro Furnace/ElectroFurnaceLoop.ogg";
    }

    @Override
    public String getInterruptSoundFile() {
        return null;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

