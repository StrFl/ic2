/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.block.machine;

import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMiningPipe
extends BlockMetaData {
    public BlockMiningPipe(InternalName internalName1) {
        super(internalName1, Material.iron, ItemBlockIC2.class);
        this.setHardness(6.0f);
        this.setResistance(10.0f);
        this.setBlockBounds(0.375f, 0.0f, 0.375f, 0.625f, 1.0f, 0.625f);
        Ic2Items.miningPipe = new ItemStack((Block)this);
    }

    @Override
    public String getTextureFolder(int id) {
        return "machine";
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isNormalCube(IBlockAccess world, int i, int j, int k) {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }
}

