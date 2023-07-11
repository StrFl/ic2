/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ILatheItem;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerLathe;
import ic2.core.block.machine.tileentity.TileEntityLathe;
import ic2.core.item.tool.ItemLathingTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiLathe
extends GuiIC2 {
    private final ContainerLathe container;
    public static final int renderLength = 120;
    public static final int renderWidth = 32;

    public GuiLathe(ContainerLathe container) {
        super(container);
        this.container = container;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        GL11.glPushMatrix();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (((TileEntityLathe)this.container.base).latheSlot.get() != null && ((TileEntityLathe)this.container.base).latheSlot.get().getItem() instanceof ILatheItem) {
            ItemStack stack = ((TileEntityLathe)this.container.base).latheSlot.get();
            GuiLathe.renderILatheItemIntoGUI(stack, 40, 45);
            ILatheItem i = (ILatheItem)stack.getItem();
            int segLength = 24;
            int[] state = i.getCurrentState(stack);
            int max = i.getWidth(stack);
            for (int j = 0; j < 5; ++j) {
                Minecraft.getMinecraft().fontRenderer.drawString(StatCollector.translateToLocalFormatted((String)"ic2.Lathe.gui.info", (Object[])new Object[]{state[j], max}), 40 + segLength * j, 37, 0x404040);
            }
        }
        if (((TileEntityLathe)this.container.base).toolSlot.get() != null && ((TileEntityLathe)this.container.base).toolSlot.get().getItem() instanceof ItemLathingTool) {
            RenderItem renderItem = new RenderItem();
            for (int i = 0; i < 5; ++i) {
                renderItem.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, ((TileEntityLathe)this.container.base).toolSlot.get(), 44 + i * 24, 17);
            }
        }
        GL11.glPopMatrix();
    }

    public static void renderILatheItemIntoGUI(ItemStack stack, int posX, int posY) {
        ILatheItem i = (ILatheItem)stack.getItem();
        int segLength = 24;
        int[] state = i.getCurrentState(stack);
        Minecraft.getMinecraft().renderEngine.bindTexture(i.getTexture(stack));
        for (int j = 0; j < 5; ++j) {
            int segWidth = (int)(32.0f / (float)i.getWidth(stack) * (float)state[j] + 0.5f);
            int offset = (int)((float)(32 - segWidth) / 2.0f + 0.5f);
            GuiLathe.drawTexturedModalRectOV(posX + segLength * j, posY + offset, segLength * j, offset, segLength, segWidth);
        }
    }

    private static void drawTexturedModalRectOV(int p_73729_1_, int p_73729_2_, int p_73729_3_, int p_73729_4_, int p_73729_5_, int p_73729_6_) {
        float f = 0.00390625f;
        float f1 = 0.00390625f;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + p_73729_6_), 1.0, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + p_73729_6_), 1.0, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + 0), 1.0, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + 0), 1.0, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.draw();
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal((String)"ic2.Lathe.gui.name");
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUILathe.png");
    }

    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        IC2.network.get().initiateClientTileEntityEvent((TileEntity)this.container.base, guibutton.id);
    }

    public void initGui() {
        super.initGui();
        for (int i = 0; i < 5; ++i) {
            this.buttonList.add(new GuiButton(i, (this.width - this.xSize) / 2 + 40 + i * 24, (this.height - this.ySize) / 2 + 15, 24, 20, ""));
        }
    }
}

