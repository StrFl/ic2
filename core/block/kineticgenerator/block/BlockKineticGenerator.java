/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.kineticgenerator.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.kineticgenerator.tileentity.TileEntityElectricKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityManualKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntitySteamKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityStirlingKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWindKineticGenerator;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemKineticGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockKineticGenerator
extends BlockMultiID {
    public BlockKineticGenerator(InternalName internalName1) {
        super(internalName1, Material.iron, ItemKineticGenerator.class);
        this.setHardness(3.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.WindKineticGenerator = new ItemStack((Block)this, 1, 0);
        Ic2Items.SteamKineticGenerator = new ItemStack((Block)this, 1, 1);
        Ic2Items.ElectricKineticGenerator = new ItemStack((Block)this, 1, 2);
        Ic2Items.ManualKineticGenerator = new ItemStack((Block)this, 1, 3);
        Ic2Items.WaterKineticGenerator = new ItemStack((Block)this, 1, 4);
        Ic2Items.StirlingKineticGenerator = new ItemStack((Block)this, 1, 5);
        GameRegistry.registerTileEntity(TileEntityWindKineticGenerator.class, (String)"Wind Kinetic Generator");
        GameRegistry.registerTileEntity(TileEntitySteamKineticGenerator.class, (String)"Steam Kinetic Generator");
        GameRegistry.registerTileEntity(TileEntityElectricKineticGenerator.class, (String)"Electric Kinetic Generator");
        GameRegistry.registerTileEntity(TileEntityManualKineticGenerator.class, (String)"Manual Kinetic Generator");
        GameRegistry.registerTileEntity(TileEntityWaterKineticGenerator.class, (String)"Water Kinetic Generator");
        GameRegistry.registerTileEntity(TileEntityStirlingKineticGenerator.class, (String)"Stirling Kinetic Generator");
    }

    @Override
    public String getTextureFolder(int id) {
        return "kineticgenerator";
    }

    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        try {
            switch (meta) {
                case 0: {
                    return TileEntityWindKineticGenerator.class;
                }
                case 1: {
                    return TileEntitySteamKineticGenerator.class;
                }
                case 2: {
                    return TileEntityElectricKineticGenerator.class;
                }
                case 3: {
                    return TileEntityManualKineticGenerator.class;
                }
                case 4: {
                    return TileEntityWaterKineticGenerator.class;
                }
                case 5: {
                    return TileEntityStirlingKineticGenerator.class;
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float a, float b, float c) {
        if (entityPlayer.isSneaking()) {
            return false;
        }
        TileEntity te = this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te != null && te instanceof TileEntityManualKineticGenerator) {
            return ((TileEntityManualKineticGenerator)te).playerKlicked(entityPlayer);
        }
        return super.onBlockActivated(world, x, y, z, entityPlayer, side, a, b, c);
    }
}

