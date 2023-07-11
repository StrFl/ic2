/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockColored
 *  net.minecraft.block.material.Material
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.event.PaintEvent;
import ic2.api.event.RetextureEvent;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityWall;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.Util;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockWall
extends BlockMultiID {
    protected int colorMultiplier = -1;

    public BlockWall(InternalName internalName1) {
        super(internalName1, Material.rock, ItemBlockIC2.class);
        this.setHardness(3.0f);
        this.setResistance(30.0f);
        this.setStepSound(soundTypeStone);
        this.setCreativeTab(null);
        Ic2Items.constructionFoamWall = new ItemStack((Block)this);
        GameRegistry.registerTileEntity(TileEntityWall.class, (String)"CF-Wall");
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @Override
    public String getTextureFolder(int id) {
        return "cf";
    }

    @Override
    public String getTextureName(int index) {
        if (index >= 0 && index <= 15) {
            return InternalName.blockWall.name() + "." + Util.getColorName(index).name();
        }
        return null;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityWall te = (TileEntityWall)this.getOwnTe(blockAccess, x, y, z);
        if (te == null) {
            return null;
        }
        Block referencedBlock = te.getReferencedBlock(side);
        if (referencedBlock != null) {
            try {
                return referencedBlock.getIcon(te.retextureRefSide[side], te.retextureRefMeta[side]);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return super.getIcon(blockAccess, x, y, z, side);
    }

    @Override
    public int getRenderType() {
        return IC2.platform.getRenderId("wall");
    }

    public int quantityDropped(Random r) {
        return 0;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        return TileEntityWall.class;
    }

    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int color) {
        if ((color = BlockColored.func_150031_c((int)color)) != world.getBlockMetadata(x, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, color, 3);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onPaint(PaintEvent event) {
        if (event.world.getBlock(event.x, event.y, event.z) != this) {
            return;
        }
        if (event.color != event.world.getBlockMetadata(event.x, event.y, event.z)) {
            event.world.setBlockMetadataWithNotify(event.x, event.y, event.z, event.color, 3);
            event.painted = true;
        }
    }

    public ItemStack createStackedBlock(int i) {
        return null;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return Ic2Items.constructionFoam.copy();
    }

    @SubscribeEvent
    public void onRetexture(RetextureEvent event) {
        TileEntityWall te = (TileEntityWall)this.getOwnTe((IBlockAccess)event.world, event.x, event.y, event.z);
        if (te == null) {
            return;
        }
        if (te.retexture(event.side, event.referencedBlock, event.referencedMeta, event.referencedSide)) {
            event.applied = true;
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List par3List) {
        for (int j = 0; j < 16; ++j) {
            par3List.add(new ItemStack(item, 1, j));
        }
    }

    public int colorMultiplier(IBlockAccess par1iBlockAccess, int x, int y, int z) {
        if (this.colorMultiplier != -1) {
            return this.colorMultiplier;
        }
        return super.colorMultiplier(par1iBlockAccess, x, y, z);
    }
}

