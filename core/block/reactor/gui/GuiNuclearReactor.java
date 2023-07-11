/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
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
package ic2.core.block.reactor.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.reactor.container.ContainerNuclearReactor;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
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

@SideOnly(value=Side.CLIENT)
public class GuiNuclearReactor
extends GuiContainer {
    public ContainerNuclearReactor container;
    public String name;
    private final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUINuclearReaktor.png");
    private final ResourceLocation backgroundfluid = new ResourceLocation(IC2.textureDomain, "textures/gui/GUINuclearReaktorFluid.png");

    public GuiNuclearReactor(ContainerNuclearReactor container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.NuclearReactor.gui.name");
        this.ySize = 243;
        this.xSize = 212;
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 0x404040);
        if (((TileEntityNuclearReactorElectric)this.container.base).isFluidCooled()) {
            String tooltip;
            this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.NuclearReactor.gui.info.hU", (Object[])new Object[]{((TileEntityNuclearReactorElectric)this.container.base).EmitHeat, 100 * ((TileEntityNuclearReactorElectric)this.container.base).heat / ((TileEntityNuclearReactorElectric)this.container.base).maxHeat}) + "%", 8, 140, 5752026);
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.NuclearReactor.gui.mode.fluid"), 5, 160, 22, 177);
            FluidStack fluidstackinput = ((TileEntityNuclearReactorElectric)this.container.base).getinputtank().getFluid();
            FluidStack fluidstackoutput = ((TileEntityNuclearReactorElectric)this.container.base).getoutputtank().getFluid();
            if (fluidstackinput != null && fluidstackinput.getFluid() != null) {
                tooltip = fluidstackinput.getFluid().getName() + ": " + fluidstackinput.amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb");
                GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, tooltip, 10, 54, 21, 100);
            }
            if (fluidstackoutput != null && fluidstackoutput.getFluid() != null) {
                tooltip = fluidstackoutput.getFluid().getName() + ": " + fluidstackoutput.amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb");
                GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, tooltip, 190, 54, 202, 100);
            }
        } else {
            this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.NuclearReactor.gui.info.EU", (Object[])new Object[]{Math.round(((TileEntityNuclearReactorElectric)this.container.base).output * 5.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/nuclear")), 100 * ((TileEntityNuclearReactorElectric)this.container.base).heat / ((TileEntityNuclearReactorElectric)this.container.base).maxHeat}) + "%", 8, 140, 5752026);
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.NuclearReactor.gui.mode.electric"), 5, 160, 22, 177);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (((TileEntityNuclearReactorElectric)this.container.base).isFluidCooled()) {
            this.mc.getTextureManager().bindTexture(this.backgroundfluid);
        } else {
            this.mc.getTextureManager().bindTexture(this.background);
        }
        int xOffset = (this.width - this.xSize) / 2;
        int yOffset = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xOffset, yOffset, 0, 0, this.xSize, this.ySize);
        int size = ((TileEntityNuclearReactorElectric)this.container.base).getReactorSize();
        int startX = xOffset + 26;
        int startY = yOffset + 25;
        for (int y = 0; y < 6; ++y) {
            for (int x = size; x < 9; ++x) {
                this.drawTexturedModalRect(startX + x * 18, startY + y * 18, 213, 1, 16, 16);
            }
        }
        if (((TileEntityNuclearReactorElectric)this.container.base).isFluidCooled()) {
            IIcon fluidIconoutput;
            int liquidHeight;
            IIcon fluidIconinput;
            int i2 = ((TileEntityNuclearReactorElectric)this.container.base).gaugeHeatScaled(160);
            this.drawTexturedModalRect(xOffset + 26 + 160 - i2, yOffset + 23, 0, 243, i2, 2);
            this.drawTexturedModalRect(xOffset + 26 + 160 - i2, yOffset + 41, 0, 243, i2, 2);
            this.drawTexturedModalRect(xOffset + 26 + 160 - i2, yOffset + 59, 0, 243, i2, 2);
            this.drawTexturedModalRect(xOffset + 26 + 160 - i2, yOffset + 77, 0, 243, i2, 2);
            this.drawTexturedModalRect(xOffset + 26 + 160 - i2, yOffset + 95, 0, 243, i2, 2);
            this.drawTexturedModalRect(xOffset + 26 + 160 - i2, yOffset + 113, 0, 243, i2, 2);
            this.drawTexturedModalRect(xOffset + 26 + 160 - i2, yOffset + 131, 0, 243, i2, 2);
            if (((TileEntityNuclearReactorElectric)this.container.base).getinputtank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityNuclearReactorElectric)this.container.base).getinputtank().getFluid().fluidID).getIcon()) != null) {
                this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                liquidHeight = ((TileEntityNuclearReactorElectric)this.container.base).gaugeLiquidScaled(47, 0);
                DrawUtil.drawRepeated(fluidIconinput, xOffset + 10, yOffset + 101 - liquidHeight, 12.0, liquidHeight, this.zLevel);
                this.mc.renderEngine.bindTexture(this.background);
                this.drawTexturedModalRect(xOffset + 11, yOffset + 59, 218, 123, 9, 37);
            }
            if (((TileEntityNuclearReactorElectric)this.container.base).getoutputtank().getFluidAmount() > 0 && (fluidIconoutput = FluidRegistry.getFluid((int)((TileEntityNuclearReactorElectric)this.container.base).getoutputtank().getFluid().fluidID).getIcon()) != null) {
                this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                liquidHeight = ((TileEntityNuclearReactorElectric)this.container.base).gaugeLiquidScaled(47, 1);
                DrawUtil.drawRepeated(fluidIconoutput, xOffset + 190, yOffset + 101 - liquidHeight, 12.0, liquidHeight, this.zLevel);
                this.mc.renderEngine.bindTexture(this.background);
                this.drawTexturedModalRect(xOffset + 192, yOffset + 59, 218, 81, 9, 37);
            }
        }
    }
}

