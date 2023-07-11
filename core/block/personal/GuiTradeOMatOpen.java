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
import ic2.core.block.personal.ContainerTradeOMatOpen;
import ic2.core.block.personal.TileEntityTradeOMat;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiTradeOMatOpen
extends GuiContainer {
    private final ContainerTradeOMatOpen container;
    private final String name;
    private final String wantLabel;
    private final String offerLabel;
    private final String totalTradesLabel0;
    private final String totalTradesLabel1;
    private final String stockLabel;
    private final String inv;
    private final boolean isAdmin;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUITradeOMatOpen.png");

    public GuiTradeOMatOpen(ContainerTradeOMatOpen container1, boolean isAdmin1) {
        super((Container)container1);
        this.container = container1;
        this.name = StatCollector.translateToLocal((String)"ic2.blockPersonalTrader");
        this.wantLabel = StatCollector.translateToLocal((String)"ic2.container.personalTrader.want");
        this.offerLabel = StatCollector.translateToLocal((String)"ic2.container.personalTrader.offer");
        this.totalTradesLabel0 = StatCollector.translateToLocal((String)"ic2.container.personalTrader.totalTrades0");
        this.totalTradesLabel1 = StatCollector.translateToLocal((String)"ic2.container.personalTrader.totalTrades1");
        this.stockLabel = StatCollector.translateToLocal((String)"ic2.container.personalTrader.stock");
        this.inv = StatCollector.translateToLocal((String)"container.inventory");
        this.isAdmin = isAdmin1;
    }

    public void initGui() {
        super.initGui();
        if (this.isAdmin) {
            this.buttonList.add(new GuiButton(0, (this.width - this.xSize) / 2 + 152, (this.height - this.ySize) / 2 + 4, 20, 20, "\u221e"));
        }
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 0x404040);
        this.fontRendererObj.drawString(this.inv, 8, this.ySize - 96 + 2, 0x404040);
        this.fontRendererObj.drawString(this.wantLabel, 12, 23, 0x404040);
        this.fontRendererObj.drawString(this.offerLabel, 12, 57, 0x404040);
        this.fontRendererObj.drawString(this.totalTradesLabel0, 108, 28, 0x404040);
        this.fontRendererObj.drawString(this.totalTradesLabel1, 108, 36, 0x404040);
        this.fontRendererObj.drawString("" + ((TileEntityTradeOMat)this.container.base).totalTradeCount, 112, 44, 0x404040);
        this.fontRendererObj.drawString(this.stockLabel + " " + (((TileEntityTradeOMat)this.container.base).stock < 0 ? "\u221e" : "" + ((TileEntityTradeOMat)this.container.base).stock), 108, 60, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
    }

    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        if (guibutton.id == 0) {
            IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, 0);
        }
    }
}

