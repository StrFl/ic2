/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  org.apache.logging.log4j.Level
 */
package ic2.core.init;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import ic2.core.AdvCraftingRecipeManager;
import ic2.core.AdvRecipe;
import ic2.core.AdvShapelessRecipe;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.init.MainConfig;
import ic2.core.util.Config;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.Level;

public class Rezepte {
    private static List<IRecipeInput> disabledRecipeOutputs;
    private static Queue<PendingRecipe> pendingRecipes;

    public static void loadRecipes() {
        Recipes.advRecipes = new AdvCraftingRecipeManager();
        Config shapedRecipes = new Config("shaped recipes");
        Config shapelessRecipes = new Config("shapeless recipes");
        Config blastfurnace = new Config("blast furnace recipes");
        Config blockCutter = new Config("block cutter recipes");
        Config compressor = new Config("compressor recipes");
        Config extractor = new Config("extractor recipes");
        Config macerator = new Config("macerator recipes");
        Config mfcutting = new Config("metal former cutting recipes");
        Config mfextruding = new Config("metal former extruding recipes");
        Config mfrolling = new Config("metal former rolling recipes");
        Config oreWashing = new Config("ore washing recipes");
        Config centrifuge = new Config("thermal centrifuge recipes");
        try {
            shapedRecipes.load(Rezepte.getConfigFile("shaped_recipes"));
            shapelessRecipes.load(Rezepte.getConfigFile("shapeless_recipes"));
            blastfurnace.load(Rezepte.getConfigFile("blast_furnace"));
            blockCutter.load(Rezepte.getConfigFile("block_cutter"));
            compressor.load(Rezepte.getConfigFile("compressor"));
            extractor.load(Rezepte.getConfigFile("extractor"));
            macerator.load(Rezepte.getConfigFile("macerator"));
            mfcutting.load(Rezepte.getConfigFile("metal_former_cutting"));
            mfextruding.load(Rezepte.getConfigFile("metal_former_extruding"));
            mfrolling.load(Rezepte.getConfigFile("metal_former_rolling"));
            oreWashing.load(Rezepte.getConfigFile("ore_washer"));
            centrifuge.load(Rezepte.getConfigFile("thermal_centrifuge"));
        }
        catch (Exception e) {
            IC2.log.warn(LogCategory.Recipe, e, "Recipe loading failed.");
        }
        disabledRecipeOutputs = ConfigUtil.asRecipeInputList(MainConfig.get(), "recipes/disable");
        Rezepte.loadCraftingRecipes(shapedRecipes, true);
        Rezepte.loadCraftingRecipes(shapelessRecipes, false);
        Rezepte.loadMachineRecipes(blastfurnace, Recipes.blastfurance, MachineType.Normal);
        Rezepte.loadMachineRecipes(blockCutter, Recipes.blockcutter, MachineType.BlockCutter);
        Rezepte.loadMachineRecipes(compressor, Recipes.compressor, MachineType.Normal);
        Rezepte.loadMachineRecipes(extractor, Recipes.extractor, MachineType.Normal);
        Rezepte.loadMachineRecipes(macerator, Recipes.macerator, MachineType.Normal);
        Rezepte.loadMachineRecipes(mfcutting, Recipes.metalformerCutting, MachineType.Normal);
        Rezepte.loadMachineRecipes(mfextruding, Recipes.metalformerExtruding, MachineType.Normal);
        Rezepte.loadMachineRecipes(mfrolling, Recipes.metalformerRolling, MachineType.Normal);
        Rezepte.loadMachineRecipes(oreWashing, Recipes.oreWashing, MachineType.OreWashingPlant);
        Rezepte.loadMachineRecipes(centrifuge, Recipes.centrifuge, MachineType.ThermalCentrifuge);
        IC2.log.debug(LogCategory.Recipe, "%d recipes failed to load in the first pass.", pendingRecipes.size());
    }

    public static void loadFailedRecipes() {
        PendingRecipe recipe;
        while ((recipe = pendingRecipes.poll()) != null) {
            if (recipe.isCraftingRecipe) {
                Rezepte.loadCraftingRecipe(recipe.value, recipe.shaped, true, recipe.inputOptionIndex);
                continue;
            }
            Rezepte.loadMachineRecipe(recipe.value, recipe.manager, recipe.machineType, true);
        }
    }

    private static void loadCraftingRecipes(Config config, boolean shaped) throws Config.ParseException {
        int amount = 0;
        int successful = 0;
        Iterator<Config.Value> it = config.valueIterator();
        while (it.hasNext()) {
            Config.Value value = it.next();
            if (Rezepte.loadCraftingRecipe(value, shaped, false, -1)) {
                ++successful;
            }
            ++amount;
        }
        IC2.log.log(LogCategory.Recipe, Level.INFO, "Successfully loaded " + successful + " out of " + amount + " recipes for " + config.name);
    }

