/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.item.tool.ContainerMeter;
import ic2.core.util.GuiTooltipHelper;
import ic2.core.util.Util;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiToolMeter
extends GuiIC2 {
    public ContainerMeter container;
    public String name;

    public GuiToolMeter(ContainerMeter container1) {
        super(container1, 217);
        this.container = container1;
    }

    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        int xMin = (this.width - this.xSize) / 2;
        int yMin = (this.height - this.ySize) / 2;
        int x = i - xMin;
        int y = j - yMin;
        if (x >= 112 && y >= 55 && x <= 131 && y <= 74) {
            this.container.setMode(ContainerMeter.Mode.EnergyIn);
        }
        if (x >= 132 && y >= 55 && x <= 151 && y <= 74) {
            this.container.setMode(ContainerMeter.Mode.EnergyOut);
        }
        if (x >= 112 && y >= 75 && x <= 131 && y <= 94) {
            this.container.setMode(ContainerMeter.Mode.EnergyGain);
        }
        if (x >= 132 && y >= 75 && x <= 151 && y <= 94) {
            this.container.setMode(ContainerMeter.Mode.Voltage);
        }
        if (x >= 26 && y >= 111 && x <= 83 && y <= 123) {
            this.container.reset();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        String unit = this.container.getMode() == ContainerMeter.Mode.Voltage ? "ic2.generic.text.v" : "ic2.generic.text.EUt";
        unit = StatCollector.translateToLocal((String)unit);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode"), 115, 43, 2157374);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.itemToolMEter.avg"), 15, 41, 2157374);
        this.fontRendererObj.drawString("" + Util.toSiString(this.container.getResultAvg(), 6) + unit, 15, 51, 2157374);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.itemToolMEter.max/min"), 15, 64, 2157374);
        this.fontRendererObj.drawString("" + Util.toSiString(this.container.getResultMax(), 6) + unit, 15, 74, 2157374);
        this.fontRendererObj.drawString("" + Util.toSiString(this.container.getResultMin(), 6) + unit, 15, 84, 2157374);
        this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.itemToolMEter.cycle", (Object[])new Object[]{this.container.getResultCount() / 20}), 15, 100, 2157374);
        this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.reset"), 39, 114, 2157374);
        switch (this.container.getMode()) {
            case EnergyIn: {
                this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.EnergyIn"), 105, 100, 2157374);
                break;
            }
            case EnergyOut: {
                this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.EnergyOut"), 105, 100, 2157374);
                break;
            }
            case EnergyGain: {
                this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.EnergyGain"), 105, 100, 2157374);
                break;
            }
            case Voltage: {
                this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.Voltage"), 105, 100, 2157374);
            }
        }
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.switch") + " " + StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.EnergyIn"), 112, 55, 131, 74);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.switch") + " " + StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.EnergyOut"), 132, 55, 151, 74);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.switch") + " " + StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.EnergyGain"), 112, 75, 131, 94);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.switch") + " " + StatCollector.translateToLocal((String)"ic2.itemToolMEter.mode.Voltage"), 132, 75, 151, 94);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        switch (this.container.getMode()) {
            case EnergyIn: {
                this.drawTexturedModalRect(this.xoffset + 112, this.yoffset + 55, 176, 0, 40, 40);
                break;
            }
            case EnergyOut: {
                this.drawTexturedModalRect(this.xoffset + 112, this.yoffset + 55, 176, 40, 40, 40);
                break;
            }
            case EnergyGain: {
                this.drawTexturedModalRect(this.xoffset + 112, this.yoffset + 55, 176, 120, 40, 40);
                break;
            }
            case Voltage: {
                this.drawTexturedModalRect(this.xoffset + 112, this.yoffset + 55, 176, 80, 40, 40);
            }
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.itemToolMEter");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIToolEUMeter.png");
    }
}

