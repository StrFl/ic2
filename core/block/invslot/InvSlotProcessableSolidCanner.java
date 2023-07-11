/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.tileentity.TileEntitySolidCanner;
import ic2.core.util.StackUtil;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class InvSlotProcessableSolidCanner
extends InvSlotProcessableGeneric {
    public InvSlotProcessableSolidCanner(TileEntitySolidCanner base1, String name1, int oldStartIndex1, int count) {
        super((TileEntityInventory)base1, name1, oldStartIndex1, count, null);
    }

    @Override
    public void consume() {
        super.consume();
        ItemStack containerStack = ((TileEntitySolidCanner)this.base).canInputSlot.get();
        if (containerStack != null && containerStack.stackSize <= 0) {
            ((TileEntitySolidCanner)this.base).canInputSlot.put(null);
        }
    }

    @Override
    protected RecipeOutput getOutputFor(ItemStack input, boolean adjustInput, boolean forAccept) {
        return this.getOutput(((TileEntitySolidCanner)this.base).canInputSlot.get(), input, adjustInput, forAccept);
    }

    @Override
    protected boolean allowEmptyInput() {
        return true;
    }

    protected RecipeOutput getOutput(ItemStack container, ItemStack fill, boolean adjustInput, boolean forAccept) {
        RecipeOutput output = Recipes.cannerBottle.getOutputFor(container, fill, adjustInput, forAccept);
        if (output == null) {
            if (forAccept ? container == null && fill == null : container == null || fill == null) {
                return null;
            }
            if (forAccept && container == null) {
                if (fill.getItem() instanceof ItemFood) {
                    ItemStack ret = Ic2Items.filledTinCan.copy();
                    ret.stackSize = (((ItemFood)fill.getItem()).func_150905_g(fill) + 1) / 2;
                    return new RecipeOutput(null, ret);
                }
            } else if (forAccept && fill == null) {
                if (StackUtil.isStackEqual(container, Ic2Items.tinCan)) {
                    return new RecipeOutput(null, Ic2Items.filledTinCan.copy());
                }
            } else if (fill.getItem() instanceof ItemFood && StackUtil.isStackEqual(container, Ic2Items.tinCan)) {
                ItemStack ret = StackUtil.copyWithSize(Ic2Items.filledTinCan, ((ItemFood)fill.getItem()).func_150905_g(fill));
                RecipeOutput tmp = new RecipeOutput(null, ret);
                if (forAccept || container != null && container.stackSize >= ret.stackSize && fill.stackSize >= 1) {
                    if (adjustInput) {
                        if (container != null) {
                            container.stackSize -= ret.stackSize;
                        }
                        --fill.stackSize;
                    }
                    return tmp;
                }
            }
        }
        return output;
    }
}

