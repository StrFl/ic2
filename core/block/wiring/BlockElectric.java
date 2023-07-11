/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.wiring.TileEntityElectricBatBox;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.block.wiring.TileEntityElectricCESU;
import ic2.core.block.wiring.TileEntityElectricMFE;
import ic2.core.block.wiring.TileEntityElectricMFSU;
import ic2.core.block.wiring.TileEntityTransformerEV;
import ic2.core.block.wiring.TileEntityTransformerHV;
import ic2.core.block.wiring.TileEntityTransformerLV;
import ic2.core.block.wiring.TileEntityTransformerMV;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.block.ItemElectricBlock;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockElectric
extends BlockMultiID {
    public BlockElectric(InternalName internalName1) {
        super(internalName1, Material.iron, ItemElectricBlock.class);
        this.setHardness(1.5f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.batBox = new ItemStack((Block)this, 1, 0);
        Ic2Items.mfeUnit = new ItemStack((Block)this, 1, 1);
        Ic2Items.mfsUnit = new ItemStack((Block)this, 1, 2);
        Ic2Items.lvTransformer = new ItemStack((Block)this, 1, 3);
        Ic2Items.mvTransformer = new ItemStack((Block)this, 1, 4);
        Ic2Items.hvTransformer = new ItemStack((Block)this, 1, 5);
        Ic2Items.evTransformer = new ItemStack((Block)this, 1, 6);
        Ic2Items.cesuUnit = new ItemStack((Block)this, 1, 7);
        GameRegistry.registerTileEntity(TileEntityElectricBatBox.class, (String)"BatBox");
        GameRegistry.registerTileEntity(TileEntityElectricCESU.class, (String)"CESU");
        GameRegistry.registerTileEntity(TileEntityElectricMFE.class, (String)"MFE");
        GameRegistry.registerTileEntity(TileEntityElectricMFSU.class, (String)"MFSU");
        GameRegistry.registerTileEntity(TileEntityTransformerLV.class, (String)"LV-Transformer");
        GameRegistry.registerTileEntity(TileEntityTransformerMV.class, (String)"MV-Transformer");
        GameRegistry.registerTileEntity(TileEntityTransformerHV.class, (String)"HV-Transformer");
        GameRegistry.registerTileEntity(TileEntityTransformerEV.class, (String)"EV-Transformer");
    }

    @Override
    public String getTextureFolder(int id) {
        return "wiring";
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        if (ConfigUtil.getBool(MainConfig.get(), "balance/ignoreWrenchRequirement")) {
            return Item.getItemFromBlock((Block)this);
        }
        switch (meta) {
            case 0: 
            case 3: {
                return Item.getItemFromBlock((Block)this);
            }
        }
        return Ic2Items.machine.getItem();
    }

    public int damageDropped(int meta) {
        if (ConfigUtil.getBool(MainConfig.get(), "balance/ignoreWrenchRequirement")) {
            return meta;
        }
        switch (meta) {
            case 0: 
            case 3: {
                return meta;
            }
        }
        return Ic2Items.machine.getItemDamage();
    }

    public int quantityDropped(Random random) {
        return 1;
    }

    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityBlock te = (TileEntityBlock)this.getOwnTe(blockAccess, x, y, z);
        if (!(te instanceof TileEntityElectricBlock)) {
            return 0;
        }
        return ((TileEntityElectricBlock)te).isEmittingRedstone() ? 15 : 0;
    }

    public boolean canProvidePower() {
        return true;
    }

    public boolean isNormalCube(IBlockAccess world, int i, int j, int k) {
        return false;
    }

    public boolean isBlockOpaqueCube(World world, int i, int j, int k) {
        return true;
    }

    public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        switch (meta) {
            case 0: {
                return TileEntityElectricBatBox.class;
            }
            case 1: {
                return TileEntityElectricMFE.class;
            }
            case 2: {
                return TileEntityElectricMFSU.class;
            }
            case 3: {
                return TileEntityTransformerLV.class;
            }
            case 4: {
                return TileEntityTransformerMV.class;
            }
            case 5: {
                return TileEntityTransformerHV.class;
            }
            case 6: {
                return TileEntityTransformerEV.class;
            }
            case 7: {
                return TileEntityElectricCESU.class;
            }
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        TileEntityBlock te = (TileEntityBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return;
        }
        if (te instanceof TileEntityElectricBlock) {
            NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemStack);
            ((TileEntityElectricBlock)te).energy = nbttagcompound.getDouble("energy");
        }
        if (entityliving == null) {
            te.setFacing((short)1);
        } else {
            int yaw = MathHelper.floor_double((double)((double)(entityliving.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
            int pitch = Math.round(entityliving.rotationPitch);
            if (pitch >= 65) {
                te.setFacing((short)1);
            } else if (pitch <= -65) {
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

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() == 2 || stack.getItemDamage() == 5 ? EnumRarity.uncommon : EnumRarity.common;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntityBlock te = (TileEntityBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (!(te instanceof TileEntityBlock)) {
            return 0;
        }
        TileEntityElectricBlock teb = (TileEntityElectricBlock)te;
        return (int)Math.round(Util.map(teb.energy, teb.maxStorage, 15.0));
    }
}

