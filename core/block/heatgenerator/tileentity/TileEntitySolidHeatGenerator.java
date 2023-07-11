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
package ic2.core.block.heatgenerator.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.heatgenerator.container.ContainerSolidHeatGenerator;
import ic2.core.block.heatgenerator.gui.GuiSolidHeatGenerator;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntitySolidHeatGenerator
extends TileEntityHeatSourceInventory
implements IHasGui {
    private int heatbuffer = 0;
    public int activityMeter = 0;
    public int ticksSinceLastActiveUpdate;
    public int fuel = 0;
    public int itemFuelTime = 0;
    public final InvSlotConsumableFuel fuelSlot = new InvSlotConsumableFuel((TileEntityInventory)this, "fuel", 1, 1, false);
    public final InvSlotOutput outputslot = new InvSlotOutput(this, "output", 2, 1);
    public static final int emittedHU = Math.round(20.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/solid"));

    public TileEntitySolidHeatGenerator() {
        this.ticksSinceLastActiveUpdate = IC2.random.nextInt(256);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFuel()) {
            needsInvUpdate = this.gainFuel();
        }
        boolean newActive = this.gainheat();
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (!this.delayActiveUpdate()) {
            this.setActive(newActive);
        } else {
            if (this.ticksSinceLastActiveUpdate % 256 == 0) {
                this.setActive(this.activityMeter > 0);
                this.activityMeter = 0;
            }
            this.activityMeter = newActive ? ++this.activityMeter : --this.activityMeter;
            ++this.ticksSinceLastActiveUpdate;
        }
    }

    public boolean gainheat() {
        if (this.isConverting()) {
            this.heatbuffer += this.getMaxHeatEmittedPerTick();
            --this.fuel;
            if (this.fuel == 0 && (int)(Math.random() * 2.0) == 1) {
                this.outputslot.add(Ic2Items.AshesDust.copy());
            }
            return true;
        }
        return false;
    }

    public boolean needsFuel() {
        return this.fuel <= 0 && this.getHeatBuffer() == 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.fuel = nbttagcompound.getInteger("fuel");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("fuel", this.fuel);
    }

    public int gaugeFuelScaled(int i) {
        if (this.fuel <= 0) {
            return 0;
        }
        if (this.itemFuelTime <= 0) {
            this.itemFuelTime = this.fuel;
        }
        return Math.min(this.fuel * i / this.itemFuelTime, i);
    }

    public boolean delayActiveUpdate() {
        return false;
    }

    public boolean gainFuel() {
        if (this.outputslot.canAdd(Ic2Items.AshesDust.copy())) {
            int fuelValue = this.fuelSlot.consumeFuel() / 4;
            if (fuelValue == 0) {
                return false;
            }
            this.fuel += fuelValue;
            this.itemFuelTime = fuelValue;
            return true;
        }
        return false;
    }

    public boolean isConverting() {
        return this.fuel > 0;
    }

    @Override
    protected int fillHeatBuffer(int maxAmount) {
        if (this.heatbuffer - maxAmount >= 0) {
            this.heatbuffer -= maxAmount;
            return maxAmount;
        }
        maxAmount = this.heatbuffer;
        this.heatbuffer = 0;
        return maxAmount;
    }

    @Override
    public int getMaxHeatEmittedPerTick() {
        return emittedHU;
    }

    @Override
    public String getInventoryName() {
        return "SolidHeatGenerator";
    }

    public ContainerBase<TileEntitySolidHeatGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerSolidHeatGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiSolidHeatGenerator(new ContainerSolidHeatGenerator(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }
}

