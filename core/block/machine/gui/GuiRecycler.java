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
import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityRecycler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiRecycler
extends GuiIC2 {
    public ContainerStandardMachine<? extends TileEntityRecycler> container;

    public GuiRecycler(ContainerStandardMachine<? extends TileEntityRecycler> container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int chargeLevel = (int)(14.0f * ((TileEntityRecycler)this.container.base).getChargeLevel());
        int progress = (int)(24.0f * ((TileEntityRecycler)this.container.base).getProgress());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 56, this.yoffset + 36 + 14 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
        if (progress > 0) {
            this.drawTexturedModalRect(this.xoffset + 79, this.yoffset + 34, 176, 14, progress + 1, 16);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Recycler.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIRecycler.png");
    }
}

