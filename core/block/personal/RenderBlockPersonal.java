/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.personal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.RenderBlock;
import ic2.core.block.personal.TileEntityPersonalChest;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderBlockPersonal
extends RenderBlock {
    private final TileEntityPersonalChest invte = new TileEntityPersonalChest();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        if (metadata != 0) {
            Tessellator var4 = Tessellator.instance;
            GL11.glRotatef((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)-0.5f);
            var4.startDrawingQuads();
            var4.setNormal(0.0f, -1.0f, 0.0f);
            renderer.renderFaceYNeg(block, 0.0, 0.0, 0.0, block.getIcon(0, metadata));
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(0.0f, 1.0f, 0.0f);
            renderer.renderFaceYPos(block, 0.0, 0.0, 0.0, block.getIcon(1, metadata));
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(0.0f, 0.0f, -1.0f);
            renderer.renderFaceZNeg(block, 0.0, 0.0, 0.0, block.getIcon(2, metadata));
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(0.0f, 0.0f, 1.0f);
            renderer.renderFaceZPos(block, 0.0, 0.0, 0.0, block.getIcon(3, metadata));
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(-1.0f, 0.0f, 0.0f);
            renderer.renderFaceXNeg(block, 0.0, 0.0, 0.0, block.getIcon(4, metadata));
            var4.draw();
            var4.startDrawingQuads();
            var4.setNormal(1.0f, 0.0f, 0.0f);
            renderer.renderFaceXPos(block, 0.0, 0.0, 0.0, block.getIcon(5, metadata));
            var4.draw();
            GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.5f);
        } else {
            GL11.glRotatef((float)90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)-0.5f);
            TileEntityRendererDispatcher.instance.renderTileEntityAt((TileEntity)this.invte, 0.0, 0.0, 0.0, 0.0f);
            GL11.glEnable((int)32826);
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
        if (world.getBlockMetadata(x, y, z) != 0) {
            renderer.renderStandardBlock(block, x, y, z);
        }
        return false;
    }
}

