/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.item.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.machine.gui.GuiLathe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class LatheRenderer
implements IItemRenderer {
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return Minecraft.getMinecraft().gameSettings.fancyGraphics && type != IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON && type != IItemRenderer.ItemRenderType.ENTITY;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            return false;
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            return helper == IItemRenderer.ItemRendererHelper.ENTITY_BOBBING || helper == IItemRenderer.ItemRendererHelper.ENTITY_ROTATION;
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            return helper == IItemRenderer.ItemRendererHelper.ENTITY_ROTATION || helper == IItemRenderer.ItemRendererHelper.ENTITY_BOBBING;
        }
        return true;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object ... data) {
        GL11.glPushMatrix();
        if (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glScalef((float)0.125f, (float)0.33f, (float)0.125f);
            GL11.glTranslatef((float)3.0f, (float)10.0f, (float)0.0f);
        } else if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glScalef((float)0.015625f, (float)0.015625f, (float)0.015625f);
        } else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef((float)0.0078125f, (float)0.0078125f, (float)0.0078125f);
            GL11.glTranslatef((float)30.0f, (float)60.0f, (float)0.0f);
            GL11.glRotatef((float)80.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        }
        GuiLathe.renderILatheItemIntoGUI(item, 0, 0);
        GL11.glPopMatrix();
    }
}

