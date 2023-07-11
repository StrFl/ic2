/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.heatgenerator.block;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.heatgenerator.tileentity.TileEntityElectricHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityRTHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntitySolidHeatGenerator;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemHeatGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockHeatGenerator
extends BlockMultiID {
    public BlockHeatGenerator(InternalName internalName1) {
        super(internalName1, Material.iron, ItemHeatGenerator.class);
        this.setHardness(3.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.SolidHeatGenerator = new ItemStack((Block)this, 1, 0);
        Ic2Items.FluidHeatGenerator = new ItemStack((Block)this, 1, 1);
        Ic2Items.RTHeatGenerator = new ItemStack((Block)this, 1, 2);
        Ic2Items.ElecHeatGenerator = new ItemStack((Block)this, 1, 3);
        GameRegistry.registerTileEntity(TileEntitySolidHeatGenerator.class, (String)"Solid Heat Generator");
        GameRegistry.registerTileEntity(TileEntityFluidHeatGenerator.class, (String)"Fluid Heat Generator");
        GameRegistry.registerTileEntity(TileEntityRTHeatGenerator.class, (String)"Radioisotope Thermo Heat Generator");
        GameRegistry.registerTileEntity(TileEntityElectricHeatGenerator.class, (String)"Electric Heat Generator");
    }

    @Override
    public String getTextureFolder(int id) {
        return "heatgenerator";
    }

    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        try {
            switch (meta) {
                case 0: {
                    return TileEntitySolidHeatGenerator.class;
                }
                case 1: {
                    return TileEntityFluidHeatGenerator.class;
                }
                case 2: {
                    return TileEntityRTHeatGenerator.class;
                }
                case 3: {
                    return TileEntityElectricHeatGenerator.class;
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}

