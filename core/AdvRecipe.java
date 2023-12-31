/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.InventoryCrafting
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.FluidContainerRegistry
 *  net.minecraftforge.fluids.FluidContainerRegistry$FluidContainerData
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.IFluidContainerItem
 *  net.minecraftforge.oredict.OreDictionary
 *  org.apache.commons.lang3.StringUtils
 */
package ic2.core;

import ic2.api.item.ElectricItem;
import ic2.api.recipe.IRecipeInput;
import ic2.core.IC2;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

public class AdvRecipe
implements IRecipe {
    private static final boolean debug = Util.hasAssertions();
    public final ItemStack output;
    public final Object[] input;
    public final Object[] inputMirrored;
    public final int[] masks;
    public final int[] masksMirrored;
    public final int inputWidth;
    public final int inputHeight;
    public final boolean hidden;

    public static void addAndRegister(ItemStack result, Object ... args) {
        block2: {
            try {
                CraftingManager.getInstance().getRecipeList().add(new AdvRecipe(result, args));
            }
            catch (RuntimeException e) {
                if (MainConfig.ignoreInvalidRecipes) break block2;
                throw e;
            }
        }
    }

    public AdvRecipe(ItemStack result, Object ... args) {
        if (result == null) {
            AdvRecipe.displayError("null result", null, null, false);
        }
        HashMap<Character, Object> charMapping = new HashMap<Character, Object>();
        ArrayList<String> inputArrangement = new ArrayList<String>();
        Character lastChar = null;
        boolean isHidden = false;
        for (Object arg : args) {
            if (arg instanceof String) {
                if (lastChar == null) {
                    String str;
                    if (!charMapping.isEmpty()) {
                        AdvRecipe.displayError("oredict name without preceding char", "Name: " + arg, result, false);
                    }
                    if ((str = (String)arg).isEmpty() || str.length() > 3) {
                        AdvRecipe.displayError("none or too many crafting columns", "Input: " + str + "\nSize: " + str.length(), result, false);
                    }
                    inputArrangement.add(str);
                    continue;
                }
                charMapping.put(lastChar, arg);
                lastChar = null;
                continue;
            }
            if (arg instanceof Character) {
                if (lastChar != null) {
                    AdvRecipe.displayError("two consecutive char definitions", "Input: " + arg + "\nprev. Input: " + lastChar, result, false);
                }
                lastChar = (Character)arg;
                continue;
            }
            if (arg instanceof IRecipeInput || arg instanceof ItemStack || arg instanceof Block || arg instanceof Item || arg.getClass().isArray() || arg instanceof Iterable) {
                if (lastChar == null) {
                    AdvRecipe.displayError("item without preceding char", "Input: " + arg + "\nType: " + (arg == null ? "null" : arg.getClass().getName()), result, false);
                }
                if (arg instanceof Block) {
                    arg = new ItemStack((Block)arg, 1);
                } else if (arg instanceof Item) {
                    arg = new ItemStack((Item)arg, 1);
                }
                AdvRecipe.expand(arg);
                charMapping.put(lastChar, arg);
                lastChar = null;
                continue;
            }
            if (arg instanceof Boolean) {
                isHidden = (Boolean)arg;
                continue;
            }
            AdvRecipe.displayError("unknown type", "Input: " + arg + "\nType: " + arg.getClass().getName(), result, false);
        }
        this.hidden = isHidden;
        this.inputHeight = inputArrangement.size();
        if (lastChar != null) {
            AdvRecipe.displayError("one or more unused mapping chars", "Letter: " + lastChar, result, false);
        }
        if (this.inputHeight == 0 || this.inputHeight > 3) {
            AdvRecipe.displayError("none or too many crafting rows", "Size: " + inputArrangement.size(), result, false);
        }
        if (charMapping.size() == 0) {
            AdvRecipe.displayError("no mapping chars", null, result, false);
        }
        this.inputWidth = ((String)inputArrangement.get(0)).length();
        if (debug) {
            if (StringUtils.containsOnly((CharSequence)((CharSequence)inputArrangement.get(0)), (char[])new char[]{' '})) {
                IC2.log.warn(LogCategory.Recipe, "Leading empty row in shaped recipe for %s, from %s.", result, AdvRecipe.getCaller());
            }
            if (StringUtils.containsOnly((CharSequence)((CharSequence)inputArrangement.get(this.inputHeight - 1)), (char[])new char[]{' '})) {
                IC2.log.warn(LogCategory.Recipe, "Trailing empty row in shaped recipe for %s, from %s.", result, AdvRecipe.getCaller());
            }
            for (int pass = 0; pass < 2; ++pass) {
                boolean found = true;
                for (int y = 0; y < this.inputHeight; ++y) {
                    String str = (String)inputArrangement.get(y);
                    if ((pass != 0 || str.charAt(0) == ' ') && (pass != 1 || str.charAt(this.inputWidth - 1) == ' ')) continue;
                    found = false;
                    break;
                }
                if (!found) continue;
                if (pass == 0) {
                    IC2.log.warn(LogCategory.Recipe, "Leading empty column in shaped recipe for %s, from %s.", result, AdvRecipe.getCaller());
                    continue;
                }
                IC2.log.warn(LogCategory.Recipe, "Trailing empty column in shaped recipe for %s, from %s.", result, AdvRecipe.getCaller());
            }
        }
        int xMasks = -this.inputWidth + 4;
        int yMasks = -this.inputHeight + 4;
        int mask = 0;
        ArrayList inputs = new ArrayList();
        for (int y = 0; y < 3; ++y) {
            Object str = null;
            if (y < this.inputHeight && ((String)(str = (String)inputArrangement.get(y))).length() != this.inputWidth) {
                AdvRecipe.displayError("no fixed width", "Expected: " + this.inputWidth + "\nGot: " + ((String)str).length(), result, false);
            }
            for (int x = 0; x < 3; ++x) {
                char c;
                mask <<= 1;
                if (x >= this.inputWidth || str == null || (c = ((String)str).charAt(x)) == ' ') continue;
                if (!charMapping.containsKey(Character.valueOf(c))) {
                    AdvRecipe.displayError("missing char mapping", "Letter: " + c, result, false);
                }
                inputs.add(charMapping.get(Character.valueOf(c)));
                mask |= 1;
            }
        }
        this.input = inputs.toArray();
        boolean mirror = false;
        if (this.inputWidth != 1) {
            for (String s : inputArrangement) {
                if (s.charAt(0) == s.charAt(this.inputWidth - 1)) continue;
                mirror = true;
                break;
            }
        }
        if (!mirror) {
            this.inputMirrored = null;
        } else {
            Object[] tmp = new Object[9];
            int j = 0;
            for (int i = 0; i < 9; ++i) {
                if ((mask & 1 << 8 - i) == 0) continue;
                tmp[i] = this.input[j];
                ++j;
            }
            Object old = tmp[0];
            tmp[0] = tmp[2];
            tmp[2] = old;
            old = tmp[3];
            tmp[3] = tmp[5];
            tmp[5] = old;
            old = tmp[6];
            tmp[6] = tmp[8];
            tmp[8] = old;
            this.inputMirrored = new Object[this.input.length];
            int j2 = 0;
            for (int i = 0; i < 9; ++i) {
                if (tmp[i] == null) continue;
                this.inputMirrored[j2] = tmp[i];
                ++j2;
            }
        }
        this.masks = new int[xMasks * yMasks];
        this.masksMirrored = (int[])(!mirror ? null : new int[this.masks.length]);
        for (int y = 0; y < yMasks; ++y) {
            int yMask = mask >>> y * 3;
            for (int x = 0; x < xMasks; ++x) {
                int xyMask;
                this.masks[x + y * xMasks] = xyMask = yMask >>> x;
                if (!mirror) continue;
                this.masksMirrored[x + y * xMasks] = xyMask << 2 & 0x124 | xyMask & 0x92 | xyMask >>> 2 & 0x49;
            }
        }
        this.output = result;
    }

    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        return this.getCraftingResult(inventorycrafting) != null;
    }

    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
        ItemStack ret;
        int size = inventorycrafting.getSizeInventory();
        int mask = 0;
        for (int i = 0; i < size; ++i) {
            mask <<= 1;
            if (inventorycrafting.getStackInSlot(i) == null) continue;
            mask |= 1;
        }
        if (size == 4) {
            mask = (mask & 0xC) << 5 | (mask & 3) << 4;
        }
        if (AdvRecipe.checkMask(mask, this.masks) && (ret = this.checkItems((IInventory)inventorycrafting, this.input)) != null) {
            return ret;
        }
        if (this.masksMirrored != null && AdvRecipe.checkMask(mask, this.masksMirrored) && (ret = this.checkItems((IInventory)inventorycrafting, this.inputMirrored)) != null) {
            return ret;
        }
        return null;
    }

    public int getRecipeSize() {
        return this.input.length;
    }

    public ItemStack getRecipeOutput() {
        return this.output;
    }

    public static boolean recipeContains(Object[] inputs, ItemStack item) {
        for (Object input : inputs) {
            if (input == null) continue;
            List<ItemStack> realInputs = AdvRecipe.expand(input);
            for (ItemStack realInput : realInputs) {
                if (!item.isItemEqual(realInput)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean canShow(Object[] input, ItemStack output, boolean hidden) {
        return !hidden || !ConfigUtil.getBool(MainConfig.get(), "misc/hideSecretRecipes");
    }

    public boolean canShow() {
        return AdvRecipe.canShow(this.input, this.output, this.hidden);
    }

    public static List<ItemStack> expand(Object o) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        if (o instanceof IRecipeInput) {
            ret.addAll(((IRecipeInput)o).getInputs());
        } else if (o instanceof String) {
            String s = (String)o;
            if (s.startsWith("liquid$")) {
                String name = s.substring(7);
                for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
                    String fluidName = FluidRegistry.getFluidName((int)data.fluid.fluidID);
                    if (fluidName == null || !StackUtil.check(data.filledContainer) || !name.equals(fluidName)) continue;
                    ret.add(data.filledContainer);
                }
            } else {
                for (ItemStack stack : OreDictionary.getOres((String)((String)o))) {
                    if (!StackUtil.check(stack)) continue;
                    ret.add(stack);
                }
            }
        } else if (o instanceof ItemStack) {
            if (StackUtil.check((ItemStack)o)) {
                ret.add((ItemStack)o);
            }
        } else if (o.getClass().isArray()) {
            assert (Array.getLength(o) != 0);
            for (int i = 0; i < Array.getLength(o); ++i) {
                ret.addAll(AdvRecipe.expand(Array.get(o, i)));
            }
        } else if (o instanceof Iterable) {
            assert (((Iterable)o).iterator().hasNext());
            for (Object o2 : (Iterable)o) {
                ret.addAll(AdvRecipe.expand(o2));
            }
        } else {
            AdvRecipe.displayError("unknown type", "Input: " + o + "\nType: " + o.getClass().getName(), null, false);
            return null;
        }
        return ret;
    }

    public static List<ItemStack>[] expandArray(Object[] array) {
        List[] ret = new List[array.length];
        for (int i = 0; i < array.length; ++i) {
            ret[i] = array[i] == null ? null : AdvRecipe.expand(array[i]);
        }
        return ret;
    }

    public static void displayError(String cause, String tech, ItemStack result, boolean shapeless) {
        String msg = "An invalid crafting recipe was attempted to be added. This could happen due to a bug in IndustrialCraft 2 or an addon.\n\n(Technical information: Adv" + (shapeless ? "Shapeless" : "") + "Recipe, " + cause + ")\n" + (result != null ? "Output: " + result + "\n" : "") + (tech != null ? tech + "\n" : "") + "Source: " + AdvRecipe.getCaller();
        if (MainConfig.ignoreInvalidRecipes) {
            IC2.log.warn(LogCategory.Recipe, msg);
            throw new RuntimeException(msg);
        }
        IC2.platform.displayError(msg, new Object[0]);
    }

    private static String getCaller() {
        String ret = "unknown";
        for (StackTraceElement st : Thread.currentThread().getStackTrace()) {
            String pkg;
            String className = st.getClassName();
            int pkgSeparator = className.lastIndexOf(46);
            String string = pkg = pkgSeparator == -1 ? "" : className.substring(0, pkgSeparator);
            if (pkg.equals("ic2.core") || pkg.startsWith("ic2.api") || pkg.startsWith("java.")) continue;
            ret = className + "." + st.getMethodName() + "(" + st.getFileName() + ":" + st.getLineNumber() + ")";
            break;
        }
        return ret;
    }

    private static boolean checkMask(int mask, int[] request) {
        for (int cmpMask : request) {
            if (mask != cmpMask) continue;
            return true;
        }
        return false;
    }

    private ItemStack checkItems(IInventory inventory, Object[] request) {
        int size = inventory.getSizeInventory();
        double outputCharge = 0.0;
        int j = 0;
        for (int i = 0; i < size; ++i) {
            ItemStack offer = inventory.getStackInSlot(i);
            if (offer == null) continue;
            List<ItemStack> requestedItemStacks = AdvRecipe.expand(request[j]);
            boolean found = false;
            for (ItemStack requestedItemStack : requestedItemStacks) {
                if (!offer.isItemEqual(requestedItemStack) && (requestedItemStack.getItemDamage() != Short.MAX_VALUE || offer.getItem() != requestedItemStack.getItem())) continue;
                outputCharge += ElectricItem.manager.getCharge(offer);
                found = true;
                break;
            }
            if (!found) {
                return null;
            }
            ++j;
        }
        ItemStack ret = this.output.copy();
        Item item = ret.getItem();
        ElectricItem.manager.charge(ret, outputCharge, Integer.MAX_VALUE, true, false);
        if (item instanceof IFluidContainerItem) {
            StackUtil.getOrCreateNbtData(ret);
        }
        return ret;
    }
}

