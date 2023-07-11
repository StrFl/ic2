/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.machine.container.ContainerNuke;
import ic2.core.block.machine.gui.GuiNuke;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class TileEntityNuke
extends TileEntityInventory
implements IHasGui {
    public int RadiationRange;
    public final InvSlotConsumableId outsideSlot;
    public final InvSlotConsumableId insideSlot = new InvSlotConsumableId((TileEntityInventory)this, "insideSlot", -1, 1, Ic2Items.uraniumBlock.getItem(), Ic2Items.Uran238.getItem(), Ic2Items.Uran235.getItem(), Ic2Items.smallUran235.getItem(), Ic2Items.Plutonium.getItem(), Ic2Items.smallPlutonium.getItem());

    public TileEntityNuke() {
        this.outsideSlot = new InvSlotConsumableId((TileEntityInventory)this, "outsideSlot", -1, 1, Ic2Items.industrialTnt.getItem());
    }

    public int getRadiationRange() {
        return this.RadiationRange;
    }

    public void setRadiationRange(int range) {
        if (range != this.RadiationRange) {
            this.RadiationRange = range;
        }
    }

    public float getNukeExplosivePower() {
        if (this.outsideSlot.isEmpty()) {
            return -1.0f;
        }
        int itntCount = this.outsideSlot.get().stackSize;
        double ret = 5.0 * Math.pow(itntCount, 0.3333333333333333);
        if (this.insideSlot.isEmpty()) {
            this.setRadiationRange(0);
        } else {
            Item inside = this.insideSlot.get().getItem();
            int nuclearCount = this.insideSlot.get().stackSize;
            if (inside == Ic2Items.Uran238.getItem()) {
                this.setRadiationRange(itntCount);
            } else if (inside == Ic2Items.uraniumBlock.getItem()) {
                this.setRadiationRange(itntCount * 6);
            } else if (inside == Ic2Items.smallUran235.getItem()) {
                this.setRadiationRange(itntCount * 2);
                if (itntCount >= 64) {
                    ret += 0.05555555555555555 * Math.pow(nuclearCount, 1.6);
                }
            } else if (inside == Ic2Items.Uran235.getItem()) {
                this.setRadiationRange(itntCount * 2);
                if (itntCount >= 32) {
                    ret += 0.5 * Math.pow(nuclearCount, 1.4);
                }
            } else if (inside == Ic2Items.smallPlutonium.getItem()) {
                this.setRadiationRange(itntCount * 3);
                if (itntCount >= 32) {
                    ret += 0.05555555555555555 * Math.pow(nuclearCount, 2.0);
                }
            } else if (inside == Ic2Items.Plutonium.getItem()) {
                this.setRadiationRange(itntCount * 4);
                if (itntCount >= 16) {
                    ret += 0.5 * Math.pow(nuclearCount, 1.8);
                }
            }
        }
        ret = Math.min(ret, (double)ConfigUtil.getFloat(MainConfig.get(), "protection/nukeExplosionPowerLimit"));
        return (float)ret;
    }

    public void onIgnite(EntityPlayer player) {
        this.outsideSlot.clear();
        this.insideSlot.clear();
    }

    public ContainerBase<TileEntityNuke> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerNuke(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiNuke(new ContainerNuke(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Nuke";
    }
}

