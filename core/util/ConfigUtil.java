/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameData
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 */
package ic2.core.util;

import cpw.mods.fml.common.registry.GameData;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputFluidContainer;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.IC2;
import ic2.core.util.Config;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ConfigUtil {
    public static List<String> asList(String str) {
        if ((str = str.trim()).isEmpty()) {
            return new ArrayList<String>(0);
        }
        return Arrays.asList(str.split("\\s*,\\s*"));
    }

    public static List<IRecipeInput> asRecipeInputList(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            try {
                return ConfigUtil.asRecipeInputList(value.getString());
            }
            catch (ParseException e) {
                throw new Config.ParseException("Invalid value", value, e);
            }
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return null;
        }
    }

    public static List<ItemStack> asStackList(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            try {
                return ConfigUtil.asStackList(value.getString());
            }
            catch (ParseException e) {
                throw new Config.ParseException("Invalid value", value, e);
            }
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return null;
        }
    }

    public static ItemStack asStack(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            try {
                return ConfigUtil.asStack(value.getString());
            }
            catch (ParseException e) {
                throw new Config.ParseException("Invalid value", value, e);
            }
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return null;
        }
    }

    public static String getString(Config config, String key) {
        return config.get(key).getString();
    }

    public static boolean getBool(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            return value.getBool();
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return false;
        }
    }

    public static int getInt(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            return value.getInt();
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return 0;
        }
    }

    public static float getFloat(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            return value.getFloat();
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return 0.0f;
        }
    }

    public static double getDouble(Config config, String key) {
        Config.Value value = config.get(key);
        try {
            return value.getDouble();
        }
        catch (Config.ParseException e) {
            ConfigUtil.displayError(e, key);
            return 0.0;
        }
    }

    public static List<ItemStack> asStackList(String str) throws ParseException {
        List<String> parts = ConfigUtil.asList(str);
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>(parts.size());
        for (String part : parts) {
            ret.add(ConfigUtil.asStack(part));
        }
        return ret;
    }

    public static List<IRecipeInput> asRecipeInputList(String str) throws ParseException {
        return ConfigUtil.asRecipeInputList(str, false);
    }

    public static List<IRecipeInput> asRecipeInputList(String str, boolean allowNull) throws ParseException {
        List<String> parts = ConfigUtil.asList(str);
        ArrayList<IRecipeInput> ret = new ArrayList<IRecipeInput>(parts.size());
        for (String part : parts) {
            IRecipeInput input = ConfigUtil.asRecipeInput(part);
            if (input == null && !allowNull) {
                throw new ParseException("There is no item matching " + part + ".", -1);
            }
            ret.add(input);
        }
        return ret;
    }

    public static ItemStack asStack(String str) throws ParseException {
        int pos = str.indexOf(64);
        String itemName = pos == -1 ? str : str.substring(0, pos);
        Item item = (Item)GameData.getItemRegistry().getRaw(itemName);
        if (item == null) {
            return null;
        }
        if (pos == -1) {
            return new ItemStack(item);
        }
        String meta = str.substring(pos + 1);
        if (meta.equals("*")) {
            return new ItemStack(item, 1, Short.MAX_VALUE);
        }
        return new ItemStack(item, 1, NumberFormat.getIntegerInstance().parse(meta).intValue());
    }

    public static ItemStack asStackWithAmount(String str) throws ParseException {
        int pos = str.indexOf(64);
        String itemName = pos == -1 ? str : str.substring(0, pos);
        Item item = (Item)GameData.getItemRegistry().getRaw(itemName);
        if (item == null) {
            return null;
        }
        if (pos == -1) {
            return new ItemStack(item);
        }
        String meta = str.substring(pos + 1);
        int amount = 1;
        int amountIndex = meta.indexOf(64);
        if (amountIndex != -1) {
            amount = Integer.parseInt(meta.substring(amountIndex + 1));
            meta = meta.substring(0, amountIndex);
        }
        if (meta.equals("*")) {
            return new ItemStack(item, amount, Short.MAX_VALUE);
        }
        return new ItemStack(item, amount, NumberFormat.getIntegerInstance().parse(meta).intValue());
    }

    public static String fromStack(ItemStack stack) {
        String ret = GameData.getItemRegistry().getNameForObject((Object)stack.getItem());
        if (stack.getItemDamage() == Short.MAX_VALUE) {
            ret = ret + "@*";
        } else if (stack.getItemDamage() != 0) {
            ret = ret + "@" + stack.getItemDamage();
        }
        return ret;
    }

    public static String fromStackWithAmount(ItemStack stack) {
        String ret = GameData.getItemRegistry().getNameForObject((Object)stack.getItem());
        if (stack.getItemDamage() == Short.MAX_VALUE) {
            ret = ret + "@*";
        } else if (stack.getItemDamage() != 0) {
            ret = ret + "@" + stack.getItemDamage();
        } else if (stack.stackSize != 1) {
            ret = ret + "@0";
        }
        if (stack.stackSize != 1) {
            ret = ret + "@" + stack.stackSize;
        }
        return ret;
    }

    public static IRecipeInput asRecipeInput(Config.Value value) {
        try {
            return ConfigUtil.asRecipeInput(value.getString());
        }
        catch (ParseException e) {
            throw new Config.ParseException("Invalid value", value, e);
        }
    }

    public static IRecipeInput asRecipeInput(String str) throws ParseException {
        int pos = str.indexOf(64);
        String itemName = str;
        Integer meta = null;
        if (pos != -1) {
            itemName = str.substring(0, pos);
            String metaStr = str.substring(pos + 1);
            meta = metaStr.equals("*") ? Integer.valueOf(Short.MAX_VALUE) : Integer.valueOf(NumberFormat.getIntegerInstance().parse(metaStr).intValue());
        }
        if ((pos = itemName.indexOf(58)) == -1) {
            throw new IllegalArgumentException("Invalid name specification: " + str);
        }
        String domain = itemName.substring(0, pos);
        if (domain.equals("OreDict")) {
            String name = itemName.substring(pos + 1);
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Invalid ore dictionary specification: " + str);
            }
            return new RecipeInputOreDict(name, 1, meta);
        }
        if (domain.equals("Fluid")) {
            Fluid fluid = FluidRegistry.getFluid((String)itemName.substring(pos + 1));
            if (fluid == null) {
                return null;
            }
            return new RecipeInputFluidContainer(fluid);
        }
        Item item = (Item)GameData.getItemRegistry().getRaw(itemName);
        if (item == null) {
            return null;
        }
        return new RecipeInputItemStack(new ItemStack(item, 1, meta == null ? 0 : meta));
    }

    public static IRecipeInput asRecipeInputWithAmount(String str) throws ParseException {
        int pos = str.indexOf(64);
        String itemName = str;
        Integer meta = null;
        int amount = 1;
        if (pos != -1) {
            itemName = str.substring(0, pos);
            String metaStr = str.substring(pos + 1);
            int amountIndex = metaStr.indexOf(64);
            if (amountIndex != -1) {
                amount = Integer.parseInt(metaStr.substring(amountIndex + 1));
                metaStr = metaStr.substring(0, amountIndex);
            }
            if (metaStr.equals("*")) {
                meta = Short.MAX_VALUE;
            } else if (!metaStr.equals("-1")) {
                meta = NumberFormat.getIntegerInstance().parse(metaStr).intValue();
            }
        }
        if ((pos = itemName.indexOf(58)) == -1) {
            throw new IllegalArgumentException("Invalid name specification: " + str);
        }
        String domain = itemName.substring(0, pos);
        if (domain.equals("OreDict")) {
            String name = itemName.substring(pos + 1);
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Invalid ore dictionary specification: " + str);
            }
            return new RecipeInputOreDict(name, amount, meta);
        }
        if (domain.equals("Fluid")) {
            Fluid fluid = FluidRegistry.getFluid((String)itemName.substring(pos + 1));
            if (fluid == null) {
                return null;
            }
            return new RecipeInputFluidContainer(fluid, amount);
        }
        Item item = (Item)GameData.getItemRegistry().getRaw(itemName);
        if (item == null) {
            return null;
        }
        return new RecipeInputItemStack(new ItemStack(item, amount, meta == null ? 0 : meta));
    }

    private static void displayError(Config.ParseException e, String key) {
        IC2.platform.displayError("The IC2 config file contains an invalid entry for %s.\n\n%s%s", key, e.getMessage(), e.getCause() != null ? "\n\n" + e.getCause().getMessage() : "");
    }
}

