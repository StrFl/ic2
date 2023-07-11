/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.machine.container.ContainerElectrolyzer;
import ic2.core.block.machine.gui.GuiElectrolyzer;
import ic2.core.block.wiring.TileEntityElectricBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityElectrolyzer
extends TileEntityInventory
implements IHasGui {
    public short energy = 0;
    public TileEntityElectricBlock mfe = null;
    public int ticker = IC2.random.nextInt(16);
    public final InvSlotConsumableItemStack waterSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "water", 0, InvSlot.Access.IO, 1, InvSlot.InvSide.TOP, Ic2Items.waterCell);
    public final InvSlotConsumableItemStack hydrogenSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "hydrogen", 1, InvSlot.Access.IO, 1, InvSlot.InvSide.BOTTOM, Ic2Items.electrolyzedWaterCell);

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.energy = nbttagcompound.getShort("energy");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("energy", this.energy);
    }

    @Override
    public String getInventoryName() {
        return "Electrolyzer";
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        boolean turnActive = false;
        if (this.ticker++ % 16 == 0) {
            this.mfe = this.lookForMFE();
        }
        if (this.mfe == null) {
            return;
        }
        if (this.shouldDrain() && this.canDrain()) {
            needsInvUpdate = this.drain();
            turnActive = true;
        }
        if (this.shouldPower() && (this.canPower() || this.energy > 0)) {
            needsInvUpdate = this.power();
            turnActive = true;
        }
        if (this.getActive() != turnActive) {
            this.setActive(turnActive);
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    public boolean shouldDrain() {
        return this.mfe != null && this.mfe.energy / (double)this.mfe.maxStorage >= 0.7;
    }

    public boolean shouldPower() {
        return this.mfe != null && this.mfe.energy / (double)this.mfe.maxStorage <= 0.3;
    }

    public boolean canDrain() {
        return this.waterSlot.consume(1, true, false) != null && (this.hydrogenSlot.isEmpty() || this.hydrogenSlot.get().stackSize < Math.min(this.hydrogenSlot.getStackSizeLimit(), this.hydrogenSlot.get().getMaxStackSize()));
    }

    public boolean canPower() {
        return this.hydrogenSlot.consume(1, true, false) != null && (this.waterSlot.isEmpty() || this.waterSlot.get().stackSize < Math.min(this.waterSlot.getStackSizeLimit(), this.waterSlot.get().getMaxStackSize()));
    }

    public boolean drain() {
        this.mfe.energy -= (double)this.processRate();
        this.energy = (short)(this.energy + this.processRate());
        if (this.energy >= 20000) {
            this.energy = (short)(this.energy - 20000);
            this.waterSlot.consume(1);
            if (this.hydrogenSlot.isEmpty()) {
                this.hydrogenSlot.put(Ic2Items.electrolyzedWaterCell.copy());
            } else {
                ++this.hydrogenSlot.get().stackSize;
            }
            return true;
        }
        return false;
    }

    public boolean power() {
        if (this.energy > 0) {
            int out = Math.min(this.energy, this.processRate());
            this.energy = (short)(this.energy - out);
            this.mfe.energy += (double)out;
            return false;
        }
        this.energy = (short)(this.energy + (12000 + 2000 * this.mfe.tier));
        this.hydrogenSlot.consume(1);
        if (this.waterSlot.isEmpty()) {
            this.waterSlot.put(Ic2Items.waterCell.copy());
        } else {
            ++this.waterSlot.get().stackSize;
        }
        return true;
    }

    public int processRate() {
        switch (this.mfe.tier) {
            default: {
                return 2;
            }
            case 2: {
                return 8;
            }
            case 3: {
                return 32;
            }
            case 4: 
        }
        return 128;
    }

    public TileEntityElectricBlock lookForMFE() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
            if (!(te instanceof TileEntityElectricBlock)) continue;
            return (TileEntityElectricBlock)te;
        }
        return null;
    }

    public int gaugeEnergyScaled(int i) {
        if (this.energy <= 0) {
            return 0;
        }
        int r = this.energy * i / 20000;
        if (r > i) {
            r = i;
        }
        return r;
    }

    public ContainerBase<TileEntityElectrolyzer> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerElectrolyzer(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiElectrolyzer(new ContainerElectrolyzer(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }
}

