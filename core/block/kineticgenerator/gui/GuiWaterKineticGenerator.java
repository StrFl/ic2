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
package ic2.core.block.kineticgenerator.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.kineticgenerator.container.ContainerWaterKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiWaterKineticGenerator
extends GuiContainer {
    public ContainerWaterKineticGenerator container;
    public String name;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIWaterKineticGenerator.png");

    public GuiWaterKineticGenerator(ContainerWaterKineticGenerator container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.WaterKineticGenerator.gui.name");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 0x404040);
        if (((TileEntityWaterKineticGenerator)this.container.base).type == -1) {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.WaterKineticGenerator.gui.wrongbiome1"), 38, 52, 2157374);
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.WaterKineticGenerator.gui.wrongbiome2"), 45, 69, 2157374);
        } else if (!((TileEntityWaterKineticGenerator)this.container.base).rotorSlot.isEmpty()) {
            if (((TileEntityWaterKineticGenerator)this.container.base).checkSpace(((TileEntityWaterKineticGenerator)this.container.base).getRotorDiameter(), true) != 0) {
                this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.WaterKineticGenerator.gui.rotorspace"), 20, 52, 2157374);
            } else {
                this.fontRendererObj.drawString(StatCollector.translateToLocalFormatted((String)"ic2.WaterKineticGenerator.gui.output", (Object[])new Object[]{((TileEntityWaterKineticGenerator)this.container.base).getKuOutput()}), 55, 52, 2157374);
                this.fontRendererObj.drawString(((TileEntityWaterKineticGenerator)this.container.base).getRotorhealth() + " %", 46, 70, 2157374);
            }
        } else {
            this.fontRendererObj.drawString(StatCollector.translateToLocal((String)"ic2.WaterKineticGenerator.gui.rotormiss"), 27, 52, 2157374);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
    }
}

