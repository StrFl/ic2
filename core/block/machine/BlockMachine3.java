/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.tileentity.TileEntityBlastFurnace;
import ic2.core.block.machine.tileentity.TileEntityBlockCutter;
import ic2.core.block.machine.tileentity.TileEntityCropHavester;
import ic2.core.block.machine.tileentity.TileEntityFluidDistributor;
import ic2.core.block.machine.tileentity.TileEntityItemBuffer;
import ic2.core.block.machine.tileentity.TileEntityLathe;
import ic2.core.block.machine.tileentity.TileEntitySolarDestiller;
import ic2.core.block.machine.tileentity.TileEntitySortingMachine;
import ic2.core.block.machine.tileentity.TileEntitySteamGenerator;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.block.ItemMachine3;
import ic2.core.util.ConfigUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockMachine3
extends BlockMultiID {
    public BlockMachine3(InternalName internalName1) {
        super(internalName1, Material.iron, ItemMachine3.class);
        this.setHardness(2.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.steamgenerator = new ItemStack((Block)this, 1, 0);
        Ic2Items.blastfurnace = new ItemStack((Block)this, 1, 1);
        Ic2Items.blockcutter = new ItemStack((Block)this, 1, 2);
        Ic2Items.solardestiller = new ItemStack((Block)this, 1, 3);
        Ic2Items.fluiddistributor = new ItemStack((Block)this, 1, 4);
        Ic2Items.sortingmachine = new ItemStack((Block)this, 1, 5);
        Ic2Items.itembuffer = new ItemStack((Block)this, 1, 6);
        Ic2Items.crophavester = new ItemStack((Block)this, 1, 7);
        Ic2Items.lathe = new ItemStack((Block)this, 1, 8);
        GameRegistry.registerTileEntity(TileEntitySteamGenerator.class, (String)"Steam Generator");
        GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, (String)"Blast Furnace");
        GameRegistry.registerTileEntity(TileEntityBlockCutter.class, (String)"Block Cutter");
        GameRegistry.registerTileEntity(TileEntitySolarDestiller.class, (String)"Solar Destiller");
        GameRegistry.registerTileEntity(TileEntityFluidDistributor.class, (String)"Fluid Distributor");
        GameRegistry.registerTileEntity(TileEntitySortingMachine.class, (String)"Sorting Machine");
        GameRegistry.registerTileEntity(TileEntityItemBuffer.class, (String)"Item Buffer");
        GameRegistry.registerTileEntity(TileEntityCropHavester.class, (String)"Crop Havester");
        GameRegistry.registerTileEntity(TileEntityLathe.class, (String)"Lathe");
    }

    @Override
    public String getTextureFolder(int id) {
        return "machine";
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        if (ConfigUtil.getBool(MainConfig.get(), "balance/ignoreWrenchRequirement")) {
            return Item.getItemFromBlock((Block)this);
        }
        switch (meta) {
            case 0: {
                return Item.getItemFromBlock((Block)this);
            }
            case 2: {
                return Ic2Items.advancedMachine.getItem();
            }
        }
        return Ic2Items.machine.getItem();
    }

    public int damageDropped(int meta) {
        if (ConfigUtil.getBool(MainConfig.get(), "balance/ignoreWrenchRequirement")) {
            return meta;
        }
        switch (meta) {
            case 0: {
                return meta;
            }
            case 2: {
                return Ic2Items.advancedMachine.getItemDamage();
            }
        }
        return Ic2Items.machine.getItemDamage();
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        switch (meta) {
            case 0: {
                return TileEntitySteamGenerator.class;
            }
            case 1: {
                return TileEntityBlastFurnace.class;
            }
            case 2: {
                return TileEntityBlockCutter.class;
            }
            case 3: {
                return TileEntitySolarDestiller.class;
            }
            case 4: {
                return TileEntityFluidDistributor.class;
            }
            case 5: {
                return TileEntitySortingMachine.class;
            }
            case 6: {
                return TileEntityItemBuffer.class;
            }
            case 7: {
                return TileEntityCropHavester.class;
            }
            case 8: {
                return TileEntityLathe.class;
            }
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        TileEntityBlock te = (TileEntityBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return;
        }
        if (te instanceof TileEntitySortingMachine) {
            ((TileEntitySortingMachine)te).setFacing((short)2);
        }
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }
}

