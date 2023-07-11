/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ISpecialArmor$ArmorProperties
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingFallEvent
 */
package ic2.core.item.armor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class ItemArmorHazmat
extends ItemArmorUtility {
    public ItemArmorHazmat(InternalName internalName, int type) {
        super(internalName, InternalName.hazmat, type);
        this.setMaxDamage(64);
        if (this.armorType == 3) {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (this.armorType == 0 && this.hazmatAbsorbs(source) && ItemArmorHazmat.hasCompleteHazmat(player)) {
            if (source == DamageSource.inFire || source == DamageSource.lava) {
                player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 60, 1));
            }
            return new ISpecialArmor.ArmorProperties(10, 1.0, Integer.MAX_VALUE);
        }
        if (this.armorType == 3 && source == DamageSource.fall) {
            return new ISpecialArmor.ArmorProperties(10, damage < 8.0 ? 1.0 : 0.875, (armor.getMaxDamage() - armor.getItemDamage() + 2) * 2 * 25);
        }
        return new ISpecialArmor.ArmorProperties(0, 0.05, (armor.getMaxDamage() - armor.getItemDamage() + 2) / 2 * 25);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (this.hazmatAbsorbs(source) && ItemArmorHazmat.hasCompleteHazmat(entity)) {
            return;
        }
        int damageTotal = damage * 2;
        if (this.armorType == 3 && source == DamageSource.fall) {
            damageTotal = (damage + 1) / 2;
        }
        stack.damageItem(damageTotal, entity);
    }

    @SubscribeEvent
    public void onEntityLivingFallEvent(LivingFallEvent event) {
        if (IC2.platform.isSimulating() && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entity;
            ItemStack armor = player.inventory.armorInventory[0];
            if (armor != null && armor.getItem() == this) {
                int fallDamage = (int)event.distance - 3;
                if (fallDamage >= 8) {
                    return;
                }
                int armorDamage = (fallDamage + 1) / 2;
                if (armorDamage <= armor.getMaxDamage() - armor.getItemDamage() && armorDamage >= 0) {
                    armor.damageItem(armorDamage, (EntityLivingBase)player);
                    event.setCanceled(true);
                }
            }
        }
    }

    public boolean isRepairable() {
        return true;
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return 1;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        boolean ret = false;
        if (this.armorType == 0) {
            if (player.isBurning() && ItemArmorHazmat.hasCompleteHazmat((EntityLivingBase)player)) {
                if (this.isInLava(player)) {
                    player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 20, 0, true));
                }
                player.extinguish();
            }
            if (player.getAir() <= 100 && player.inventory.hasItemStack(Ic2Items.airCell)) {
                StackUtil.consumeInventoryItem(player, Ic2Items.airCell);
                player.inventory.addItemStackToInventory(new ItemStack(Ic2Items.cell.getItem()));
                player.setAir(player.getAir() + 150);
                ret = true;
            }
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    public boolean isInLava(EntityPlayer player) {
        int var6;
        int var5;
        double var2 = player.posY + 0.02;
        int var4 = MathHelper.floor_double((double)player.posX);
        Block block = player.worldObj.getBlock(var4, var5 = MathHelper.floor_float((float)MathHelper.floor_double((double)var2)), var6 = MathHelper.floor_double((double)player.posZ));
        if (block.getMaterial() == Material.lava || block.getMaterial() == Material.fire) {
            float var8 = BlockLiquid.getLiquidHeightPercent((int)player.worldObj.getBlockMetadata(var4, var5, var6)) - 0.11111111f;
            float var9 = (float)(var5 + 1) - var8;
            return var2 < (double)var9;
        }
        return false;
    }

    public static boolean hasCompleteHazmat(EntityLivingBase living) {
        for (int i = 1; i < 5; ++i) {
            ItemStack stack = living.getEquipmentInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemArmorHazmat) continue;
            return false;
        }
        return true;
    }

    public boolean hazmatAbsorbs(DamageSource source) {
        return source == DamageSource.inFire || source == DamageSource.inWall || source == DamageSource.lava || source == DamageSource.onFire || source == IC2DamageSource.electricity || source == IC2DamageSource.radiation;
    }

    @Override
    public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
        return false;
    }
}

