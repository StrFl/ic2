/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.ColorizerFoliage
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemRubLeaves;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRubLeaves
extends BlockLeaves {
    int[] adjacentTreeBlocks;

    public BlockRubLeaves(InternalName internalName) {
        this.setBlockName(internalName.name());
        this.setCreativeTab(IC2.tabIC2);
        GameRegistry.registerBlock((Block)this, ItemRubLeaves.class, (String)internalName.name());
        Ic2Items.rubberLeaves = new ItemStack((Block)this);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(IC2.textureDomain + ":" + this.getUnlocalizedName().substring(5));
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(int par1, int par2) {
        return this.blockIcon;
    }

    @SideOnly(value=Side.CLIENT)
    public int getRenderColor(int i) {
        return ColorizerFoliage.getFoliageColorBirch();
    }

    @SideOnly(value=Side.CLIENT)
    public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
        return ColorizerFoliage.getFoliageColorBirch();
    }

    public int quantityDropped(Random random) {
        return random.nextInt(35) != 0 ? 0 : 1;
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        return Ic2Items.rubberSapling.getItem();
    }

    public int damageDropped(int i) {
        return 0;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    public void dropBlockAsItemWithChance(World world, int par2, int par3, int par4, int meta, float par6, int fortune) {
        if (!world.isRemote && world.rand.nextInt(35) == 0) {
            Item item = this.getItemDropped(meta, world.rand, fortune);
            this.dropBlockAsItem(world, par2, par3, par4, new ItemStack(item, 1, this.damageDropped(meta)));
        }
    }

    public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return true;
    }

    public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
        return true;
    }

    public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack((Block)this, 1, world.getBlockMetadata(x, y, z) & 3));
        return ret;
    }

    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(item, 1, 0));
    }

    public String[] func_150125_e() {
        return null;
    }

    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 30;
    }

    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 20;
    }
}

