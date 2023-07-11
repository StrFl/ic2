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
import ic2.core.ContainerBase;
import ic2.core.item.ItemRadioactive;
import ic2.core.item.tool.ContainerContainmentbox;
import ic2.core.item.tool.GuiContainmentbox;
import ic2.core.item.tool.HandHeldInventory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HandHeldContainmentbox
extends HandHeldInventory {
    public HandHeldContainmentbox(EntityPlayer entityPlayer, ItemStack itemStack1, int inventorySize) {
        super(entityPlayer, itemStack1, inventorySize);
    }

    public ContainerBase<HandHeldContainmentbox> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerContainmentbox(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiContainmentbox(new ContainerContainmentbox(entityPlayer, this));
    }

    public String getInventoryName() {
        return "Containmentbox";
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return itemStack.getItem() instanceof ItemRadioactive;
    }
}

