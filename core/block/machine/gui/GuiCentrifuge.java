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
import ic2.core.block.machine.container.ContainerCentrifuge;
import ic2.core.block.machine.tileentity.TileEntityCentrifuge;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiCentrifuge
extends GuiIC2 {
    public ContainerCentrifuge container;

    public GuiCentrifuge(ContainerCentrifuge container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, StatCollector.translateToLocal((String)"ic2.generic.text.heat") + " " + ((TileEntityCentrifuge)this.container.base).heat + "/" + ((TileEntityCentrifuge)this.container.base).workheat, 65, 61, 91, 76);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        if (((TileEntityCentrifuge)this.container.base).getActive()) {
            this.drawTexturedModalRect(this.xoffset + 93, this.yoffset + 62, 177, 36, 14, 14);
        }
        int i2 = ((TileEntityCentrifuge)this.container.base).gaugeHeatScaled(22);
        this.drawTexturedModalRect(this.xoffset + 67, this.yoffset + 67, 176, 30, i2, 6);
        int chargeLevel = (int)(14.0f * ((TileEntityCentrifuge)this.container.base).getChargeLevel());
        int progress = (int)(30.0f * ((TileEntityCentrifuge)this.container.base).getProgress());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 11, this.yoffset + 42 + 14 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
        if (progress > 0) {
            this.drawTexturedModalRect(this.xoffset + 84, this.yoffset + 54 - progress, 177, 50, 5, progress);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Centrifuge.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUITermalCentrifuge.png");
    }
}

