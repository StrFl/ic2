/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.IC2Potion;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.item.armor.ItemArmorHazmat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemRadioactive
extends ItemIC2 {
    protected final int radiationLength;
    protected final int amplifier;

    public ItemRadioactive(InternalName internalName, int radiationLength1, int amplifier1) {
        super(internalName);
        this.radiationLength = radiationLength1;
        this.amplifier = amplifier1;
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
        EntityLivingBase entityLiving;
        if (entity instanceof EntityLivingBase && !ItemArmorHazmat.hasCompleteHazmat(entityLiving = (EntityLivingBase)entity)) {
            PotionEffect effect = ((EntityPlayer)entity).getActivePotionEffect((Potion)IC2Potion.radiation);
            if (effect == null) {
                IC2Potion.radiation.applyTo(entityLiving, this.radiationLength * 20, this.amplifier);
            } else {
                IC2Potion.radiation.applyTo(entityLiving, ((EntityPlayer)entity).getActivePotionEffect((Potion)IC2Potion.radiation).getDuration() + this.radiationLength, this.amplifier);
            }
        }
    }
}

