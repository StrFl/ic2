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
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.invslot.InvSlotConsumableSolidCanner;
import ic2.core.block.invslot.InvSlotProcessableSolidCanner;
import ic2.core.block.machine.container.ContainerSolidCanner;
import ic2.core.block.machine.gui.GuiSolidCanner;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntitySolidCanner
extends TileEntityStandardMachine {
    public final InvSlotConsumableSolidCanner canInputSlot;

    public TileEntitySolidCanner() {
        super(2, 200, 4);
        this.inputSlot = new InvSlotProcessableSolidCanner(this, "input", 0, 1);
        this.canInputSlot = new InvSlotConsumableSolidCanner(this, "canInput", 1, 1);
    }

    @Override
    public RecipeOutput getOutput() {
        if (this.inputSlot.isEmpty()) {
            return null;
        }
        RecipeOutput output = this.inputSlot.process();
        if (output == null) {
            return null;
        }
        if (!this.outputSlot.canAdd(output.items)) {
            return null;
        }
        return output;
    }

    public ContainerBase<TileEntitySolidCanner> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerSolidCanner(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiSolidCanner(new ContainerSolidCanner(entityPlayer, this));
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
    public String getStartSoundFile() {
        return null;
    }

    @Override
    public String getInterruptSoundFile() {
        return null;
    }

    @Override
    public String getInventoryName() {
        return "Solid Canning Machine";
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

