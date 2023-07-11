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
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.BlockBase;
import ic2.core.block.BlockTextureStitched;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMetaData
extends BlockBase {
    public BlockMetaData(InternalName internalName1, Material mat) {
        super(internalName1, mat);
    }

    public BlockMetaData(InternalName internalName1, Material material, Class<? extends ItemBlockIC2> itemClass) {
        super(internalName1, material, itemClass);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        int metaCount = this.getMetaCount();
        this.textures = new IIcon[metaCount][6];
        for (int index = 0; index < metaCount; ++index) {
            String textureFolder = this.getTextureFolder(index);
            textureFolder = textureFolder == null ? "" : textureFolder + "/";
            String name = IC2.textureDomain + ":" + textureFolder + this.getTextureName(index);
            int side = 0;
            while (side < 6) {
                int subIndex = side++;
                String subName = name + ":" + subIndex;
                BlockTextureStitched texture = new BlockTextureStitched(subName, subIndex);
                this.textures[index][subIndex] = texture;
                ((TextureMap)iconRegister).setTextureEntry(subName, (TextureAtlasSprite)texture);
            }
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        int meta = iBlockAccess.getBlockMetadata(x, y, z);
        return this.getIcon(side, meta);
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float a, float b, float c) {
        if (entityPlayer.isSneaking()) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IHasGui) {
            if (IC2.platform.isSimulating()) {
                return IC2.platform.launchGui(entityPlayer, (IHasGui)te);
            }
            return true;
        }
        return false;
    }

    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack((Block)this, 1, world.getBlockMetadata(x, y, z));
    }
}

