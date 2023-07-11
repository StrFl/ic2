/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerSolarDestiller;
import ic2.core.block.machine.tileentity.TileEntitySolarDestiller;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

@SideOnly(value=Side.CLIENT)
public class GuiSolarDestiller
extends GuiIC2 {
    public ContainerSolarDestiller container;

    public GuiSolarDestiller(ContainerSolarDestiller container1) {
        super(container1, 184);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        FluidStack fluidstackinput = ((TileEntitySolarDestiller)this.container.base).inputTank.getFluid();
        FluidStack fluidstackoutput = ((TileEntitySolarDestiller)this.container.base).outputTank.getFluid();
        if (fluidstackinput != null && fluidstackinput.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackinput.getFluid().getName() + ": " + fluidstackinput.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 37, 43, 89, 60);
        }
        if (fluidstackoutput != null && fluidstackoutput.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackoutput.getFluid().getName() + ": " + fluidstackoutput.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 115, 55, 131, 97);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        int liquidHeight;
        IIcon fluidIconinput;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        if (((TileEntitySolarDestiller)this.container.base).work()) {
            this.drawTexturedModalRect(this.xoffset + 36, this.yoffset + 26, 0, 184, 97, 29);
        }
        if (((TileEntitySolarDestiller)this.container.base).inputTank.getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntitySolarDestiller)this.container.base).inputTank.getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntitySolarDestiller)this.container.base).gaugeLiquidScaled(18, 0);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 37, this.yoffset + 61 - liquidHeight, 53.0, liquidHeight, this.zLevel);
        }
        if (((TileEntitySolarDestiller)this.container.base).outputTank.getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntitySolarDestiller)this.container.base).outputTank.getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntitySolarDestiller)this.container.base).gaugeLiquidScaled(43, 1);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 115, this.yoffset + 98 - liquidHeight, 17.0, liquidHeight, this.zLevel);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.SolarDestiller.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUISolarDestiller.png");
    }
}

