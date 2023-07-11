/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.biome.BiomeGenBase
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 */
package ic2.core.crop;

import ic2.api.crops.ICropTile;
import ic2.api.item.ItemWrapper;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.crop.Ic2CropCard;
import ic2.core.crop.TileEntityCrop;
import ic2.core.util.StackUtil;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public class CropEating
extends Ic2CropCard {
    private final double movementMultiplier = 0.5;
    private final double length = 1.0;
    private static final IC2DamageSource damage = new IC2DamageSource("cropEating");

    @Override
    public String discoveredBy() {
        return "Hasudako";
    }

    @Override
    public String name() {
        return "eatingplant";
    }

    @Override
    public int tier() {
        return 6;
    }

    @Override
    public int stat(int n) {
        switch (n) {
            case 0: {
                return 1;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return 3;
            }
            case 3: {
                return 1;
            }
            case 4: {
                return 4;
            }
        }
        return 0;
    }

    @Override
    public String[] attributes() {
        return new String[]{"Bad", "Food"};
    }

    @Override
    public int maxSize() {
        return 6;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        if (crop.getSize() < 3) {
            return crop.getLightLevel() > 10;
        }
        return crop.isBlockBelow(Blocks.lava) && crop.getSize() < this.maxSize() && crop.getLightLevel() > 10;
    }

    @Override
    public int getOptimalHavestSize(ICropTile crop) {
        return 4;
    }

    @Override
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getSize() >= 4 && crop.getSize() < 6;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        if (crop.getSize() >= 4 && crop.getSize() < 6) {
            return new ItemStack(Blocks.cactus);
        }
        return null;
    }

    @Override
    public void tick(ICropTile crop) {
        List list;
        if (crop.getSize() == 1) {
            return;
        }
        TileEntityCrop te = (TileEntityCrop)crop;
        ChunkCoordinates coords = crop.getLocation();
        double xcentered = (double)coords.posX + 0.5;
        double ycentered = (double)coords.posY + 0.5;
        double zcentered = (double)coords.posZ + 0.5;
        if (crop.getCustomData().getBoolean("eaten")) {
            StackUtil.dropAsEntity(crop.getWorld(), coords.posX, coords.posY, coords.posZ, new ItemStack(Items.rotten_flesh));
            crop.getCustomData().setBoolean("eaten", false);
        }
        if ((list = crop.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox((double)((double)coords.posX + 0.5 - 1.0), (double)coords.posY, (double)((double)coords.posZ + 0.5 - 1.0), (double)((double)coords.posX + 0.5 + 1.0), (double)((double)coords.posY + 1.0 + 1.0), (double)((double)coords.posZ + 0.5 + 1.0)))).isEmpty()) {
            return;
        }
        Collections.shuffle(list);
        for (EntityLivingBase entity : list) {
            if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) continue;
            entity.motionX = (xcentered - entity.posX) * 0.5;
            entity.motionZ = (zcentered - entity.posZ) * 0.5;
            if (entity.motionY > -0.05) {
                entity.motionY = -0.05;
            }
            entity.attackEntityFrom((DamageSource)damage, (float)crop.getSize() * 2.0f);
            if (!CropEating.hasMetalAromor(entity)) {
                entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 64, 50));
                entity.addPotionEffect(new PotionEffect(Potion.invisibility.id, 64, 0));
                entity.addPotionEffect(new PotionEffect(Potion.blindness.id, 64, 0));
            }
            if (this.canGrow(crop)) {
                te.growthPoints = (int)((double)te.growthPoints + (double)te.calcGrowthRate() * 0.5);
            }
            crop.getWorld().playSoundEffect(xcentered, ycentered, zcentered, "random.burp", 1.0f, IC2.random.nextFloat() * 0.1f + 0.9f);
            crop.getCustomData().setBoolean("eaten", true);
            break;
        }
    }

    @Override
    public int getrootslength(ICropTile crop) {
        return 5;
    }

    @Override
    public int growthDuration(ICropTile crop) {
        float multiplier = 1.0f;
        ChunkCoordinates coords = crop.getLocation();
        BiomeGenBase biome = crop.getWorld().getBiomeGenForCoords(coords.posX, coords.posZ);
        if (BiomeDictionary.isBiomeOfType((BiomeGenBase)biome, (BiomeDictionary.Type)BiomeDictionary.Type.SWAMP) || BiomeDictionary.isBiomeOfType((BiomeGenBase)biome, (BiomeDictionary.Type)BiomeDictionary.Type.MOUNTAIN)) {
            multiplier /= 1.5f;
        }
        return (int)((float)super.growthDuration(crop) * (multiplier /= 1.0f + (float)crop.getAirQuality() / 10.0f));
    }

    private static boolean hasMetalAromor(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer)entity;
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null || !ItemWrapper.isMetalArmor(stack, player)) continue;
            return true;
        }
        return false;
    }
}

