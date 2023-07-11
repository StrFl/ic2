/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package ic2.core.migration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemMigrate
extends ItemIC2 {
    private final ItemStack target;
    private final boolean keepMeta;

    public ItemMigrate(InternalName oldName, ItemStack target1, boolean keepMeta1) {
        super(oldName);
        this.target = target1;
        this.keepMeta = keepMeta1;
        this.setCreativeTab(null);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (this.target != null) {
            return this.target.getItem().getIconFromDamage(this.keepMeta ? meta : this.target.getItemDamage());
        }
        return this.itemIcon;
    }

    public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean isHeld) {
        if (this.target != null) {
            itemStack.func_150996_a(this.target.getItem());
            if (!this.keepMeta) {
                itemStack.setItemDamage(this.target.getItemDamage());
            }
        }
    }
}

