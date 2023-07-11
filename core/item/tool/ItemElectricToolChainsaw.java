/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.block.Block
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.stats.StatList
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IShearable
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.EntityInteractEvent
 */
package ic2.core.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.IHitSoundOverride;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.init.InternalName;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class ItemElectricToolChainsaw
extends ItemElectricTool
implements IHitSoundOverride {
    public static boolean wasEquipped = false;
    public static AudioSource audioSource;

    public ItemElectricToolChainsaw(InternalName internalName) {
        this(internalName, 100, ItemElectricTool.HarvestLevel.Iron);
    }

    public ItemElectricToolChainsaw(InternalName internalName, int operationEnergyCost, ItemElectricTool.HarvestLevel harvestLevel) {
        super(internalName, operationEnergyCost, harvestLevel, EnumSet.of(ItemElectricTool.ToolClass.Axe, ItemElectricTool.ToolClass.Sword, ItemElectricTool.ToolClass.Shears));
        this.maxCharge = 30000;
        this.transferLimit = 100;
        this.tier = 1;
        this.efficiencyOnProperMaterial = 12.0f;
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!IC2.platform.isSimulating()) {
            return super.onItemRightClick(stack, world, player);
        }
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            NBTTagCompound compoundTag = StackUtil.getOrCreateNbtData(stack);
            if (compoundTag.getBoolean("disableShear")) {
                compoundTag.setBoolean("disableShear", false);
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.normal");
            } else {
                compoundTag.setBoolean("disableShear", true);
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.noShear");
            }
        }
        return super.onItemRightClick(stack, world, player);
    }

    public Multimap getAttributeModifiers(ItemStack stack) {
        HashMultimap ret;
        if (ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
            ret = HashMultimap.create();
            ret.put((Object)SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), (Object)new AttributeModifier(field_111210_e, "Tool modifier", 9.0, 0));
        } else {
            ret = super.getAttributeModifiers(stack);
        }
        return ret;
    }

    @Override
    public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
        ElectricItem.manager.use(itemstack, this.operationEnergyCost, attacker);
        if (attacker instanceof EntityPlayer && entityliving instanceof EntityCreeper && entityliving.getHealth() <= 0.0f) {
            IC2.achievements.issueAchievement((EntityPlayer)attacker, "killCreeperChainsaw");
        }
        return true;
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        IShearable target;
        if (!IC2.platform.isSimulating()) {
            return;
        }
        Entity entity = event.target;
        EntityPlayer player = event.entityPlayer;
        ItemStack itemstack = player.inventory.getStackInSlot(player.inventory.currentItem);
        if (itemstack != null && itemstack.getItem() == this && entity instanceof IShearable && !StackUtil.getOrCreateNbtData(itemstack).getBoolean("disableShear") && ElectricItem.manager.use(itemstack, this.operationEnergyCost, (EntityLivingBase)player) && (target = (IShearable)entity).isShearable(itemstack, (IBlockAccess)entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ)) {
            ArrayList drops = target.onSheared(itemstack, (IBlockAccess)entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ, EnchantmentHelper.getEnchantmentLevel((int)Enchantment.fortune.effectId, (ItemStack)itemstack));
            for (ItemStack stack : drops) {
                EntityItem ent = entity.entityDropItem(stack, 1.0f);
                ent.motionY += (double)(itemRand.nextFloat() * 0.05f);
                ent.motionX += (double)((itemRand.nextFloat() - itemRand.nextFloat()) * 0.1f);
                ent.motionZ += (double)((itemRand.nextFloat() - itemRand.nextFloat()) * 0.1f);
            }
        }
    }

    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        IShearable target;
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        if (StackUtil.getOrCreateNbtData(itemstack).getBoolean("disableShear")) {
            return false;
        }
        Block block = player.worldObj.getBlock(x, y, z);
        if (block instanceof IShearable && (target = (IShearable)block).isShearable(itemstack, (IBlockAccess)player.worldObj, x, y, z) && ElectricItem.manager.use(itemstack, this.operationEnergyCost, (EntityLivingBase)player)) {
            ArrayList drops = target.onSheared(itemstack, (IBlockAccess)player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel((int)Enchantment.fortune.effectId, (ItemStack)itemstack));
            for (ItemStack stack : drops) {
                float f = 0.7f;
                double d = (double)(itemRand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
                double d1 = (double)(itemRand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
                double d2 = (double)(itemRand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
                EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
                entityitem.delayBeforeCanPickup = 10;
                player.worldObj.spawnEntityInWorld((Entity)entityitem);
            }
            player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock((Block)block)], 1);
        }
        return false;
    }

    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        boolean isEquipped;
        boolean bl = isEquipped = flag && entity instanceof EntityLivingBase;
        if (IC2.platform.isRendering()) {
            if (isEquipped && !wasEquipped) {
                if (audioSource == null) {
                    audioSource = IC2.audioManager.createSource(entity, PositionSpec.Hand, "Tools/Chainsaw/ChainsawIdle.ogg", true, false, IC2.audioManager.getDefaultVolume());
                }
                if (audioSource != null) {
                    audioSource.play();
                }
            } else if (!isEquipped && audioSource != null) {
                audioSource.stop();
                audioSource.remove();
                audioSource = null;
                if (entity instanceof EntityLivingBase) {
                    IC2.audioManager.playOnce(entity, PositionSpec.Hand, "Tools/Chainsaw/ChainsawStop.ogg", true, IC2.audioManager.getDefaultVolume());
                }
            } else if (audioSource != null) {
                audioSource.updatePosition();
            }
            wasEquipped = isEquipped;
        }
    }

    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        if (audioSource != null) {
            audioSource.stop();
            audioSource.remove();
            audioSource = null;
        }
        return true;
    }

    @Override
    public String getHitSoundForBlock(int x, int y, int z) {
        String[] soundEffects = new String[]{"Tools/Chainsaw/ChainsawUseOne.ogg", "Tools/Chainsaw/ChainsawUseTwo.ogg"};
        return soundEffects[itemRand.nextInt(soundEffects.length)];
    }
}

