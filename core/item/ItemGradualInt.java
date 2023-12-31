/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.item;

import ic2.api.item.ICustomDamageItem;
import ic2.core.init.InternalName;
import ic2.core.item.ItemGradual;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemGradualInt
extends ItemGradual
implements ICustomDamageItem {
    private final int maxDmg;

    public ItemGradualInt(InternalName internalName, int maxdmg) {
        super(internalName);
        this.maxDmg = maxdmg;
    }

    @Override
    public int getCustomDamage(ItemStack stack) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        return nbt.getInteger("advDmg");
    }

    @Override
    public int getMaxCustomDamage(ItemStack stack) {
        return this.maxDmg;
    }

    @Override
    public void setCustomDamage(ItemStack stack, int damage) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        nbt.setInteger("advDmg", damage);
        int maxStackDamage = stack.getMaxDamage();
        if (maxStackDamage > 2) {
            stack.setItemDamage(1 + (int)Util.map(damage, this.maxDmg, maxStackDamage - 2));
        }
    }

    @Override
    public boolean applyCustomDamage(ItemStack stack, int damage, EntityLivingBase src) {
        this.setCustomDamage(stack, this.getCustomDamage(stack) + damage);
        return true;
    }
}

