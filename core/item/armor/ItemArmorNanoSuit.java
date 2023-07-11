/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.util.StackUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class ItemArmorNanoSuit
extends ItemArmorElectric {
    public ItemArmorNanoSuit(InternalName internalName, int armorType1) {
        super(internalName, InternalName.nano, armorType1, 1000000.0, 1600.0, 3);
        if (armorType1 == 3) {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (source == DamageSource.fall && this.armorType == 3) {
            int energyPerDamage = this.getEnergyPerDamage();
            int damageLimit = Integer.MAX_VALUE;
            if (energyPerDamage > 0) {
                damageLimit = (int)Math.min((double)damageLimit, 25.0 * ElectricItem.manager.getCharge(armor) / (double)energyPerDamage);
            }
            return new ISpecialArmor.ArmorProperties(10, damage < 8.0 ? 1.0 : 0.875, damageLimit);
        }
        return super.getProperties(player, armor, source, damage, slot);
    }

    @SubscribeEvent
    public void onEntityLivingFallEvent(LivingFallEvent event) {
        EntityLivingBase entity;
        ItemStack armor;
        if (IC2.platform.isSimulating() && event.entity instanceof EntityLivingBase && (armor = (entity = (EntityLivingBase)event.entity).getEquipmentInSlot(1)) != null && armor.getItem() == this) {
            int fallDamage = (int)event.distance - 3;
            if (fallDamage >= 8) {
                return;
            }
            double energyCost = this.getEnergyPerDamage() * fallDamage;
            if (energyCost <= ElectricItem.manager.getCharge(armor)) {
                ElectricItem.manager.discharge(armor, energyCost, Integer.MAX_VALUE, true, false, false);
                event.setCanceled(true);
            }
        }
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        byte toggleTimer = nbtData.getByte("toggleTimer");
        boolean ret = false;
        switch (this.armorType) {
            case 0: {
                IC2.platform.profilerStartSection("NanoHelmet");
                boolean Nightvision = nbtData.getBoolean("Nightvision");
                short hubmode = nbtData.getShort("HudMode");
                if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    boolean bl = Nightvision = !Nightvision;
                    if (IC2.platform.isSimulating()) {
                        nbtData.setBoolean("Nightvision", Nightvision);
                        if (Nightvision) {
                            IC2.platform.messagePlayer(player, "Nightvision enabled.", new Object[0]);
                        } else {
                            IC2.platform.messagePlayer(player, "Nightvision disabled.", new Object[0]);
                        }
                    }
                }
                if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isHudModeKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    hubmode = hubmode == 2 ? (short)0 : (short)(hubmode + 1);
                    if (IC2.platform.isSimulating()) {
                        nbtData.setShort("HudMode", hubmode);
                        switch (hubmode) {
                            case 0: {
                                IC2.platform.messagePlayer(player, "HUD disabled.", new Object[0]);
                                break;
                            }
                            case 1: {
                                IC2.platform.messagePlayer(player, "HUD (basic) enabled.", new Object[0]);
                                break;
                            }
                            case 2: {
                                IC2.platform.messagePlayer(player, "HUD (extended) enabled", new Object[0]);
                            }
                        }
                    }
                }
                if (IC2.platform.isSimulating() && toggleTimer > 0) {
                    toggleTimer = (byte)(toggleTimer - 1);
                    nbtData.setByte("toggleTimer", toggleTimer);
                }
                if (Nightvision && IC2.platform.isSimulating() && ElectricItem.manager.use(itemStack, 1.0, (EntityLivingBase)player)) {
                    int x = MathHelper.floor_double((double)player.posX);
                    int z = MathHelper.floor_double((double)player.posZ);
                    int y = MathHelper.floor_double((double)player.posY);
                    int skylight = player.worldObj.getBlockLightValue(x, y, z);
                    if (skylight > 8) {
                        IC2.platform.removePotion((EntityLivingBase)player, Potion.nightVision.id);
                        player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0, true));
                    } else {
                        IC2.platform.removePotion((EntityLivingBase)player, Potion.blindness.id);
                        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, 0, true));
                    }
                    ret = true;
                }
                IC2.platform.profilerEndSection();
            }
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public double getDamageAbsorptionRatio() {
        return 0.9;
    }

    @Override
    public int getEnergyPerDamage() {
        return 5000;
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }
}

