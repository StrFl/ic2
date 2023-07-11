/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package ic2.core;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class GuiIC2
extends GuiContainer {
    public ContainerBase<? extends IInventory> container;
    protected int xoffset;
    protected int yoffset;

    public GuiIC2(ContainerBase<? extends IInventory> container) {
        this(container, 176, 166);
    }

    public GuiIC2(ContainerBase<? extends IInventory> container, int ySize) {
        this(container, 176, ySize);
    }

    public GuiIC2(ContainerBase<? extends IInventory> container, int xSize, int ySize) {
        super(container);
        this.container = container;
        this.ySize = ySize;
        this.xSize = xSize;
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.getName(), (this.xSize - this.fontRendererObj.getStringWidth(this.getName())) / 2, 6, 0x404040);
        if (this.container.base instanceof IUpgradableBlock) {
            GuiTooltipHelper.drawUpgradeslotTooltip(par1 - this.guiLeft, par2 - this.guiTop, 0, 0, 12, 12, (IUpgradableBlock)this.container.base, 25, 0);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(this.getResourceLocation());
        this.xoffset = (this.width - this.xSize) / 2;
        this.yoffset = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(this.xoffset, this.yoffset, 0, 0, this.xSize, this.ySize);
        if (this.container.base instanceof IUpgradableBlock) {
            this.mc.getTextureManager().bindTexture(new ResourceLocation(IC2.textureDomain, "textures/gui/infobutton.png"));
            this.drawTexturedModalRect(this.xoffset + 3, this.yoffset + 3, 0, 0, 10, 10);
            this.mc.getTextureManager().bindTexture(this.getResourceLocation());
        }
    }

    public abstract String getName();

    public abstract ResourceLocation getResourceLocation();
}

