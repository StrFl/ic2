/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.inventory.Container
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerScanner;
import ic2.core.block.machine.tileentity.TileEntityScanner;
import ic2.core.util.GuiTooltipHelper;
import ic2.core.util.Util;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiScanner
extends GuiContainer {
    public ContainerScanner container;
    public String[] info = new String[9];
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIScanner.png");

    public GuiScanner(ContainerScanner container1) {
        super((Container)container1);
        this.container = container1;
        this.info[0] = StatCollector.translateToLocal((String)"ic2.Scanner.gui.name");
        this.info[1] = StatCollector.translateToLocal((String)"ic2.Scanner.gui.info1");
        this.info[2] = StatCollector.translateToLocal((String)"ic2.Scanner.gui.info2");
        this.info[3] = StatCollector.translateToLocal((String)"ic2.Scanner.gui.info3");
        this.info[4] = StatCollector.translateToLocal((String)"ic2.Scanner.gui.info4");
        this.info[5] = StatCollector.translateToLocal((String)"ic2.Scanner.gui.info5");
        this.info[6] = StatCollector.translateToLocal((String)"ic2.Scanner.gui.info6");
        this.info[7] = StatCollector.translateToLocal((String)"ic2.Scanner.gui.info7");
        this.info[8] = StatCollector.translateToLocal((String)"ic2.Scanner.gui.info8");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.info[0], (this.xSize / 2 - this.fontRendererObj.getStringWidth(this.info[0])) / 2, 6, 0x404040);
        this.fontRendererObj.drawString(this.info[5] + ":", 105, 6, 0x404040);
        TileEntityScanner te = (TileEntityScanner)this.container.base;
        switch (te.getState()) {
            case IDLE: {
                this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.Scanner.gui.idle"), 10, 69, 15461152);
                break;
            }
            case NO_STORAGE: {
                this.fontRendererObj.drawString(this.info[2], 10, 69, 15461152);
                break;
            }
            case SCANNING: {
                this.fontRendererObj.drawString(this.info[1], 10, 69, 2157374);
                this.fontRendererObj.drawString(te.getPercentageDone() + "%", 125, 69, 2157374);
                break;
            }
            case NO_ENERGY: {
                this.fontRendererObj.drawString(this.info[3], 10, 69, 14094352);
                break;
            }
            case ALREADY_RECORDED: {
                this.fontRendererObj.drawString(this.info[8], 10, 69, 14094352);
                break;
            }
            case FAILED: {
                this.fontRendererObj.drawString(this.info[4], 10, 69, 2157374);
                this.fontRendererObj.drawString(this.info[6], 110, 30, 14094352);
                break;
            }
            case COMPLETED: 
            case TRANSFER_ERROR: {
                if (te.getState() == TileEntityScanner.State.COMPLETED) {
                    this.fontRendererObj.drawString(this.info[4], 10, 69, 2157374);
                }
                if (te.getState() == TileEntityScanner.State.TRANSFER_ERROR) {
                    this.fontRendererObj.drawString(this.info[7], 10, 69, 14094352);
                }
                this.fontRendererObj.drawString(Util.toSiString(te.patternUu, 4) + "B UUM", 105, 25, 0xFFFFFF);
                this.fontRendererObj.drawString(Util.toSiString(te.patternEu, 4) + "EU", 105, 36, 0xFFFFFF);
            }
        }
        if (te.getState() == TileEntityScanner.State.COMPLETED || te.getState() == TileEntityScanner.State.TRANSFER_ERROR || te.getState() == TileEntityScanner.State.FAILED) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.Scanner.gui.button.delete"), 102, 49, 114, 61);
            if (te.getState() != TileEntityScanner.State.FAILED) {
                GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.Scanner.gui.button.save"), 143, 49, 167, 61);
            }
        }
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        TileEntityScanner te = (TileEntityScanner)this.container.base;
        if (te.isDone()) {
            if (x >= 102 && y >= 49 && x <= 111 && y <= 61) {
                IC2.network.get().initiateClientTileEntityEvent(te, 0);
            }
            if (x >= 143 && y >= 49 && x <= 167 && y <= 61) {
                IC2.network.get().initiateClientTileEntityEvent(te, 1);
            }
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
        TileEntityScanner te = (TileEntityScanner)this.container.base;
        if (te.isDone()) {
            this.drawTexturedModalRect(j + 102, k + 49, 176, 57, 12, 12);
            this.drawTexturedModalRect(j + 143, k + 49, 176, 69, 24, 12);
        }
        int chargeLevel = (int)(14.0f * te.getChargeLevel());
        int scanningloop = te.getSubPercentageDoneScaled(66);
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(j + 9, k + 38 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
        if (scanningloop > 0) {
            this.drawTexturedModalRect(j + 30, k + 20, 176, 14, scanningloop, 43);
        }
    }
}

