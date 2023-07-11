/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 */
package ic2.core.init;

import ic2.core.Ic2Items;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ScannerRecipes {
    public static void AddScannerRecipes() {
        ScannerRecipes.addRecipe(new ItemStack(Blocks.dirt), 30, 128);
        ScannerRecipes.addRecipe(new ItemStack(Blocks.cobblestone), 4, 128);
        ScannerRecipes.addRecipe(new ItemStack(Blocks.gravel), 35, 128);
        ScannerRecipes.addRecipe(new ItemStack((Block)Blocks.sand), 5, 800);
        ScannerRecipes.addRecipe(new ItemStack(Blocks.stone), 5, 1180);
        ScannerRecipes.addRecipe(new ItemStack(Blocks.glass), 5, 1180);
        ScannerRecipes.addRecipe(new ItemStack(Items.coal), 284, 8000);
        ScannerRecipes.addRecipe(Ic2Items.coalDust, 295, 9000);
        ScannerRecipes.addRecipe(new ItemStack(Items.diamond), 10121, 560000);
        ScannerRecipes.addRecipe(new ItemStack(Items.dye, 1, 4), 2432, 35000);
        ScannerRecipes.addRecipe(new ItemStack(Items.redstone), 427, 4000);
        ScannerRecipes.addRecipe(new ItemStack(Blocks.gold_ore), 5481, 145000);
        ScannerRecipes.addRecipe(new ItemStack(Blocks.iron_ore), 551, 150000);
        ScannerRecipes.addRecipe(Ic2Items.copperOre, 349, 12000);
        ScannerRecipes.addRecipe(Ic2Items.tinOre, 415, 15000);
        ScannerRecipes.addRecipe(Ic2Items.leadOre, 2600, 230000);
        ScannerRecipes.addRecipe(Ic2Items.uraniumOre, 6775, 200000);
        ScannerRecipes.addRecipe(Ic2Items.iridiumOre, 22000, 1000000);
        ScannerRecipes.addRecipe(Ic2Items.resin, 200, 10000);
        ScannerRecipes.addRecipe(new ItemStack(Items.quartz), 35, 40000);
        ScannerRecipes.addRecipe(new ItemStack(Items.clay_ball), 50, 1000);
    }

    public static void addRecipe(ItemStack output, int recordedAmountUU, int recordedAmountEU) {
    }
}

