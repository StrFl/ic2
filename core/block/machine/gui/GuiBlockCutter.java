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
import ic2.core.block.machine.container.ContainerBlockCutter;
import ic2.core.block.machine.tileentity.TileEntityBlockCutter;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiBlockCutter
extends GuiIC2 {
    public ContainerBlockCutter container;

    public GuiBlockCutter(ContainerBlockCutter container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.BlockCutter.gui.bladeslot"), 70, 33, 85, 51, -15, 0);
        if (((TileEntityBlockCutter)this.container.base).isbladetoweak()) {
            GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.BlockCutter.gui.bladetoweak"), 63, 54, 93, 80);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        if (((TileEntityBlockCutter)this.container.base).isbladetoweak()) {
            this.drawTexturedModalRect(this.xoffset + 63, this.yoffset + 54, 176, 34, 30, 26);
        }
        int chargeLevel = (int)(14.0f * ((TileEntityBlockCutter)this.container.base).getChargeLevel());
        int progress = (int)(46.0f * ((TileEntityBlockCutter)this.container.base).getProgress());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 26, this.yoffset + 36 + 14 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
        if (progress > 0) {
            this.drawTexturedModalRect(this.xoffset + 55, this.yoffset + 33, 176, 14, progress + 1, 19);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.BlockCutter.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIBlockCutter.png");
    }
}

