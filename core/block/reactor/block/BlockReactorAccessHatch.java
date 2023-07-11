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
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.reactor.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.reactor.tileentity.TileEntityReactorAccessHatch;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockReactorAccessHatch
extends BlockMultiID {
    public BlockReactorAccessHatch(InternalName internalName1) {
        super(internalName1, Material.iron, ItemBlockIC2.class);
        this.setHardness(40.0f);
        this.setResistance(90.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.reactorAccessHatch = new ItemStack((Block)this, 1, 0);
        GameRegistry.registerTileEntity(TileEntityReactorAccessHatch.class, (String)"Reactor Access Hatch");
    }

    @Override
    public String getTextureFolder(int id) {
        return "reactor";
    }

    public IHasGui getReactorGui(World world, int x, int y, int z) {
        for (int xoffset = -1; xoffset < 2; ++xoffset) {
            for (int yoffset = -1; yoffset < 2; ++yoffset) {
                for (int zoffset = -1; zoffset < 2; ++zoffset) {
                    TileEntity te = world.getTileEntity(x + xoffset, y + yoffset, z + zoffset);
                    if (!(te instanceof IReactorChamber) && !(te instanceof IReactor)) continue;
                    return (IHasGui)te;
                }
            }
        }
        this.onNeighborBlockChange(world, x, y, z, world.getBlock(x, y, z));
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
        if (entityplayer.isSneaking()) {
            return false;
        }
        IHasGui reactor = this.getReactorGui(world, i, j, k);
        if (reactor == null) {
            this.onNeighborBlockChange(world, i, j, k, this);
            return false;
        }
        if (!IC2.platform.isSimulating()) {
            return true;
        }
        return IC2.platform.launchGui(entityplayer, reactor);
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        try {
            return TileEntityReactorAccessHatch.class;
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(Ic2Items.reactorAccessHatch.copy());
        return ret;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }
}

