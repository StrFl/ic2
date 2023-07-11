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
import ic2.core.item.tool.ContainerToolScanner;
import ic2.core.item.tool.GuiToolScanner;
import ic2.core.item.tool.HandHeldInventory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HandHeldScanner
extends HandHeldInventory {
    ItemStack itemScanner;
    EntityPlayer entityPlayer;

    public HandHeldScanner(EntityPlayer entityPlayer, ItemStack itemScanner) {
        super(entityPlayer, itemScanner, 0);
        this.itemScanner = itemScanner;
        this.entityPlayer = entityPlayer;
    }

    public ContainerBase<HandHeldScanner> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerToolScanner(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiToolScanner(new ContainerToolScanner(entityPlayer, this));
    }

    public String getInventoryName() {
        return "toolscanner";
    }

    public boolean hasCustomInventoryName() {
        return false;
    }
}

