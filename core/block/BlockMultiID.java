/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureType
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.common.util.ForgeDirection
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.tile.IWrenchable;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.BlockBase;
import ic2.core.block.BlockTextureStitched;
import ic2.core.block.TileEntityBlock;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.mutable.MutableObject;

public abstract class BlockMultiID
extends BlockBase {
    public int renderMask = 63;
    private static final Class<?>[] emptyClassArray = new Class[0];
    private static final Object[] emptyObjArray = new Object[0];
    private static final int tesBeforeBreakLimit = 8;
    private static ArrayDeque<TileEntity> tesBeforeBreak = new ArrayDeque(8);

    public BlockMultiID(InternalName internalName1, Material material) {
        super(internalName1, material);
    }

    public BlockMultiID(InternalName internalName1, Material material, Class<? extends ItemBlockIC2> itemClass) {
        super(internalName1, material, itemClass);
    }

    @Override
    public int getFacing(IBlockAccess iBlockAccess, int x, int y, int z) {
        TileEntity te = this.getOwnTe(iBlockAccess, x, y, z);
        if (te instanceof TileEntityBlock) {
            return ((TileEntityBlock)te).getFacing();
        }
        int meta = iBlockAccess.getBlockMetadata(x, y, z);
        return this.getFacing(meta);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        int metaCount = this.getMetaCount();
        this.textures = new IIcon[metaCount][12];
        for (int index = 0; index < metaCount; ++index) {
            String textureFolder = this.getTextureFolder(index);
            textureFolder = textureFolder == null ? "" : textureFolder + "/";
            String name = IC2.textureDomain + ":" + textureFolder + this.getTextureName(index);
            for (int active = 0; active < 2; ++active) {
                for (int side = 0; side < 6; ++side) {
                    int subIndex = active * 6 + side;
                    String subName = name + ":" + subIndex;
                    BlockTextureStitched texture = new BlockTextureStitched(subName, subIndex);
                    this.textures[index][subIndex] = texture;
                    ((TextureMap)iconRegister).setTextureEntry(subName, (TextureAtlasSprite)texture);
                }
            }
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        int facing = this.getFacing(iBlockAccess, x, y, z);
        boolean active = this.isActive(iBlockAccess, x, y, z);
        int meta = iBlockAccess.getBlockMetadata(x, y, z);
        int index = this.getTextureIndex(meta);
        if (index >= this.textures.length) {
            return null;
        }
        int subIndex = BlockMultiID.getTextureSubIndex(facing, side);
        if (active) {
            subIndex += 6;
        }
        try {
            return this.textures[index][subIndex];
        }
        catch (Exception e) {
            IC2.platform.displayError(e, "Coordinates: %d/%d/%d\nSide: %d\nBlock: %s\nMeta: %d\nFacing: %d\nActive: %s\nIndex: %d\nSubIndex: %d", new Object[]{x, y, z, side, this, meta, facing, active, index, subIndex});
            return null;
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        int facing = this.getFacing(meta);
        int index = this.getTextureIndex(meta);
        int subIndex = BlockMultiID.getTextureSubIndex(facing, side);
        if (index >= this.textures.length) {
            return null;
        }
        try {
            return this.textures[index][subIndex];
        }
        catch (Exception e) {
            IC2.platform.displayError(e, "Side: " + side + "\nBlock: " + (Object)((Object)this) + "\nMeta: " + meta + "\nFacing: " + facing + "\nIndex: " + index + "\nSubIndex: " + subIndex, new Object[0]);
            return null;
        }
    }

    public int getRenderType() {
        return IC2.platform.getRenderId("default");
    }

    @SideOnly(value=Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if ((this.renderMask & 1 << side) != 0) {
            return super.shouldSideBeRendered(blockAccess, x, y, z, side);
        }
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public void onRender(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity te = this.getOwnTe(blockAccess, x, y, z);
        if (te instanceof TileEntityBlock) {
            ((TileEntityBlock)te).onRender();
        }
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float a, float b, float c) {
        if (entityPlayer.isSneaking()) {
            return false;
        }
        TileEntity te = this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te instanceof IHasGui) {
            if (IC2.platform.isSimulating()) {
                return IC2.platform.launchGui(entityPlayer, (IHasGui)te);
            }
            return true;
        }
        return false;
    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList ret = super.getDrops(world, x, y, z, metadata, fortune);
        TileEntity te = this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            Iterator<TileEntity> it = tesBeforeBreak.descendingIterator();
            while (it.hasNext()) {
                TileEntity te2 = it.next();
                if (te2.getWorldObj() != world || te2.xCoord != x || te2.yCoord != y || te2.zCoord != z) continue;
                te = te2;
                it.remove();
                break;
            }
        }
        if (te instanceof IInventory) {
            IInventory inv = (IInventory)te;
            for (int i = 0; i < inv.getSizeInventory(); ++i) {
                ItemStack itemStack = inv.getStackInSlot(i);
                if (itemStack == null) continue;
                ret.add(itemStack);
            }
        }
        return ret;
    }

    public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
        TileEntity te = this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te instanceof TileEntityBlock) {
            TileEntityBlock teb = (TileEntityBlock)te;
            teb.onBlockBreak(this, meta);
            teb.onUnloaded();
        }
        if (te != null) {
            if (te instanceof IHasGui) {
                for (Object obj : world.playerEntities) {
                    if (!(obj instanceof EntityPlayerMP)) continue;
                    EntityPlayerMP player = (EntityPlayerMP)obj;
                    if (!(player.openContainer instanceof ContainerBase)) continue;
                    ContainerBase container = (ContainerBase)player.openContainer;
                    if (container.base != te) continue;
                    player.closeScreen();
                }
            }
            if (tesBeforeBreak.size() >= 8) {
                tesBeforeBreak.pop();
            }
            tesBeforeBreak.push(te);
        }
        if (Ic2Items.copperOre != null && this.getUnlocalizedName().equals(Ic2Items.copperOre.getUnlocalizedName())) {
            this.dropXpOnBlockBreak(world, x, y, z, 1);
        }
        if (Ic2Items.tinOre != null && this.getUnlocalizedName().equals(Ic2Items.tinOre.getUnlocalizedName())) {
            this.dropXpOnBlockBreak(world, x, y, z, 1);
        }
        if (Ic2Items.uraniumOre != null && this.getUnlocalizedName().equals(Ic2Items.uraniumOre.getUnlocalizedName())) {
            this.dropXpOnBlockBreak(world, x, y, z, 2);
        }
        if (Ic2Items.leadOre != null && this.getUnlocalizedName().equals(Ic2Items.leadOre.getUnlocalizedName())) {
            this.dropXpOnBlockBreak(world, x, y, z, 1);
        }
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        Iterator<TileEntity> it = tesBeforeBreak.descendingIterator();
        while (it.hasNext()) {
            TileEntity te = it.next();
            if (te.getWorldObj() != world || te.xCoord != x || te.yCoord != y || te.zCoord != z) continue;
            it.remove();
            break;
        }
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        TileEntity tileEntity = this.getOwnTe((IBlockAccess)world, x, y, z);
        if (tileEntity instanceof IWrenchable) {
            IWrenchable te = (IWrenchable)tileEntity;
            if (entityliving == null) {
                te.setFacing((short)2);
            } else {
                int l = MathHelper.floor_double((double)((double)(entityliving.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
                switch (l) {
                    case 0: {
                        te.setFacing((short)2);
                        break;
                    }
                    case 1: {
                        te.setFacing((short)5);
                        break;
                    }
                    case 2: {
                        te.setFacing((short)3);
                        break;
                    }
                    case 3: {
                        te.setFacing((short)4);
                    }
                }
            }
        }
    }

    public final boolean hasTileEntity(int metadata) {
        return true;
    }

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    public final TileEntity createTileEntity(World world, int metadata) {
        MutableObject ctorArgTypes = new MutableObject(emptyClassArray);
        MutableObject ctorArgs = new MutableObject((Object)emptyObjArray);
        Class<? extends TileEntity> teClass = this.getTeClass(metadata, (MutableObject<Class<?>[]>)ctorArgTypes, (MutableObject<Object[]>)ctorArgs);
        if (teClass == null) {
            return null;
        }
        try {
            Constructor<? extends TileEntity> ctor = teClass.getConstructor((Class[])ctorArgTypes.getValue());
            return ctor.newInstance((Object[])ctorArgs.getValue());
        }
        catch (Throwable t) {
            throw new RuntimeException("Error constructing " + teClass + " with " + Arrays.asList((Object[])ctorArgTypes.getValue()) + ", " + Arrays.asList((Object[])ctorArgs.getValue()) + ".", t);
        }
    }

    public abstract Class<? extends TileEntity> getTeClass(int var1, MutableObject<Class<?>[]> var2, MutableObject<Object[]> var3);

    public TileEntity getOwnTe(IBlockAccess blockAccess, int x, int y, int z) {
        Class<?> actualClass;
        TileEntity te;
        int meta;
        Block block;
        if (blockAccess instanceof World) {
            Chunk chunk = Util.getLoadedChunk((World)blockAccess, x >> 4, z >> 4);
            if (chunk == null) {
                return null;
            }
            block = chunk.getBlock(x & 0xF, y, z & 0xF);
            meta = chunk.getBlockMetadata(x & 0xF, y, z & 0xF);
            te = blockAccess.getTileEntity(x, y, z);
        } else {
            block = blockAccess.getBlock(x, y, z);
            meta = blockAccess.getBlockMetadata(x, y, z);
            te = blockAccess.getTileEntity(x, y, z);
        }
        Class<? extends TileEntity> expectedClass = this.getTeClass(meta, null, null);
        Class<?> clazz = actualClass = te != null ? te.getClass() : null;
        if (actualClass != expectedClass) {
            if (block != this) {
                if (Util.inDev()) {
                    StackTraceElement[] st = new Throwable().getStackTrace();
                    IC2.log.warn(LogCategory.Block, "Own tile entity query from %s to foreign block %s instead of %s at %s.", st.length > 1 ? st[1] : "?", block != null ? block.getClass() : null, ((Object)((Object)this)).getClass(), Util.formatPosition(blockAccess, x, y, z));
                }
                return null;
            }
            IC2.log.warn(LogCategory.Block, "Mismatched tile entity at %s, got %s, expected %s.", Util.formatPosition(blockAccess, x, y, z), actualClass, expectedClass);
            if (blockAccess instanceof World) {
                World world = (World)blockAccess;
                te = this.createTileEntity(world, meta);
                world.setTileEntity(x, y, z, te);
            } else {
                return null;
            }
        }
        return te;
    }

    public final boolean isActive(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity te = this.getOwnTe(blockAccess, x, y, z);
        if (te instanceof TileEntityBlock) {
            return ((TileEntityBlock)te).getActive();
        }
        return false;
    }

    public void getSubBlocks(Item j, CreativeTabs tabs, List itemList) {
        Item item = Item.getItemFromBlock((Block)this);
        if (!item.getHasSubtypes()) {
            itemList.add(new ItemStack((Block)this));
        } else {
            ItemStack is;
            for (int i = 0; i < 16 && (is = new ItemStack((Block)this, 1, i)).getItem().getUnlocalizedName(is) != null; ++i) {
                itemList.add(is);
            }
        }
    }

    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack((Block)this, 1, world.getBlockMetadata(x, y, z));
    }

    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
        int newFacing;
        IWrenchable te;
        if (axis == ForgeDirection.UNKNOWN) {
            return false;
        }
        TileEntity tileEntity = this.getOwnTe((IBlockAccess)worldObj, x, y, z);
        if (tileEntity instanceof IWrenchable && (te = (IWrenchable)tileEntity).wrenchCanSetFacing(null, newFacing = ForgeDirection.getOrientation((int)te.getFacing()).getRotation(axis).ordinal())) {
            te.setFacing((short)newFacing);
        }
        return false;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block srcBlock) {
        TileEntity te = this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te instanceof TileEntityBlock) {
            ((TileEntityBlock)te).onNeighborUpdate(srcBlock);
        }
    }
}

