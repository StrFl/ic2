/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 */
package ic2.core.block.machine.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerItemBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiItemBuffer
extends GuiIC2 {
    public GuiItemBuffer(ContainerItemBuffer container) {
        super(container);
        this.container = container;
        this.ySize = 232;
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.ItemBuffer.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIItemBuffer.png");
    }
}

