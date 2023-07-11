/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ITerraformingBP;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import ic2.core.block.machine.tileentity.TileEntityCompressor;
import ic2.core.block.machine.tileentity.TileEntityElectricFurnace;
import ic2.core.block.machine.tileentity.TileEntityElectrolyzer;
import ic2.core.block.machine.tileentity.TileEntityExtractor;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import ic2.core.block.machine.tileentity.TileEntityIronFurnace;
import ic2.core.block.machine.tileentity.TileEntityMacerator;
import ic2.core.block.machine.tileentity.TileEntityMagnetizer;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.block.machine.tileentity.TileEntityMiner;
import ic2.core.block.machine.tileentity.TileEntityPump;
import ic2.core.block.machine.tileentity.TileEntityRecycler;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.block.ItemMachine;
import ic2.core.util.ConfigUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockMachine
extends BlockMultiID {
    public BlockMachine(InternalName internalName1) {
        super(internalName1, Material.iron, ItemMachine.class);
        this.setHardness(2.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.machine = new ItemStack((Block)this, 1, 0);
        Ic2Items.advancedMachine = new ItemStack((Block)this, 1, 12);
        Ic2Items.ironFurnace = new ItemStack((Block)this, 1, 1);
        Ic2Items.electroFurnace = new ItemStack((Block)this, 1, 2);
        Ic2Items.macerator = new ItemStack((Block)this, 1, 3);
        Ic2Items.extractor = new ItemStack((Block)this, 1, 4);
        Ic2Items.compressor = new ItemStack((Block)this, 1, 5);
        Ic2Items.canner = new ItemStack((Block)this, 1, 6);
        Ic2Items.miner = new ItemStack((Block)this, 1, 7);
        Ic2Items.pump = new ItemStack((Block)this, 1, 8);
        Ic2Items.magnetizer = new ItemStack((Block)this, 1, 9);
        Ic2Items.electrolyzer = new ItemStack((Block)this, 1, 10);
        Ic2Items.recycler = new ItemStack((Block)this, 1, 11);
        Ic2Items.inductionFurnace = new ItemStack((Block)this, 1, 13);
        Ic2Items.massFabricator = new ItemStack((Block)this, 1, 14);
        Ic2Items.terraformer = new ItemStack((Block)this, 1, 15);
        GameRegistry.registerTileEntity(TileEntityIronFurnace.class, (String)"Iron Furnace");
        GameRegistry.registerTileEntity(TileEntityElectricFurnace.class, (String)"Electric Furnace");
        GameRegistry.registerTileEntity(TileEntityMacerator.class, (String)"Macerator");
        GameRegistry.registerTileEntity(TileEntityExtractor.class, (String)"Extractor");
        GameRegistry.registerTileEntity(TileEntityCompressor.class, (String)"Compressor");
        GameRegistry.registerTileEntity(TileEntityCanner.class, (String)"Canning Machine");
        GameRegistry.registerTileEntity(TileEntityMiner.class, (String)"Miner");
        GameRegistry.registerTileEntity(TileEntityPump.class, (String)"Pump");
        GameRegistry.registerTileEntity(TileEntityMagnetizer.class, (String)"Magnetizer");
        GameRegistry.registerTileEntity(TileEntityElectrolyzer.class, (String)"Electrolyzer");
        GameRegistry.registerTileEntity(TileEntityRecycler.class, (String)"Recycler");
        GameRegistry.registerTileEntity(TileEntityInduction.class, (String)"Induction Furnace");
        GameRegistry.registerTileEntity(TileEntityMatter.class, (String)"Mass Fabricator");
        GameRegistry.registerTileEntity(TileEntityTerra.class, (String)"Terraformer");
    }

    @Override
    public String getTextureFolder(int id) {
        return "machine";
    }

    public int damageDropped(int meta) {
        if (ConfigUtil.getBool(MainConfig.get(), "balance/ignoreWrenchRequirement")) {
            return meta;
        }
        switch (meta) {
            case 1: 
            case 2: 
            case 7: 
            case 9: {
                return meta;
            }
            case 12: 
            case 13: 
            case 14: 
            case 15: {
                return 12;
            }
        }
        return 0;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        switch (meta) {
            case 1: {
                return TileEntityIronFurnace.class;
            }
            case 2: {
                return TileEntityElectricFurnace.class;
            }
            case 3: {
                return TileEntityMacerator.class;
            }
            case 4: {
                return TileEntityExtractor.class;
            }
            case 5: {
                return TileEntityCompressor.class;
            }
            case 6: {
                return TileEntityCanner.class;
            }
            case 7: {
                return TileEntityMiner.class;
            }
            case 8: {
                return TileEntityPump.class;
            }
            case 9: {
                return TileEntityMagnetizer.class;
            }
            case 10: {
                return TileEntityElectrolyzer.class;
            }
            case 11: {
                return TileEntityRecycler.class;
            }
            case 13: {
                return TileEntityInduction.class;
            }
            case 14: {
                return TileEntityMatter.class;
            }
            case 15: {
                return TileEntityTerra.class;
            }
        }
        return null;
    }

    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (!IC2.platform.isRendering()) {
            return;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 1 && this.isActive((IBlockAccess)world, x, y, z)) {
            TileEntityBlock te = (TileEntityBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
            if (te == null) {
                return;
            }
            short facing = te.getFacing();
            float f = (float)x + 0.5f;
            float f1 = (float)y + 0.0f + random.nextFloat() * 6.0f / 16.0f;
            float f2 = (float)z + 0.5f;
            float f3 = 0.52f;
            float f4 = random.nextFloat() * 0.6f - 0.3f;
            switch (facing) {
                case 4: {
                    world.spawnParticle("smoke", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0, 0.0, 0.0);
                    world.spawnParticle("flame", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0, 0.0, 0.0);
                    break;
                }
                case 5: {
                    world.spawnParticle("smoke", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0, 0.0, 0.0);
                    world.spawnParticle("flame", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0, 0.0, 0.0);
                    break;
                }
                case 2: {
                    world.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0, 0.0, 0.0);
                    world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0, 0.0, 0.0);
                    break;
                }
                case 3: {
                    world.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0, 0.0, 0.0);
                    world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0, 0.0, 0.0);
                }
            }
        } else if (meta == 3 && this.isActive((IBlockAccess)world, x, y, z)) {
            float f = (float)x + 1.0f;
            float f1 = (float)y + 1.0f;
            float f2 = (float)z + 1.0f;
            for (int i = 0; i < 4; ++i) {
                float fmod = -0.2f - random.nextFloat() * 0.6f;
                float f1mod = -0.1f + random.nextFloat() * 0.2f;
                float f2mod = -0.2f - random.nextFloat() * 0.6f;
                world.spawnParticle("smoke", (double)(f + fmod), (double)(f1 + f1mod), (double)(f2 + f2mod), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() == 14 ? EnumRarity.rare : (stack.getItemDamage() == 15 || stack.getItemDamage() == 13 || stack.getItemDamage() == 12 ? EnumRarity.uncommon : EnumRarity.common);
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntityBlock te = (TileEntityBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return 0;
        }
        if (te instanceof TileEntityInduction) {
            TileEntityInduction tei = (TileEntityInduction)te;
            return (int)Math.floor((float)tei.heat / (float)TileEntityInduction.maxHeat * 15.0f);
        }
        if (te instanceof TileEntityMatter) {
            return (int)Math.floor(((TileEntityMatter)te).energy / 1000000.0 * 15.0);
        }
        if (te instanceof TileEntityElectrolyzer) {
            return (int)Math.floor((float)((TileEntityElectrolyzer)te).energy / 20000.0f * 15.0f);
        }
        if (te instanceof TileEntityStandardMachine) {
            TileEntityStandardMachine tem = (TileEntityStandardMachine)te;
            return (int)Math.floor(tem.getProgress() * 15.0f);
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getBlockMetadata(x, y, z) == 15) {
            if (player.isSneaking()) {
                return false;
            }
            if (world.isRemote) {
                return true;
            }
            TileEntityTerra te = (TileEntityTerra)world.getTileEntity(x, y, z);
            if (player.inventory.getCurrentItem() == null) {
                te.ejectBlueprint();
                return true;
            }
            if (player.inventory.getCurrentItem().getItem() instanceof ITerraformingBP) {
                te.insertBlueprint(player.inventory.getCurrentItem().copy());
                --player.inventory.getCurrentItem().stackSize;
                return true;
            }
            return false;
        }
        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }
}

