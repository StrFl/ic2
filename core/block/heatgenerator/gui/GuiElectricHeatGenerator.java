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
import ic2.core.block.heatgenerator.container.ContainerElectricHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityElectricHeatGenerator;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiElectricHeatGenerator
extends GuiContainer {
    public ContainerElectricHeatGenerator container;
    public String name;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIElectricHeatGenerator.png");

    public GuiElectricHeatGenerator(ContainerElectricHeatGenerator container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.ElectricHeatGenerator.gui.name");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 4, 0x404040);
        this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.ElectricHeatGenerator.gui.hUmax", (Object[])new Object[]{((TileEntityElectricHeatGenerator)this.container.base).gettransmitHeat(), ((TileEntityElectricHeatGenerator)this.container.base).getMaxHeatEmittedPerTick()}), 38, 70, 5752026);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.ElectricHeatGenerator.gui.tooltipheat"), 48, 65, 128, 79);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.ElectricHeatGenerator.gui.coils"), 61, 26, 114, 62, -15, 0);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
        int chargeLevel = (int)(14.0f * ((TileEntityElectricHeatGenerator)this.container.base).getChargeLevel());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(j + 9, k + 57 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
    }
}

