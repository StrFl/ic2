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
import ic2.core.block.machine.container.ContainerCondenser;
import ic2.core.block.machine.tileentity.TileEntityCondenser;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GuiCondenser
extends GuiIC2 {
    public ContainerCondenser container;

    public GuiCondenser(ContainerCondenser container1) {
        super(container1, 184);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        FluidStack fluidstackinput = ((TileEntityCondenser)this.container.base).getinputtank().getFluid();
        FluidStack fluidstackoutput = ((TileEntityCondenser)this.container.base).getoutputtank().getFluid();
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocalFormatted((String)"ic2.Condenser.gui.tooltipvent", (Object[])new Object[]{((TileEntityCondenser)this.container.base).euperVent}), 25, 25, 42, 60, -15, 0);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocalFormatted((String)"ic2.Condenser.gui.tooltipvent", (Object[])new Object[]{((TileEntityCondenser)this.container.base).euperVent}), 133, 25, 150, 60, -15, 0);
        if (fluidstackinput != null && fluidstackinput.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackinput.getFluid().getName() + ": " + fluidstackinput.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 46, 27, 129, 59);
        }
        if (fluidstackoutput != null && fluidstackoutput.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackoutput.getFluid().getName() + ": " + fluidstackoutput.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 46, 74, 129, 88);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        int liquidHeight;
        IIcon fluidIconinput;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int i1 = ((TileEntityCondenser)this.container.base).gaugeprogress(84);
        this.drawTexturedModalRect(this.xoffset + 46, this.yoffset + 62, 0, 184, i1, 9);
        int chargeLevel = Math.round(((TileEntityCondenser)this.container.base).getChargeLevel() * 14.0f);
        this.drawTexturedModalRect(this.xoffset + 9, this.yoffset + 39 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        if (((TileEntityCondenser)this.container.base).getinputtank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityCondenser)this.container.base).getinputtank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntityCondenser)this.container.base).gaugeLiquidScaled(33, 0);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 46, this.yoffset + 60 - liquidHeight, 84.0, liquidHeight, this.zLevel);
        }
        if (((TileEntityCondenser)this.container.base).getoutputtank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityCondenser)this.container.base).getoutputtank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntityCondenser)this.container.base).gaugeLiquidScaled(15, 1);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 46, this.yoffset + 89 - liquidHeight, 84.0, liquidHeight, this.zLevel);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Condenser.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUICondenser.png");
    }
}

