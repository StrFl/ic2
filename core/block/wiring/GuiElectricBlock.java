/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.wiring;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIconButton;
import ic2.core.IC2;
import ic2.core.block.wiring.ContainerElectricBlock;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiElectricBlock
extends GuiContainer {
    private final ContainerElectricBlock container;
    private final String armorInv;
    private final String level;
    private final String name;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIElectricBlock.png");

    public GuiElectricBlock(ContainerElectricBlock container1) {
        super((Container)container1);
        this.ySize = 196;
        this.container = container1;
        this.armorInv = StatCollector.translateToLocal((String)"ic2.EUStorage.gui.info.armor");
        this.level = StatCollector.translateToLocal((String)"ic2.EUStorage.gui.info.level");
        switch (((TileEntityElectricBlock)container1.base).tier) {
            case 1: {
                this.name = StatCollector.translateToLocal((String)"ic2.blockBatBox");
                break;
            }
            case 2: {
                this.name = StatCollector.translateToLocal((String)"ic2.blockCESU");
                break;
            }
            case 3: {
                this.name = StatCollector.translateToLocal((String)"ic2.blockMFE");
                break;
            }
            case 4: {
                this.name = StatCollector.translateToLocal((String)"ic2.blockMFSU");
                break;
            }
            default: {
                this.name = null;
            }
        }
    }

    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiIconButton(0, (this.width - this.xSize) / 2 + 152, (this.height - this.ySize) / 2 + 4, 20, 20, new ItemStack(Items.redstone), true));
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(this.name, (this.xSize - this.fontRendererObj.getStringWidth(this.name)) / 2, 6, 0x404040);
        this.fontRendererObj.drawString(this.armorInv, 8, this.ySize - 126 + 3, 0x404040);
        this.fontRendererObj.drawString(this.level, 79, 25, 0x404040);
        int e = (int)Math.min(((TileEntityElectricBlock)this.container.base).energy, (double)((TileEntityElectricBlock)this.container.base).maxStorage);
        this.fontRendererObj.drawString(" " + e, 110, 35, 0x404040);
        this.fontRendererObj.drawString("/" + ((TileEntityElectricBlock)this.container.base).maxStorage, 110, 45, 0x404040);
        String output = StatCollector.translateToLocalFormatted((String)"ic2.EUStorage.gui.info.output", (Object[])new Object[]{((TileEntityElectricBlock)this.container.base).output});
        this.fontRendererObj.drawString(output, 85, 60, 0x404040);
        GuiTooltipHelper.drawAreaTooltip(par1 - this.guiLeft, par2 - this.guiTop, ((TileEntityElectricBlock)this.container.base).getredstoneMode(), 153, 3, 172, 22);
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(background);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
        if (((TileEntityElectricBlock)this.container.base).energy > 0.0) {
            int i1 = (int)(24.0f * ((TileEntityElectricBlock)this.container.base).getChargeLevel());
            this.drawTexturedModalRect(j + 79, k + 34, 176, 14, i1 + 1, 16);
        }
    }

    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        if (guibutton.id == 0) {
            IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, 0);
        }
    }
}

