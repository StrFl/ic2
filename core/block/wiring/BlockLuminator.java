/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.material.Material
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.wiring;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.Direction;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.BlockPoleFence;
import ic2.core.block.wiring.BlockCable;
import ic2.core.block.wiring.TileEntityLuminator;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemLuminator;
import ic2.core.util.AabbUtil;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockLuminator
extends BlockMultiID {
    boolean light;

    public BlockLuminator(InternalName internalName1) {
        super(internalName1, Material.glass, ItemLuminator.class);
        this.setStepSound(soundTypeGlass);
        this.setHardness(0.3f);
        this.setResistance(0.5f);
        if (internalName1 == InternalName.blockLuminator) {
            this.light = true;
            this.setLightLevel(1.0f);
            this.setCreativeTab(null);
            GameRegistry.registerTileEntity(TileEntityLuminator.class, (String)"Luminator");
        } else {
            this.light = false;
        }
    }

    @Override
    public String getTextureFolder(int id) {
        return "wiring";
    }

    @Override
    public int getTextureIndex(int meta) {
        return 0;
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int direction) {
        if (!world.isAirBlock(x, y, z)) {
            return false;
        }
        switch (direction) {
            case 0: {
                ++y;
                break;
            }
            case 1: {
                --y;
                break;
            }
            case 2: {
                ++z;
                break;
            }
            case 3: {
                --z;
                break;
            }
            case 4: {
                ++x;
                break;
            }
            case 5: {
                --x;
            }
        }
        return BlockLuminator.isSupportingBlock(world, x, y, z);
    }

    public static boolean isSupportingBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return !block.isAir((IBlockAccess)world, x, y, z) && (block.isOpaqueCube() || BlockLuminator.isSpecialSupporter((IBlockAccess)world, x, y, z));
    }

    public static boolean isSpecialSupporter(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block.isAir(world, x, y, z)) {
            return false;
        }
        return block instanceof BlockFence || block instanceof BlockPoleFence || block instanceof BlockCable || StackUtil.equals(block, Ic2Items.reinforcedGlass) || block == Blocks.glass;
    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        TileEntityLuminator te = (TileEntityLuminator)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return true;
        }
        if (te.ignoreBlockStay) {
            return true;
        }
        int facing = world.getBlockMetadata(x, y, z);
        switch (facing) {
            case 0: {
                ++y;
                break;
            }
            case 1: {
                --y;
                break;
            }
            case 2: {
                ++z;
                break;
            }
            case 3: {
                --z;
                break;
            }
            case 4: {
                ++x;
                break;
            }
            case 5: {
                --x;
            }
        }
        return BlockLuminator.isSupportingBlock(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!this.canBlockStay(world, x, y, z)) {
            world.setBlockToAir(x, y, z);
        }
        super.onNeighborBlockChange(world, x, y, z, neighbor);
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return IC2.platform.getRenderId("luminator");
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z, int meta) {
        return this.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float[] box = BlockLuminator.getBoxOfLuminator((IBlockAccess)world, x, y, z);
        return AxisAlignedBB.getBoundingBox((double)(box[0] + (float)x), (double)(box[1] + (float)y), (double)(box[2] + (float)z), (double)(box[3] + (float)x), (double)(box[4] + (float)y), (double)(box[5] + (float)z));
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return this.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 absDirection) {
        Vec3 direction = Vec3.createVectorHelper((double)(absDirection.xCoord - origin.xCoord), (double)(absDirection.yCoord - origin.yCoord), (double)(absDirection.zCoord - origin.zCoord));
        double maxLength = direction.lengthVector();
        Vec3 intersection = Vec3.createVectorHelper((double)0.0, (double)0.0, (double)0.0);
        Direction intersectingDirection = AabbUtil.getIntersection(origin, direction, this.getCollisionBoundingBoxFromPool(world, x, y, z), intersection);
        if (intersectingDirection != null && intersection.distanceTo(origin) <= maxLength) {
            return new MovingObjectPosition(x, y, z, intersectingDirection.toSideValue(), intersection);
        }
        return null;
    }

    public static float[] getBoxOfLuminator(IBlockAccess world, int x, int y, int z) {
        int facing = world.getBlockMetadata(x, y, z);
        float px = 0.0625f;
        switch (facing) {
            case 0: {
                ++y;
                break;
            }
            case 1: {
                --y;
                break;
            }
            case 2: {
                ++z;
                break;
            }
            case 3: {
                --z;
                break;
            }
            case 4: {
                ++x;
                break;
            }
            case 5: {
                --x;
            }
        }
        boolean fullCover = BlockLuminator.isSpecialSupporter(world, x, y, z);
        switch (facing) {
            case 1: {
                return new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f * px, 1.0f};
            }
            case 2: {
                if (fullCover) {
                    return new float[]{0.0f, 0.0f, 15.0f * px, 1.0f, 1.0f, 1.0f};
                }
                return new float[]{6.0f * px, 3.0f * px, 14.0f * px, 1.0f - 6.0f * px, 1.0f - 3.0f * px, 1.0f};
            }
            case 3: {
                if (fullCover) {
                    return new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f * px};
                }
                return new float[]{6.0f * px, 3.0f * px, 0.0f, 1.0f - 6.0f * px, 1.0f - 3.0f * px, 2.0f * px};
            }
            case 4: {
                if (fullCover) {
                    return new float[]{15.0f * px, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
                }
                return new float[]{14.0f * px, 3.0f * px, 6.0f * px, 1.0f, 1.0f - 3.0f * px, 1.0f - 6.0f * px};
            }
            case 5: {
                if (fullCover) {
                    return new float[]{0.0f, 0.0f, 0.0f, 1.0f * px, 1.0f, 1.0f};
                }
                return new float[]{0.0f, 3.0f * px, 6.0f * px, 2.0f * px, 1.0f - 3.0f * px, 1.0f - 6.0f * px};
            }
        }
        if (fullCover) {
            return new float[]{0.0f, 15.0f * px, 0.0f, 1.0f, 1.0f, 1.0f};
        }
        return new float[]{4.0f * px, 13.0f * px, 4.0f * px, 1.0f - 4.0f * px, 1.0f, 1.0f - 4.0f * px};
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isNormalCube() {
        return false;
    }

    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        if (this.light && entity instanceof EntityMob) {
            entity.setFire(entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD ? 20 : 10);
        }
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        return TileEntityLuminator.class;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tabs, List itemList) {
        if (!this.light) {
            super.getSubBlocks(item, tabs, itemList);
        }
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntityLuminator te = (TileEntityLuminator)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return 0;
        }
        return (int)Math.round(Util.map(te.energy, te.getMaxEnergy(), 15.0));
    }
}

