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
import ic2.core.item.tool.ContainerContainmentbox;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(value=Side.CLIENT)
public class GuiContainmentbox
extends GuiIC2 {
    public ContainerContainmentbox container;

    public GuiContainmentbox(ContainerContainmentbox container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.itemContainmentbox");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIContainmentbox.png");
    }
}

