/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 */
package ic2.core.block.invslot;

import ic2.api.recipe.RecipeOutput;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class InvSlotProcessableSmelting
extends InvSlotProcessable {
    public InvSlotProcessableSmelting(TileEntityInventory base1, String name1, int oldStartIndex1, int count) {
        super(base1, name1, oldStartIndex1, count);
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        return FurnaceRecipes.smelting().getSmeltingResult(itemStack) != null;
    }

    @Override
    public RecipeOutput process() {
        ItemStack input = this.consume(1, true, true);
        if (input == null) {
            return null;
        }
        return new RecipeOutput(null, FurnaceRecipes.smelting().getSmeltingResult(input).copy());
    }

    @Override
    public void consume() {
        this.consume(1, false, true);
    }
}

