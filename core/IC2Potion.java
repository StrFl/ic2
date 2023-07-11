/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  cpw.mods.fml.relauncher.ReflectionHelper
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 */
package ic2.core;

import com.google.common.base.Throwables;
import cpw.mods.fml.relauncher.ReflectionHelper;
import ic2.core.IC2DamageSource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class IC2Potion
extends Potion {
    public static IC2Potion radiation;
    private final List<ItemStack> curativeItems;

    public static void init() {
        radiation.setPotionName("ic2.potion.radiation");
        radiation.setIconIndex(6, 0);
        radiation.setEffectiveness(0.25);
    }

    public static IC2Potion registerPotion(int id, boolean badEffect, int liquidColor, ItemStack ... curativeItems1) {
        if (id >= Potion.potionTypes.length) {
            Potion[] potionNew = new Potion[Math.max(256, id)];
            System.arraycopy(Potion.potionTypes, 0, potionNew, 0, Potion.potionTypes.length);
            Field f = ReflectionHelper.findField(Potion.class, (String[])new String[]{"field_76425_a", "potionTypes"});
            f.setAccessible(true);
            try {
                Field modfield = Field.class.getDeclaredField("modifiers");
                modfield.setAccessible(true);
                modfield.setInt(f, f.getModifiers() & 0xFFFFFFEF);
                f.set(null, potionNew);
            }
            catch (Exception e) {
                Throwables.propagate((Throwable)e);
            }
        }
        return new IC2Potion(id, badEffect, liquidColor, curativeItems1);
    }

    private IC2Potion(int id1, boolean badEffect, int liquidColor, ItemStack ... curativeItems1) {
        super(id1, badEffect, liquidColor);
        this.curativeItems = Arrays.asList(curativeItems1);
    }

    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (this.id == IC2Potion.radiation.id) {
            entity.attackEntityFrom((DamageSource)IC2DamageSource.radiation, (float)(amplifier / 100) + 0.5f);
        }
    }

    public boolean isReady(int duration, int amplifier) {
        if (this.id == IC2Potion.radiation.id) {
            int rate = 25 >> amplifier;
            return rate > 0 ? duration % rate == 0 : true;
        }
        return false;
    }

    public void applyTo(EntityLivingBase entity, int duration, int amplifier) {
        PotionEffect effect = new PotionEffect(this.id, duration, amplifier);
        effect.setCurativeItems(this.curativeItems);
        entity.addPotionEffect(effect);
    }
}

