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
package ic2.core.block.heatgenerator.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.heatgenerator.container.ContainerSolidHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntitySolidHeatGenerator;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiSolidHeatGenerator
extends GuiContainer {
    public ContainerSolidHeatGenerator container;
    public String name;
    public String tooltipheat;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUISolidHeatGenerator.png");

    public GuiSolidHeatGenerator(ContainerSolidHeatGenerator container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.SolidHeatGenerator.gui.name");
        this.tooltipheat = StatCollector.translateToLocal((String)"ic2.SolidHeatGenerator.gui.tooltipheat");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 4, 0x404040);
        this.fontRendererObj.drawString(((TileEntitySolidHeatGenerator)this.container.base).gettransmitHeat() + " / " + ((TileEntitySolidHeatGenerator)this.container.base).getMaxHeatEmittedPerTick(), (this.xSize - this.fontRendererObj.getStringWidth(((TileEntitySolidHeatGenerator)this.container.base).gettransmitHeat() + " / " + ((TileEntitySolidHeatGenerator)this.container.base).getMaxHeatEmittedPerTick())) / 2, 70, 5752026);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, this.tooltipheat, 48, 65, 128, 79);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
        if (((TileEntitySolidHeatGenerator)this.container.base).fuel > 0) {
            int l = ((TileEntitySolidHeatGenerator)this.container.base).gaugeFuelScaled(12);
            this.drawTexturedModalRect(j + 81, k + 29 + 12 - l, 176, 12 - l, 14, l + 2);
        }
    }
}

