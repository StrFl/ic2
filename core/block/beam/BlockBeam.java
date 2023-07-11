/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.material.Material
 *  net.minecraft.tileentity.TileEntity
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.beam;

import cpw.mods.fml.common.registry.GameRegistry;
import ic2.core.block.BlockMultiID;
import ic2.core.block.beam.TileAccelerator;
import ic2.core.block.beam.TileEmitter;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBeam;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockBeam
extends BlockMultiID {
    public BlockBeam(InternalName internalName1) {
        super(internalName1, Material.iron, ItemBeam.class);
        GameRegistry.registerTileEntity(TileEmitter.class, (String)"Particle emitter");
        GameRegistry.registerTileEntity(TileAccelerator.class, (String)"Particle accelerator");
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        switch (meta) {
            case 0: {
                return TileEmitter.class;
            }
            case 1: {
                return TileAccelerator.class;
            }
        }
        return null;
    }
}

