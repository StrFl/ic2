/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.RenderBlock;
import ic2.core.block.machine.tileentity.TileEntityMagnetizer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderBlockFence
extends RenderBlock {
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) {
        Tessellator tessellator = Tessellator.instance;
        for (int i1 = 0; i1 < 4; ++i1) {
            float f4 = 0.125f;
            if (i1 == 0) {
                block.setBlockBounds(0.5f - f4, 0.0f, 0.0f, 0.5f + f4, 1.0f, f4 * 2.0f);
            }
            if (i1 == 1) {
                block.setBlockBounds(0.5f - f4, 0.0f, 1.0f - f4 * 2.0f, 0.5f + f4, 1.0f, 1.0f);
            }
            f4 = 0.0625f;
            if (i1 == 2) {
                block.setBlockBounds(0.5f - f4, 1.0f - f4 * 3.0f, -f4 * 2.0f, 0.5f + f4, 1.0f - f4, 1.0f + f4 * 2.0f);
            }
            if (i1 == 3) {
                block.setBlockBounds(0.5f - f4, 0.5f - f4 * 3.0f, -f4 * 2.0f, 0.5f + f4, 0.5f - f4, 1.0f + f4 * 2.0f);
            }
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
        }
        block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        renderblocks.setRenderBoundsFromBlock(block);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess iblockaccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        super.renderWorldBlock(iblockaccess, x, y, z, block, modelId, renderblocks);
        float w = 0.25f;
        float d = (1.0f - w) / 2.0f;
        float wi = 0.125f;
        float di = (1.0f - wi) / 2.0f;
        float ht1 = 0.75f;
        float ht2 = 0.9375f;
        float hb1 = 0.375f;
        float hb2 = 0.5625f;
        block.setBlockBounds(d, 0.0f, d, d + w, 1.0f, d + w);
        renderblocks.setRenderBoundsFromBlock(block);
        renderblocks.renderStandardBlock(block, x, y, z);
        if (this.connectsToNeighbor(iblockaccess, x + 1, y, z, (short)4, block)) {
            block.setBlockBounds(d + w, ht1, di, 1.0f + d, ht2, di + wi);
            renderblocks.setRenderBoundsFromBlock(block);
            renderblocks.renderStandardBlock(block, x, y, z);
            block.setBlockBounds(d + w, hb1, di, 1.0f + d, hb2, di + wi);
            renderblocks.setRenderBoundsFromBlock(block);
            renderblocks.renderStandardBlock(block, x, y, z);
        }
        if (this.connectsToNeighbor(iblockaccess, x, y, z + 1, (short)2, block)) {
            block.setBlockBounds(di, ht1, d + w, di + wi, ht2, 1.0f + d);
            renderblocks.setRenderBoundsFromBlock(block);
            renderblocks.renderStandardBlock(block, x, y, z);
            block.setBlockBounds(di, hb1, d + w, di + wi, hb2, 1.0f + d);
            renderblocks.setRenderBoundsFromBlock(block);
            renderblocks.renderStandardBlock(block, x, y, z);
        }
        if (this.connectsToNeighbor(iblockaccess, x - 1, y, z, (short)5, block)) {
            block.setBlockBounds(-d, ht1, di, d, ht2, di + wi);
            renderblocks.setRenderBoundsFromBlock(block);
            renderblocks.renderStandardBlock(block, x, y, z);
            block.setBlockBounds(-d, hb1, di, d, hb2, di + wi);
            renderblocks.setRenderBoundsFromBlock(block);
            renderblocks.renderStandardBlock(block, x, y, z);
        }
        if (this.connectsToNeighbor(iblockaccess, x, y, z - 1, (short)3, block)) {
            block.setBlockBounds(di, ht1, -d, di + wi, ht2, d);
            renderblocks.setRenderBoundsFromBlock(block);
            renderblocks.renderStandardBlock(block, x, y, z);
            block.setBlockBounds(di, hb1, -d, di + wi, hb2, d);
            renderblocks.setRenderBoundsFromBlock(block);
            renderblocks.renderStandardBlock(block, x, y, z);
        }
        block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        renderblocks.setRenderBoundsFromBlock(block);
        return true;
    }

    private boolean connectsToNeighbor(IBlockAccess world, int x, int y, int z, short i, Block block) {
        Block neighbor = world.getBlock(x, y, z);
        if (neighbor instanceof BlockFence || block == neighbor) {
            return true;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        return block.getMaterial() == Material.iron && te != null && te instanceof TileEntityMagnetizer && ((TileEntityMagnetizer)te).getFacing() == i;
    }
}

