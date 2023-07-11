/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.block.machine;

import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockMiningTip
extends BlockMetaData {
    public BlockMiningTip(InternalName internalName1) {
        super(internalName1, Material.iron, ItemBlockIC2.class);
        this.setHardness(6.0f);
        this.setResistance(10.0f);
        Ic2Items.miningPipeTip = new ItemStack((Block)this);
    }

    @Override
    public String getTextureFolder(int id) {
        return "machine";
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        return false;
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        return Ic2Items.miningPipe.getItem();
    }

    public void getSubBlocks(Item item, CreativeTabs tabs, List itemList) {
    }
}

