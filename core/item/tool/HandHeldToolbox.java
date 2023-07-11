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
package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ItemWrapper;
import ic2.core.ContainerBase;
import ic2.core.item.tool.ContainerToolbox;
import ic2.core.item.tool.Guitoolbox;
import ic2.core.item.tool.HandHeldInventory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HandHeldToolbox
extends HandHeldInventory {
    public HandHeldToolbox(EntityPlayer entityPlayer, ItemStack itemStack1, int inventorySize) {
        super(entityPlayer, itemStack1, inventorySize);
    }

    public ContainerBase<HandHeldToolbox> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerToolbox(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new Guitoolbox(new ContainerToolbox(entityPlayer, this));
    }

    public String getInventoryName() {
        return "toolbox";
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        return ItemWrapper.canBeStoredInToolbox(itemstack);
    }
}

