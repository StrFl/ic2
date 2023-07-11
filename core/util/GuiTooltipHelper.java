/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.util;

import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.upgrade.UpgradeRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public final class GuiTooltipHelper {
    public static void drawUpgradeslotTooltip(int x, int y, int minX, int minY, int maxX, int maxY, IUpgradableBlock te, int yoffset, int xoffset) {
        if (te instanceof IUpgradableBlock && x >= minX && x <= maxX && y >= minY && y <= maxY) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            int width = fontRenderer.getStringWidth(StatCollector.translateToLocal((String)"ic2.generic.text.upgrade"));
            List<ItemStack> compatibleUpgrades = GuiTooltipHelper.getCompatibleUpgrades(te);
            for (ItemStack itemstack : compatibleUpgrades) {
                if (fontRenderer.getStringWidth(itemstack.getDisplayName()) <= width) continue;
                width = fontRenderer.getStringWidth(itemstack.getDisplayName());
            }
            GuiTooltipHelper.drawTooltip(x, y, yoffset, xoffset, StatCollector.translateToLocal((String)"ic2.generic.text.upgrade"), true, width);
            yoffset += 15;
            for (ItemStack itemstack : compatibleUpgrades) {
                GuiTooltipHelper.drawTooltip(x, y, yoffset, xoffset, itemstack.getDisplayName(), false, width);
                yoffset += 14;
            }
        }
    }

    private static List<ItemStack> getCompatibleUpgrades(IUpgradableBlock block) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        Set<UpgradableProperty> properties = block.getUpgradableProperties();
        for (ItemStack stack : UpgradeRegistry.getUpgrades()) {
            IUpgradeItem item = (IUpgradeItem)stack.getItem();
            if (!item.isSuitableFor(stack, properties)) continue;
            ret.add(stack);
        }
        return ret;
    }

    public static void drawAreaTooltip(int x, int y, String tooltip, int minX, int minY, int maxX, int maxY, boolean drawborder) {
        GuiTooltipHelper.drawAreaTooltip(x, y, tooltip, minX, minY, maxX, maxY, 0, 0, drawborder);
    }

    public static void drawAreaTooltip(int x, int y, String tooltip, int minX, int minY, int maxX, int maxY) {
        GuiTooltipHelper.drawAreaTooltip(x, y, tooltip, minX, minY, maxX, maxY, 0, 0, true);
    }

    public static void drawAreaTooltip(int x, int y, String tooltip, int minX, int minY, int maxX, int maxY, int yoffset, int xoffset) {
        GuiTooltipHelper.drawAreaTooltip(x, y, tooltip, minX, minY, maxX, maxY, yoffset, xoffset, true);
    }

    public static void drawAreaTooltip(int x, int y, String tooltip, int minX, int minY, int maxX, int maxY, int yoffset, int xoffset, boolean drawborder) {
        if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
            GuiTooltipHelper.drawTooltip(x, y, yoffset, xoffset, tooltip, drawborder, 0);
        }
    }

    public static void drawTooltip(int x, int y, int yoffset, int xoffset, String tooltip, boolean drawborder, int width) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        if (width == 0) {
            x -= fontRenderer.getStringWidth(tooltip) / 2;
        }
        y -= 12;
        x += xoffset;
        y += yoffset;
        if (width == 0) {
            width = fontRenderer.getStringWidth(tooltip);
        }
        width += 8;
        int height = 8;
        int backgroundColor = 255;
        int borderColor = -1162167553;
        GL11.glPushAttrib((int)16704);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable((int)2929);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        GuiTooltipHelper.drawRectangle(tessellator, x - 3, y - 4, x + width + 3, y - 3, backgroundColor);
        GuiTooltipHelper.drawRectangle(tessellator, x - 3, y + height + 3, x + width + 3, y + height + 4, backgroundColor);
        GuiTooltipHelper.drawRectangle(tessellator, x - 3, y - 3, x + width + 3, y + height + 3, backgroundColor);
        GuiTooltipHelper.drawRectangle(tessellator, x - 4, y - 3, x - 3, y + height + 3, backgroundColor);
        GuiTooltipHelper.drawRectangle(tessellator, x + width + 3, y - 3, x + width + 4, y + height + 3, backgroundColor);
        if (drawborder) {
            GuiTooltipHelper.drawRectangle(tessellator, x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, borderColor);
            GuiTooltipHelper.drawRectangle(tessellator, x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, borderColor);
            GuiTooltipHelper.drawRectangle(tessellator, x - 3, y - 3, x + width + 3, y - 3 + 1, borderColor);
            GuiTooltipHelper.drawRectangle(tessellator, x - 3, y + height + 2, x + width + 3, y + height + 3, borderColor);
        }
        tessellator.draw();
        GL11.glEnable((int)3553);
        fontRenderer.drawStringWithShadow(tooltip, x + 4, y, -2);
        GL11.glPopAttrib();
    }

    private static void drawRectangle(Tessellator tessellator, int x1, int y1, int x2, int y2, int color) {
        tessellator.setColorRGBA(color >>> 24 & 0xFF, color >>> 16 & 0xFF, color >>> 8 & 0xFF, color & 0xFF);
        tessellator.addVertex((double)x2, (double)y1, 300.0);
        tessellator.addVertex((double)x1, (double)y1, 300.0);
        tessellator.addVertex((double)x1, (double)y2, 300.0);
        tessellator.addVertex((double)x2, (double)y2, 300.0);
    }
}

