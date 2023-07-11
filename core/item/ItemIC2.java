/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemIC2
extends Item {
    private int rarity = 0;
    protected IIcon[] textures;

    public ItemIC2(InternalName internalName) {
        this.setUnlocalizedName(internalName.name());
        this.setCreativeTab(IC2.tabIC2);
        GameRegistry.registerItem((Item)this, (String)internalName.name());
    }

    public String getTextureFolder() {
        return null;
    }

    public String getTextureName(int index) {
        if (!this.hasSubtypes && index > 0) {
            return null;
        }
        String name = this.getUnlocalizedName(new ItemStack((Item)this, 1, index));
        if (name != null && name.length() > 4) {
            return name.substring(4);
        }
        return name;
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        int indexCount = 0;
        while (this.getTextureName(indexCount) != null) {
            if (++indexCount <= Short.MAX_VALUE) continue;
            throw new RuntimeException("More Item Icons than actually possible @ " + this.getUnlocalizedName());
        }
        this.textures = new IIcon[indexCount];
        String textureFolder = this.getTextureFolder() == null ? "" : this.getTextureFolder() + "/";
        for (int index = 0; index < indexCount; ++index) {
            this.textures[index] = iconRegister.registerIcon(IC2.textureDomain + ":" + textureFolder + this.getTextureName(index));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (meta < this.textures.length) {
            return this.textures[meta];
        }
        return this.textures.length < 1 ? null : this.textures[0];
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(5);
    }

    public String getUnlocalizedName(ItemStack itemStack) {
        return this.getUnlocalizedName();
    }

    public String getItemStackDisplayName(ItemStack itemStack) {
        return StatCollector.translateToLocal((String)this.getUnlocalizedName(itemStack));
    }

    public ItemIC2 setRarity(int aRarity) {
        this.rarity = aRarity;
        return this;
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.values()[this.rarity];
    }
}

