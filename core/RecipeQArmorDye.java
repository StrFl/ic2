/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockColored
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.InventoryCrafting
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.RecipesArmorDyes
 *  net.minecraft.world.World
 */
package ic2.core;

import ic2.core.item.armor.ItemArmorQuantumSuit;
import java.util.ArrayList;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.world.World;

public class RecipeQArmorDye
extends RecipesArmorDyes {
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World) {
        ItemStack itemstack = null;
        ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();
        for (int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i) {
            ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(i);
            if (itemstack1 == null) continue;
            if (itemstack1.getItem() instanceof ItemArmorQuantumSuit) {
                if (itemstack != null) {
                    return false;
                }
                itemstack = itemstack1;
                continue;
            }
            if (itemstack1.getItem() != Items.dye) {
                return false;
            }
            arraylist.add(itemstack1);
        }
        return itemstack != null && !arraylist.isEmpty();
    }

    public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting) {
        int i1;
        float f1;
        float f;
        int l;
        int k;
        ItemStack itemstack = null;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        ItemArmorQuantumSuit itemarmor = null;
        for (k = 0; k < par1InventoryCrafting.getSizeInventory(); ++k) {
            ItemStack itemstack1 = par1InventoryCrafting.getStackInSlot(k);
            if (itemstack1 == null) continue;
            if (itemstack1.getItem() instanceof ItemArmor) {
                itemarmor = (ItemArmorQuantumSuit)itemstack1.getItem();
                if (itemstack != null) {
                    return null;
                }
                itemstack = itemstack1.copy();
                itemstack.stackSize = 1;
                if (!itemarmor.hasColor(itemstack1)) continue;
                l = itemarmor.getColor(itemstack);
                f = (float)(l >> 16 & 0xFF) / 255.0f;
                f1 = (float)(l >> 8 & 0xFF) / 255.0f;
                float f2 = (float)(l & 0xFF) / 255.0f;
                i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0f);
                aint[0] = (int)((float)aint[0] + f * 255.0f);
                aint[1] = (int)((float)aint[1] + f1 * 255.0f);
                aint[2] = (int)((float)aint[2] + f2 * 255.0f);
                ++j;
                continue;
            }
            if (itemstack1.getItem() != Items.dye) {
                return null;
            }
            float[] afloat = EntitySheep.fleeceColorTable[BlockColored.func_150031_c((int)itemstack1.getItemDamage())];
            int j1 = (int)(afloat[0] * 255.0f);
            int k1 = (int)(afloat[1] * 255.0f);
            i1 = (int)(afloat[2] * 255.0f);
            i += Math.max(j1, Math.max(k1, i1));
            aint[0] = aint[0] + j1;
            aint[1] = aint[1] + k1;
            aint[2] = aint[2] + i1;
            ++j;
        }
        if (itemarmor == null) {
            return null;
        }
        k = aint[0] / j;
        int l1 = aint[1] / j;
        l = aint[2] / j;
        f = (float)i / (float)j;
        f1 = Math.max(k, Math.max(l1, l));
        k = (int)((float)k * f / f1);
        l1 = (int)((float)l1 * f / f1);
        l = (int)((float)l * f / f1);
        i1 = (k << 8) + l1;
        i1 = (i1 << 8) + l;
        itemarmor.colorQArmor(itemstack, i1);
        return itemstack;
    }
}

