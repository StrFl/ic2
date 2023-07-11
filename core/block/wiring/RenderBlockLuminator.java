/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.world.IBlockAccess
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.wiring;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.RenderBlock;
import ic2.core.block.wiring.BlockLuminator;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderBlockLuminator
extends RenderBlock {
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) {
        Tessellator tessellator = Tessellator.instance;
        float f4 = 0.125f;
        block.setBlockBounds(0.5f - f4, 0.0f, 0.5f - f4, 0.5f + f4, 1.0f, 0.5f + f4);
        renderblocks.setRenderBoundsFromBlock(block);
        GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)-0.5f);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, -1.0f, 0.0f);
        renderblocks.renderFaceYNeg(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSide(0));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 1.0f, 0.0f);
        renderblocks.renderFaceYPos(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSide(1));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 0.0f, -1.0f);
        renderblocks.renderFaceZNeg(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSide(2));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0f, 0.0f, 1.0f);
        renderblocks.renderFaceZPos(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSide(3));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0f, 0.0f, 0.0f);
        renderblocks.renderFaceXNeg(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSide(4));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0f, 0.0f, 0.0f);
        renderblocks.renderFaceXPos(block, 0.0, 0.0, 0.0, block.getBlockTextureFromSide(5));
        tessellator.draw();
        GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.5f);
        block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        renderblocks.setRenderBoundsFromBlock(block);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess iblockaccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        super.renderWorldBlock(iblockaccess, x, y, z, block, modelId, renderblocks);
        float[] box = BlockLuminator.getBoxOfLuminator(iblockaccess, x, y, z);
        block.setBlockBounds(box[0], box[1], box[2], box[3], box[4], box[5]);
        renderblocks.setRenderBoundsFromBlock(block);
        renderblocks.renderStandardBlock(block, x, y, z);
        block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        renderblocks.setRenderBoundsFromBlock(block);
        return true;
    }
}

