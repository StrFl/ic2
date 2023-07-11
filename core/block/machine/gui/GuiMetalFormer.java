/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.container.ContainerMetalFormer;
import ic2.core.block.machine.tileentity.TileEntityMetalFormer;
import ic2.core.util.GuiTooltipHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiMetalFormer
extends GuiIC2 {
    public ContainerMetalFormer container;

    public GuiMetalFormer(ContainerMetalFormer container1) {
        super(container1);
        this.container = container1;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String tooltip = "";
        GL11.glPushAttrib((int)64);
        RenderItem renderItem = new RenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        switch (((TileEntityMetalFormer)this.container.base).getMode()) {
            case 0: {
                renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.copperCableItem, 67, 55);
                tooltip = StatCollector.translateToLocal((String)"ic2.MetalFormer.gui.switch.Extruding");
                break;
            }
            case 1: {
                renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.ForgeHammer, 67, 55);
                tooltip = StatCollector.translateToLocal((String)"ic2.MetalFormer.gui.switch.Rolling");
                break;
            }
            case 2: {
                renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, Ic2Items.cutter, 67, 55);
                tooltip = StatCollector.translateToLocal((String)"ic2.MetalFormer.gui.switch.Cutting");
            }
        }
        GuiTooltipHelper.drawAreaTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, tooltip, 65, 50, 85, 70);
        GL11.glPopAttrib();
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, guibutton.id);
    }

    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, (this.width - this.xSize) / 2 + 65, (this.height - this.ySize) / 2 + 53, 20, 20, ""));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        int chargeLevel = (int)(14.0f * ((TileEntityMetalFormer)this.container.base).getChargeLevel());
        int progress = (int)(48.0f * ((TileEntityMetalFormer)this.container.base).getProgress());
        if (chargeLevel > 0) {
            this.drawTexturedModalRect(this.xoffset + 17, this.yoffset + 50 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
        }
        if (progress > 0) {
            this.drawTexturedModalRect(this.xoffset + 51, this.yoffset + 37, 177, 14, progress, 13);
        }
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.MetalFormer.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIMetalFormer.png");
    }
}

