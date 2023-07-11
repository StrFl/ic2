/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.oredict.OreDictionary$OreRegisterEvent
 */
package ic2.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.api.recipe.IMachineRecipeManagerExt;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.RecipeOutput;
import ic2.core.IC2;
import ic2.core.init.MainConfig;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Tuple;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

public class BasicMachineRecipeManager
implements IMachineRecipeManagerExt {
    private final Map<IRecipeInput, RecipeOutput> recipes = new HashMap<IRecipeInput, RecipeOutput>();
    private final Map<Item, Map<Integer, Tuple.T2<IRecipeInput, RecipeOutput>>> recipeCache = new IdentityHashMap<Item, Map<Integer, Tuple.T2<IRecipeInput, RecipeOutput>>>();
    private final List<Tuple.T2<IRecipeInput, RecipeOutput>> uncacheableRecipes = new ArrayList<Tuple.T2<IRecipeInput, RecipeOutput>>();
    private boolean oreRegisterEventSubscribed;

    @Override
    public void addRecipe(IRecipeInput input, NBTTagCompound metadata, ItemStack ... outputs) {
        if (!this.addRecipe(input, metadata, false, outputs)) {
            this.displayError("ambiguous recipe: [" + input.getInputs() + " -> " + Arrays.asList(outputs) + "]");
        }
    }

    @Override
    public boolean addRecipe(IRecipeInput input, NBTTagCompound metadata, boolean overwrite, ItemStack ... outputs) {
        return this.addRecipe(input, new RecipeOutput(metadata, outputs), overwrite);
    }

    @Override
    public RecipeOutput getOutputFor(ItemStack input, boolean adjustInput) {
        if (input == null) {
            return null;
        }
        Tuple.T2<IRecipeInput, RecipeOutput> data = this.getRecipe(input);
        if (data == null) {
            return null;
        }
        if (!(input.stackSize < ((IRecipeInput)data.a).getAmount() || input.getItem().hasContainerItem(input) && input.stackSize != ((IRecipeInput)data.a).getAmount())) {
            if (adjustInput) {
                if (input.getItem().hasContainerItem(input)) {
                    ItemStack container = input.getItem().getContainerItem(input);
                    input.func_150996_a(container.getItem());
                    input.stackSize = container.stackSize;
                    input.setItemDamage(container.getItemDamage());
                    input.stackTagCompound = container.stackTagCompound;
                } else {
                    input.stackSize -= ((IRecipeInput)data.a).getAmount();
                }
            }
            return (RecipeOutput)data.b;
        }
        return null;
    }

    @Override
    public Map<IRecipeInput, RecipeOutput> getRecipes() {
        return new AbstractMap<IRecipeInput, RecipeOutput>(){

            @Override
            public Set<Map.Entry<IRecipeInput, RecipeOutput>> entrySet() {
                return new AbstractSet<Map.Entry<IRecipeInput, RecipeOutput>>(){

                    @Override
                    public Iterator<Map.Entry<IRecipeInput, RecipeOutput>> iterator() {
                        return new Iterator<Map.Entry<IRecipeInput, RecipeOutput>>(){
                            private final Iterator<Map.Entry<IRecipeInput, RecipeOutput>> recipeIt;
                            private IRecipeInput lastInput;
                            {
                                this.recipeIt = BasicMachineRecipeManager.this.recipes.entrySet().iterator();
                            }

                            @Override
                            public boolean hasNext() {
                                return this.recipeIt.hasNext();
                            }

                            @Override
                            public Map.Entry<IRecipeInput, RecipeOutput> next() {
                                Map.Entry<IRecipeInput, RecipeOutput> ret = this.recipeIt.next();
                                this.lastInput = ret.getKey();
                                return ret;
                            }

                            @Override
                            public void remove() {
                                this.recipeIt.remove();
                                BasicMachineRecipeManager.this.removeCachedRecipes(this.lastInput);
                            }
                        };
                    }

                    @Override
                    public int size() {
                        return BasicMachineRecipeManager.this.recipes.size();
                    }
                };
            }

            @Override
            public RecipeOutput put(IRecipeInput key, RecipeOutput value) {
                BasicMachineRecipeManager.this.addRecipe(key, value, true);
                return null;
            }
        };
    }

    @SubscribeEvent
    public void onOreRegister(OreDictionary.OreRegisterEvent event) {
        ArrayList<Tuple.T2<IRecipeInput, RecipeOutput>> datas = new ArrayList<Tuple.T2<IRecipeInput, RecipeOutput>>();
        for (Map.Entry<IRecipeInput, RecipeOutput> entry : this.recipes.entrySet()) {
            if (entry.getKey().getClass() != RecipeInputOreDict.class) continue;
            RecipeInputOreDict recipe = (RecipeInputOreDict)entry.getKey();
            if (!recipe.input.equals(event.Name)) continue;
            datas.add(new Tuple.T2<IRecipeInput, RecipeOutput>(entry.getKey(), entry.getValue()));
        }
        for (Tuple.T2 t2 : datas) {
            this.addToCache(event.Ore, t2);
        }
    }

    private Tuple.T2<IRecipeInput, RecipeOutput> getRecipe(ItemStack input) {
        Map<Integer, Tuple.T2<IRecipeInput, RecipeOutput>> metaMap = this.recipeCache.get(input.getItem());
        if (metaMap != null) {
            Tuple.T2<IRecipeInput, RecipeOutput> data = metaMap.get(Short.MAX_VALUE);
            if (data != null) {
                return data;
            }
            int meta = input.getItemDamage();
            data = metaMap.get(meta);
            if (data != null) {
                return data;
            }
        }
        for (Tuple.T2<IRecipeInput, RecipeOutput> data : this.uncacheableRecipes) {
            if (!((IRecipeInput)data.a).matches(input)) continue;
            return data;
        }
        return null;
    }

    private boolean addRecipe(IRecipeInput input, RecipeOutput output, boolean overwrite) {
        if (input == null) {
            this.displayError("The recipe input is null");
            return false;
        }
        ListIterator<ItemStack> it = output.items.listIterator();
        while (it.hasNext()) {
            ItemStack stack = it.next();
            if (stack == null) {
                this.displayError("An output ItemStack is null.");
                return false;
            }
            if (!StackUtil.check(stack)) {
                this.displayError("The output ItemStack " + StackUtil.toStringSafe(stack) + " is invalid.");
                return false;
            }
            if (input.matches(stack) && (output.metadata == null || !output.metadata.hasKey("ignoreSameInputOutput"))) {
                this.displayError("The output ItemStack " + stack.toString() + " is the same as the recipe input " + input + ".");
                return false;
            }
            it.set(stack.copy());
        }
        for (ItemStack is : input.getInputs()) {
            Tuple.T2<IRecipeInput, RecipeOutput> data = this.getRecipe(is);
            if (data == null) continue;
            if (overwrite) {
                do {
                    this.recipes.remove(data.a);
                    this.removeCachedRecipes((IRecipeInput)data.a);
                } while ((data = this.getRecipe(is)) != null);
                continue;
            }
            return false;
        }
        this.recipes.put(input, output);
        this.addToCache(input, output);
        return true;
    }

    private void addToCache(IRecipeInput input, RecipeOutput output) {
        Tuple.T2<IRecipeInput, RecipeOutput> data = new Tuple.T2<IRecipeInput, RecipeOutput>(input, output);
        List<ItemStack> stacks = this.getStacksFromRecipe(input);
        if (stacks != null) {
            for (ItemStack stack : stacks) {
                this.addToCache(stack, data);
            }
            if (input.getClass() == RecipeInputOreDict.class && !this.oreRegisterEventSubscribed) {
                MinecraftForge.EVENT_BUS.register((Object)this);
                this.oreRegisterEventSubscribed = true;
            }
        } else {
            this.uncacheableRecipes.add(data);
        }
    }

    private void addToCache(ItemStack stack, Tuple.T2<IRecipeInput, RecipeOutput> data) {
        Item item = stack.getItem();
        Map<Integer, Tuple.T2<IRecipeInput, RecipeOutput>> metaMap = this.recipeCache.get(item);
        if (metaMap == null) {
            metaMap = new HashMap<Integer, Tuple.T2<IRecipeInput, RecipeOutput>>();
            this.recipeCache.put(item, metaMap);
        }
        int meta = stack.getItemDamage();
        metaMap.put(meta, data);
    }

    private void removeCachedRecipes(IRecipeInput input) {
        List<ItemStack> stacks = this.getStacksFromRecipe(input);
        if (stacks != null) {
            for (ItemStack stack : stacks) {
                Item item = stack.getItem();
                int meta = stack.getItemDamage();
                Map<Integer, Tuple.T2<IRecipeInput, RecipeOutput>> map = this.recipeCache.get(item);
                if (map == null) {
                    IC2.log.warn(LogCategory.Recipe, "Inconsistent recipe cache, the entry for the item " + item + "(" + stack + ") is missing.");
                    continue;
                }
                map.remove(meta);
                if (!map.isEmpty()) continue;
                this.recipeCache.remove(item);
            }
        } else {
            Iterator<Tuple.T2<IRecipeInput, RecipeOutput>> it = this.uncacheableRecipes.iterator();
            while (it.hasNext()) {
                Tuple.T2<IRecipeInput, RecipeOutput> data = it.next();
                if (data.a != input) continue;
                it.remove();
            }
        }
    }

    private List<ItemStack> getStacksFromRecipe(IRecipeInput recipe) {
        if (recipe.getClass() == RecipeInputItemStack.class) {
            return recipe.getInputs();
        }
        if (recipe.getClass() == RecipeInputOreDict.class) {
            Integer meta = ((RecipeInputOreDict)recipe).meta;
            if (meta == null) {
                return recipe.getInputs();
            }
            ArrayList<ItemStack> ret = new ArrayList<ItemStack>(recipe.getInputs());
            ListIterator<ItemStack> it = ret.listIterator();
            while (it.hasNext()) {
                ItemStack stack = (ItemStack)it.next();
                if (stack.getItemDamage() == meta.intValue()) continue;
                stack = stack.copy();
                stack.setItemDamage(meta.intValue());
                it.set(stack);
            }
            return ret;
        }
        return null;
    }

    private void displayError(String msg) {
        if (!MainConfig.ignoreInvalidRecipes) {
            throw new RuntimeException(msg);
        }
        IC2.log.warn(LogCategory.Recipe, msg);
    }
}

