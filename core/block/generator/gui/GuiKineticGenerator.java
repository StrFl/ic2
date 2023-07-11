/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.inventory.Container
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.generator.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.generator.container.ContainerKineticGenerator;
import ic2.core.block.generator.tileentity.TileEntityKineticGenerator;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiKineticGenerator
extends GuiContainer {
    public ContainerKineticGenerator container;
    public String name;
    public String tooltip;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIKineticGenerator.png");

    public GuiKineticGenerator(ContainerKineticGenerator container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.KineticGenerator.gui.name");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 4, 0x404040);
        this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.KineticGenerator.gui.Output", (Object[])new Object[]{Float.valueOf((float)Math.round(((TileEntityKineticGenerator)this.container.base).getproduction() * 10.0) / 10.0f)}), 42, 52, 2157374);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocalFormatted((String)"ic2.generic.text.bufferEU", (Object[])new Object[]{(int)((TileEntityKineticGenerator)this.container.base).EUstorage}), 59, 33, 116, 46);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
        int i1 = ((TileEntityKineticGenerator)this.container.base).gaugeEUStorageScaled(58);
        this.drawTexturedModalRect(j + 62, k + 36, 179, 18, i1, 8);
    }
}

