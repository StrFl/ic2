/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.EnumPlantType
 */
package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.WorldGenRubTree;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class BlockRubSapling
extends BlockSapling {
    public BlockRubSapling(InternalName internalName) {
        this.setHardness(0.0f);
        this.setStepSound(soundTypeGrass);
        this.setBlockName(internalName.name());
        this.setCreativeTab(IC2.tabIC2);
        GameRegistry.registerBlock((Block)this, ItemBlockIC2.class, (String)internalName.name());
        Ic2Items.rubberSapling = new ItemStack((Block)this);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(IC2.textureDomain + ":" + this.getUnlocalizedName());
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(int par1, int par2) {
        return this.blockIcon;
    }

    public String getUnlocalizedName() {
        return super.getUnlocalizedName().substring(5);
    }

    public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return true;
    }

    public void updateTick(World world, int i, int j, int k, Random random) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        if (!this.canBlockStay(world, i, j, k)) {
            this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlockToAir(i, j, k);
            return;
        }
        if (world.getBlockLightValue(i, j + 1, k) >= 9 && random.nextInt(30) == 0) {
            this.func_149878_d(world, i, j, k, random);
        }
    }

    public void func_149878_d(World world, int i, int j, int k, Random random) {
        new WorldGenRubTree().grow(world, i, j, k, random);
    }

    public int damageDropped(int i) {
        return 0;
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        ItemStack equipped = entityplayer.getCurrentEquippedItem();
        if (equipped == null) {
            return false;
        }
        if (equipped.getItem() == Items.dye && equipped.getItemDamage() == 15) {
            this.func_149878_d(world, i, j, k, world.rand);
            if (!entityplayer.capabilities.isCreativeMode) {
                --equipped.stackSize;
            }
            entityplayer.swingItem();
        }
        return false;
    }

    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(item, 1, 0));
    }

    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Plains;
    }
}

