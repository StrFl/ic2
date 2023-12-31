/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 */
package ic2.core.block;

import ic2.core.block.IObscurable;
import ic2.core.block.RenderBlock;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class RenderBlockWall
extends RenderBlock {
    @Override
    public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderBlocks) {
        super.renderWorldBlock(blockAccess, x, y, z, block, modelId, renderBlocks);
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if (!(te instanceof IObscurable)) {
            return false;
        }
        IObscurable wall = (IObscurable)te;
        ArrayList<Variant> variants = new ArrayList<Variant>();
        for (int side = 0; side < 6; ++side) {
            int colorMultiplier;
            Block referencedBlock = wall.getReferencedBlock(side);
            int meta = wall.getReferencedMeta(side);
            if (referencedBlock != null) {
                try {
                    colorMultiplier = referencedBlock.colorMultiplier(blockAccess, x, y, z);
                    RenderType renderType = this.getRenderType(referencedBlock.getRenderType());
                    this.addSideToVariant(side, meta, colorMultiplier, renderType, variants);
                }
                catch (Exception e) {
                    referencedBlock = null;
                }
            }
            if (referencedBlock != null) continue;
            colorMultiplier = block.colorMultiplier(blockAccess, x, y, z);
            this.addSideToVariant(side, meta, colorMultiplier, RenderType.Default, variants);
        }
        boolean ret = false;
        for (Variant variant : variants) {
            boolean result;
            wall.setRenderMask(variant.renderMask);
            wall.setColorMultiplier(variant.colorMultiplier);
            switch (variant.renderType) {
                case Default: {
                    result = renderBlocks.renderStandardBlock(block, x, y, z);
                    break;
                }
                case RotatedPillar: {
                    result = RenderBlockWall.renderBlockLog(block, variant.meta, x, y, z, renderBlocks);
                    break;
                }
                default: {
                    throw new RuntimeException("unhandled render type: " + (Object)((Object)variant.renderType));
                }
            }
            ret = ret || result;
        }
        wall.setRenderMask(63);
        wall.setColorMultiplier(-1);
        return ret;
    }

    private RenderType getRenderType(int id) {
        for (RenderType type : RenderType.renderTypes) {
            if (type.id != id) continue;
            return type;
        }
        return RenderType.Default;
    }

    private void addSideToVariant(int side, int meta, int colorMultiplier, RenderType renderType, List<Variant> variants) {
        Variant variant = null;
        for (Variant cVariant : variants) {
            if (!cVariant.matches(meta, colorMultiplier, renderType)) continue;
            variant = cVariant;
            break;
        }
        if (variant == null) {
            variant = new Variant(meta, colorMultiplier, renderType);
            variants.add(variant);
        }
        variant.addSide(side);
    }

    private static boolean renderBlockLog(Block p_147742_1_, int meta, int x, int y, int z, RenderBlocks renderBlocks) {
        int rotation = meta & 0xC;
        if (rotation == 4) {
            renderBlocks.uvRotateEast = 1;
            renderBlocks.uvRotateWest = 1;
            renderBlocks.uvRotateTop = 1;
            renderBlocks.uvRotateBottom = 1;
        } else if (rotation == 8) {
            renderBlocks.uvRotateSouth = 1;
            renderBlocks.uvRotateNorth = 1;
        }
        boolean ret = renderBlocks.renderStandardBlock(p_147742_1_, x, y, z);
        renderBlocks.uvRotateSouth = 0;
        renderBlocks.uvRotateEast = 0;
        renderBlocks.uvRotateWest = 0;
        renderBlocks.uvRotateNorth = 0;
        renderBlocks.uvRotateTop = 0;
        renderBlocks.uvRotateBottom = 0;
        return ret;
    }

    static enum RenderType {
        Default(0, false),
        RotatedPillar(31, true);

        final int id;
        final boolean usesMeta;
        static final RenderType[] renderTypes;

        private RenderType(int id, boolean usesMeta) {
            this.id = id;
            this.usesMeta = usesMeta;
        }

        static {
            renderTypes = RenderType.values();
        }
    }

    static class Variant {
        final int meta;
        final int colorMultiplier;
        final RenderType renderType;
        int renderMask;

        Variant(int meta, int colorMultiplier, RenderType renderType) {
            this.meta = meta;
            this.colorMultiplier = colorMultiplier;
            this.renderType = renderType;
        }

        boolean matches(int meta, int colorMultiplier, RenderType renderType) {
            return this.colorMultiplier == colorMultiplier && this.renderType == renderType && (!renderType.usesMeta || this.meta == meta);
        }

        void addSide(int side) {
            this.renderMask |= 1 << side;
        }
    }
}

