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
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.tileentity.TileEntityAdvMiner;
import ic2.core.block.machine.tileentity.TileEntityCentrifuge;
import ic2.core.block.machine.tileentity.TileEntityCondenser;
import ic2.core.block.machine.tileentity.TileEntityCropmatron;
import ic2.core.block.machine.tileentity.TileEntityFermenter;
import ic2.core.block.machine.tileentity.TileEntityFluidBottler;
import ic2.core.block.machine.tileentity.TileEntityFluidRegulator;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.block.machine.tileentity.TileEntityMetalFormer;
import ic2.core.block.machine.tileentity.TileEntityOreWashing;
import ic2.core.block.machine.tileentity.TileEntityPatternStorage;
import ic2.core.block.machine.tileentity.TileEntityReplicator;
import ic2.core.block.machine.tileentity.TileEntityScanner;
import ic2.core.block.machine.tileentity.TileEntitySolidCanner;
import ic2.core.block.machine.tileentity.TileEntityTeleporter;
import ic2.core.block.machine.tileentity.TileEntityTesla;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.block.ItemMachine2;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockMachine2
extends BlockMultiID {
    public BlockMachine2(InternalName internalName1) {
        super(internalName1, Material.iron, ItemMachine2.class);
        this.setHardness(2.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.teleporter = new ItemStack((Block)this, 1, 0);
        Ic2Items.teslaCoil = new ItemStack((Block)this, 1, 1);
        Ic2Items.cropmatron = new ItemStack((Block)this, 1, 2);
        Ic2Items.centrifuge = new ItemStack((Block)this, 1, 3);
        Ic2Items.metalformer = new ItemStack((Block)this, 1, 4);
        Ic2Items.orewashingplant = new ItemStack((Block)this, 1, 5);
        Ic2Items.patternstorage = new ItemStack((Block)this, 1, 6);
        Ic2Items.scanner = new ItemStack((Block)this, 1, 7);
        Ic2Items.replicator = new ItemStack((Block)this, 1, 8);
        Ic2Items.solidcanner = new ItemStack((Block)this, 1, 9);
        Ic2Items.fluidbottler = new ItemStack((Block)this, 1, 10);
        Ic2Items.advminer = new ItemStack((Block)this, 1, 11);
        Ic2Items.liquidheatexchanger = new ItemStack((Block)this, 1, 12);
        Ic2Items.fermenter = new ItemStack((Block)this, 1, 13);
        Ic2Items.fluidregulator = new ItemStack((Block)this, 1, 14);
        Ic2Items.condenser = new ItemStack((Block)this, 1, 15);
        GameRegistry.registerTileEntity(TileEntityTeleporter.class, (String)"Teleporter");
        GameRegistry.registerTileEntity(TileEntityTesla.class, (String)"Tesla Coil");
        GameRegistry.registerTileEntity(TileEntityCropmatron.class, (String)"Crop-Matron");
        GameRegistry.registerTileEntity(TileEntityCentrifuge.class, (String)"Thermal Centrifuge");
        GameRegistry.registerTileEntity(TileEntityMetalFormer.class, (String)"Metal Former");
        GameRegistry.registerTileEntity(TileEntityOreWashing.class, (String)"Ore Washing Plant");
        GameRegistry.registerTileEntity(TileEntityPatternStorage.class, (String)"Pattern Storage");
        GameRegistry.registerTileEntity(TileEntityScanner.class, (String)"Scanner");
        GameRegistry.registerTileEntity(TileEntityReplicator.class, (String)"Replicator");
        GameRegistry.registerTileEntity(TileEntitySolidCanner.class, (String)"Solid Canner Maschine");
        GameRegistry.registerTileEntity(TileEntityFluidBottler.class, (String)"Fluid Bottler Maschine");
        GameRegistry.registerTileEntity(TileEntityAdvMiner.class, (String)"Advanced Miner");
        GameRegistry.registerTileEntity(TileEntityLiquidHeatExchanger.class, (String)"Liquid Heat Exchanger");
        GameRegistry.registerTileEntity(TileEntityFermenter.class, (String)"Fermenter");
        GameRegistry.registerTileEntity(TileEntityFluidRegulator.class, (String)"Fluid Regulator");
        GameRegistry.registerTileEntity(TileEntityCondenser.class, (String)"Condenser");
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
            case 11: {
                return Item.getItemFromBlock((Block)this);
            }
            case 0: 
            case 6: 
            case 7: 
            case 8: {
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
            case 11: {
                return meta;
            }
            case 0: 
            case 6: 
            case 7: 
            case 8: {
                return Ic2Items.advancedMachine.getItemDamage();
            }
        }
        return Ic2Items.machine.getItemDamage();
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        switch (meta) {
            case 0: {
                return TileEntityTeleporter.class;
            }
            case 1: {
                return TileEntityTesla.class;
            }
            case 2: {
                return TileEntityCropmatron.class;
            }
            case 3: {
                return TileEntityCentrifuge.class;
            }
            case 4: {
                return TileEntityMetalFormer.class;
            }
            case 5: {
                return TileEntityOreWashing.class;
            }
            case 6: {
                return TileEntityPatternStorage.class;
            }
            case 7: {
                return TileEntityScanner.class;
            }
            case 8: {
                return TileEntityReplicator.class;
            }
            case 9: {
                return TileEntitySolidCanner.class;
            }
            case 10: {
                return TileEntityFluidBottler.class;
            }
            case 11: {
                return TileEntityAdvMiner.class;
            }
            case 12: {
                return TileEntityLiquidHeatExchanger.class;
            }
            case 13: {
                return TileEntityFermenter.class;
            }
            case 14: {
                return TileEntityFluidRegulator.class;
            }
            case 15: {
                return TileEntityCondenser.class;
            }
        }
        return null;
    }

    public void randomDisplayTick(World world, int i, int j, int k, Random random) {
        world.getBlockMetadata(i, j, k);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        NBTTagCompound nbttagcompound;
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        if (!IC2.platform.isSimulating()) {
            return;
        }
        TileEntityBlock te = (TileEntityBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return;
        }
        if (te instanceof TileEntityAdvMiner) {
            nbttagcompound = StackUtil.getOrCreateNbtData(stack);
            ((TileEntityAdvMiner)te).energy = nbttagcompound.getDouble("energy");
        }
        if (te instanceof TileEntityPatternStorage) {
            nbttagcompound = StackUtil.getOrCreateNbtData(stack);
            ((TileEntityPatternStorage)te).readContents(nbttagcompound);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() == 0 ? EnumRarity.rare : EnumRarity.common;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntityBlock te = (TileEntityBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return 0;
        }
        if (te instanceof TileEntityTeleporter) {
            return ((TileEntityTeleporter)te).targetSet ? 15 : 0;
        }
        return 0;
    }
}

