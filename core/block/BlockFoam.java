/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.EnumCreatureType
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFoam
extends BlockMetaData {
    public BlockFoam(InternalName internalName1) {
        super(internalName1, Material.cloth, ItemBlockIC2.class);
        this.setTickRandomly(true);
        this.setHardness(0.01f);
        this.setResistance(10.0f);
        this.setStepSound(soundTypeCloth);
        Ic2Items.constructionFoam = new ItemStack((Block)this);
    }

    @Override
    public String getTextureFolder(int id) {
        return "cf";
    }

    public int tickRate(World world) {
        return 500;
    }

    public int quantityDropped(Random r) {
        return 0;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isNormalCube(IBlockAccess world, int i, int j, int k) {
        return true;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return null;
    }

    public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    public void updateTick(World world, int i, int j, int k, Random random) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        if (world.getBlockLightValue(i, j, k) * 6 >= world.rand.nextInt(1000)) {
            world.setBlock(i, j, k, StackUtil.getBlock(Ic2Items.constructionFoamWall), 7, 7);
        } else {
            world.scheduleBlockUpdate(i, j, k, (Block)this, this.tickRate(world));
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float xOffset, float yOffset, float zOffset) {
        ItemStack itemStack = entityPlayer.getCurrentEquippedItem();
        if (itemStack != null && StackUtil.equals((Block)Blocks.sand, itemStack)) {
            world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.constructionFoamWall), 7, 7);
            if (!entityPlayer.capabilities.isCreativeMode) {
                --itemStack.stackSize;
                if (itemStack.stackSize <= 0) {
                    entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = null;
                }
            }
            return true;
        }
        return false;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block.isAir((IBlockAccess)world, x, y, z) || block == Blocks.fire || block.getMaterial().isLiquid();
    }

    public ItemStack createStackedBlock(int i) {
        return null;
    }

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
}

