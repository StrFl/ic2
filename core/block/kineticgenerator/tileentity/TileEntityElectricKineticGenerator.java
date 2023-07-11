/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.kineticgenerator.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.energy.tile.IKineticSource;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.kineticgenerator.container.ContainerElectricKineticGenerator;
import ic2.core.block.kineticgenerator.gui.GuiElectricKineticGenertor;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import java.util.EnumSet;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityElectricKineticGenerator
extends TileEntityInventory
implements IKineticSource,
IHasGui {
    public InvSlotConsumableItemStack slotMotor;
    public InvSlotDischarge dischargeSlot;
    private final float kuPerEU = 4.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/kineticgenerator/electric");
    private boolean newActive;
    public double ku = 0.0;
    public final int maxKU = 1000;
    protected final Energy energy;

    public TileEntityElectricKineticGenerator() {
        this.slotMotor = new InvSlotConsumableItemStack((TileEntityInventory)this, "slotMotor", 0, 10, Ic2Items.elemotor);
        this.slotMotor.setStackSizeLimit(1);
        this.dischargeSlot = new InvSlotDischarge(this, 6, InvSlot.Access.NONE, 4);
        this.energy = this.addComponent(Energy.asBasicSink(this, 10000.0, 4).addManagedSlot(this.dischargeSlot));
        this.newActive = false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.updateDirections();
    }

    @Override
    public void setFacing(short facing) {
        super.setFacing(facing);
        this.updateDirections();
    }

    private void updateDirections() {
        this.energy.setDirections(EnumSet.complementOf(EnumSet.of(Direction.fromSideValue(this.getFacing()))), Direction.noDirections);
    }

    @Override
    public int maxrequestkineticenergyTick(ForgeDirection directionFrom) {
        if (directionFrom.ordinal() != this.getFacing()) {
            return 0;
        }
        return (int)Math.min((double)this.getMaxKU(), this.ku);
    }

    public int getMaxKU() {
        int counter = 0;
        int a = this.getMaxKUForGUI() / 10;
        for (int i = 0; i < this.slotMotor.size(); ++i) {
            if (this.slotMotor.get(i) == null) continue;
            counter += a;
        }
        return counter;
    }

    public int getMaxKUForGUI() {
        return 1000;
    }

    @Override
    public int requestkineticenergy(ForgeDirection directionFrom, int requestkineticenergy) {
        int max = this.maxrequestkineticenergyTick(directionFrom);
        int out = max > requestkineticenergy ? requestkineticenergy : max;
        this.ku -= (double)out;
        this.markDirty();
        return out;
    }

    @Override
    public String getInventoryName() {
        return "Electric Kinetic Generator";
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (1000.0 - this.ku > 1.0) {
            double max = Math.min(1000.0 - this.ku, this.energy.getEnergy() * (double)this.kuPerEU);
            this.energy.useEnergy(max / (double)this.kuPerEU);
            this.ku += max;
            if (max > 0.0) {
                this.markDirty();
                this.newActive = true;
            } else {
                this.newActive = false;
            }
        } else {
            this.newActive = false;
        }
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
    }

    public ContainerBase<TileEntityElectricKineticGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerElectricKineticGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiElectricKineticGenertor((ContainerElectricKineticGenerator)this.getGuiContainer(entityPlayer));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    public final float getChargeLevel() {
        return (float)Math.min(1.0, this.energy.getFillRatio());
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return side != this.getFacing();
    }
}

