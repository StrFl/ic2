/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 */
package ic2.core.block.wiring;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.core.IC2;
import ic2.core.block.RenderBlock;
import ic2.core.block.wiring.TileEntityCable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@SideOnly(value=Side.CLIENT)
public class RenderBlockCable
extends RenderBlock {
    private static final float[] faceColors = new float[]{0.6f, 0.6f, 0.5f, 1.0f, 0.8f, 0.8f};

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        super.renderWorldBlock(blockAccess, x, y, z, block, modelId, renderblocks);
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityCable)) {
            return true;
        }
        TileEntityCable cable = (TileEntityCable)te;
        if (cable.foamed == 1) {
            return renderblocks.renderStandardBlock(block, x, y, z);
        }
        if (cable.foamed == 2) {
            return IC2.platform.getRender("wall").renderWorldBlock(blockAccess, x, y, z, block, modelId, renderblocks);
        }
        float th = cable.getCableThickness();
        float sp = (1.0f - th) / 2.0f;
        int connectivity = cable.connectivity;
        byte renderSide = cable.renderSide;
        IIcon[] textures = new IIcon[6];
        for (int side = 0; side < 6; ++side) {
            textures[side] = renderblocks.getBlockIcon(block, blockAccess, x, y, z, side);
        }
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));
        if (connectivity == 0) {
            renderblocks.setRenderBounds((double)sp, (double)sp, (double)sp, (double)(sp + th), (double)(sp + th), (double)(sp + th));
            for (Direction face : Direction.directions) {
                RenderBlockCable.renderFace(tessellator, renderblocks, block, x, y, z, textures, face);
            }
        } else {
            float[] dim;
            int mask;
            int dirIndex;
            boolean centerRendered = false;
            for (Direction dir : Direction.directions) {
                dirIndex = dir.ordinal();
                if (dirIndex % 2 != 0 || (connectivity & (mask = 3 << dirIndex)) != mask) continue;
                dim = new float[]{sp, sp, sp, sp + th, sp + th, sp + th};
                dim[dirIndex / 2] = 0.0f;
                dim[dirIndex / 2 + 3] = 1.0f;
                renderblocks.setRenderBounds((double)dim[0], (double)dim[1], (double)dim[2], (double)dim[3], (double)dim[4], (double)dim[5]);
                for (Direction face : Direction.directions) {
                    if (face.ordinal() / 2 == dirIndex / 2 && (renderSide & 1 << face.ordinal()) == 0) continue;
                    RenderBlockCable.renderFace(tessellator, renderblocks, block, x, y, z, textures, face);
                }
                connectivity &= ~mask;
                centerRendered = true;
            }
            for (Direction dir : Direction.directions) {
                dirIndex = dir.ordinal();
                mask = 1 << dirIndex;
                if ((connectivity & mask) == 0) continue;
                dim = new float[]{sp, sp, sp, sp + th, sp + th, sp + th};
                float min = centerRendered ? sp + th : sp;
                float max = centerRendered ? sp : sp + th;
                dim[dirIndex / 2] = dirIndex % 2 == 0 ? 0.0f : min;
                dim[dirIndex / 2 + 3] = dirIndex % 2 == 0 ? max : 1.0f;
                renderblocks.setRenderBounds((double)dim[0], (double)dim[1], (double)dim[2], (double)dim[3], (double)dim[4], (double)dim[5]);
                for (Direction face : Direction.directions) {
                    if (face == dir && (renderSide & mask) == 0) continue;
                    RenderBlockCable.renderFace(tessellator, renderblocks, block, x, y, z, textures, face);
                }
                centerRendered = true;
            }
        }
        renderblocks.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    private static void renderFace(Tessellator tessellator, RenderBlocks renderBlocks, Block block, int x, int y, int z, IIcon[] textures, Direction face) {
        int dirIndex = face.ordinal();
        tessellator.setColorOpaque_F(faceColors[dirIndex], faceColors[dirIndex], faceColors[dirIndex]);
        switch (face) {
            case XN: {
                renderBlocks.renderFaceXNeg(block, (double)x, (double)y, (double)z, textures[dirIndex]);
                break;
            }
            case XP: {
                renderBlocks.renderFaceXPos(block, (double)x, (double)y, (double)z, textures[dirIndex]);
                break;
            }
            case YN: {
                renderBlocks.renderFaceYNeg(block, (double)x, (double)y, (double)z, textures[dirIndex]);
                break;
            }
            case YP: {
                renderBlocks.renderFaceYPos(block, (double)x, (double)y, (double)z, textures[dirIndex]);
                break;
            }
            case ZN: {
                renderBlocks.renderFaceZNeg(block, (double)x, (double)y, (double)z, textures[dirIndex]);
                break;
            }
            case ZP: {
                renderBlocks.renderFaceZPos(block, (double)x, (double)y, (double)z, textures[dirIndex]);
            }
        }
    }
}

