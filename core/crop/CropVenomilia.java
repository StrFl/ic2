/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 */
package ic2.core.crop;

import ic2.api.crops.ICropTile;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.crop.Ic2CropCard;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class CropVenomilia
extends Ic2CropCard {
    @Override
    public String name() {
        return "venomilia";
    }

    @Override
    public String discoveredBy() {
        return "raGan";
    }

    @Override
    public int tier() {
        return 3;
    }

    @Override
    public int stat(int n) {
        switch (n) {
            case 0: {
                return 3;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return 3;
            }
            case 3: {
                return 3;
            }
            case 4: {
                return 3;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return new String[]{"Purple", "Flower", "Tulip", "Poison"};
    }

    @Override
    public int maxSize() {
        return 6;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getSize() <= 4 && crop.getLightLevel() >= 12 || crop.getSize() == 5;
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getSize() >= 4;
    }

    @Override
    public int getOptimalHavestSize(ICropTile crop) {
        return 5;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        if (crop.getSize() == 5) {
            return new ItemStack(Ic2Items.grinPowder.getItem(), 1);
        }
        if (crop.getSize() >= 4) {
            return new ItemStack(Items.dye, 1, 5);
        }
        return null;
    }

    @Override
    public byte getSizeAfterHarvest(ICropTile crop) {
        return 3;
    }

    @Override
    public int growthDuration(ICropTile crop) {
        if (crop.getSize() >= 3) {
            return 600;
        }
        return 400;
    }

    @Override
    public boolean rightclick(ICropTile crop, EntityPlayer player) {
        if (!player.isSneaking()) {
            this.onEntityCollision(crop, (Entity)player);
        }
        return crop.harvest(true);
    }

    @Override
    public boolean leftclick(ICropTile crop, EntityPlayer player) {
        if (!player.isSneaking()) {
            this.onEntityCollision(crop, (Entity)player);
        }
        return crop.pick(true);
    }

    @Override
    public boolean onEntityCollision(ICropTile crop, Entity entity) {
        if (crop.getSize() == 5 && entity instanceof EntityLivingBase) {
            if (entity instanceof EntityPlayer && ((EntityPlayer)entity).isSneaking() && IC2.random.nextInt(50) != 0) {
                return super.onEntityCollision(crop, entity);
            }
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.poison.id, (IC2.random.nextInt(10) + 5) * 20, 0));
            crop.setSize((byte)4);
            crop.updateState();
        }
        return super.onEntityCollision(crop, entity);
    }

    @Override
    public boolean isWeed(ICropTile crop) {
        return crop.getSize() == 5 && crop.getGrowth() >= 8;
    }
}

