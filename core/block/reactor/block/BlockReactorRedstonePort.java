/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.reactor.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.reactor.tileentity.TileEntityReactorRedstonePort;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockReactorRedstonePort
extends BlockMultiID {
    public BlockReactorRedstonePort(InternalName internalName1) {
        super(internalName1, Material.iron, ItemBlockIC2.class);
        this.setHardness(40.0f);
        this.setResistance(90.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.reactorRedstonePort = new ItemStack((Block)this, 1, 0);
        GameRegistry.registerTileEntity(TileEntityReactorRedstonePort.class, (String)"Reactor Redstone Port");
    }

    @Override
    public String getTextureFolder(int id) {
        return "reactor";
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        try {
            return TileEntityReactorRedstonePort.class;
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