    private static boolean loadCraftingRecipe(Config.Value value, boolean shaped, boolean lastAttempt, int inputOptionIndex) {
        ItemStack output;
        try {
            output = ConfigUtil.asStack(value.name);
        }
        catch (ParseException e) {
            throw new Config.ParseException("invalid key", value, e);
        }
        if (output == null) {
            if (lastAttempt) {
                IC2.log.warn(LogCategory.Recipe, new Config.ParseException("invalid output specified: " + value.name, value), "Skipping recipe for %s due to unresolvable output.", value.name);
            } else {
                pendingRecipes.add(new PendingRecipe(value, shaped, -1));
            }
            return false;
        }
        for (IRecipeInput disable : disabledRecipeOutputs) {
            if (!disable.matches(output)) continue;
            return true;
        }
        boolean requireIc2Circuits = ConfigUtil.getBool(MainConfig.get(), "recipes/requireIc2Circuits");
        try {
            String[] inputOptions = value.getString().split("\\s*,\\s*");
            block6: for (int i = 0; i < inputOptions.length; ++i) {
                if (inputOptionIndex != -1 && i != inputOptionIndex) continue;
                String inputOption = inputOptions[i];
                boolean isShapeSpec = shaped;
                ArrayList<Object> inputs = new ArrayList<Object>();
                int count = 1;
                boolean hidden = false;
                for (String part : Rezepte.splitWhitespace(inputOption)) {
                    if (part.startsWith("@")) {
                        if (part.startsWith("@count:")) {
                            count = Integer.parseInt(part.substring(7));
                            continue;
                        }
                        if (part.equals("@hidden")) {
                            hidden = true;
                            continue;
                        }
                        throw new Config.ParseException("invalid attribute: " + part, value);
                    }
                    if (isShapeSpec) {
                        isShapeSpec = false;
                        if (part.startsWith("\"")) {
                            if (!part.endsWith("\"")) {
                                throw new Config.ParseException("missing end quote: " + part, value);
                            }
                            part = part.substring(1, part.length() - 1);
                        }
                        String[] rows = part.split("\\|");
                        Integer width = null;
                        for (String row : rows) {
                            if (width != null && width.intValue() != row.length()) {
                                throw new Config.ParseException("inconsistent recipe row width", value);
                            }
                            width = row.length();
                        }
                        inputs.addAll(Arrays.asList(rows));
                        continue;
                    }
                    ArrayList<IRecipeInput> input = new ArrayList<IRecipeInput>();
                    boolean isPatternIndex = shaped;
                    String[] stringArray = part.split("\\s*\\|\\s*");
                    int n = stringArray.length;
                    for (int j = 0; j < n; ++j) {
                        IRecipeInput cInput;
                        String subPart;
                        String ingredient = subPart = stringArray[j];
                        if (isPatternIndex) {
                            isPatternIndex = false;
                            int pos = ingredient.indexOf(":");
                            if (pos != 1) {
                                throw new Config.ParseException("no valid pattern index character found: " + part, value);
                            }
                            inputs.add(Character.valueOf(ingredient.charAt(0)));
                            ingredient = ingredient.substring(2);
                        }
                        if ((cInput = ConfigUtil.asRecipeInput(ingredient)) == null) {
                            if (lastAttempt) {
                                IC2.log.warn(LogCategory.Recipe, new Config.ParseException("invalid ingredient specified: " + value.name, value), "Skipping recipe for %s due to unresolvable input.", value.name);
                                continue block6;
                            }
                            pendingRecipes.add(new PendingRecipe(value, shaped, i));
                            continue block6;
                        }
                        if (cInput instanceof RecipeInputOreDict) {
                            RecipeInputOreDict odInput = (RecipeInputOreDict)cInput;
                            if (odInput.input.equals("circuitBasic") && requireIc2Circuits) {
                                cInput = new RecipeInputItemStack(Ic2Items.electronicCircuit);
                            } else if (odInput.input.equals("circuitAdvanced") && requireIc2Circuits) {
                                cInput = new RecipeInputItemStack(Ic2Items.advancedCircuit);
                            }
                        }
                        input.add(cInput);
                    }
                    if (input.size() == 1) {
                        inputs.add(input.get(0));
                        continue;
                    }
                    inputs.add(input);
                }
                output = StackUtil.copyWithSize(output, count);
                if (hidden) {
                    inputs.add(hidden);
                }
                if (shaped) {
                    AdvRecipe.addAndRegister(output, inputs.toArray());
                } else {
                    AdvShapelessRecipe.addAndRegister(output, inputs.toArray());
                }
                return true;
            }
        }
        catch (Config.ParseException e) {
            throw e;
        }
        catch (Exception e) {
            throw new Config.ParseException("generic parse error", value, e);
        }
        return false;
    }

    private static void loadMachineRecipes(Config config, IMachineRecipeManager machine, MachineType type) {
        int amount = 0;
        int successful = 0;
        Iterator<Config.Value> it = config.valueIterator();
        while (it.hasNext()) {
            Config.Value value = it.next();
            if (Rezepte.loadMachineRecipe(value, machine, type, false)) {
                ++successful;
            }
            ++amount;
        }
        IC2.log.log(LogCategory.Recipe, Level.INFO, "Successfully loaded " + successful + " out of " + amount + " recipes for " + config.name);
    }

