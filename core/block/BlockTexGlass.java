/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.EnumCreatureType
 *  net.minecraft.world.IBlockAccess
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.BlockMetaData;
import ic2.core.init.InternalName;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.IBlockAccess;

public class BlockTexGlass
extends BlockMetaData {
    public BlockTexGlass(InternalName internalName1) {
        super(internalName1, Material.glass);
        this.setHardness(5.0f);
        this.setResistance(180.0f);
        this.setStepSound(Block.soundTypeGlass);
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        if (iBlockAccess.getBlock(x, y, z) == this) {
            return false;
        }
        return super.shouldSideBeRendered(iBlockAccess, x, y, z, side);
    }
}

