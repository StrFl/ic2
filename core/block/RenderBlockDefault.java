/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.world.IBlockAccess
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.BlockBase;
import ic2.core.block.RenderBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@SideOnly(value=Side.CLIENT)
public class RenderBlockDefault
extends RenderBlock {
    @Override
    public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        if (block instanceof BlockBase) {
            int facing = ((BlockBase)block).getFacing(blockAccess, x, y, z);
            if (facing == 1) {
                renderblocks.uvRotateEast = 3;
                renderblocks.uvRotateWest = 3;
            } else if (facing == 2) {
                renderblocks.uvRotateBottom = 3;
                renderblocks.uvRotateTop = 3;
            } else if (facing == 4) {
                renderblocks.uvRotateBottom = 2;
                renderblocks.uvRotateTop = 1;
            } else if (facing == 5) {
                renderblocks.uvRotateBottom = 1;
                renderblocks.uvRotateTop = 2;
            }
            if (facing == 0 || facing == 1) {
                renderblocks.uvRotateNorth = 2;
                renderblocks.uvRotateSouth = 1;
            }
        }
        super.renderWorldBlock(blockAccess, x, y, z, block, modelId, renderblocks);
        boolean ret = renderblocks.renderStandardBlock(block, x, y, z);
        renderblocks.uvRotateBottom = 0;
        renderblocks.uvRotateTop = 0;
        renderblocks.uvRotateNorth = 0;
        renderblocks.uvRotateSouth = 0;
        renderblocks.uvRotateEast = 0;
        renderblocks.uvRotateWest = 0;
        return ret;
    }
}

