/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.tool;

import ic2.api.item.IBoxable;
import ic2.api.item.ILatheItem;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class ItemLathingTool
extends ItemIC2
implements ILatheItem.ILatheTool,
IBoxable {
    public ItemLathingTool(InternalName internalName) {
        super(internalName);
        this.setMaxDamage(50);
        this.setMaxStackSize(1);
    }

    @Override
    public int getCustomDamage(ItemStack stack) {
        return this.getDamage(stack);
    }

    @Override
    public int getMaxCustomDamage(ItemStack stack) {
        return this.getMaxDamage(stack);
    }

    @Override
    public void setCustomDamage(ItemStack stack, int damage) {
        this.setDamage(stack, damage);
    }

    @Override
    public boolean applyCustomDamage(ItemStack stack, int damage, EntityLivingBase src) {
        stack.damageItem(damage, src);
        return damage > 0;
    }

    @Override
    public int getHardness(ItemStack stack) {
        return 3;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

