/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.inventory.Container
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerPatternStorage;
import ic2.core.block.machine.tileentity.TileEntityPatternStorage;
import ic2.core.util.GuiTooltipHelper;
import ic2.core.util.Util;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiPatternStorage
extends GuiContainer {
    public ContainerPatternStorage container;
    public String info;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIPatternStorage.png");

    public GuiPatternStorage(ContainerPatternStorage container1) {
        super((Container)container1);
        this.container = container1;
        this.info = StatCollector.translateToLocal((String)"ic2.PatternStorage.gui.name");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        TileEntityPatternStorage te = (TileEntityPatternStorage)this.container.base;
        this.fontRendererObj.drawString(this.info, (this.xSize - this.fontRendererObj.getStringWidth(this.info)) / 2, 6, 0x404040);
        String idxStr = Math.min(te.index + 1, te.maxIndex) + " / " + te.maxIndex;
        this.fontRendererObj.drawString(idxStr, (this.xSize - this.fontRendererObj.getStringWidth(idxStr)) / 2, 30, 0x404040);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.generic.text.Name") + " ", 10, 48, 0xFFFFFF);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.generic.text.UUMatte") + " ", 10, 59, 0xFFFFFF);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.generic.text.Energy") + " ", 10, 70, 0xFFFFFF);
        if (te.pattern != null) {
            this.fontRendererObj.drawString(Util.toSiString(te.patternUu, 4) + StatCollector.translateToLocal((String)"ic2.generic.text.bucketUnit"), 80, 59, 0xFFFFFF);
            this.fontRendererObj.drawString(Util.toSiString(te.patternEu, 4) + StatCollector.translateToLocal((String)"ic2.generic.text.EU"), 80, 70, 0xFFFFFF);
            this.fontRendererObj.drawString(te.pattern.getDisplayName(), 40, 48, 0xFFFFFF);
        }
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.PatternStorage.gui.info.last"), 7, 19, 15, 36);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.PatternStorage.gui.info.next"), 36, 19, 44, 36);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.PatternStorage.gui.info.export"), 10, 37, 25, 44);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.PatternStorage.gui.info.import"), 26, 37, 41, 44);
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        TileEntityPatternStorage te = (TileEntityPatternStorage)this.container.base;
        if (x >= 7 && y >= 19 && x <= 15 && y <= 36) {
            IC2.network.get().initiateClientTileEntityEvent(te, 0);
        } else if (x >= 36 && y >= 19 && x <= 44 && y <= 36) {
            IC2.network.get().initiateClientTileEntityEvent(te, 1);
        } else if (x >= 10 && y >= 37 && x <= 25 && y <= 44) {
            IC2.network.get().initiateClientTileEntityEvent(te, 2);
        } else if (x >= 26 && y >= 37 && x <= 41 && y <= 44) {
            IC2.network.get().initiateClientTileEntityEvent(te, 3);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
        TileEntityPatternStorage te = (TileEntityPatternStorage)this.container.base;
        if (te.pattern != null) {
            RenderItem renderItem = new RenderItem();
            renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, te.pattern, j + 152, k + 29);
        }
    }
}

