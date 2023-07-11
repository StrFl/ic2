/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.generator.tileentity.TileEntityGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntityRTGenerator;
import ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityWindGenerator;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.init.InternalName;
import ic2.core.util.LogCategory;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

public final class BlockTileEntity
extends BlockMultiID {
    public BlockTileEntity(InternalName internalName1) {
        super(internalName1, Material.iron);
        Ic2Items.teBlock = new ItemStack((Block)this);
    }

    public TileEntity getTileEntity(int id) {
        switch (id) {
            case 100: {
                return new TileEntityGenerator();
            }
            case 101: {
                return new TileEntityGeoGenerator();
            }
            case 102: {
                return new TileEntityWaterGenerator();
            }
            case 103: {
                return new TileEntitySolarGenerator();
            }
            case 104: {
                return new TileEntityWindGenerator();
            }
            case 105: {
                return new TileEntityNuclearReactorElectric();
            }
            case 106: {
                return new TileEntityRTGenerator();
            }
            case 107: {
                return new TileEntitySemifluidGenerator();
            }
        }
        IC2.log.warn(LogCategory.Block, "te block with an invalid id requested: %d.", id);
        return null;
    }

    public String getName(int id) {
        switch (id) {
            case 100: {
                return "blockGenerator";
            }
            case 101: {
                return "blockGeoGenerator";
            }
            case 102: {
                return "blockWaterGenerator";
            }
            case 103: {
                return "blockSolarGenerator";
            }
            case 104: {
                return "blockWindGenerator";
            }
            case 105: {
                return "blockNuclearReactor";
            }
            case 106: {
                return "blockRTGenerator";
            }
            case 107: {
                return "blockSemifluidGenerator";
            }
        }
        return null;
    }

    @Override
    public String getTextureFolder(int id) {
        if (id >= 100 && id <= 107) {
            return "generator";
        }
        return null;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        switch (stack.getItemDamage()) {
            default: 
        }
        return EnumRarity.common;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        return null;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
        TileEntity te = this.getTileEntity(stack.getItemDamage());
        assert (te instanceof TileEntityBlock);
        world.setTileEntity(x, y, z, te);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset) {
        MutableBoolean result = new MutableBoolean();
        if (this.getTe(world, x, y, z).onBlockActivated(player, xOffset, yOffset, zOffset, result)) {
            return result.booleanValue();
        }
        return super.onBlockActivated(world, x, y, z, player, side, xOffset, yOffset, zOffset);
    }

    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        this.getTe(world, x, y, z).randomDisplayTick(random);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = super.getDrops(world, x, y, z, metadata, fortune);
        this.getTe(world, x, y, z).adjustDrops(ret, fortune);
        return ret;
    }

    private TileEntityBlock getTe(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        return (TileEntityBlock)te;
    }
}

