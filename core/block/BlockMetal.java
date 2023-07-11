/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EnumCreatureType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.IBlockAccess
 */
package ic2.core.block;

import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockMetal;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

public class BlockMetal
extends BlockMetaData {
    public BlockMetal(InternalName internalName1) {
        super(internalName1, Material.iron, ItemBlockMetal.class);
        this.setHardness(4.0f);
        this.setStepSound(Block.soundTypeMetal);
        Ic2Items.bronzeBlock = new ItemStack((Block)this, 1, 2);
        Ic2Items.copperBlock = new ItemStack((Block)this, 1, 0);
        Ic2Items.tinBlock = new ItemStack((Block)this, 1, 1);
        Ic2Items.uraniumBlock = new ItemStack((Block)this, 1, 3);
        Ic2Items.leadBlock = new ItemStack((Block)this, 1, 4);
        Ic2Items.advironblock = new ItemStack((Block)this, 1, 5);
    }

    public int damageDropped(int i) {
        return i;
    }

    public void getSubBlocks(Item j, CreativeTabs tabs, List itemList) {
        for (int i = 0; i < 16; ++i) {
            ItemStack is = new ItemStack((Block)this, 1, i);
            if (is.getItem().getUnlocalizedName(is) == null) continue;
            itemList.add(is);
        }
    }

    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        int meta = worldObj.getBlockMetadata(x, y, z);
        return meta == 2 || meta == 3;
    }

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
}

