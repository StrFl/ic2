/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.api.recipe;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IMachineRecipeManager {
    public void addRecipe(IRecipeInput var1, NBTTagCompound var2, ItemStack ... var3);

    public RecipeOutput getOutputFor(ItemStack var1, boolean var2);

    public Map<IRecipeInput, RecipeOutput> getRecipes();
}

