/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.inventory.Container
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.fluids.FluidStack
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.heatgenerator.gui;

import ic2.core.IC2;
import ic2.core.block.heatgenerator.container.ContainerFluidHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class GuiFluidHeatGenerator
extends GuiContainer {
    public ContainerFluidHeatGenerator container;
    public String name;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIFluidHeatGenerator.png");

    public GuiFluidHeatGenerator(ContainerFluidHeatGenerator container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.FluidHeatGenerator.gui.name");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 0x404040);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.FluidHeatGenerator.gui.info.Emit") + ((TileEntityFluidHeatGenerator)this.container.base).gettransmitHeat(), 96, 33, 5752026);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.FluidHeatGenerator.gui.info.MaxEmit") + ((TileEntityFluidHeatGenerator)this.container.base).getMaxHeatEmittedPerTick(), 96, 52, 5752026);
        FluidStack fluidstack = ((TileEntityFluidHeatGenerator)this.container.base).getFluidStackfromTank();
        if (fluidstack != null && fluidstack.getFluid() != null) {
            String tooltip = fluidstack.getFluid().getName() + ": " + fluidstack.amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb");
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, tooltip, 73, 23, 83, 71);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIcon;
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int xOffset = (this.width - this.xSize) / 2;
        int yOffset = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xOffset, yOffset, 0, 0, this.xSize, this.ySize);
        if (((TileEntityFluidHeatGenerator)this.container.base).getTankAmount() > 0 && (fluidIcon = ((TileEntityFluidHeatGenerator)this.container.base).getFluidfromTank().getIcon()) != null) {
            this.drawTexturedModalRect(xOffset + 70, yOffset + 20, 176, 17, 20, 55);
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = ((TileEntityFluidHeatGenerator)this.container.base).gaugeLiquidScaled(47);
            DrawUtil.drawRepeated(fluidIcon, xOffset + 74, yOffset + 24 + 47 - liquidHeight, 12.0, liquidHeight, this.zLevel);
            this.mc.renderEngine.bindTexture(background);
            this.drawTexturedModalRect(xOffset + 74, yOffset + 24, 176, 72, 12, 47);
        }
    }
}

