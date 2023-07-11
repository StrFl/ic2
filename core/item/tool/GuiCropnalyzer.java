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
package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.item.tool.ContainerCropnalyzer;
import ic2.core.item.tool.HandHeldCropnalyzer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiCropnalyzer
extends GuiContainer {
    public ContainerCropnalyzer container;
    public String name;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUICropnalyzer.png");

    public GuiCropnalyzer(ContainerCropnalyzer container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.itemCropnalyzer");
        this.ySize = 223;
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, 74, 11, 0);
        int level = ((HandHeldCropnalyzer)this.container.base).getScannedLevel();
        if (level <= -1) {
            return;
        }
        if (level == 0) {
            this.fontRendererObj.drawString("UNKNOWN", 8, 37, 0xFFFFFF);
            return;
        }
        this.fontRendererObj.drawString(((HandHeldCropnalyzer)this.container.base).getSeedName(), 8, 37, 0xFFFFFF);
        if (level >= 2) {
            this.fontRendererObj.drawString("Tier: " + ((HandHeldCropnalyzer)this.container.base).getSeedTier(), 8, 50, 0xFFFFFF);
            this.fontRendererObj.drawString("Discovered by:", 8, 73, 0xFFFFFF);
            this.fontRendererObj.drawString(((HandHeldCropnalyzer)this.container.base).getSeedDiscovered(), 8, 86, 0xFFFFFF);
        }
        if (level >= 3) {
            this.fontRendererObj.drawString(((HandHeldCropnalyzer)this.container.base).getSeedDesc(0), 8, 109, 0xFFFFFF);
            this.fontRendererObj.drawString(((HandHeldCropnalyzer)this.container.base).getSeedDesc(1), 8, 122, 0xFFFFFF);
        }
        if (level >= 4) {
            this.fontRendererObj.drawString("Growth:", 118, 37, 11403055);
            this.fontRendererObj.drawString("" + ((HandHeldCropnalyzer)this.container.base).getSeedGrowth(), 118, 50, 11403055);
            this.fontRendererObj.drawString("Gain:", 118, 73, 15649024);
            this.fontRendererObj.drawString("" + ((HandHeldCropnalyzer)this.container.base).getSeedGain(), 118, 86, 15649024);
            this.fontRendererObj.drawString("Resis.:", 118, 109, 52945);
            this.fontRendererObj.drawString("" + ((HandHeldCropnalyzer)this.container.base).getSeedResistence(), 118, 122, 52945);
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

