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
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 */
package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public abstract class BlockBase
extends Block {
    protected final InternalName internalName;
    private static final int[][] facingAndSideToSpriteOffset = new int[][]{{3, 5, 1, 0, 4, 2}, {5, 3, 1, 0, 2, 4}, {0, 1, 3, 5, 4, 2}, {0, 1, 5, 3, 2, 4}, {0, 1, 2, 4, 3, 5}, {0, 1, 4, 2, 5, 3}};
    @SideOnly(value=Side.CLIENT)
    protected IIcon[][] textures;

    public BlockBase(InternalName internalName1, Material material) {
        this(internalName1, material, ItemBlockIC2.class);
    }

    public BlockBase(InternalName internalName1, Material material, Class<? extends ItemBlockIC2> itemClass) {
        super(material);
        this.setBlockName(internalName1.name());
        this.setCreativeTab(IC2.tabIC2);
        this.internalName = internalName1;
        GameRegistry.registerBlock((Block)this, itemClass, (String)internalName1.name());
    }

    @SideOnly(value=Side.CLIENT)
    public abstract void registerBlockIcons(IIconRegister var1);

    @SideOnly(value=Side.CLIENT)
    public abstract IIcon getIcon(IBlockAccess var1, int var2, int var3, int var4, int var5);

    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        int facing = this.getFacing(meta);
        int index = this.getTextureIndex(meta);
        int subIndex = BlockBase.getTextureSubIndex(facing, side);
        if (index >= this.textures.length) {
            return null;
        }
        try {
            return this.textures[index][subIndex];
        }
        catch (Exception e) {
            IC2.platform.displayError(e, "Side: %d\nBlock: %s\nMeta: %d\nFacing: %d\nIndex: %d\nSubIndex: %d", new Object[]{side, this, meta, facing, index, subIndex});
            return null;
        }
    }

    public String getUnlocalizedName() {
        return super.getUnlocalizedName().substring(5);
    }

    protected int getFacing(int meta) {
        return 3;
    }

    public int getFacing(IBlockAccess iBlockAccess, int x, int y, int z) {
        int meta = iBlockAccess.getBlockMetadata(x, y, z);
        return this.getFacing(meta);
    }

    protected String getTextureFolder(int index) {
        return null;
    }

    protected String getTextureName(int index) {
        Item item = Item.getItemFromBlock((Block)this);
        if (!item.getHasSubtypes()) {
            if (index == 0) {
                return this.getUnlocalizedName();
            }
            return null;
        }
        ItemStack itemStack = new ItemStack((Block)this, 1, index);
        String ret = item.getUnlocalizedName(itemStack);
        if (ret == null) {
            return null;
        }
        return ret.substring(4).replace("item", "block");
    }

    public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    protected int getTextureIndex(int meta) {
        return meta;
    }

    public static final int getTextureSubIndex(int facing, int side) {
        return facingAndSideToSpriteOffset[facing][side];
    }

    protected int getMetaCount() {
        int metaCount = 0;
        while (this.getTextureName(metaCount) != null) {
            ++metaCount;
        }
        return metaCount;
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.common;
    }
}

