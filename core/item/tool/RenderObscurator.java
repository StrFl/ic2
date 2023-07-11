/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameData
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.IIcon
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.registry.GameData;
import ic2.core.util.DrawUtil;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderObscurator
implements IItemRenderer {
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.EQUIPPED || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON || type == IItemRenderer.ItemRenderType.ENTITY;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return type == IItemRenderer.ItemRenderType.ENTITY;
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object ... data) {
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        Block referencedBlock = (Block)GameData.getBlockRegistry().getRaw(nbtData.getString("referencedBlock"));
        IIcon overlayIcon = RenderObscurator.getOverlayIcon(referencedBlock, nbtData);
        int overlayColor = RenderObscurator.getOverlayColor(referencedBlock);
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            DrawUtil.renderIcon(itemStack.getIconIndex(), 16.0, 0.0, 0.0f, 0.0f, -1.0f);
            if (overlayIcon != null) {
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                GL11.glColor3f((float)((float)(overlayColor >> 16 & 0xFF) / 255.0f), (float)((float)(overlayColor >> 8 & 0xFF) / 255.0f), (float)((float)(overlayColor & 0xFF) / 255.0f));
                DrawUtil.renderIcon(overlayIcon, 2.0, 2.0, 10.0, 10.0, 0.0, 0.0f, 0.0f, -1.0f);
            }
        } else {
            IIcon baseIcon = itemStack.getIconIndex();
            ItemRenderer.renderItemIn2D((Tessellator)Tessellator.instance, (float)baseIcon.getMaxU(), (float)baseIcon.getMinV(), (float)baseIcon.getMinU(), (float)baseIcon.getMaxV(), (int)baseIcon.getIconWidth(), (int)baseIcon.getIconHeight(), (float)0.0625f);
            if (overlayIcon != null) {
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                GL11.glColor3f((float)((float)(overlayColor >> 16 & 0xFF) / 255.0f), (float)((float)(overlayColor >> 8 & 0xFF) / 255.0f), (float)((float)(overlayColor & 0xFF) / 255.0f));
                DrawUtil.renderIcon(overlayIcon, 0.875, 0.875, 0.375, 0.375, 0.001, 0.0f, 0.0f, 1.0f);
                DrawUtil.renderIcon(overlayIcon, 0.875, 0.875, 0.375, 0.375, -0.0635, 0.0f, 0.0f, -1.0f);
            }
        }
    }

    private static IIcon getOverlayIcon(Block referencedBlock, NBTTagCompound nbtData) {
        if (referencedBlock != null) {
            try {
                return referencedBlock.getIcon(nbtData.getInteger("referencedSide"), nbtData.getInteger("referencedMeta"));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    private static int getOverlayColor(Block referencedBlock) {
        if (referencedBlock != null) {
            try {
                return referencedBlock.getBlockColor();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return 0xFFFFFF;
    }
}

