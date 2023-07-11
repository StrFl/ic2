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
import ic2.core.block.machine.container.ContainerFermenter;
import ic2.core.block.machine.tileentity.TileEntityFermenter;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GuiFermenter
extends GuiIC2 {
    public ContainerFermenter container;

    public GuiFermenter(ContainerFermenter container1) {
        super(container1, 184);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.Fermenter.gui.info.conversion") + " " + ((TileEntityFermenter)this.container.base).getheatbuffer_proz() + "%", 41, 40, 82, 44);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.Fermenter.gui.info.waste"), 37, 87, 78, 95);
        GuiTooltipHelper.drawUpgradeslotTooltip(par1 - this.guiLeft, par2 - this.guiTop, 124, 82, 159, 99, (IUpgradableBlock)this.container.base, 30, -120);
        FluidStack fluidstackinput = ((TileEntityFermenter)this.container.base).getinputtank().getFluid();
        FluidStack fluidstackoutput = ((TileEntityFermenter)this.container.base).getoutputtank().getFluid();
        if (fluidstackinput != null && fluidstackinput.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackinput.getFluid().getName() + ": " + fluidstackinput.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 37, 48, 86, 79);
        }
        if (fluidstackoutput != null && fluidstackoutput.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackoutput.getFluid().getName() + ": " + fluidstackoutput.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 128, 25, 141, 73);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        int liquidHeight;
        IIcon fluidIconinput;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int i1 = ((TileEntityFermenter)this.container.base).gaugeprogress(42);
        this.drawTexturedModalRect(this.xoffset + 37, this.yoffset + 87, 176, 0, i1, 8);
        int i2 = ((TileEntityFermenter)this.container.base).gaugeheatbuffer(42);
        this.drawTexturedModalRect(this.xoffset + 41, this.yoffset + 40, 176, 9, i2, 4);
        if (((TileEntityFermenter)this.container.base).getinputtank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityFermenter)this.container.base).getinputtank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntityFermenter)this.container.base).gaugeLiquidScaled(30, 0);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 38, this.yoffset + 79 - liquidHeight, 48.0, liquidHeight, this.zLevel);
        }
        if (((TileEntityFermenter)this.container.base).getoutputtank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityFermenter)this.container.base).getoutputtank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntityFermenter)this.container.base).gaugeLiquidScaled(47, 1);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 129, this.yoffset + 73 - liquidHeight, 12.0, liquidHeight, this.zLevel);
            this.mc.renderEngine.bindTexture(this.getResourceLocation());
            this.drawTexturedModalRect(this.xoffset + 129, this.yoffset + 28, 176, 72, 12, 47);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Fermenter.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIFermenter.png");
    }
}

