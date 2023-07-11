/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package ic2.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiIconButton
extends GuiButton {
    private ResourceLocation texture;
    private int textureX;
    private int textureY;
    private ItemStack itemStack = null;
    private boolean drawQuantity;
    private RenderItem renderItem;

    public GuiIconButton(int id1, int x, int y, int w, int h, ResourceLocation texture1, int textureX1, int textureY1) {
        super(id1, x, y, w, h, "");
        this.texture = texture1;
        this.textureX = textureX1;
        this.textureY = textureY1;
    }

    public GuiIconButton(int id1, int x, int y, int w, int h, ItemStack icon, boolean drawQuantity1) {
        super(id1, x, y, w, h, "");
        this.itemStack = icon;
        this.drawQuantity = drawQuantity1;
    }

    public void drawButton(Minecraft minecraft, int i, int j) {
        super.drawButton(minecraft, i, j);
        if (this.itemStack == null) {
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            minecraft.getTextureManager().bindTexture(this.texture);
            this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 1, this.textureX, this.textureY, this.width - 4, this.height - 4);
        } else {
            if (this.renderItem == null) {
                this.renderItem = new RenderItem();
            }
            this.renderItem.renderItemIntoGUI(minecraft.fontRenderer, minecraft.renderEngine, this.itemStack, this.xPosition + 2, this.yPosition + 1);
            if (this.drawQuantity) {
                this.renderItem.renderItemOverlayIntoGUI(minecraft.fontRenderer, minecraft.renderEngine, this.itemStack, this.xPosition + 2, this.xPosition + 1);
            }
        }
    }
}

