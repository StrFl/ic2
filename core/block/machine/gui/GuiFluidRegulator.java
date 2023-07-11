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
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.machine.gui;

import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerFluidRegulator;
import ic2.core.block.machine.tileentity.TileEntityFluidRegulator;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class GuiFluidRegulator
extends GuiContainer {
    public ContainerFluidRegulator container;
    public String name;
    public String tooltipheat;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIFluidRegulator.png");

    public GuiFluidRegulator(ContainerFluidRegulator container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.FluidRegulator.gui.name");
        this.ySize = 184;
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 0x404040);
        this.fontRendererObj.drawString(((TileEntityFluidRegulator)this.container.base).getoutputmb() + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 105, 57, 2157374);
        this.fontRendererObj.drawString(((TileEntityFluidRegulator)this.container.base).getmodegui(), 145, 57, 2157374);
        FluidStack fluidstack = ((TileEntityFluidRegulator)this.container.base).getFluidStackfromTank();
        if (fluidstack != null && fluidstack.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstack.getFluid().getName() + ": " + fluidstack.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 81, 37, 94, 85);
        }
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        TileEntityFluidRegulator te = (TileEntityFluidRegulator)this.container.base;
        if (x >= 102 && y >= 68 && x <= 110 && y <= 76) {
            IC2.network.get().initiateClientTileEntityEvent(te, -1000);
        }
        if (x >= 112 && y >= 68 && x <= 120 && y <= 76) {
            IC2.network.get().initiateClientTileEntityEvent(te, -100);
        }
        if (x >= 122 && y >= 68 && x <= 130 && y <= 76) {
            IC2.network.get().initiateClientTileEntityEvent(te, -10);
        }
        if (x >= 132 && y >= 68 && x <= 140 && y <= 76) {
            IC2.network.get().initiateClientTileEntityEvent(te, -1);
        }
        if (x >= 132 && y >= 44 && x <= 140 && y <= 52) {
            IC2.network.get().initiateClientTileEntityEvent(te, 1);
        }
        if (x >= 122 && y >= 44 && x <= 130 && y <= 52) {
            IC2.network.get().initiateClientTileEntityEvent(te, 10);
        }
        if (x >= 112 && y >= 44 && x <= 120 && y <= 52) {
            IC2.network.get().initiateClientTileEntityEvent(te, 100);
        }
        if (x >= 102 && y >= 44 && x <= 110 && y <= 52) {
            IC2.network.get().initiateClientTileEntityEvent(te, 1000);
        }
        if (x >= 151 && y >= 44 && x <= 161 && y <= 52) {
            IC2.network.get().initiateClientTileEntityEvent(te, 1001);
        }
        if (x >= 151 && y >= 68 && x <= 161 && y <= 76) {
            IC2.network.get().initiateClientTileEntityEvent(te, 1002);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIconinput;
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int xOffset = (this.width - this.xSize) / 2;
        int yOffset = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xOffset, yOffset, 0, 0, this.xSize, this.ySize);
        int chargeLevel = Math.round(((TileEntityFluidRegulator)this.container.base).getChargeLevel() * 14.0f);
        this.drawTexturedModalRect(xOffset + 9, yOffset + 52 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        if (((TileEntityFluidRegulator)this.container.base).getFluidTank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityFluidRegulator)this.container.base).getFluidTank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = ((TileEntityFluidRegulator)this.container.base).gaugeLiquidScaled(47, 0);
            DrawUtil.drawRepeated(fluidIconinput, xOffset + 82, yOffset + 85 - liquidHeight, 12.0, liquidHeight, this.zLevel);
            this.mc.renderEngine.bindTexture(background);
            this.drawTexturedModalRect(xOffset + 84, yOffset + 43, 179, 74, 12, 47);
        }
    }
}

