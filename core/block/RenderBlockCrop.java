/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.RenderBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@SideOnly(value=Side.CLIENT)
public class RenderBlockCrop
extends RenderBlock {
    public static void renderBlockCropsImpl(IIcon icon, int x, int y, int z) {
        Tessellator tessellator = Tessellator.instance;
        double yBase = (double)y - 0.0625;
        double uStart = icon.getInterpolatedU(0.0);
        double uEnd = icon.getInterpolatedU(16.0);
        double vStart = icon.getInterpolatedV(0.0);
        double vEnd = icon.getInterpolatedV(16.0);
        double x1 = (double)x + 0.5 - 0.25;
        double x2 = (double)x + 0.5 + 0.25;
        double z1 = (double)z + 0.5 - 0.5;
        double z2 = (double)z + 0.5 + 0.5;
        tessellator.addVertexWithUV(x1, yBase + 1.0, z1, uStart, vStart);
        tessellator.addVertexWithUV(x1, yBase + 0.0, z1, uStart, vEnd);
        tessellator.addVertexWithUV(x1, yBase + 0.0, z2, uEnd, vEnd);
        tessellator.addVertexWithUV(x1, yBase + 1.0, z2, uEnd, vStart);
        tessellator.addVertexWithUV(x1, yBase + 1.0, z2, uStart, vStart);
        tessellator.addVertexWithUV(x1, yBase + 0.0, z2, uStart, vEnd);
        tessellator.addVertexWithUV(x1, yBase + 0.0, z1, uEnd, vEnd);
        tessellator.addVertexWithUV(x1, yBase + 1.0, z1, uEnd, vStart);
        tessellator.addVertexWithUV(x2, yBase + 1.0, z2, uStart, vStart);
        tessellator.addVertexWithUV(x2, yBase + 0.0, z2, uStart, vEnd);
        tessellator.addVertexWithUV(x2, yBase + 0.0, z1, uEnd, vEnd);
        tessellator.addVertexWithUV(x2, yBase + 1.0, z1, uEnd, vStart);
        tessellator.addVertexWithUV(x2, yBase + 1.0, z1, uStart, vStart);
        tessellator.addVertexWithUV(x2, yBase + 0.0, z1, uStart, vEnd);
        tessellator.addVertexWithUV(x2, yBase + 0.0, z2, uEnd, vEnd);
        tessellator.addVertexWithUV(x2, yBase + 1.0, z2, uEnd, vStart);
        x1 = (double)x + 0.5 - 0.5;
        x2 = (double)x + 0.5 + 0.5;
        z1 = (double)z + 0.5 - 0.25;
        z2 = (double)z + 0.5 + 0.25;
        tessellator.addVertexWithUV(x1, yBase + 1.0, z1, uStart, vStart);
        tessellator.addVertexWithUV(x1, yBase + 0.0, z1, uStart, vEnd);
        tessellator.addVertexWithUV(x2, yBase + 0.0, z1, uEnd, vEnd);
        tessellator.addVertexWithUV(x2, yBase + 1.0, z1, uEnd, vStart);
        tessellator.addVertexWithUV(x2, yBase + 1.0, z1, uStart, vStart);
        tessellator.addVertexWithUV(x2, yBase + 0.0, z1, uStart, vEnd);
        tessellator.addVertexWithUV(x1, yBase + 0.0, z1, uEnd, vEnd);
        tessellator.addVertexWithUV(x1, yBase + 1.0, z1, uEnd, vStart);
        tessellator.addVertexWithUV(x2, yBase + 1.0, z2, uStart, vStart);
        tessellator.addVertexWithUV(x2, yBase + 0.0, z2, uStart, vEnd);
        tessellator.addVertexWithUV(x1, yBase + 0.0, z2, uEnd, vEnd);
        tessellator.addVertexWithUV(x1, yBase + 1.0, z2, uEnd, vStart);
        tessellator.addVertexWithUV(x1, yBase + 1.0, z2, uStart, vStart);
        tessellator.addVertexWithUV(x1, yBase + 0.0, z2, uStart, vEnd);
        tessellator.addVertexWithUV(x2, yBase + 0.0, z2, uEnd, vEnd);
        tessellator.addVertexWithUV(x2, yBase + 1.0, z2, uEnd, vStart);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        super.renderWorldBlock(blockAccess, x, y, z, block, modelId, renderer);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));
        tessellator.setColorOpaque_F(1.0f, 1.0f, 1.0f);
        RenderBlockCrop.renderBlockCropsImpl(renderer.getBlockIcon(block, blockAccess, x, y, z, 0), x, y, z);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }
}

