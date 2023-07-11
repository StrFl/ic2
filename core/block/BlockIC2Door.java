/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.item.Item
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 */
package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockIC2Door
extends BlockDoor {
    private IIcon[] textures;
    private Item itemDropped;

    public BlockIC2Door(InternalName internalName) {
        super(Material.iron);
        this.setHardness(50.0f);
        this.setResistance(150.0f);
        this.setStepSound(Block.soundTypeMetal);
        this.disableStats();
        this.setBlockName(internalName.name());
        this.setCreativeTab(null);
        GameRegistry.registerBlock((Block)this, (String)internalName.name());
    }

    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.textures = new IIcon[2];
        this.textures[0] = iconRegister.registerIcon(IC2.textureDomain + ":" + this.getUnlocalizedName().substring(5) + "." + (Object)((Object)InternalName.top));
        this.textures[1] = iconRegister.registerIcon(IC2.textureDomain + ":" + this.getUnlocalizedName().substring(5) + "." + (Object)((Object)InternalName.bottom));
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        return this.getIcon(side, iBlockAccess.getBlockMetadata(x, y, z));
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if ((meta & 8) == 8) {
            return this.textures[0];
        }
        return this.textures[1];
    }

    public BlockIC2Door setItemDropped(Item item) {
        this.itemDropped = item;
        return this;
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        if ((meta & 8) == 8) {
            return null;
        }
        return this.itemDropped;
    }
}