    private static boolean loadMachineRecipe(Config.Value value, IMachineRecipeManager machine, MachineType type, boolean lastAttempt) {
        IRecipeInput input;
        ArrayList<ItemStack> outputs = new ArrayList<ItemStack>();
        NBTTagCompound metadata = new NBTTagCompound();
        try {
            input = ConfigUtil.asRecipeInputWithAmount(value.name);
        }
        catch (ParseException e) {
            throw new Config.ParseException("invalid key", value, e);
        }
        if (input == null) {
            if (lastAttempt) {
                IC2.log.warn(LogCategory.Recipe, new Config.ParseException("invalid input specified: " + value.name, value), "Skipping recipe due to unresolvable input %s.", value.name);
            } else {
                pendingRecipes.add(new PendingRecipe(value, machine, type));
            }
            return false;
        }
        try {
            for (String part : Rezepte.splitWhitespace(value.getString())) {
                if (part.startsWith("@")) {
                    if (part.startsWith("@ignoreSameInputOutput")) {
                        metadata.setBoolean("ignoreSameInputOutput", true);
                        continue;
                    }
                    if (part.startsWith("@hardness:") && type == MachineType.BlockCutter) {
                        metadata.setInteger("hardness", Integer.parseInt(part.substring(10)));
                        continue;
                    }
                    if (part.startsWith("@heat:") && type == MachineType.ThermalCentrifuge) {
                        metadata.setInteger("minHeat", Integer.parseInt(part.substring(6)));
                        continue;
                    }
                    if (part.startsWith("@fluid:") && type == MachineType.OreWashingPlant) {
                        metadata.setInteger("amount", Integer.parseInt(part.substring(7)));
                        continue;
                    }
                    throw new Config.ParseException("invalid attribute: " + part, value);
                }
                ItemStack cOutput = ConfigUtil.asStackWithAmount(part);
                if (cOutput == null) {
                    if (lastAttempt) {
                        IC2.log.warn(LogCategory.Recipe, new Config.ParseException("invalid output specified: " + value.name, value), "Skipping recipe using %s due to unresolvable output %s.", value.name, part);
                    } else {
                        pendingRecipes.add(new PendingRecipe(value, machine, type));
                    }
                    return false;
                }
                outputs.add(cOutput);
            }
            if (type.tagRequired != null && (metadata.hasNoTags() || !metadata.hasKey(type.tagRequired))) {
                IC2.log.warn(LogCategory.Recipe, "Could not add machine recipe: " + value + " missing tag.");
                return false;
            }
            if (metadata.hasNoTags()) {
                metadata = null;
            }
            try {
                machine.addRecipe(input, metadata, outputs.toArray(new ItemStack[outputs.size()]));
                return true;
            }
            catch (Exception e) {
                IC2.log.warn(LogCategory.Recipe, e, "Failed to add recipe. Ignoring");
            }
        }
        catch (Config.ParseException e) {
            throw e;
        }
        catch (Exception e) {
            throw new Config.ParseException("generic parse error", value, e);
        }
        return false;
    }

    private static List<String> splitWhitespace(String str) {
        String dummy = str.replaceAll("\\\\.", "xx");
        ArrayList<String> ret = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < str.length(); ++i) {
            char c = dummy.charAt(i);
            if (c == '\"') {
                quoted = !quoted;
            }
            boolean split = false;
            if (!quoted && Character.isWhitespace(c)) {
                split = true;
            }
            if (split) {
                if (current.length() <= 0) continue;
                ret.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(str.charAt(i));
        }
        if (current.length() > 0) {
            ret.add(current.toString());
        }
        return ret;
    }

    public static InputStream getConfigFile(String name) throws FileNotFoundException {
        File file = new File(IC2.platform.getMinecraftDir(), "config/ic2/" + name + ".ini");
        if (file.canRead() && file.isFile()) {
            return new FileInputStream(file);
        }
        return Rezepte.class.getResourceAsStream("/assets/ic2/config/" + name + ".ini");
    }

    static {
        pendingRecipes = new ArrayDeque<PendingRecipe>();
    }

    private static class PendingRecipe {
        final Config.Value value;
        final boolean isCraftingRecipe;
        final boolean shaped;
        final int inputOptionIndex;
        final IMachineRecipeManager manager;
        final MachineType machineType;

        public PendingRecipe(Config.Value value, boolean shaped, int inputOptionIndex) {
            this.value = value;
            this.isCraftingRecipe = true;
            this.shaped = shaped;
            this.inputOptionIndex = inputOptionIndex;
            this.manager = null;
            this.machineType = null;
        }

        public PendingRecipe(Config.Value value, IMachineRecipeManager manager, MachineType machineType) {
            this.value = value;
            this.isCraftingRecipe = false;
            this.shaped = false;
            this.inputOptionIndex = -1;
            this.manager = manager;
            this.machineType = machineType;
        }
    }

    private static enum MachineType {
        Normal(null),
        BlockCutter("hardness"),
        ThermalCentrifuge("minHeat"),
        OreWashingPlant("amount");

        private String tagRequired;

        private MachineType(String tagRequired) {
            this.tagRequired = tagRequired;
        }
    }
}

