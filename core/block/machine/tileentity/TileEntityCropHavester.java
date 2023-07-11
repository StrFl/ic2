/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerCropHavester;
import ic2.core.block.machine.gui.GuiCropHavester;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.crop.TileEntityCrop;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCropHavester
extends TileEntityElectricMachine
implements IHasGui,
IUpgradableBlock {
    public final InvSlot contentSlot = new InvSlot(this, "content", 1, InvSlot.Access.IO, 15);
    public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, "upgrade", 16, 1);
    public final InvSlotConsumableId cropnalyzerSlot = new InvSlotConsumableId((TileEntityInventory)this, "cropnalyzer", 8, 7, Ic2Items.cropnalyzer.getItem());
    public int scanX = -5;
    public int scanY = -1;
    public int scanZ = -5;

    public TileEntityCropHavester() {
        super(10000, 1, 0, false);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        ItemStack upgrade = this.upgradeSlot.get(0);
        if (upgrade != null && ((IUpgradeItem)upgrade.getItem()).onTick(upgrade, this)) {
            super.markDirty();
        }
        if (this.energy >= 201.0) {
            this.scan();
        }
    }

    public void scan() {
        TileEntityCrop crop;
        ItemStack[] drops;
        ItemStack cropnalyzer = this.cropnalyzerSlot.get(0);
        ++this.scanX;
        if (this.scanX > 5) {
            this.scanX = -5;
            ++this.scanZ;
            if (this.scanZ > 5) {
                this.scanZ = -5;
                ++this.scanY;
                if (this.scanY > 1) {
                    this.scanY = -1;
                }
            }
        }
        this.energy -= 1.0;
        TileEntity te = this.worldObj.getTileEntity(this.xCoord + this.scanX, this.yCoord + this.scanY, this.zCoord + this.scanZ);
        if (te instanceof TileEntityCrop && !this.isInvFull() && (drops = (crop = (TileEntityCrop)te).harvest_automated(cropnalyzer != null)) != null) {
            for (ItemStack drop : drops) {
                if (StackUtil.putInInventory(this, Direction.XN, drop, true) == 0) {
                    StackUtil.dropAsEntity(this.worldObj, this.xCoord, this.yCoord, this.zCoord, drop);
                } else {
                    StackUtil.putInInventory(this, Direction.XN, drop, false);
                }
                this.energy -= 100.0;
                if (cropnalyzer == null) continue;
                this.energy -= 100.0;
            }
        }
    }

    public ContainerBase<TileEntityCropHavester> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerCropHavester(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiCropHavester(new ContainerCropHavester(entityPlayer, this));
    }

    @Override
    public String getInventoryName() {
        return "CropHavester";
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemProducing);
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
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    private boolean isInvFull() {
        for (int i = 0; i < this.contentSlot.size(); ++i) {
            ItemStack stack = this.contentSlot.get(i);
            if (stack != null && stack.stackSize >= 64) continue;
            return false;
        }
        return true;
    }
}

