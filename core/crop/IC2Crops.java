/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.biome.BiomeGenBase
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 */
package ic2.core.crop;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.crops.BaseSeed;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.crop.CropAurelia;
import ic2.core.crop.CropBrownMushroom;
import ic2.core.crop.CropCarrots;
import ic2.core.crop.CropCocoa;
import ic2.core.crop.CropCoffee;
import ic2.core.crop.CropColorFlower;
import ic2.core.crop.CropCyprium;
import ic2.core.crop.CropEating;
import ic2.core.crop.CropFerru;
import ic2.core.crop.CropHops;
import ic2.core.crop.CropMelon;
import ic2.core.crop.CropNetherWart;
import ic2.core.crop.CropPlumbiscus;
import ic2.core.crop.CropPotato;
import ic2.core.crop.CropPumpkin;
import ic2.core.crop.CropRedMushroom;
import ic2.core.crop.CropRedWheat;
import ic2.core.crop.CropReed;
import ic2.core.crop.CropShining;
import ic2.core.crop.CropStagnium;
import ic2.core.crop.CropStickreed;
import ic2.core.crop.CropTerraWart;
import ic2.core.crop.CropVenomilia;
import ic2.core.crop.CropWeed;
import ic2.core.crop.CropWheat;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public class IC2Crops
extends Crops {
    private final Map<BiomeDictionary.Type, Integer> humidityBiomeTypeBonus = new EnumMap<BiomeDictionary.Type, Integer>(BiomeDictionary.Type.class);
    private final Map<BiomeDictionary.Type, Integer> nutrientBiomeTypeBonus = new EnumMap<BiomeDictionary.Type, Integer>(BiomeDictionary.Type.class);
    private final Map<ItemStack, BaseSeed> baseSeeds = new HashMap<ItemStack, BaseSeed>();
    public static CropCard cropWheat = new CropWheat();
    public static CropCard cropPumpkin = new CropPumpkin();
    public static CropCard cropMelon = new CropMelon();
    public static CropCard cropYellowFlower = new CropColorFlower("dandelion", new String[]{"Yellow", "Flower"}, 11);
    public static CropCard cropRedFlower = new CropColorFlower("rose", new String[]{"Red", "Flower", "Rose"}, 1);
    public static CropCard cropBlackFlower = new CropColorFlower("blackthorn", new String[]{"Black", "Flower", "Rose"}, 0);
    public static CropCard cropPurpleFlower = new CropColorFlower("tulip", new String[]{"Purple", "Flower", "Tulip"}, 5);
    public static CropCard cropBlueFlower = new CropColorFlower("cyazint", new String[]{"Blue", "Flower"}, 6);
    public static CropCard cropVenomilia = new CropVenomilia();
    public static CropCard cropReed = new CropReed();
    public static CropCard cropStickReed = new CropStickreed();
    public static CropCard cropCocoa = new CropCocoa();
    public static CropCard cropFerru = new CropFerru();
    public static CropCard cropAurelia = new CropAurelia();
    public static CropCard cropRedwheat = new CropRedWheat();
    public static CropCard cropNetherWart = new CropNetherWart();
    public static CropCard cropTerraWart = new CropTerraWart();
    public static CropCard cropCoffee = new CropCoffee();
    public static CropCard cropHops = new CropHops();
    public static CropCard cropCarrots = new CropCarrots();
    public static CropCard cropPotato = new CropPotato();
    public static CropCard cropredmushroom = new CropRedMushroom();
    public static CropCard cropbrownmushroom = new CropBrownMushroom();
    public static CropCard cropEatingPlant = new CropEating();
    public static CropCard cropCyprium = new CropCyprium();
    public static CropCard cropStagnium = new CropStagnium();
    public static CropCard cropPlumbiscus = new CropPlumbiscus();
    public static CropCard cropShining = new CropShining();
    private final Map<String, Map<String, CropCard>> cropMap = new HashMap<String, Map<String, CropCard>>();
    private final CropCard[] legacyIds = new CropCard[256];

    public static void init() {
        Crops.instance = new IC2Crops();
        Crops.weed = new CropWeed();
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.JUNGLE, 10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.SWAMP, 10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.MUSHROOM, 5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.FOREST, 5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.RIVER, 2);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.PLAINS, 0);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.SAVANNA, -2);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.HILLS, -5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.MOUNTAIN, -5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.WASTELAND, -8);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.END, -10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.NETHER, -10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.DEAD, -10);
        IC2Crops.registerCrops();
        IC2Crops.registerBaseSeeds();
    }

    public static void registerCrops() {
        if (!(Crops.instance.registerCrop(weed, 0) && Crops.instance.registerCrop(cropWheat, 1) && Crops.instance.registerCrop(cropPumpkin, 2) && Crops.instance.registerCrop(cropMelon, 3) && Crops.instance.registerCrop(cropYellowFlower, 4) && Crops.instance.registerCrop(cropRedFlower, 5) && Crops.instance.registerCrop(cropBlackFlower, 6) && Crops.instance.registerCrop(cropPurpleFlower, 7) && Crops.instance.registerCrop(cropBlueFlower, 8) && Crops.instance.registerCrop(cropVenomilia, 9) && Crops.instance.registerCrop(cropReed, 10) && Crops.instance.registerCrop(cropStickReed, 11) && Crops.instance.registerCrop(cropCocoa, 12) && Crops.instance.registerCrop(cropFerru, 13) && Crops.instance.registerCrop(cropAurelia, 14) && Crops.instance.registerCrop(cropRedwheat, 15) && Crops.instance.registerCrop(cropNetherWart, 16) && Crops.instance.registerCrop(cropTerraWart, 17) && Crops.instance.registerCrop(cropCoffee, 18) && Crops.instance.registerCrop(cropHops, 19) && Crops.instance.registerCrop(cropCarrots, 20) && Crops.instance.registerCrop(cropPotato, 21) && Crops.instance.registerCrop(cropredmushroom, 22) && Crops.instance.registerCrop(cropbrownmushroom, 23) && Crops.instance.registerCrop(cropEatingPlant, 24) && Crops.instance.registerCrop(cropCyprium, 25) && Crops.instance.registerCrop(cropStagnium, 26) && Crops.instance.registerCrop(cropPlumbiscus, 27) && Crops.instance.registerCrop(cropShining, 28))) {
            IC2.platform.displayError("One or more crops have failed to initialize.\nThis could happen due to a crop addon using a crop ID already taken by a crop from IndustrialCraft 2.", new Object[0]);
        }
    }

    public static void registerBaseSeeds() {
        Crops.instance.registerBaseSeed(new ItemStack(Items.wheat_seeds, 1, Short.MAX_VALUE), cropWheat, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.pumpkin_seeds, 1, Short.MAX_VALUE), cropPumpkin, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.melon_seeds, 1, Short.MAX_VALUE), cropMelon, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.nether_wart, 1, Short.MAX_VALUE), cropNetherWart, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Ic2Items.terraWart.getItem(), 1, Short.MAX_VALUE), cropTerraWart, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Ic2Items.coffeeBeans.getItem(), 1, Short.MAX_VALUE), cropCoffee, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.reeds, 1, Short.MAX_VALUE), cropReed, 1, 3, 0, 2);
        Crops.instance.registerBaseSeed(new ItemStack(Items.dye, 1, 3), cropCocoa, 1, 0, 0, 0);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.red_flower, 4, Short.MAX_VALUE), cropRedFlower, 4, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.yellow_flower, 4, Short.MAX_VALUE), cropYellowFlower, 4, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.carrot, 1, Short.MAX_VALUE), cropCarrots, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.potato, 1, Short.MAX_VALUE), cropPotato, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.brown_mushroom, 4, Short.MAX_VALUE), cropbrownmushroom, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.red_mushroom, 4, Short.MAX_VALUE), cropredmushroom, 1, 1, 1, 1);
    }

    @Override
    public void addBiomenutrientsBonus(BiomeDictionary.Type type, int nutrientsBonus) {
        this.nutrientBiomeTypeBonus.put(type, nutrientsBonus);
    }

    @Override
    public void addBiomehumidityBonus(BiomeDictionary.Type type, int humidityBonus) {
        this.humidityBiomeTypeBonus.put(type, humidityBonus);
    }

    @Override
    public int getHumidityBiomeBonus(BiomeGenBase biome) {
        Integer ret = 0;
        for (BiomeDictionary.Type type : BiomeDictionary.getTypesForBiome((BiomeGenBase)biome)) {
            Integer val = this.humidityBiomeTypeBonus.get(type);
            if (val == null || val <= ret) continue;
            ret = val;
        }
        return ret;
    }

    @Override
    public int getNutrientBiomeBonus(BiomeGenBase biome) {
        Integer ret = 0;
        for (BiomeDictionary.Type type : BiomeDictionary.getTypesForBiome((BiomeGenBase)biome)) {
            Integer val = this.nutrientBiomeTypeBonus.get(type);
            if (val == null || val <= ret) continue;
            ret = val;
        }
        return ret;
    }

    @Override
    public CropCard getCropCard(String owner, String name) {
        Map<String, CropCard> map = this.cropMap.get(owner);
        if (map == null) {
            return null;
        }
        return map.get(name);
    }

    @Override
    public CropCard getCropCard(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey("owner") && nbt.hasKey("name")) {
            return this.getCropCard(nbt.getString("owner"), nbt.getString("name"));
        }
        if (nbt.hasKey("id")) {
            return IC2Crops.getCropFromId(nbt.getShort("id"));
        }
        return null;
    }

    @Override
    public Collection<CropCard> getCrops() {
        return new AbstractCollection<CropCard>(){

            @Override
            public Iterator<CropCard> iterator() {
                return new Iterator<CropCard>(){
                    private final Iterator<Map<String, CropCard>> mapIterator;
                    private Iterator<CropCard> iterator;
                    {
                        this.mapIterator = IC2Crops.this.cropMap.values().iterator();
                        this.iterator = this.getNextIterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.iterator != null && this.iterator.hasNext();
                    }

                    @Override
                    public CropCard next() {
                        if (this.iterator == null) {
                            throw new NoSuchElementException("no more elements");
                        }
                        CropCard ret = this.iterator.next();
                        if (!this.iterator.hasNext()) {
                            this.iterator = this.getNextIterator();
                        }
                        return ret;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("This iterator is read-only.");
                    }

                    private Iterator<CropCard> getNextIterator() {
                        Iterator<CropCard> ret = null;
                        while (this.mapIterator.hasNext() && ret == null) {
                            ret = this.mapIterator.next().values().iterator();
                            if (ret.hasNext()) continue;
                            ret = null;
                        }
                        return ret;
                    }
                };
            }

            @Override
            public int size() {
                int ret = 0;
                for (Map map : IC2Crops.this.cropMap.values()) {
                    ret += map.size();
                }
                return ret;
            }
        };
    }

    @Override
    public CropCard[] getCropList() {
        return this.legacyIds;
    }

    @Override
    public short registerCrop(CropCard crop) {
        int id = -1;
        for (int i = 0; i < this.legacyIds.length; ++i) {
            if (this.legacyIds[i] != null) continue;
            id = i;
            break;
        }
        this.registerCrop(crop, id);
        return (short)id;
    }

    @Override
    public boolean registerCrop(CropCard crop, int i) {
        if (i >= 0 && i < this.legacyIds.length && this.legacyIds[i] == null) {
            this.legacyIds[i] = crop;
        }
        String owner = crop.owner();
        String name = crop.name();
        Map<String, CropCard> map = this.cropMap.get(owner);
        if (map == null) {
            map = new HashMap<String, CropCard>();
            this.cropMap.put(owner, map);
        }
        map.put(name, crop);
        return true;
    }

    @Override
    public boolean registerBaseSeed(ItemStack stack, int id, int size, int growth, int gain, int resistance) {
        CropCard crop = IC2Crops.getCropFromId(id);
        if (crop == null) {
            return false;
        }
        return this.registerBaseSeed(stack, crop, size, growth, gain, resistance);
    }

    @Override
    public boolean registerBaseSeed(ItemStack stack, CropCard crop, int size, int growth, int gain, int resistance) {
        for (ItemStack key : this.baseSeeds.keySet()) {
            if (key.getItem() != stack.getItem() || key.getItemDamage() != stack.getItemDamage()) continue;
            return false;
        }
        this.baseSeeds.put(stack, new BaseSeed(crop, size, growth, gain, resistance, stack.stackSize));
        return true;
    }

    @Override
    public BaseSeed getBaseSeed(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        for (Map.Entry<ItemStack, BaseSeed> entry : this.baseSeeds.entrySet()) {
            ItemStack key = entry.getKey();
            if (key.getItem() != stack.getItem() || key.getItemDamage() != Short.MAX_VALUE && key.getItemDamage() != stack.getItemDamage()) continue;
            return this.baseSeeds.get(key);
        }
        return null;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void startSpriteRegistration(IIconRegister iconRegister) {
        for (Map<String, CropCard> map : this.cropMap.values()) {
            for (CropCard cropCard : map.values()) {
                cropCard.registerSprites(iconRegister);
            }
        }
    }

    @Override
    public int getIdFor(CropCard crop) {
        if (crop == null) {
            return -1;
        }
        for (int i = 0; i < this.legacyIds.length; ++i) {
            if (this.legacyIds[i] != crop) continue;
            return i;
        }
        return -1;
    }

    protected static CropCard getCropFromId(int id) {
        CropCard[] crops = Crops.instance.getCropList();
        if (id < 0 || id >= crops.length) {
            return null;
        }
        return crops[id];
    }
}

