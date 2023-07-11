/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.inventory.Container
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.personal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.personal.ContainerEnergyOMatOpen;
import ic2.core.block.personal.TileEntityEnergyOMat;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiEnergyOMatOpen
extends GuiContainer {
    public ContainerEnergyOMatOpen container;
    public String name;
    public String offerLabel;
    public String inv;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIEnergyOMatOpen.png");

    public GuiEnergyOMatOpen(ContainerEnergyOMatOpen container1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.blockPersonalTraderEnergy");
        this.offerLabel = StatCollector.translateToLocal((String)"ic2.container.personalTrader.offer");
        this.inv = StatCollector.translateToLocal((String)"container.inventory");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 0x404040);
        this.fontRendererObj.drawString(this.inv, 8, this.ySize - 96 + 2, 0x404040);
        this.fontRendererObj.drawString(this.offerLabel, 100, 60, 0x404040);
        this.fontRendererObj.drawString(((TileEntityEnergyOMat)this.container.base).euOffer + " EU", 100, 68, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
    }

    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.guiLeft + 102, this.guiTop + 16, 32, 10, "-100k"));
        this.buttonList.add(new GuiButton(1, this.guiLeft + 102, this.guiTop + 26, 32, 10, "-10k"));
        this.buttonList.add(new GuiButton(2, this.guiLeft + 102, this.guiTop + 36, 32, 10, "-1k"));
        this.buttonList.add(new GuiButton(3, this.guiLeft + 102, this.guiTop + 46, 32, 10, "-100"));
        this.buttonList.add(new GuiButton(4, this.guiLeft + 134, this.guiTop + 16, 32, 10, "+100k"));
        this.buttonList.add(new GuiButton(5, this.guiLeft + 134, this.guiTop + 26, 32, 10, "+10k"));
        this.buttonList.add(new GuiButton(6, this.guiLeft + 134, this.guiTop + 36, 32, 10, "+1k"));
        this.buttonList.add(new GuiButton(7, this.guiLeft + 134, this.guiTop + 46, 32, 10, "+100"));
    }

    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, guibutton.id);
    }
}

