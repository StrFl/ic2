/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.inventory.Container
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.generator.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.generator.container.ContainerWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiWaterGenerator
extends GuiContainer {
    public ContainerWaterGenerator container;
    public String name;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIWaterGenerator.png");

    public GuiWaterGenerator(ContainerWaterGenerator container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.WaterGenerator.gui.name");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
        if (((TileEntityWaterGenerator)this.container.base).fuel > 0) {
            int l = ((TileEntityWaterGenerator)this.container.base).gaugeFuelScaled(14);
            this.drawTexturedModalRect(j + 80, k + 36 + 14 - l, 176, 14 - l, 14, l);
        }
    }
}

