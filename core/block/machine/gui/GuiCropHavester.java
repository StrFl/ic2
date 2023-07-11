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
import ic2.core.block.machine.container.ContainerCropHavester;
import ic2.core.block.machine.tileentity.TileEntityCropHavester;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiCropHavester
extends GuiIC2 {
    public ContainerCropHavester container;

    public GuiCropHavester(ContainerCropHavester container1) {
        super(container1, 191);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        if (((TileEntityCropHavester)this.container.base).energy > 0.0) {
            int chargeLevel = (int)(14.0f * ((TileEntityCropHavester)this.container.base).getChargeLevel());
            this.drawTexturedModalRect(this.xoffset + 153, this.yoffset + 56 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.CropHavester.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUICropHavester.png");
    }
}

