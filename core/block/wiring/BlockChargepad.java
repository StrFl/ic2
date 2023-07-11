/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.wiring;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.wiring.TileEntityChargepadBatBox;
import ic2.core.block.wiring.TileEntityChargepadBlock;
import ic2.core.block.wiring.TileEntityChargepadCESU;
import ic2.core.block.wiring.TileEntityChargepadMFE;
import ic2.core.block.wiring.TileEntityChargepadMFSU;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemChargepadBlock;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockChargepad
extends BlockMultiID {
    public BlockChargepad(InternalName internalName) {
        super(internalName, Material.iron, ItemChargepadBlock.class);
        this.setHardness(1.5f);
        this.setStepSound(soundTypeMetal);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.95f, 1.0f);
        Ic2Items.ChargepadbatBox = new ItemStack((Block)this, 1, 0);
        Ic2Items.ChargepadcesuUnit = new ItemStack((Block)this, 1, 1);
        Ic2Items.ChargepadmfeUnit = new ItemStack((Block)this, 1, 2);
        Ic2Items.ChargepadmfsUnit = new ItemStack((Block)this, 1, 3);
        GameRegistry.registerTileEntity(TileEntityChargepadBatBox.class, (String)"Chargepad BatBox");
        GameRegistry.registerTileEntity(TileEntityChargepadCESU.class, (String)"Chargepad CESU");
        GameRegistry.registerTileEntity(TileEntityChargepadMFE.class, (String)"Chargepad MFE");
        GameRegistry.registerTileEntity(TileEntityChargepadMFSU.class, (String)"Chargepad MFSU");
    }

    @Override
    public String getTextureFolder(int id) {
        return "wiring";
    }

    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityChargepadBlock te = (TileEntityChargepadBlock)this.getOwnTe(blockAccess, x, y, z);
        if (te == null) {
            return 0;
        }
        return te.isEmittingRedstone() ? 15 : 0;
    }

    public boolean canProvidePower() {
        return true;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        switch (meta) {
            case 0: {
                return TileEntityChargepadBatBox.class;
            }
            case 1: {
                return TileEntityChargepadCESU.class;
            }
            case 2: {
                return TileEntityChargepadMFE.class;
            }
            case 3: {
                return TileEntityChargepadMFSU.class;
            }
        }
        return null;
    }

    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (!IC2.platform.isRendering()) {
            return;
        }
        TileEntityChargepadBlock te = (TileEntityChargepadBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return;
        }
        te.spawnParticles(world, x, y, z, random);
    }

    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        if (entity instanceof EntityPlayer) {
            TileEntityChargepadBlock te = (TileEntityChargepadBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
            if (te == null) {
                return;
            }
            te.playerstandsat((EntityPlayer)entity);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        TileEntityChargepadBlock te = (TileEntityChargepadBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return;
        }
        NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(stack);
        te.energy = nbttagcompound.getDouble("energy");
        if (entity == null) {
            te.setFacing((short)0);
        } else {
            int yaw = MathHelper.floor_double((double)((double)(entity.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
            int pitch = Math.round(entity.rotationPitch);
            if (pitch <= -65) {
                te.setFacing((short)0);
            } else {
                switch (yaw) {
                    case 0: {
                        te.setFacing((short)2);
                        break;
                    }
                    case 1: {
                        te.setFacing((short)5);
                        break;
                    }
                    case 2: {
                        te.setFacing((short)3);
                        break;
                    }
                    case 3: {
                        te.setFacing((short)4);
                    }
                }
            }
        }
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isNormalCube(IBlockAccess world, int i, int j, int k) {
        return false;
    }
}

