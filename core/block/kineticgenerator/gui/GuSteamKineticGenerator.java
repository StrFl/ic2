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
package ic2.core.block.kineticgenerator.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.kineticgenerator.container.ContainerSteamKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntitySteamKineticGenerator;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

@SideOnly(value=Side.CLIENT)
public class GuSteamKineticGenerator
extends GuiIC2 {
    public ContainerSteamKineticGenerator container;

    public GuSteamKineticGenerator(ContainerSteamKineticGenerator container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        FluidStack fluidstackinput;
        super.drawGuiContainerForegroundLayer(par1, par2);
        if (!((TileEntitySteamKineticGenerator)this.container.base).isturbine()) {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.SteamKineticGenerator.gui.error.noturbine"), 10, 55, 14946604);
        }
        if (((TileEntitySteamKineticGenerator)this.container.base).isturbefilledupwithwater() && ((TileEntitySteamKineticGenerator)this.container.base).isturbine()) {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.SteamKineticGenerator.gui.error.filledupwithwater"), 10, 55, 14946604);
        }
        if (((TileEntitySteamKineticGenerator)this.container.base).isturbine() && !((TileEntitySteamKineticGenerator)this.container.base).isturbefilledupwithwater()) {
            if (((TileEntitySteamKineticGenerator)this.container.base).getActive()) {
                this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.SteamKineticGenerator.gui.aktive"), 10, 55, 2157374);
            } else {
                this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.SteamKineticGenerator.gui.waiting"), 10, 55, 2157374);
            }
            this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.SteamKineticGenerator.gui.turbine.ouput", (Object[])new Object[]{((TileEntitySteamKineticGenerator)this.container.base).gethUoutput()}), 10, 71, 2157374);
        }
        if (!((TileEntitySteamKineticGenerator)this.container.base).isHotSteam() && ((TileEntitySteamKineticGenerator)this.container.base).isturbine()) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.SteamKineticGenerator.gui.condensationwarrning"), 110, 20, 139, 46);
        }
        if (!((TileEntitySteamKineticGenerator)this.container.base).isturbine()) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.SteamKineticGenerator.gui.turbineslot"), 79, 25, 96, 42);
        }
        if ((fluidstackinput = ((TileEntitySteamKineticGenerator)this.container.base).getTank().getFluid()) != null && ((TileEntitySteamKineticGenerator)this.container.base).isturbine() && fluidstackinput.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstackinput.getFluid().getName() + ": " + fluidstackinput.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 76, 21, 100, 46, -15, 0);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIconinput;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        if (!((TileEntitySteamKineticGenerator)this.container.base).isHotSteam() && ((TileEntitySteamKineticGenerator)this.container.base).isturbine()) {
            this.drawTexturedModalRect(this.xoffset + 110, this.yoffset + 20, 176, 0, 30, 26);
        }
        if (((TileEntitySteamKineticGenerator)this.container.base).getTank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntitySteamKineticGenerator)this.container.base).getTank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = ((TileEntitySteamKineticGenerator)this.container.base).gaugeLiquidScaled(26, 0);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 75, this.yoffset + 47 - liquidHeight, 26.0, liquidHeight, this.zLevel);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.SteamKineticGenerator.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUISteamKineticGenerator.png");
    }
}

