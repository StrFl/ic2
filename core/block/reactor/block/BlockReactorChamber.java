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
import ic2.api.Direction;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockReactorChamber
extends BlockMultiID {
    TileEntityNuclearReactorElectric reactor;

    public BlockReactorChamber(InternalName internalName1) {
        super(internalName1, Material.iron, ItemBlockIC2.class);
        this.setHardness(2.0f);
        this.setStepSound(soundTypeMetal);
        Ic2Items.reactorChamber = new ItemStack((Block)this, 1, 0);
        GameRegistry.registerTileEntity(TileEntityReactorChamberElectric.class, (String)"Reactor Chamber");
    }

    @Override
    public String getTextureFolder(int id) {
        return "reactor";
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (world.checkChunksExist(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1) && !this.canPlaceBlockAt(world, x, y, z)) {
            world.setBlockToAir(x, y, z);
            this.dropBlockAsItem(world, x, y, z, Ic2Items.reactorChamber.copy());
        }
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int count = 0;
        for (Direction dir : Direction.directions) {
            if (!(dir.applyTo(world, x, y, z) instanceof TileEntityNuclearReactorElectric)) continue;
            ++count;
        }
        return count == 1;
    }

    public void randomDisplayTick(World world, int i, int j, int k, Random random) {
        int n;
        this.reactor = this.getReactorEntity(world, i, j, k);
        if (this.reactor == null) {
            this.onNeighborBlockChange(world, i, j, k, this);
            return;
        }
        int puffs = this.reactor.heat / 1000;
        if (puffs <= 0) {
            return;
        }
        puffs = world.rand.nextInt(puffs);
        for (n = 0; n < puffs; ++n) {
            world.spawnParticle("smoke", (double)((float)i + random.nextFloat()), (double)((float)j + 0.95f), (double)((float)k + random.nextFloat()), 0.0, 0.0, 0.0);
        }
        puffs -= world.rand.nextInt(4) + 3;
        for (n = 0; n < puffs; ++n) {
            world.spawnParticle("flame", (double)((float)i + random.nextFloat()), (double)((float)j + 1.0f), (double)((float)k + random.nextFloat()), 0.0, 0.0, 0.0);
        }
    }

    public TileEntityNuclearReactorElectric getReactorEntity(World world, int x, int y, int z) {
        for (Direction dir : Direction.directions) {
            TileEntity te = dir.applyTo(world, x, y, z);
            if (!(te instanceof TileEntityNuclearReactorElectric)) continue;
            return (TileEntityNuclearReactorElectric)te;
        }
        this.onNeighborBlockChange(world, x, y, z, world.getBlock(x, y, z));
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
        if (entityplayer.isSneaking()) {
            return false;
        }
        TileEntityNuclearReactorElectric reactor = this.getReactorEntity(world, i, j, k);
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
            return TileEntityReactorChamberElectric.class;
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(Ic2Items.machine.copy());
        return ret;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }
}

