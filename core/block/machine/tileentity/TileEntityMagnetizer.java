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
 *  net.minecraft.util.MathHelper
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.comp.Redstone;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerMagnetizer;
import ic2.core.block.machine.gui.GuiMagnetizer;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityMagnetizer
extends TileEntityElectricMachine
implements IHasGui,
IUpgradableBlock {
    public InvSlotUpgrade upgradeSlot;
    private int ticker = IC2.random.nextInt(16);
    public static final int defaultMaxEnergy = 100;
    public static final int defaultTier = 1;
    protected final Redstone redstone;

    public TileEntityMagnetizer() {
        super(100, 1, 1);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 3, 4);
        this.redstone = this.addComponent(new Redstone(this));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        try {
            this.energy = nbttagcompound.getDouble("energy");
        }
        catch (Exception e) {
            this.energy = nbttagcompound.getShort("energy");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.energy);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.setOverclockRates();
        }
    }

    public void setOverclockRates() {
        this.upgradeSlot.onChanged();
        this.setTier(TileEntityStandardMachine.applyModifier(1, this.upgradeSlot.extraTier, 1.0));
        this.maxEnergy = TileEntityStandardMachine.applyModifier(100, this.upgradeSlot.extraEnergyStorage, this.upgradeSlot.energyStorageMultiplier);
    }

    @Override
    protected void updateEntityServer() {
        int need;
        int y;
        super.updateEntityServer();
        if (this.ticker++ % 16 != 0 || this.redstone.hasRedstoneInput()) {
            return;
        }
        Direction dir = Direction.fromSideValue(this.getFacing());
        int x = this.xCoord + dir.xOffset;
        int z = this.zCoord + dir.zOffset;
        double multiplier = (double)(1 + this.upgradeSlot.extraEnergyDemand) * this.upgradeSlot.energyDemandMultiplier;
        for (y = this.yCoord; y > 0 && y >= this.yCoord - this.distance() && !(this.energy < multiplier) && StackUtil.equals(this.worldObj.getBlock(x, y, z), Ic2Items.ironFence); --y) {
            need = 15 - this.worldObj.getBlockMetadata(x, y, z);
            if (need <= 0) continue;
            if ((double)need * multiplier > this.energy) {
                need = MathHelper.floor_double((double)(this.energy / multiplier));
            }
            this.worldObj.setBlockMetadataWithNotify(x, y, z, this.worldObj.getBlockMetadata(x, y, z) + need, 7);
            this.energy -= (double)need * multiplier;
        }
        for (y = this.yCoord; y < IC2.getWorldHeight(this.worldObj) && y <= this.yCoord + this.distance() && !(this.energy < multiplier) && StackUtil.equals(this.worldObj.getBlock(x, y, z), Ic2Items.ironFence); ++y) {
            need = 15 - this.worldObj.getBlockMetadata(x, y, z);
            if (need <= 0) continue;
            if ((double)need * multiplier > this.energy) {
                need = MathHelper.floor_double((double)(this.energy / multiplier));
            }
            this.worldObj.setBlockMetadataWithNotify(x, y, z, this.worldObj.getBlockMetadata(x, y, z) + need, 7);
            this.energy -= (double)need * multiplier;
        }
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return direction.ordinal() != this.getFacing();
    }

    @Override
    public String getInventoryName() {
        return "Magnetizer";
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return this.getFacing() != side && side != 0 && side != 1;
    }

    private int distance() {
        return 20 + this.upgradeSlot.augmentation;
    }

    @Override
    public ContainerBase<?> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerMagnetizer(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiMagnetizer(new ContainerMagnetizer(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public double getEnergy() {
        return this.energy;
    }

    @Override
    public boolean useEnergy(double amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
            return true;
        }
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Augmentable, UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage);
    }
}

