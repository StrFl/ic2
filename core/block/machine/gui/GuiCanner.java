/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidTank
 */
package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerCanner;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;

@SideOnly(value=Side.CLIENT)
public class GuiCanner
extends GuiIC2 {
    public ContainerCanner container;

    public GuiCanner(ContainerCanner container) {
        super(container, 184);
        this.container = container;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        FluidTank outputtank;
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String tooltip = "";
        switch (((TileEntityCanner)this.container.base).getMode()) {
            case BottleSolid: {
                tooltip = StatCollector.translateToLocal((String)"ic2.Canner.gui.switch.BottleSolid");
                break;
            }
            case EmptyLiquid: {
                tooltip = StatCollector.translateToLocal((String)"ic2.Canner.gui.switch.EmptyLiquid");
                break;
            }
            case BottleLiquid: {
                tooltip = StatCollector.translateToLocal((String)"ic2.Canner.gui.switch.BottleLiquid");
                break;
            }
            case EnrichLiquid: {
                tooltip = StatCollector.translateToLocal((String)"ic2.Canner.gui.switch.EnrichLiquid");
            }
        }
        GuiTooltipHelper.drawAreaTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, tooltip, 63, 81, 112, 94);
        GuiTooltipHelper.drawAreaTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, StatCollector.translateToLocal((String)"ic2.Canner.gui.switchTanks"), 77, 64, 100, 77);
        FluidTank inputtank = ((TileEntityCanner)this.container.base).getinputtank();
        if (inputtank.getFluid() != null) {
            String tooltipinputtank = inputtank.getFluid().getFluid().getName() + ": " + inputtank.getFluid().amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb");
            GuiTooltipHelper.drawAreaTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, tooltipinputtank, 42, 45, 55, 93);
        }
        if ((outputtank = ((TileEntityCanner)this.container.base).getoutputtank()).getFluid() != null) {
            String tooltoutputtank = outputtank.getFluid().getFluid().getName() + ": " + outputtank.getFluid().amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb");
            GuiTooltipHelper.drawAreaTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, tooltoutputtank, 120, 45, 133, 93);
        }
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        if (x >= 63 && y >= 81 && x <= 112 && y <= 94) {
            IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, 0);
        } else if (x >= 77 && y >= 64 && x <= 100 && y <= 77) {
            IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, 1);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        int liquidHeight;
        IIcon fluidIcon;
        int progressSize;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        switch (((TileEntityCanner)this.container.base).getMode()) {
            case BottleSolid: {
                this.drawTexturedModalRect(this.xoffset + 63, this.yoffset + 81, 196, 20, 50, 14);
                this.drawTexturedModalRect(this.xoffset + 59, this.yoffset + 53, 3, 4, 9, 18);
                this.drawTexturedModalRect(this.xoffset + 99, this.yoffset + 53, 3, 4, 18, 23);
                break;
            }
            case EmptyLiquid: {
                this.drawTexturedModalRect(this.xoffset + 63, this.yoffset + 81, 196, 35, 50, 14);
                this.drawTexturedModalRect(this.xoffset + 71, this.yoffset + 43, 196, 0, 26, 18);
                this.drawTexturedModalRect(this.xoffset + 59, this.yoffset + 53, 3, 4, 9, 18);
                break;
            }
            case BottleLiquid: {
                this.drawTexturedModalRect(this.xoffset + 63, this.yoffset + 81, 196, 50, 50, 14);
                this.drawTexturedModalRect(this.xoffset + 99, this.yoffset + 53, 3, 4, 18, 23);
                this.drawTexturedModalRect(this.xoffset + 71, this.yoffset + 43, 196, 0, 26, 18);
                break;
            }
            case EnrichLiquid: {
                this.drawTexturedModalRect(this.xoffset + 63, this.yoffset + 81, 196, 65, 50, 14);
            }
        }
        int chargeSize = Math.round(((TileEntityCanner)this.container.base).getChargeLevel() * 14.0f);
        if (chargeSize > 0) {
            this.drawTexturedModalRect(this.xoffset + 9, this.yoffset + 61 + (14 - chargeSize), 176, 14 - chargeSize, 14, chargeSize);
        }
        if ((progressSize = Math.round(((TileEntityCanner)this.container.base).getProgress() * 23.0f)) > 0) {
            this.drawTexturedModalRect(this.xoffset + 74, this.yoffset + 22, 233, 0, progressSize, 14);
        }
        if (((TileEntityCanner)this.container.base).getinputtank().getFluidAmount() > 0 && (fluidIcon = FluidRegistry.getFluid((int)((TileEntityCanner)this.container.base).getinputtank().getFluid().fluidID).getIcon()) != null) {
            this.drawTexturedModalRect(this.xoffset + 39, this.yoffset + 42, 176, 14, 20, 55);
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntityCanner)this.container.base).gaugeLiquidScaled(47, 0);
            DrawUtil.drawRepeated(fluidIcon, this.xoffset + 43, this.yoffset + 46 + 47 - liquidHeight, 12.0, liquidHeight, this.zLevel);
            this.mc.renderEngine.bindTexture(this.getResourceLocation());
            this.drawTexturedModalRect(this.xoffset + 43, this.yoffset + 46, 176, 69, 12, 47);
        }
        if (((TileEntityCanner)this.container.base).getoutputtank().getFluidAmount() > 0 && (fluidIcon = FluidRegistry.getFluid((int)((TileEntityCanner)this.container.base).getoutputtank().getFluid().fluidID).getIcon()) != null) {
            this.drawTexturedModalRect(this.xoffset + 117, this.yoffset + 42, 176, 14, 20, 55);
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            liquidHeight = ((TileEntityCanner)this.container.base).gaugeLiquidScaled(47, 1);
            DrawUtil.drawRepeated(fluidIcon, this.xoffset + 121, this.yoffset + 46 + 47 - liquidHeight, 12.0, liquidHeight, this.zLevel);
            this.mc.renderEngine.bindTexture(this.getResourceLocation());
            this.drawTexturedModalRect(this.xoffset + 121, this.yoffset + 46, 176, 69, 12, 47);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Canner.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUICanner.png");
    }
}

