/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.biome.BiomeGenBase
 *  net.minecraftforge.common.BiomeDictionary$Type
 */
package ic2.api.crops;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.crops.BaseSeed;
import ic2.api.crops.CropCard;
import java.util.Collection;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public abstract class Crops {
    public static Crops instance;
    public static CropCard weed;

    public abstract void addBiomenutrientsBonus(BiomeDictionary.Type var1, int var2);

    public abstract void addBiomehumidityBonus(BiomeDictionary.Type var1, int var2);

    public abstract int getHumidityBiomeBonus(BiomeGenBase var1);

    public abstract int getNutrientBiomeBonus(BiomeGenBase var1);

    public abstract CropCard getCropCard(String var1, String var2);

    public abstract CropCard getCropCard(ItemStack var1);

    public abstract Collection<CropCard> getCrops();

    @Deprecated
    public abstract CropCard[] getCropList();

    public abstract short registerCrop(CropCard var1);

    public abstract boolean registerCrop(CropCard var1, int var2);

    @Deprecated
    public abstract boolean registerBaseSeed(ItemStack var1, int var2, int var3, int var4, int var5, int var6);

    public abstract boolean registerBaseSeed(ItemStack var1, CropCard var2, int var3, int var4, int var5, int var6);

    public abstract BaseSeed getBaseSeed(ItemStack var1);

    @SideOnly(value=Side.CLIENT)
    public abstract void startSpriteRegistration(IIconRegister var1);

    @Deprecated
    public abstract int getIdFor(CropCard var1);
}

