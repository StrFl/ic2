/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.entity.RenderItem
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
import ic2.core.block.machine.container.ContainerReplicator;
import ic2.core.block.machine.tileentity.TileEntityReplicator;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import ic2.core.util.Util;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

@SideOnly(value=Side.CLIENT)
public class GuiReplicator
extends GuiIC2 {
    public ContainerReplicator container;

    public GuiReplicator(ContainerReplicator container1) {
        super(container1, 184);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        TileEntityReplicator te = (TileEntityReplicator)this.container.base;
        int progressUu = 0;
        int progressEu = 0;
        if (te.patternUu != 0.0) {
            progressUu = Math.min((int)Math.round(100.0 * te.uuProcessed / te.patternUu), 100);
        }
        switch (te.getMode()) {
            case STOPPED: {
                this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.Replicator.gui.info.Waiting"), 54, 41, 15461152);
                break;
            }
            case SINGLE: {
                this.fontRendererObj.drawString("UU:" + progressUu + "%", 51, 41, 2157374);
                this.fontRendererObj.drawString("EU:" + progressEu + "%", 92, 41, 2157374);
                this.fontRendererObj.drawString(">", 134, 40, 2157374);
                break;
            }
            case CONTINUOUS: {
                this.fontRendererObj.drawString("UU:" + progressUu + "%", 51, 41, 2157374);
                this.fontRendererObj.drawString("EU:" + progressEu + "%", 92, 41, 2157374);
                this.fontRendererObj.drawString(">>", 130, 41, 2157374);
            }
        }
        FluidStack fluidstack = te.getFluidStackfromTank();
        if (fluidstack != null) {
            String tooltip = fluidstack.getFluid().getName() + ": " + fluidstack.amount + "mB";
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, tooltip, 30, 33, 43, 81);
        }
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.Replicator.gui.info.Stop"), 74, 81, 91, 98);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.Replicator.gui.info.single"), 92, 81, 108, 98);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.Replicator.gui.info.repeat"), 109, 81, 125, 98);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.Replicator.gui.info.last"), 80, 16, 88, 33);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.Replicator.gui.info.next"), 109, 16, 117, 33);
        if (te.pattern != null) {
            String uuReq = Util.toSiString(te.patternUu, 4) + StatCollector.translateToLocal((String)"ic2.generic.text.bucketUnit");
            String euReq = Util.toSiString(te.patternEu, 4) + StatCollector.translateToLocal((String)"ic2.generic.text.EU");
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, te.pattern.getDisplayName() + " UU: " + uuReq + " EU: " + euReq, 90, 16, 107, 33);
        }
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        TileEntityReplicator te = (TileEntityReplicator)this.container.base;
        if (x >= 80 && y >= 16 && x <= 88 && y <= 33) {
            IC2.network.get().initiateClientTileEntityEvent(te, 0);
        }
        if (x >= 109 && y >= 16 && x <= 117 && y <= 33) {
            IC2.network.get().initiateClientTileEntityEvent(te, 1);
        }
        if (x >= 75 && y >= 82 && x <= 90 && y <= 97) {
            IC2.network.get().initiateClientTileEntityEvent(te, 3);
        }
        if (x >= 92 && y >= 82 && x <= 107 && y <= 97) {
            IC2.network.get().initiateClientTileEntityEvent(te, 4);
        }
        if (x >= 109 && y >= 82 && x <= 124 && y <= 97) {
            IC2.network.get().initiateClientTileEntityEvent(te, 5);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        IIcon fluidIcon;
        super.drawGuiContainerBackgroundLayer(f, x, y);
        TileEntityReplicator te = (TileEntityReplicator)this.container.base;
        int chargeLevel = (int)(14.0f * te.getChargeLevel());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 132, this.yoffset + 97 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
        if (te.getTankAmount() > 0 && (fluidIcon = te.getFluidTank().getFluid().getFluid().getIcon()) != null) {
            this.drawTexturedModalRect(this.xoffset + 27, this.yoffset + 30, 176, 15, 20, 55);
            this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int liquidHeight = te.gaugeLiquidScaled(47);
            DrawUtil.drawRepeated(fluidIcon, this.xoffset + 31, this.yoffset + 34 + 47 - liquidHeight, 12.0, liquidHeight, this.zLevel);
            this.mc.renderEngine.bindTexture(this.getResourceLocation());
            this.drawTexturedModalRect(this.xoffset + 31, this.yoffset + 34, 176, 70, 12, 47);
        }
        if (te.pattern != null) {
            RenderItem renderItem = new RenderItem();
            renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, te.pattern, this.xoffset + 91, this.yoffset + 17);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Replicator.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIReplicator.png");
    }
}

