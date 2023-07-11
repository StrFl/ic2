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
import ic2.core.block.machine.container.ContainerLiquidHeatExchanger;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GuiLiquidHeatExchanger
extends GuiIC2 {
    public ContainerLiquidHeatExchanger container;
    public String tooltipheat;

    public GuiLiquidHeatExchanger(ContainerLiquidHeatExchanger container1) {
        super(container1, 204);
        this.container = container1;
        this.tooltipheat = StatCollector.translateToLocal((String)"ic2.LiquidHeatExchanger.gui.tooltipheat");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(((TileEntityLiquidHeatExchanger)this.container.base).gettransmitHeat() + " / " + ((TileEntityLiquidHeatExchanger)this.container.base).getMaxHeatEmittedPerTick(), (this.xSize - this.fontRendererObj.getStringWidth(((TileEntityLiquidHeatExchanger)this.container.base).gettransmitHeat() + " / " + ((TileEntityLiquidHeatExchanger)this.container.base).getMaxHeatEmittedPerTick())) / 2, 31, 5752026);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, this.tooltipheat, 49, 27, 129, 41);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.LiquidHeatExchanger.gui.tooltipvent"), 45, 49, 130, 88, -15, 0);
        FluidStack fluidstackinput = ((TileEntityLiquidHeatExchanger)this.container.base).getinputtank().getFluid();
        FluidStack fluidstackoutput = ((TileEntityLiquidHeatExchanger)this.container.base).getoutputtank().getFluid();
        if (fluidstackinput != null && fluidstackinput.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackinput.getFluid().getName() + ": " + fluidstackinput.amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 16, 45, 33, 92);
        }
        if (fluidstackoutput != null && fluidstackoutput.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackoutput.getFluid().getName() + ": " + fluidstackoutput.amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 142, 45, 159, 92);
        }
        super.drawGuiContainerForegroundLayer(par1, par2);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        int liquidHeight;
        IIcon fluidIconinput;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        if (((TileEntityLiquidHeatExchanger)this.container.base).getinputtank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityLiquidHeatExchanger)this.container.base).getinputtank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntityLiquidHeatExchanger)this.container.base).gaugeLiquidScaled(44, 0);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 19, this.yoffset + 47 + 44 - liquidHeight, 12.0, liquidHeight, this.zLevel);
        }
        if (((TileEntityLiquidHeatExchanger)this.container.base).getoutputtank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityLiquidHeatExchanger)this.container.base).getoutputtank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntityLiquidHeatExchanger)this.container.base).gaugeLiquidScaled(44, 1);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 145, this.yoffset + 47 + 44 - liquidHeight, 12.0, liquidHeight, this.zLevel);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.LiquidHeatExchanger.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIHeatSourceFluid.png");
    }
}

