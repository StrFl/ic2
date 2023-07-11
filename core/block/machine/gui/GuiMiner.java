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
import ic2.core.block.machine.container.ContainerMiner;
import ic2.core.block.machine.tileentity.TileEntityMiner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiMiner
extends GuiIC2 {
    public ContainerMiner container;

    public GuiMiner(ContainerMiner container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int len = Math.round(((TileEntityMiner)this.container.base).getChargeLevel() * 14.0f);
        if (len > 0) {
            this.drawTexturedModalRect(this.xoffset + 151, this.yoffset + 41 + 14 - len, 176, 14 - len, 14, len);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Miner.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIMiner.png");
    }
}

