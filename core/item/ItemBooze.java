/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBooze
extends ItemIC2 {
    public String[] solidRatio = new String[]{"Watery ", "Clear ", "Lite ", "", "Strong ", "Thick ", "Stodge ", "X"};
    public String[] hopsRatio = new String[]{"Soup ", "Alcfree ", "White ", "", "Dark ", "Full ", "Black ", "X"};
    public String[] timeRatioNames = new String[]{"Brew", "Youngster", "Beer", "Ale", "Dragonblood", "Black Stuff"};
    public int[] baseDuration = new int[]{300, 600, 900, 1200, 1600, 2000, 2400};
    public float[] baseIntensity = new float[]{0.4f, 0.75f, 1.0f, 1.5f, 2.0f};
    public static float rumStackability = 2.0f;
    public static int rumDuration = 600;

    public ItemBooze(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
        this.setCreativeTab(null);
    }

    @Override
    public String getTextureName(int index) {
        if (index < this.timeRatioNames.length) {
            return this.getUnlocalizedName() + "." + InternalName.beer.name() + "." + this.timeRatioNames[index];
        }
        if (index == this.timeRatioNames.length) {
            return this.getUnlocalizedName() + "." + InternalName.rum.name();
        }
        return null;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        int type = ItemBooze.getTypeOfValue(meta);
        if (type == 1) {
            int timeRatio = Math.min(ItemBooze.getTimeRatioOfBeerValue(meta), this.timeRatioNames.length - 1);
            return this.textures[timeRatio];
        }
        if (type == 2) {
            return this.textures[this.timeRatioNames.length];
        }
        return null;
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage();
        int type = ItemBooze.getTypeOfValue(meta);
        if (type == 1) {
            int timeRatio = Math.min(ItemBooze.getTimeRatioOfBeerValue(meta), this.timeRatioNames.length - 1);
            if (timeRatio == this.timeRatioNames.length - 1) {
                return this.timeRatioNames[timeRatio];
            }
            return this.solidRatio[ItemBooze.getSolidRatioOfBeerValue(meta)] + this.hopsRatio[ItemBooze.getHopsRatioOfBeerValue(meta)] + this.timeRatioNames[timeRatio];
        }
        if (type == 2) {
            return "Rum";
        }
        return "Zero";
    }

    public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player) {
        int meta = itemstack.getItemDamage();
        int type = ItemBooze.getTypeOfValue(meta);
        if (type == 0) {
            return new ItemStack(Ic2Items.mugEmpty.getItem());
        }
        if (type == 1) {
            if (ItemBooze.getTimeRatioOfBeerValue(meta) == 5) {
                return this.drinkBlackStuff(player);
            }
            int solidRatio1 = ItemBooze.getSolidRatioOfBeerValue(meta);
            int alc = ItemBooze.getHopsRatioOfBeerValue(meta);
            int duration = this.baseDuration[solidRatio1];
            float intensity = this.baseIntensity[ItemBooze.getTimeRatioOfBeerValue(meta)];
            player.getFoodStats().addStats(6 - alc, (float)solidRatio1 * 0.15f);
            int max = (int)(intensity * ((float)alc * 0.5f));
            PotionEffect slow = player.getActivePotionEffect(Potion.digSlowdown);
            int level = -1;
            if (slow != null) {
                level = slow.getAmplifier();
            }
            this.amplifyEffect(player, Potion.digSlowdown, max, intensity, duration);
            if (level > -1) {
                this.amplifyEffect(player, Potion.damageBoost, max, intensity, duration);
                if (level > 0) {
                    this.amplifyEffect(player, Potion.moveSlowdown, max / 2, intensity, duration);
                    if (level > 1) {
                        this.amplifyEffect(player, Potion.resistance, max - 1, intensity, duration);
                        if (level > 2) {
                            this.amplifyEffect(player, Potion.confusion, 0, intensity, duration);
                            if (level > 3) {
                                player.addPotionEffect(new PotionEffect(Potion.harm.id, 1, player.worldObj.rand.nextInt(3)));
                            }
                        }
                    }
                }
            }
        }
        if (type == 2) {
            if (ItemBooze.getProgressOfRumValue(meta) < 100) {
                this.drinkBlackStuff(player);
            } else {
                this.amplifyEffect(player, Potion.fireResistance, 0, rumStackability, rumDuration);
                PotionEffect def = player.getActivePotionEffect(Potion.resistance);
                int level = -1;
                if (def != null) {
                    level = def.getAmplifier();
                }
                this.amplifyEffect(player, Potion.resistance, 2, rumStackability, rumDuration);
                if (level >= 0) {
                    this.amplifyEffect(player, Potion.blindness, 0, rumStackability, rumDuration);
                }
                if (level >= 1) {
                    this.amplifyEffect(player, Potion.confusion, 0, rumStackability, rumDuration);
                }
            }
        }
        return new ItemStack(Ic2Items.mugEmpty.getItem());
    }

    public void amplifyEffect(EntityPlayer player, Potion potion1, int max, float intensity, int duration) {
        PotionEffect eff = player.getActivePotionEffect(potion1);
        if (eff == null) {
            player.addPotionEffect(new PotionEffect(potion1.id, duration, 0));
        } else {
            int newdur = eff.getDuration();
            int maxnewdur = (int)((float)duration * (1.0f + intensity * 2.0f) - (float)newdur) / 2;
            if (maxnewdur < 0) {
                maxnewdur = 0;
            }
            if (maxnewdur < duration) {
                duration = maxnewdur;
            }
            newdur += duration;
            int newamp = eff.getAmplifier();
            if (newamp < max) {
                ++newamp;
            }
            player.addPotionEffect(new PotionEffect(potion1.id, newdur, newamp));
        }
    }

    public ItemStack drinkBlackStuff(EntityPlayer player) {
        switch (player.worldObj.rand.nextInt(6)) {
            case 1: {
                player.addPotionEffect(new PotionEffect(Potion.confusion.id, 1200, 0));
                break;
            }
            case 2: {
                player.addPotionEffect(new PotionEffect(Potion.blindness.id, 2400, 0));
                break;
            }
            case 3: {
                player.addPotionEffect(new PotionEffect(Potion.poison.id, 2400, 0));
                break;
            }
            case 4: {
                player.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 2));
                break;
            }
            case 5: {
                player.addPotionEffect(new PotionEffect(Potion.harm.id, 1, player.worldObj.rand.nextInt(4)));
            }
        }
        return new ItemStack(Ic2Items.mugEmpty.getItem());
    }

    public int getMaxItemUseDuration(ItemStack itemstack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack itemstack) {
        return EnumAction.drink;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
        player.setItemInUse(itemstack, this.getMaxItemUseDuration(itemstack));
        return itemstack;
    }

    public static int getTypeOfValue(int value) {
        return ItemBooze.skipGetOfValue(value, 0, 2);
    }

    public static int getAmountOfValue(int value) {
        if (ItemBooze.getTypeOfValue(value) == 0) {
            return 0;
        }
        return ItemBooze.skipGetOfValue(value, 2, 5) + 1;
    }

    public static int getSolidRatioOfBeerValue(int value) {
        return ItemBooze.skipGetOfValue(value, 7, 3);
    }

    public static int getHopsRatioOfBeerValue(int value) {
        return ItemBooze.skipGetOfValue(value, 10, 3);
    }

    public static int getTimeRatioOfBeerValue(int value) {
        return ItemBooze.skipGetOfValue(value, 13, 3);
    }

    public static int getProgressOfRumValue(int value) {
        return ItemBooze.skipGetOfValue(value, 7, 7);
    }

    private static int skipGetOfValue(int value, int bitshift, int take) {
        take = (int)Math.pow(2.0, take) - 1;
        return (value >>= bitshift) & take;
    }
}

