/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 */
package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerBlastFurnace;
import ic2.core.block.machine.tileentity.TileEntityBlastFurnace;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiBlastFurnace
extends GuiIC2 {
    public ContainerBlastFurnace container;

    public GuiBlastFurnace(ContainerBlastFurnace container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.BlastFurnace.gui.heat") + " " + ((TileEntityBlastFurnace)this.container.base).getheatpercent() + " %", 67, 65, 109, 80);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.BlastFurnace.gui.progress") + " " + ((TileEntityBlastFurnace)this.container.base).getprogresspercent() + " %", 75, 35, 101, 60);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.BlastFurnace.gui.toolair"), 25, 55, 60, 72, -15, 0);
        if (((TileEntityBlastFurnace)this.container.base).outOfAir) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.BlastFurnace.gui.airmiss"), 116, 25, 136, 47);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int i1 = ((TileEntityBlastFurnace)this.container.base).gaugeHeatScaled(22);
        this.drawTexturedModalRect(this.xoffset + 70, this.yoffset + 69, 176, 0, i1, 8);
        if (((TileEntityBlastFurnace)this.container.base).isHot()) {
            this.drawTexturedModalRect(this.xoffset + 95, this.yoffset + 66, 176, 8, 14, 14);
        }
        if (((TileEntityBlastFurnace)this.container.base).progress > 0) {
            this.drawTexturedModalRect(this.xoffset + 75, this.yoffset + 34, 176, 23, 27, 27);
        }
        if (((TileEntityBlastFurnace)this.container.base).outOfAir) {
            this.drawTexturedModalRect(this.xoffset + 116, this.yoffset + 25, 176, 79, 22, 22);
        }
        int i2 = ((TileEntityBlastFurnace)this.container.base).gaugeprogress(27);
        this.drawTexturedModalRect(this.xoffset + 75, this.yoffset + 34 + 27 - i2, 176, 51, 27, i2);
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.BlastFurnace.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIBlastFurnace.png");
    }
}

