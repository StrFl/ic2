/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Loader
 *  cpw.mods.fml.common.ModContainer
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 */
package ic2.api.crops;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public abstract class CropCard {
    @SideOnly(value=Side.CLIENT)
    protected IIcon[] textures;
    private final String modId = CropCard.getModId();

    public abstract String name();

    public String owner() {
        return this.modId;
    }

    public String displayName() {
        return this.name();
    }

    public String discoveredBy() {
        return "unknown";
    }

    public String desc(int i) {
        String[] att = this.attributes();
        if (att == null || att.length == 0) {
            return "";
        }
        if (i == 0) {
            String s = att[0];
            if (att.length >= 2) {
                s = s + ", " + att[1];
                if (att.length >= 3) {
                    s = s + ",";
                }
            }
            return s;
        }
        if (att.length < 3) {
            return "";
        }
        String s = att[2];
        if (att.length >= 4) {
            s = s + ", " + att[3];
        }
        return s;
    }

    public int getrootslength(ICropTile crop) {
        return 1;
    }

    public abstract int tier();

    public abstract int stat(int var1);

    public abstract String[] attributes();

    public abstract int maxSize();

    @SideOnly(value=Side.CLIENT)
    public void registerSprites(IIconRegister iconRegister) {
        this.textures = new IIcon[this.maxSize()];
        for (int i = 1; i <= this.textures.length; ++i) {
            this.textures[i - 1] = iconRegister.registerIcon("ic2:crop/blockCrop." + this.name() + "." + i);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getSprite(ICropTile crop) {
        if (crop.getSize() <= 0 || crop.getSize() > this.textures.length) {
            return null;
        }
        return this.textures[crop.getSize() - 1];
    }

    public int growthDuration(ICropTile crop) {
        return this.tier() * 200;
    }

    public abstract boolean canGrow(ICropTile var1);

    public int weightInfluences(ICropTile crop, float humidity, float nutrients, float air) {
        return (int)(humidity + nutrients + air);
    }

    public boolean canCross(ICropTile crop) {
        return crop.getSize() >= 3;
    }

    public boolean rightclick(ICropTile crop, EntityPlayer player) {
        return crop.harvest(true);
    }

    public abstract int getOptimalHavestSize(ICropTile var1);

    public abstract boolean canBeHarvested(ICropTile var1);

    public float dropGainChance() {
        return (float)Math.pow(0.95, this.tier());
    }

    public abstract ItemStack getGain(ICropTile var1);

    public byte getSizeAfterHarvest(ICropTile crop) {
        return 1;
    }

    public boolean leftclick(ICropTile crop, EntityPlayer player) {
        return crop.pick(true);
    }

    public float dropSeedChance(ICropTile crop) {
        if (crop.getSize() == 1) {
            return 0.0f;
        }
        float base = 0.5f;
        if (crop.getSize() == 2) {
            base /= 2.0f;
        }
        for (int i = 0; i < this.tier(); ++i) {
            base = (float)((double)base * 0.8);
        }
        return base;
    }

    public ItemStack getSeeds(ICropTile crop) {
        return crop.generateSeeds(crop.getCrop(), crop.getGrowth(), crop.getGain(), crop.getResistance(), crop.getScanLevel());
    }

    public void onNeighbourChange(ICropTile crop) {
    }

    public int emitRedstone(ICropTile crop) {
        return 0;
    }

    public void onBlockDestroyed(ICropTile crop) {
    }

    public int getEmittedLight(ICropTile crop) {
        return 0;
    }

    public boolean onEntityCollision(ICropTile crop, Entity entity) {
        if (entity instanceof EntityLivingBase) {
            return ((EntityLivingBase)entity).isSprinting();
        }
        return false;
    }

    public void tick(ICropTile crop) {
    }

    public boolean isWeed(ICropTile crop) {
        return crop.getSize() >= 2 && (crop.getCrop() == Crops.weed || crop.getGrowth() >= 24);
    }

    @Deprecated
    public final int getId() {
        return Crops.instance.getIdFor(this);
    }

    private static String getModId() {
        ModContainer modContainer = Loader.instance().activeModContainer();
        if (modContainer != null) {
            return modContainer.getModId();
        }
        assert (false);
        return "unknown";
    }
}

