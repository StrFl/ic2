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
import ic2.core.block.BlockPoleFence;
import ic2.core.block.machine.container.ContainerMagnetizer;
import ic2.core.block.machine.tileentity.TileEntityMagnetizer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiMagnetizer
extends GuiIC2 {
    public GuiMagnetizer(ContainerMagnetizer container) {
        super(container);
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.blockMagnetizer");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIMagnetizer.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        if (BlockPoleFence.hasMetalShoes(((ContainerMagnetizer)this.container).player)) {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.Magnetizer.gui.hasMetalShoes"), 18, 66, 0x40FF40);
        } else {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.Magnetizer.gui.noMetalShoes"), 18, 66, 0xFF4040);
        }
        super.drawGuiContainerForegroundLayer(par1, par2);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int chargeLevel = (int)(14.0f * ((TileEntityMagnetizer)((ContainerMagnetizer)this.container).base).getChargeLevel());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 7, this.yoffset + 42 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
    }
}

