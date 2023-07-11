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
import ic2.core.block.machine.container.ContainerMatter;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

@SideOnly(value=Side.CLIENT)
public class GuiMatter
extends GuiIC2 {
    public ContainerMatter container;
    public String progressLabel;
    public String amplifierLabel;

    public GuiMatter(ContainerMatter container1) {
        super(container1);
        this.container = container1;
        this.progressLabel = StatCollector.translateToLocal((String)"ic2.Matter.gui.info.progress");
        this.amplifierLabel = StatCollector.translateToLocal((String)"ic2.Matter.gui.info.amplifier");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.progressLabel, 8, 22, 0x404040);
        this.fontRendererObj.drawString(((TileEntityMatter)this.container.base).getProgressAsString(), 18, 31, 0x404040);
        if (((TileEntityMatter)this.container.base).scrap > 0) {
            this.fontRendererObj.drawString(this.amplifierLabel, 8, 46, 0x404040);
            this.fontRendererObj.drawString("" + ((TileEntityMatter)this.container.base).scrap, 8, 58, 0x404040);
        }
        super.drawGuiContainerForegroundLayer(par1, par2);
        FluidStack fluidstack = ((TileEntityMatter)this.container.base).getFluidStackfromTank();
        if (fluidstack != null) {
            String tooltip = fluidstack.getFluid().getName() + ": " + fluidstack.amount + StatCollector.translateToLocal((String)"ic2.generic.text.mb");
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, tooltip, 99, 25, 112, 73);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIcon;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        if (((TileEntityMatter)this.container.base).getTankAmount() > 0 && (fluidIcon = ((TileEntityMatter)this.container.base).getFluidTank().getFluid().getFluid().getIcon()) != null) {
            this.drawTexturedModalRect(this.xoffset + 96, this.yoffset + 22, 176, 0, 20, 55);
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = ((TileEntityMatter)this.container.base).gaugeLiquidScaled(47);
            DrawUtil.drawRepeated(fluidIcon, this.xoffset + 100, this.yoffset + 26 + 47 - liquidHeight, 12.0, liquidHeight, this.zLevel);
            this.mc.renderEngine.bindTexture(this.getResourceLocation());
            this.drawTexturedModalRect(this.xoffset + 100, this.yoffset + 26, 176, 55, 12, 47);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Matter.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIMatter.png");
    }
}

