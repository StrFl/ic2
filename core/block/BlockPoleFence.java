/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.api.item.ItemWrapper;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.block.machine.tileentity.TileEntityMagnetizer;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPoleFence
extends BlockMetaData {
    private static final float halfThickness = 0.125f;

    public BlockPoleFence(InternalName internalName1) {
        super(internalName1, Material.iron, ItemBlockIC2.class);
        this.setHardness(1.5f);
        this.setResistance(5.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.ironFence = new ItemStack((Block)this);
    }

    @Override
    public int getTextureIndex(int meta) {
        return 0;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isNormalCube(IBlockAccess world, int i, int j, int k) {
        return false;
    }

    public int getRenderType() {
        return IC2.platform.getRenderId("fence");
    }

    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        boolean zneg = this.canConnectFenceTo((IBlockAccess)world, x, y, z - 1, false);
        boolean zpos = this.canConnectFenceTo((IBlockAccess)world, x, y, z + 1, false);
        boolean xneg = this.canConnectFenceTo((IBlockAccess)world, x - 1, y, z, false);
        boolean xpos = this.canConnectFenceTo((IBlockAccess)world, x + 1, y, z, false);
        float boundxneg = 0.375f;
        float boundxpos = 0.625f;
        float boundzneg = 0.375f;
        float boundzpos = 0.625f;
        if (zneg) {
            boundzneg = 0.0f;
        }
        if (zpos) {
            boundzpos = 1.0f;
        }
        if (zneg || zpos) {
            this.setBlockBounds(boundxneg, 0.0f, boundzneg, boundxpos, 1.5f, boundzpos);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
        boundzneg = 0.375f;
        boundzpos = 0.625f;
        if (xneg) {
            boundxneg = 0.0f;
        }
        if (xpos) {
            boundxpos = 1.0f;
        }
        if (xneg || xpos || !zneg && !zpos) {
            this.setBlockBounds(boundxneg, 0.0f, boundzneg, boundxpos, 1.5f, boundzpos);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
        if (zneg) {
            boundzneg = 0.0f;
        }
        if (zpos) {
            boundzpos = 1.0f;
        }
        this.setBlockBounds(boundxneg, 0.0f, boundzneg, boundxpos, 1.0f, boundzpos);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        boolean zneg = this.canConnectFenceTo(world, x, y, z - 1, false);
        boolean zpos = this.canConnectFenceTo(world, x, y, z + 1, false);
        boolean xneg = this.canConnectFenceTo(world, x - 1, y, z, false);
        boolean xpos = this.canConnectFenceTo(world, x + 1, y, z, false);
        float boundxneg = 0.375f;
        float boundxpos = 0.625f;
        float boundzneg = 0.375f;
        float boundzpos = 0.625f;
        if (zneg) {
            boundzneg = 0.0f;
        }
        if (zpos) {
            boundzpos = 1.0f;
        }
        if (xneg) {
            boundxneg = 0.0f;
        }
        if (xpos) {
            boundxpos = 1.0f;
        }
        this.setBlockBounds(boundxneg, 0.0f, boundzneg, boundxpos, 1.0f, boundzpos);
    }

    private boolean canConnectFenceTo(IBlockAccess world, int x, int y, int z, boolean pole) {
        Block block = world.getBlock(x, y, z);
        if (block == this || block instanceof BlockFence) {
            return true;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        return te != null && te instanceof TileEntityMagnetizer && !pole;
    }

    public boolean isPole(World world, int i, int j, int k) {
        return !this.canConnectFenceTo((IBlockAccess)world, i - 1, j, k, true) && !this.canConnectFenceTo((IBlockAccess)world, i + 1, j, k, true) && !this.canConnectFenceTo((IBlockAccess)world, i, j, k - 1, true) && !this.canConnectFenceTo((IBlockAccess)world, i, j, k + 1, true);
    }

    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        boolean slow;
        if (this.blockMaterial != Material.iron || !this.isPole(world, i, j, k) || !(entity instanceof EntityPlayer)) {
            return;
        }
        int meta = world.getBlockMetadata(i, j, k);
        boolean powered = meta > 0;
        EntityPlayer player = (EntityPlayer)entity;
        boolean metalShoes = BlockPoleFence.hasMetalShoes(player);
        boolean descending = player.isSneaking();
        boolean bl = slow = player.motionY >= -0.25 || player.motionY < 1.6;
        if (slow) {
            player.fallDistance = 0.0f;
        }
        if (!powered) {
            if (descending && !slow) {
                player.motionY *= 0.9;
            }
        } else {
            world.setBlockMetadataWithNotify(i, j, k, meta - 1, 7);
            if (descending) {
                if (!slow) {
                    player.motionY *= 0.8;
                }
            } else {
                player.motionY += 0.075;
                if (player.motionY > 0.0) {
                    player.motionY *= 1.03;
                }
                double maxSpeed = IC2.keyboard.isAltKeyDown(player) ? 0.1 : (metalShoes ? 1.5 : 0.5);
                player.motionY = Math.min(player.motionY, maxSpeed);
            }
        }
    }

    public static boolean hasMetalShoes(EntityPlayer player) {
        Item item;
        ItemStack shoes = player.inventory.armorInventory[0];
        return shoes != null && ((item = shoes.getItem()) == Items.iron_boots || item == Items.golden_boots || item == Items.chainmail_boots || ItemWrapper.isMetalArmor(shoes, player));
    }
}

