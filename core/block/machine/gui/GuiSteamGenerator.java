/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.core.block.machine.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerSteamGenerator;
import ic2.core.block.machine.tileentity.TileEntitySteamGenerator;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GuiSteamGenerator
extends GuiIC2 {
    public ContainerSteamGenerator container;

    public GuiSteamGenerator(ContainerSteamGenerator container) {
        super(container, 220);
        this.container = container;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.SteamGenerator.gui.heatInput", (Object[])new Object[]{((TileEntitySteamGenerator)this.container.base).getheatinput()}), 40, 136, 2157374);
        this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.SteamGenerator.gui.pressurevalve", (Object[])new Object[]{((TileEntitySteamGenerator)this.container.base).getpressurevalve()}), 25, 38, 2157374);
        this.fontRendererObj.drawString(((TileEntitySteamGenerator)this.container.base).getinputmb() + StatCollector.translateToLocal((String)"ic2.generic.text.mb") + StatCollector.translateToLocal((String)"ic2.generic.text.tick"), 92, 175, 2157374);
        this.fontRendererObj.drawString(((TileEntitySteamGenerator)this.container.base).getoutputmb() + StatCollector.translateToLocal((String)"ic2.generic.text.mb") + StatCollector.translateToLocal((String)"ic2.generic.text.tick"), 70, 29, 2157374);
        this.fontRendererObj.drawString("" + ((TileEntitySteamGenerator)this.container.base).gtoutputfluid(), 70, 49, 2157374);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocalFormatted((String)"ic2.SteamGenerator.gui.systemheat", (Object[])new Object[]{Float.valueOf(((TileEntitySteamGenerator)this.container.base).getsystemheat())}), 12, 69, 20, 146);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocalFormatted((String)"ic2.SteamGenerator.gui.calcification", (Object[])new Object[]{Float.valueOf(((TileEntitySteamGenerator)this.container.base).getcalcification())}) + "%", 155, 61, 161, 118);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.SteamGenerator.gui.info.waterinput"), 91, 172, 149, 184);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.SteamGenerator.gui.info.heatinput"), 31, 133, 141, 145);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.SteamGenerator.gui.info.pressvalve"), 22, 35, 63, 47);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.SteamGenerator.gui.info.fluidoutput"), 66, 25, 165, 57);
        FluidStack fluidstackwater = ((TileEntitySteamGenerator)this.container.base).WaterTank.getFluid();
        if (fluidstackwater != null && fluidstackwater.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackwater.getFluid().getName() + ": " + fluidstackwater.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 9, 154, 85, 202);
        }
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        TileEntitySteamGenerator te = (TileEntitySteamGenerator)this.container.base;
        if (x >= 92 && y >= 186 && x <= 100 && y <= 194) {
            IC2.network.get().initiateClientTileEntityEvent(te, -1000);
        }
        if (x >= 102 && y >= 186 && x <= 110 && y <= 194) {
            IC2.network.get().initiateClientTileEntityEvent(te, -100);
        }
        if (x >= 112 && y >= 186 && x <= 120 && y <= 194) {
            IC2.network.get().initiateClientTileEntityEvent(te, -10);
        }
        if (x >= 122 && y >= 186 && x <= 130 && y <= 194) {
            IC2.network.get().initiateClientTileEntityEvent(te, -1);
        }
        if (x >= 122 && y >= 162 && x <= 130 && y <= 170) {
            IC2.network.get().initiateClientTileEntityEvent(te, 1);
        }
        if (x >= 112 && y >= 162 && x <= 120 && y <= 170) {
            IC2.network.get().initiateClientTileEntityEvent(te, 10);
        }
        if (x >= 102 && y >= 162 && x <= 110 && y <= 170) {
            IC2.network.get().initiateClientTileEntityEvent(te, 100);
        }
        if (x >= 92 && y >= 162 && x <= 100 && y <= 170) {
            IC2.network.get().initiateClientTileEntityEvent(te, 1000);
        }
        if (x >= 23 && y >= 49 && x <= 31 && y <= 57) {
            IC2.network.get().initiateClientTileEntityEvent(te, -2100);
        }
        if (x >= 33 && y >= 49 && x <= 41 && y <= 57) {
            IC2.network.get().initiateClientTileEntityEvent(te, -2010);
        }
        if (x >= 43 && y >= 49 && x <= 51 && y <= 57) {
            IC2.network.get().initiateClientTileEntityEvent(te, -2001);
        }
        if (x >= 43 && y >= 25 && x <= 51 && y <= 33) {
            IC2.network.get().initiateClientTileEntityEvent(te, 2001);
        }
        if (x >= 33 && y >= 25 && x <= 41 && y <= 33) {
            IC2.network.get().initiateClientTileEntityEvent(te, 2010);
        }
        if (x >= 23 && y >= 25 && x <= 31 && y <= 33) {
            IC2.network.get().initiateClientTileEntityEvent(te, 2100);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIconinput;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int i = ((TileEntitySteamGenerator)this.container.base).gaugeHeatScaled(76);
        this.drawTexturedModalRect(this.xoffset + 13, this.yoffset + 70 + 76 - i, 177, 1, 7, i);
        int i2 = ((TileEntitySteamGenerator)this.container.base).gaugecalcificationScaled(58);
        this.drawTexturedModalRect(this.xoffset + 155, this.yoffset + 61 + 58 - i2, 187, 1, 7, i2);
        if (((TileEntitySteamGenerator)this.container.base).WaterTank.getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntitySteamGenerator)this.container.base).WaterTank.getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = ((TileEntitySteamGenerator)this.container.base).gaugeLiquidScaled(47, 0);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 10, this.yoffset + 202 - liquidHeight, 75.0, liquidHeight, this.zLevel);
        }
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUISteamGenerator.png");
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.SteamGenerator.gui.name");
    }
}

