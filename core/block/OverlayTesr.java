/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.tileentity.TileEntity
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block;

import ic2.core.block.BlockMultiID;
import ic2.core.block.RenderBlock;
import ic2.core.block.TileEntityBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class OverlayTesr
extends TileEntitySpecialRenderer {
    private final RenderBlocks renderBlocks = new RenderBlocks();

    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
        TileEntityBlock te = (TileEntityBlock)tileEntity;
        int mask = te.getTesrMask();
        assert (mask != 0);
        Block rawBlock = tileEntity.getBlockType();
        if (!(rawBlock instanceof BlockMultiID)) {
            return;
        }
        BlockMultiID block = (BlockMultiID)rawBlock;
        block.renderMask = mask;
        GL11.glPushAttrib((int)64);
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glShadeModel((int)7425);
        this.bindTexture(TextureMap.locationBlocksTexture);
        float zScale = 1.001f;
        GL11.glTranslatef((float)((float)(x + 0.5)), (float)((float)(y + 0.5)), (float)((float)(z + 0.5)));
        GL11.glScalef((float)zScale, (float)zScale, (float)zScale);
        GL11.glTranslatef((float)((float)(-(x + 0.5))), (float)((float)(-(y + 0.5))), (float)((float)(-(z + 0.5))));
        float f = 1.000001f;
        GL11.glTranslatef((float)-8.0f, (float)-8.0f, (float)-8.0f);
        GL11.glScalef((float)f, (float)f, (float)f);
        GL11.glTranslatef((float)8.0f, (float)8.0f, (float)8.0f);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setTranslation(x - (double)tileEntity.xCoord, y - (double)tileEntity.yCoord, z - (double)tileEntity.zCoord);
        this.renderBlocks.blockAccess = te.getWorldObj();
        this.renderBlocks.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        RenderBlock.fromTesr = true;
        this.renderBlocks.renderBlockByRenderType((Block)block, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        RenderBlock.fromTesr = false;
        tessellator.draw();
        tessellator.setTranslation(0.0, 0.0, 0.0);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        block.renderMask = 63;
        if (--te.tesrTtl == 0) {
            tileEntity.getWorldObj().markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
        }
    }
}

