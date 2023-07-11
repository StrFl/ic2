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
import ic2.core.block.machine.container.ContainerInduction;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiInduction
extends GuiIC2 {
    public ContainerInduction container;
    public String heatLabel;

    public GuiInduction(ContainerInduction container1) {
        super(container1);
        this.container = container1;
        this.heatLabel = StatCollector.translateToLocal((String)"ic2.generic.text.heat");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.heatLabel, 10, 36, 0x404040);
        this.fontRendererObj.drawString(((TileEntityInduction)this.container.base).getHeat(), 10, 44, 0x404040);
        super.drawGuiContainerForegroundLayer(par1, par2);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int chargeLevel = Math.round(((TileEntityInduction)this.container.base).getChargeLevel() * 14.0f);
        this.drawTexturedModalRect(this.xoffset + 56, this.yoffset + 36 + 14 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        int i1 = ((TileEntityInduction)this.container.base).gaugeProgressScaled(24);
        this.drawTexturedModalRect(this.xoffset + 79, this.yoffset + 34, 176, 14, i1 + 1, 16);
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Induction.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIInduction.png");
    }
}

