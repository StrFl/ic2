/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.generator.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.generator.container.ContainerBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiGenerator
extends GuiContainer {
    public ContainerBaseGenerator<? extends TileEntityBaseGenerator> container;
    public String name;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIGenerator.png");

    public GuiGenerator(ContainerBaseGenerator<? extends TileEntityBaseGenerator> container1) {
        super(container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.Generator.gui.name");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 0x404040);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocalFormatted((String)"ic2.generic.text.bufferEU", (Object[])new Object[]{((TileEntityBaseGenerator)this.container.base).storage}), 90, 35, 121, 51);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
        if (((TileEntityBaseGenerator)this.container.base).fuel > 0) {
            int l = ((TileEntityBaseGenerator)this.container.base).gaugeFuelScaled(12);
            this.drawTexturedModalRect(j + 66, k + 36 + 12 - l, 176, 12 - l, 14, l + 2);
        }
        int i1 = ((TileEntityBaseGenerator)this.container.base).gaugeStorageScaled(24);
        this.drawTexturedModalRect(j + 94, k + 35, 176, 14, i1, 17);
    }
}

