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
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerPump;
import ic2.core.block.machine.tileentity.TileEntityPump;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

@SideOnly(value=Side.CLIENT)
public class GuiPump
extends GuiIC2 {
    public ContainerPump container;

    public GuiPump(ContainerPump container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        FluidStack fluidstack = ((TileEntityPump)this.container.base).getFluidStackfromTank();
        if (fluidstack != null && fluidstack.getFluid() != null) {
            String tooltip = fluidstack.getFluid().getName() + ": " + fluidstack.amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb");
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, tooltip, 73, 19, 86, 67);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIcon;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int chargeLevel = (int)(14.0f * ((TileEntityPump)this.container.base).getChargeLevel());
        int progress = (int)(24.0f * ((TileEntityPump)this.container.base).guiProgress);
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 7, this.yoffset + 42 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
        if (progress > 0) {
            this.drawTexturedModalRect(this.xoffset + 36, this.yoffset + 34, 176, 14, progress + 1, 16);
        }
        if (((TileEntityPump)this.container.base).getTankAmount() > 0 && (fluidIcon = ((TileEntityPump)this.container.base).getFluidTank().getFluid().getFluid().getIcon()) != null) {
            this.drawTexturedModalRect(this.xoffset + 70, this.yoffset + 16, 176, 30, 20, 55);
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = ((TileEntityPump)this.container.base).gaugeLiquidScaled(47);
            DrawUtil.drawRepeated(fluidIcon, this.xoffset + 74, this.yoffset + 20 + 47 - liquidHeight, 12.0, liquidHeight, this.zLevel);
            this.mc.renderEngine.bindTexture(this.getResourceLocation());
            this.drawTexturedModalRect(this.xoffset + 74, this.yoffset + 20, 176, 85, 12, 47);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Pump.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIPump.png");
    }
}

