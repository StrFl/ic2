/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.util.Util;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemIC2Pickaxe
extends ItemPickaxe {
    private final Object repairMaterial;

    public ItemIC2Pickaxe(InternalName internalName, Item.ToolMaterial enumtoolmaterial, float efficiency, Object repairMaterial1) {
        super(enumtoolmaterial);
        this.efficiencyOnProperMaterial = efficiency;
        this.repairMaterial = repairMaterial1;
        this.setUnlocalizedName(internalName.name());
        this.setCreativeTab(IC2.tabIC2);
        GameRegistry.registerItem((Item)this, (String)internalName.name());
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(IC2.textureDomain + ":" + this.getUnlocalizedName().substring(4));
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

    public int getItemEnchantability() {
        return 13;
    }

    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && Util.matchesOD(stack2, this.repairMaterial);
    }
}

