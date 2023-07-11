/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.core.block.machine.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerFluidDistributor;
import ic2.core.block.machine.tileentity.TileEntityFluidDistributor;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GuiFluidDistributor
extends GuiIC2 {
    public ContainerFluidDistributor container;

    public GuiFluidDistributor(ContainerFluidDistributor container1) {
        super(container1);
        this.container = container1;
        this.ySize = 184;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        FluidStack fluidstack = ((TileEntityFluidDistributor)this.container.base).getFluidStackfromTank();
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.FluidDistributor.gui.mode.info"), 112, 47, 5752026);
        if (((TileEntityFluidDistributor)this.container.base).getActive()) {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.FluidDistributor.gui.mode.concentrate"), 95, 71, 5752026);
        } else {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.FluidDistributor.gui.mode.distribute"), 95, 71, 5752026);
        }
        if (fluidstack != null && fluidstack.getFluid() != null) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, fluidstack.getFluid().getName() + ": " + fluidstack.amount + " " + StatCollector.translateToLocal((String)"ic2.generic.text.mb"), 29, 37, 83, 84);
        }
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        if (x >= 117 && y >= 58 && x <= 135 && y <= 66) {
            IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, 1);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIconinput;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        if (((TileEntityFluidDistributor)this.container.base).getFluidTank().getFluidAmount() > 0 && (fluidIconinput = FluidRegistry.getFluid((int)((TileEntityFluidDistributor)this.container.base).getFluidTank().getFluid().fluidID).getIcon()) != null) {
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = ((TileEntityFluidDistributor)this.container.base).gaugeLiquidScaled(47);
            DrawUtil.drawRepeated(fluidIconinput, this.xoffset + 29, this.yoffset + 85 - liquidHeight, 55.0, liquidHeight, this.zLevel);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.FluidDistributor.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIFluidDistributor.png");
    }
}

