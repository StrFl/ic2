/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.IC2Potion;
import ic2.core.init.InternalName;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemTerraWart
extends ItemFood {
    public ItemTerraWart(InternalName internalName) {
        super(0, 1.0f, false);
        this.setAlwaysEdible();
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

    public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player) {
        --itemstack.stackSize;
        world.playSoundAtEntity((Entity)player, "random.burp", 0.5f, world.rand.nextFloat() * 0.1f + 0.9f);
        IC2.platform.removePotion((EntityLivingBase)player, Potion.confusion.id);
        IC2.platform.removePotion((EntityLivingBase)player, Potion.digSlowdown.id);
        IC2.platform.removePotion((EntityLivingBase)player, Potion.hunger.id);
        IC2.platform.removePotion((EntityLivingBase)player, Potion.moveSlowdown.id);
        IC2.platform.removePotion((EntityLivingBase)player, Potion.weakness.id);
        IC2.platform.removePotion((EntityLivingBase)player, Potion.blindness.id);
        IC2.platform.removePotion((EntityLivingBase)player, Potion.poison.id);
        IC2.platform.removePotion((EntityLivingBase)player, Potion.wither.id);
        PotionEffect effect = player.getActivePotionEffect((Potion)IC2Potion.radiation);
        if (effect != null) {
            if (effect.getDuration() <= 600) {
                IC2.platform.removePotion((EntityLivingBase)player, IC2Potion.radiation.id);
            } else {
                IC2.platform.removePotion((EntityLivingBase)player, IC2Potion.radiation.id);
                IC2Potion.radiation.applyTo((EntityLivingBase)player, effect.getDuration() - 600, effect.getAmplifier());
            }
        }
        return itemstack;
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }
}

