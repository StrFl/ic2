/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.armor;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IMetalArmor;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.util.Util;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemArmorIC2
extends ItemArmor
implements IMetalArmor {
    private final String armorName;
    private final Object repairMaterial;

    public ItemArmorIC2(InternalName internalName, ItemArmor.ArmorMaterial armorMaterial, InternalName armorName1, int armorType1, Object repairMaterial1) {
        super(armorMaterial, IC2.platform.addArmor(armorName1.name()), armorType1);
        this.repairMaterial = repairMaterial1;
        this.armorName = armorName1.name();
        this.setMaxDamage(armorMaterial.getDurability(armorType1));
        this.setUnlocalizedName(internalName.name());
        this.setCreativeTab(IC2.tabIC2);
        GameRegistry.registerItem((Item)this, (String)internalName.name());
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        String name = this.getUnlocalizedName();
        if (name != null && name.length() > 4) {
            name = name.substring(4);
        }
        this.itemIcon = iconRegister.registerIcon(IC2.textureDomain + ":" + name);
    }

    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        int suffix = this.armorType == 2 ? 2 : 1;
        return IC2.textureDomain + ":textures/armor/" + this.armorName + "_" + suffix + ".png";
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

    @Override
    public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
        return true;
    }

    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && Util.matchesOD(stack2, this.repairMaterial);
    }
}

