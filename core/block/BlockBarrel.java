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
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBarrel;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockBarrel
extends BlockMultiID {
    private static final int textureIndexNormal = 0;
    private static final int textureIndexTap = 1;

    public BlockBarrel(InternalName internalName1) {
        super(internalName1, Material.wood, ItemBlockIC2.class);
        this.setHardness(1.0f);
        this.setStepSound(soundTypeWood);
        this.setCreativeTab(null);
        Ic2Items.blockBarrel = new ItemStack((Block)this);
        GameRegistry.registerTileEntity(TileEntityBarrel.class, (String)"TEBarrel");
    }

    @Override
    public String getTextureName(int index) {
        if (index == 0) {
            return this.getUnlocalizedName();
        }
        if (index == 1) {
            return this.getUnlocalizedName() + "." + InternalName.tap.name();
        }
        return null;
    }

    @Override
    public int getTextureIndex(int meta) {
        return 0;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        TileEntityBarrel te = (TileEntityBarrel)this.getOwnTe(iBlockAccess, x, y, z);
        if (te == null) {
            return null;
        }
        int subIndex = BlockBarrel.getTextureSubIndex(3, side);
        if (side > 1 && te.treetapSide == side) {
            return this.textures[1][subIndex];
        }
        return this.textures[0][subIndex];
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float a, float b, float c) {
        TileEntityBarrel te = (TileEntityBarrel)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return false;
        }
        return ((TileEntityBarrel)this.getOwnTe((IBlockAccess)world, x, y, z)).rightclick(entityPlayer);
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        TileEntityBarrel te = (TileEntityBarrel)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return;
        }
        if (te.treetapSide > 1) {
            if (IC2.platform.isSimulating()) {
                StackUtil.dropAsEntity(world, x, y, z, new ItemStack(Ic2Items.treetap.getItem()));
            }
            te.treetapSide = 0;
            te.update();
            te.drainLiquid(1);
            return;
        }
        if (IC2.platform.isSimulating()) {
            StackUtil.dropAsEntity(world, x, y, z, new ItemStack(Ic2Items.barrel.getItem(), 1, te.calculateMetaValue()));
        }
        world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.scaffold), 0, 3);
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        return TileEntityBarrel.class;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> re = new ArrayList<ItemStack>();
        re.add(new ItemStack(Ic2Items.scaffold.getItem()));
        re.add(new ItemStack(Ic2Items.barrel.getItem(), 1, 0));
        return re;
    }
}

