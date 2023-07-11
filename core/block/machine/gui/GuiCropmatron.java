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
import ic2.core.block.machine.container.ContainerCropmatron;
import ic2.core.block.machine.tileentity.TileEntityCropmatron;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

@SideOnly(value=Side.CLIENT)
public class GuiCropmatron
extends GuiIC2 {
    public ContainerCropmatron container;

    public GuiCropmatron(ContainerCropmatron container1) {
        super(container1, 191);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        FluidStack fluidstack = ((TileEntityCropmatron)this.container.base).getFluidStackfromTank();
        if (fluidstack != null && fluidstack.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstack.getFluid().getName() + ": " + fluidstack.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 64, 74, 110, 99);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIconinput;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        if (((TileEntityCropmatron)this.container.base).energy > 0.0) {
            int chargeLevel = (int)(14.0f * ((TileEntityCropmatron)this.container.base).getChargeLevel());
            this.drawTexturedModalRect(this.xoffset + 153, this.yoffset + 95 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
        if (((TileEntityCropmatron)this.container.base).getFluidTank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityCropmatron)this.container.base).getFluidTank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = ((TileEntityCropmatron)this.container.base).gaugeLiquidScaled(24);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 64, this.yoffset + 100 - liquidHeight, 47.0, liquidHeight, this.zLevel);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Cropmatron.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUICropmatron.png");
    }
}

