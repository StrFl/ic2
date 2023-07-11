/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.common.ISpecialArmor
 *  net.minecraftforge.common.ISpecialArmor$ArmorProperties
 */
package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.ICustomDamageItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorIC2;
import ic2.core.util.LogCategory;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ISpecialArmor;

public abstract class ItemArmorElectric
extends ItemArmorIC2
implements ISpecialArmor,
IElectricItem,
IItemHudInfo,
ICustomDamageItem {
    protected final double maxCharge;
    protected final double transferLimit;
    protected final int tier;
    private final ThreadLocal<Boolean> allowDamaging = new ThreadLocal();

    public ItemArmorElectric(InternalName internalName, InternalName armorName, int armorType1, double maxCharge1, double transferLimit1, int tier1) {
        super(internalName, ItemArmor.ArmorMaterial.DIAMOND, armorName, armorType1, null);
        this.maxCharge = maxCharge1;
        this.tier = tier1;
        this.transferLimit = transferLimit1;
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    public int getItemEnchantability() {
        return 0;
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(itemStack));
        info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerTier") + " " + this.tier);
        return info;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerTier") + " " + this.tier);
    }

    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        ItemStack charged = new ItemStack((Item)this, 1);
        ElectricItem.manager.charge(charged, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, false);
        itemList.add(charged);
        itemList.add(new ItemStack((Item)this, 1, this.getMaxDamage()));
    }

    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (source.isUnblockable()) {
            return new ISpecialArmor.ArmorProperties(0, 0.0, 0);
        }
        double absorptionRatio = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
        int energyPerDamage = this.getEnergyPerDamage();
        int damageLimit = Integer.MAX_VALUE;
        if (energyPerDamage > 0) {
            damageLimit = (int)Math.min((double)damageLimit, 25.0 * ElectricItem.manager.getCharge(armor) / (double)energyPerDamage);
        }
        return new ISpecialArmor.ArmorProperties(0, absorptionRatio, damageLimit);
    }

    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        if (ElectricItem.manager.getCharge(armor) >= (double)this.getEnergyPerDamage()) {
            return (int)Math.round(20.0 * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio());
        }
        return 0;
    }

    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        ElectricItem.manager.discharge(stack, damage * this.getEnergyPerDamage(), Integer.MAX_VALUE, true, false, false);
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return this.maxCharge;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return this.tier;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return this.transferLimit;
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }

    public void setDamage(ItemStack stack, int damage) {
        if (damage == stack.getItemDamage()) {
            return;
        }
        Boolean allow = this.allowDamaging.get();
        if (allow == null || !allow.booleanValue()) {
            IC2.log.warn(LogCategory.Item, new Throwable(), "Detected invalid armor damage application:");
        } else {
            super.setDamage(stack, damage);
        }
    }

    @Override
    public int getCustomDamage(ItemStack stack) {
        return stack.getItemDamage();
    }

    @Override
    public int getMaxCustomDamage(ItemStack stack) {
        return stack.getMaxDamage();
    }

    @Override
    public void setCustomDamage(ItemStack stack, int damage) {
        this.allowDamaging.set(true);
        stack.setItemDamage(damage);
        this.allowDamaging.set(false);
    }

    @Override
    public boolean applyCustomDamage(ItemStack stack, int damage, EntityLivingBase src) {
        if (src != null) {
            stack.damageItem(damage, src);
            return true;
        }
        return stack.attemptDamageItem(damage, IC2.random);
    }

    public abstract double getDamageAbsorptionRatio();

    public abstract int getEnergyPerDamage();

    private double getBaseAbsorptionRatio() {
        switch (this.armorType) {
            case 0: {
                return 0.15;
            }
            case 1: {
                return 0.4;
            }
            case 2: {
                return 0.3;
            }
            case 3: {
                return 0.15;
            }
        }
        return 0.0;
    }
}

