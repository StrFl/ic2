/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.block.BlockMetaData;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.MaterialIC2TNT;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public abstract class BlockIC2Explosive
extends BlockMetaData {
    public boolean canExplodeByHand = false;

    public BlockIC2Explosive(InternalName internalName1, boolean manual) {
        super(internalName1, MaterialIC2TNT.instance, ItemBlockIC2.class);
        this.canExplodeByHand = manual;
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.removedByPlayer(world, null, x, y, z);
        }
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.removedByPlayer(world, null, x, y, z);
        }
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        EntityIC2Explosive entitytntprimed = this.getExplosionEntity(world, x, y, z, explosion == null ? null : explosion.getExplosivePlacedBy());
        if (entitytntprimed == null) {
            return;
        }
        entitytntprimed.fuse = world.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
        world.spawnEntityInWorld((Entity)entitytntprimed);
    }

    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        int l = world.getBlockMetadata(x, y, z);
        if (player != null && (l & 1) == 0 && !this.canExplodeByHand) {
            this.dropBlockAsItem(world, x, y, z, new ItemStack((Block)this, 1, 0));
        } else {
            EntityIC2Explosive entitytntprimed = this.getExplosionEntity(world, x, y, z, (EntityLivingBase)(player == null ? null : player));
            if (entitytntprimed == null) {
                return false;
            }
            this.onIgnite(world, player, x, y, z);
            world.spawnEntityInWorld((Entity)entitytntprimed);
            world.playSoundAtEntity((Entity)entitytntprimed, "random.fuse", 1.0f, 1.0f);
        }
        world.setBlockToAir(x, y, z);
        return false;
    }

    public abstract EntityIC2Explosive getExplosionEntity(World var1, int var2, int var3, int var4, EntityLivingBase var5);

    public void onIgnite(World world, EntityPlayer player, int x, int y, int z) {
    }
}

