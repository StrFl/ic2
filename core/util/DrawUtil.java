/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.util.IIcon
 */
package ic2.core.util;

import ic2.core.block.RenderBlock;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public final class DrawUtil {
    public static void drawRepeated(IIcon icon, double x, double y, double width, double height, double z) {
        double iconWidthStep = ((double)icon.getMaxU() - (double)icon.getMinU()) / 16.0;
        double iconHeightStep = ((double)icon.getMaxV() - (double)icon.getMinV()) / 16.0;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (double cy = y; cy < y + height; cy += 16.0) {
            double quadHeight = Math.min(16.0, height + y - cy);
            double maxY = cy + quadHeight;
            double maxV = (double)icon.getMinV() + iconHeightStep * quadHeight;
            for (double cx = x; cx < x + width; cx += 16.0) {
                double quadWidth = Math.min(16.0, width + x - cx);
                double maxX = cx + quadWidth;
                double maxU = (double)icon.getMinU() + iconWidthStep * quadWidth;
                tessellator.addVertexWithUV(cx, maxY, z, (double)icon.getMinU(), maxV);
                tessellator.addVertexWithUV(maxX, maxY, z, maxU, maxV);
                tessellator.addVertexWithUV(maxX, cy, z, maxU, (double)icon.getMinV());
                tessellator.addVertexWithUV(cx, cy, z, (double)icon.getMinU(), (double)icon.getMinV());
            }
        }
        tessellator.draw();
    }

    public static void renderIcon(IIcon icon, double size, double z, float nx, float ny, float nz) {
        DrawUtil.renderIcon(icon, 0.0, 0.0, size, size, z, nx, ny, nz);
    }

    public static void renderIcon(IIcon icon, double xStart, double yStart, double xEnd, double yEnd, double z, float nx, float ny, float nz) {
        if (icon == null) {
            icon = RenderBlock.getMissingIcon(TextureMap.locationItemsTexture);
        }
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setNormal(nx, ny, nz);
        if (nz > 0.0f) {
            tessellator.addVertexWithUV(xStart, yStart, z, (double)icon.getMinU(), (double)icon.getMinV());
            tessellator.addVertexWithUV(xEnd, yStart, z, (double)icon.getMaxU(), (double)icon.getMinV());
            tessellator.addVertexWithUV(xEnd, yEnd, z, (double)icon.getMaxU(), (double)icon.getMaxV());
            tessellator.addVertexWithUV(xStart, yEnd, z, (double)icon.getMinU(), (double)icon.getMaxV());
        } else {
            tessellator.addVertexWithUV(xStart, yEnd, z, (double)icon.getMinU(), (double)icon.getMaxV());
            tessellator.addVertexWithUV(xEnd, yEnd, z, (double)icon.getMaxU(), (double)icon.getMaxV());
            tessellator.addVertexWithUV(xEnd, yStart, z, (double)icon.getMaxU(), (double)icon.getMinV());
            tessellator.addVertexWithUV(xStart, yStart, z, (double)icon.getMinU(), (double)icon.getMinV());
        }
        tessellator.draw();
    }
}

