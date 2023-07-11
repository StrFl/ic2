/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  net.minecraftforge.fluids.FluidStack
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.item;

import ic2.core.item.ItemFluidCell;
import ic2.core.util.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class RenderLiquidCell
implements IItemRenderer {
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) || type.equals((Object)IItemRenderer.ItemRenderType.INVENTORY) || type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED) || type.equals((Object)IItemRenderer.ItemRenderType.ENTITY);
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return type == IItemRenderer.ItemRenderType.ENTITY;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object ... data) {
        ItemFluidCell cell = (ItemFluidCell)item.getItem();
        IIcon icon = item.getIconIndex();
        GL11.glEnable((int)3042);
        GL11.glEnable((int)3008);
        if (type.equals((Object)IItemRenderer.ItemRenderType.ENTITY)) {
            GL11.glRotated((double)180.0, (double)0.0, (double)0.0, (double)1.0);
            GL11.glRotated((double)90.0, (double)0.0, (double)1.0, (double)0.0);
            GL11.glTranslated((double)-0.5, (double)-0.6, (double)0.0);
        } else if (type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)) {
            GL11.glTranslated((double)1.0, (double)1.0, (double)0.0);
            GL11.glRotated((double)180.0, (double)0.0, (double)0.0, (double)1.0);
        } else if (type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED)) {
            GL11.glRotated((double)180.0, (double)0.0, (double)0.0, (double)1.0);
            GL11.glTranslated((double)-1.0, (double)-1.0, (double)0.0);
        }
        FluidStack fs = cell.getFluid(item);
        if (fs != null) {
            IIcon windowIcon = cell.getWindowIcon();
            IIcon fluidicon = fs.getFluid().getIcon(fs);
            int fluidColor = fs.getFluid().getColor(fs);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
            GL11.glBlendFunc((int)0, (int)1);
            if (type.equals((Object)IItemRenderer.ItemRenderType.INVENTORY)) {
                DrawUtil.renderIcon(windowIcon, 16.0, 0.0, 0.0f, 0.0f, -1.0f);
            } else {
                DrawUtil.renderIcon(windowIcon, 1.0, -0.001, 0.0f, 0.0f, 1.0f);
                DrawUtil.renderIcon(windowIcon, 1.0, -0.0615, 0.0f, 0.0f, -1.0f);
            }
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glDepthFunc((int)514);
            GL11.glColor3ub((byte)((byte)(fluidColor >> 16)), (byte)((byte)(fluidColor >> 8)), (byte)((byte)fluidColor));
            if (type.equals((Object)IItemRenderer.ItemRenderType.INVENTORY)) {
                DrawUtil.renderIcon(fluidicon, 16.0, 0.0, 0.0f, 0.0f, -1.0f);
            } else {
                DrawUtil.renderIcon(fluidicon, 1.0, -0.001, 0.0f, 0.0f, 1.0f);
                DrawUtil.renderIcon(fluidicon, 1.0, -0.0615, 0.0f, 0.0f, -1.0f);
            }
            GL11.glColor3ub((byte)-1, (byte)-1, (byte)-1);
            GL11.glDepthFunc((int)515);
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        GL11.glBlendFunc((int)770, (int)771);
        if (type.equals((Object)IItemRenderer.ItemRenderType.INVENTORY)) {
            DrawUtil.renderIcon(icon, 16.0, 0.001, 0.0f, 0.0f, -1.0f);
        } else {
            ItemRenderer.renderItemIn2D((Tessellator)Tessellator.instance, (float)icon.getMaxU(), (float)icon.getMinV(), (float)icon.getMinU(), (float)icon.getMaxV(), (int)icon.getIconWidth(), (int)icon.getIconHeight(), (float)0.0625f);
        }
        GL11.glDisable((int)3008);
        GL11.glDisable((int)3042);
    }
}

