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
package ic2.core.block.generator.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.generator.tileentity.TileEntityGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntityKineticGenerator;
import ic2.core.block.generator.tileentity.TileEntityRTGenerator;
import ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.block.generator.tileentity.TileEntityStirlingGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityWindGenerator;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.block.ItemGenerator;
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

public class BlockGenerator
extends BlockMultiID {
    public BlockGenerator(InternalName internalName1) {
        super(internalName1, Material.iron, ItemGenerator.class);
        this.setHardness(3.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.generator = new ItemStack((Block)this, 1, 0);
        Ic2Items.geothermalGenerator = new ItemStack((Block)this, 1, 1);
        Ic2Items.waterMill = new ItemStack((Block)this, 1, 2);
        Ic2Items.solarPanel = new ItemStack((Block)this, 1, 3);
        Ic2Items.windMill = new ItemStack((Block)this, 1, 4);
        Ic2Items.nuclearReactor = new ItemStack((Block)this, 1, 5);
        Ic2Items.RTGenerator = new ItemStack((Block)this, 1, 6);
        Ic2Items.semifluidGenerator = new ItemStack((Block)this, 1, 7);
        Ic2Items.stirlingGenerator = new ItemStack((Block)this, 1, 8);
        Ic2Items.kineticGenerator = new ItemStack((Block)this, 1, 9);
        GameRegistry.registerTileEntity(TileEntityGenerator.class, (String)"Generator");
        GameRegistry.registerTileEntity(TileEntityGeoGenerator.class, (String)"Geothermal Generator");
        GameRegistry.registerTileEntity(TileEntityWaterGenerator.class, (String)"Water Mill");
        GameRegistry.registerTileEntity(TileEntitySolarGenerator.class, (String)"Solar Panel");
        GameRegistry.registerTileEntity(TileEntityWindGenerator.class, (String)"Wind Mill");
        GameRegistry.registerTileEntity(TileEntityNuclearReactorElectric.class, (String)"Nuclear Reactor");
        GameRegistry.registerTileEntity(TileEntityRTGenerator.class, (String)"Radioisotope Thermoelectric Generator");
        GameRegistry.registerTileEntity(TileEntitySemifluidGenerator.class, (String)"Semifluid Generator");
        GameRegistry.registerTileEntity(TileEntityStirlingGenerator.class, (String)"Stirling Generator");
        GameRegistry.registerTileEntity(TileEntityKineticGenerator.class, (String)"Kinetic Generator");
    }

    @Override
    public String getTextureFolder(int id) {
        return "generator";
    }

    public int damageDropped(int meta) {
        if (ConfigUtil.getBool(MainConfig.get(), "balance/ignoreWrenchRequirement")) {
            return meta;
        }
        switch (meta) {
            case 2: {
                return 2;
            }
        }
        return 0;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        try {
            switch (meta) {
                case 0: {
                    return TileEntityGenerator.class;
                }
                case 1: {
                    return TileEntityGeoGenerator.class;
                }
                case 2: {
                    return TileEntityWaterGenerator.class;
                }
                case 3: {
                    return TileEntitySolarGenerator.class;
                }
                case 4: {
                    return TileEntityWindGenerator.class;
                }
                case 5: {
                    return TileEntityNuclearReactorElectric.class;
                }
                case 6: {
                    return TileEntityRTGenerator.class;
                }
                case 7: {
                    return TileEntitySemifluidGenerator.class;
                }
                case 8: {
                    return TileEntityStirlingGenerator.class;
                }
                case 9: {
                    return TileEntityKineticGenerator.class;
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (!IC2.platform.isRendering()) {
            return;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 && this.isActive((IBlockAccess)world, x, y, z)) {
            TileEntityBlock te = (TileEntityBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
            if (te == null) {
                return;
            }
            short l = te.getFacing();
            float f = (float)x + 0.5f;
            float f1 = (float)y + 0.0f + random.nextFloat() * 6.0f / 16.0f;
            float f2 = (float)z + 0.5f;
            float f3 = 0.52f;
            float f4 = random.nextFloat() * 0.6f - 0.3f;
            switch (l) {
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
        } else if (meta == 5) {
            int n;
            TileEntityNuclearReactorElectric te = (TileEntityNuclearReactorElectric)this.getOwnTe((IBlockAccess)world, x, y, z);
            if (te == null) {
                return;
            }
            int puffs = te.heat / 1000;
            if (puffs <= 0) {
                return;
            }
            puffs = world.rand.nextInt(puffs);
            for (n = 0; n < puffs; ++n) {
                world.spawnParticle("smoke", (double)((float)x + random.nextFloat()), (double)((float)y + 0.95f), (double)((float)z + random.nextFloat()), 0.0, 0.0, 0.0);
            }
            puffs -= world.rand.nextInt(4) + 3;
            for (n = 0; n < puffs; ++n) {
                world.spawnParticle("flame", (double)((float)x + random.nextFloat()), (double)((float)y + 1.0f), (double)((float)z + random.nextFloat()), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
        if (entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().isItemEqual(Ic2Items.reactorChamber)) {
            return false;
        }
        return super.onBlockActivated(world, i, j, k, entityplayer, side, a, b, c);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() == 5 ? EnumRarity.uncommon : EnumRarity.common;
    }
}

