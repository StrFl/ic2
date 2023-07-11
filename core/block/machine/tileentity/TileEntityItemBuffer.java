/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerItemBuffer;
import ic2.core.block.machine.gui.GuiItemBuffer;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityItemBuffer
extends TileEntityInventory
implements IHasGui,
IUpgradableBlock {
    public final InvSlot rightcontentSlot = new InvSlot(this, "rightcontent", 0, InvSlot.Access.IO, 24, InvSlot.InvSide.SIDE);
    public final InvSlot leftcontentSlot = new InvSlot(this, "leftcontent", 23, InvSlot.Access.IO, 24, InvSlot.InvSide.NOTSIDE);
    public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, "upgrade", 48, 2);
    private boolean tick = true;

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        ItemStack Upgradeleft = this.upgradeSlot.get(0);
        ItemStack Upgraderight = this.upgradeSlot.get(1);
        if (Upgradeleft != null && Upgraderight != null) {
            if (this.tick) {
                if (((IUpgradeItem)Upgradeleft.getItem()).onTick(Upgradeleft, this)) {
                    super.markDirty();
                }
            } else if (((IUpgradeItem)Upgraderight.getItem()).onTick(Upgraderight, this)) {
                super.markDirty();
            }
            this.tick = !this.tick;
        } else {
            if (Upgradeleft != null) {
                this.tick = true;
                if (((IUpgradeItem)Upgradeleft.getItem()).onTick(Upgradeleft, this)) {
                    super.markDirty();
                }
            }
            if (Upgraderight != null) {
                this.tick = false;
                if (((IUpgradeItem)Upgraderight.getItem()).onTick(Upgraderight, this)) {
                    super.markDirty();
                }
            }
        }
    }

    public ContainerBase<TileEntityItemBuffer> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerItemBuffer(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiItemBuffer(new ContainerItemBuffer(entityPlayer, this));
    }

    @Override
    public String getInventoryName() {
        return "ItemBuffer";
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemProducing);
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public double getEnergy() {
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
    }
}

