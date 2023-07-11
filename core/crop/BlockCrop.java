/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.crop;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.crop.TileEntityCrop;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockCrop
extends BlockMultiID {
    public static TileEntityCrop tempStore;
    private static final int textureIndexStick = 0;
    private static final int textureIndexStickUpgraded = 1;

    public BlockCrop(InternalName internalName1) {
        super(internalName1, Material.plants, ItemBlockIC2.class);
        this.setHardness(0.8f);
        this.setResistance(0.2f);
        this.setStepSound(soundTypeGrass);
        Ic2Items.crop = new ItemStack((Block)this, 1, 0);
        GameRegistry.registerTileEntity(TileEntityCrop.class, (String)"TECrop");
    }

    @Override
    public String getTextureFolder(int id) {
        return "crop";
    }

    @Override
    public int getTextureIndex(int meta) {
        if (meta == 0 || meta == 1) {
            return meta;
        }
        if (meta > 1 && meta <= 11) {
            return meta;
        }
        return 0;
    }

    @Override
    public String getTextureName(int index) {
        switch (index) {
            case 0: {
                return InternalName.stick.name();
            }
            case 1: {
                return InternalName.stick.name() + "." + InternalName.upgraded.name();
            }
        }
        if (index > 1 && index <= 6) {
            return "weed." + (index - 1);
        }
        if (index > 6 && index <= 11) {
            return "infested." + (index - 6);
        }
        return null;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        Crops.instance.startSpriteRegistration(iconRegister);
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        return TileEntityCrop.class;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        TileEntityCrop te = (TileEntityCrop)this.getOwnTe(iBlockAccess, x, y, z);
        if (te == null) {
            return null;
        }
        CropCard crop = te.getCrop();
        if (crop == null) {
            if (!te.upgraded) {
                return this.getIcon(side, 0);
            }
            return this.getIcon(side, 1);
        }
        return crop.getSprite(te);
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getOverlayIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side, int layer) {
        TileEntityCrop te = (TileEntityCrop)this.getOwnTe(iBlockAccess, x, y, z);
        if (te == null) {
            return null;
        }
        switch (layer) {
            case 0: {
                if (te.getvisualweedlevel() <= 0) break;
                return this.getIcon(side, te.getvisualweedlevel() + 1);
            }
            case 1: {
                if (te.getvisualInfestedlevel() <= 0) break;
                return this.getIcon(side, te.getvisualInfestedlevel() + 6);
            }
        }
        return null;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y - 1, z) == Blocks.farmland && super.canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);
        if (world.getBlock(x, y - 1, z) != Blocks.farmland) {
            world.setBlockToAir(x, y, z);
            this.dropBlockAsItem(world, x, y, z, 0, 0);
        } else {
            TileEntityCrop te = (TileEntityCrop)this.getOwnTe((IBlockAccess)world, x, y, z);
            if (te == null) {
                return;
            }
            te.onNeighbourChange();
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        double d = 0.2;
        return AxisAlignedBB.getBoundingBox((double)d, (double)0.0, (double)d, (double)(1.0 - d), (double)0.7, (double)(1.0 - d));
    }

    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        TileEntityCrop te = (TileEntityCrop)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return;
        }
        te.onEntityCollision(entity);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return IC2.platform.getRenderId("crop");
    }

    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityCrop te = (TileEntityCrop)this.getOwnTe(blockAccess, x, y, z);
        if (te == null) {
            return 0;
        }
        return te.emitRedstone();
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        tempStore = (TileEntityCrop)this.getOwnTe((IBlockAccess)world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        if (tempStore != null) {
            tempStore.onBlockDestroyed();
        }
    }

    public int getLightValue(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntityCrop te = (TileEntityCrop)this.getOwnTe(blockAccess, x, y, z);
        if (te == null) {
            return 0;
        }
        return te.getEmittedLight();
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if (world.isRemote) {
            return;
        }
        TileEntityCrop te = (TileEntityCrop)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return;
        }
        te.leftClick(player);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset) {
        if (world.isRemote) {
            return true;
        }
        TileEntityCrop te = (TileEntityCrop)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return false;
        }
        return te.rightClick(player);
    }
}

