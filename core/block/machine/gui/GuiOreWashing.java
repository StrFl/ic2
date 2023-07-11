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
import ic2.core.block.machine.container.ContainerOreWashing;
import ic2.core.block.machine.tileentity.TileEntityOreWashing;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

@SideOnly(value=Side.CLIENT)
public class GuiOreWashing
extends GuiIC2 {
    public ContainerOreWashing container;

    public GuiOreWashing(ContainerOreWashing container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        FluidStack fluidstack = ((TileEntityOreWashing)this.container.base).getFluidStackfromTank();
        if (fluidstack != null) {
            String tooltip = fluidstack.getFluid().getName() + ": " + fluidstack.amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb");
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, tooltip, 63, 23, 76, 71);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIcon;
        TileEntityOreWashing te;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int chargeLevel = (int)(14.0f * ((TileEntityOreWashing)this.container.base).getChargeLevel());
        int progress = (int)(20.0f * ((TileEntityOreWashing)this.container.base).getProgress());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 9, this.yoffset + 57 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
        if (progress > 0) {
            this.drawTexturedModalRect(this.xoffset + 102, this.yoffset + 38, 176, 117, progress + 1, 20);
        }
        if ((te = (TileEntityOreWashing)this.container.base).getTankAmount() > 0 && (fluidIcon = te.getFluidTank().getFluid().getFluid().getIcon()) != null) {
            this.drawTexturedModalRect(this.xoffset + 60, this.yoffset + 20, 176, 15, 20, 55);
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = te.gaugeLiquidScaled(47);
            DrawUtil.drawRepeated(fluidIcon, this.xoffset + 64, this.yoffset + 24 + 47 - liquidHeight, 12.0, liquidHeight, this.zLevel);
            this.mc.renderEngine.bindTexture(this.getResourceLocation());
            this.drawTexturedModalRect(this.xoffset + 64, this.yoffset + 24, 176, 70, 12, 47);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.OreWashingPlan.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIOreWashingPlant.png");
    }
}

