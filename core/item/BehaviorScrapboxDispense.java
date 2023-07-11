/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockDispenser
 *  net.minecraft.dispenser.BehaviorDefaultDispenseItem
 *  net.minecraft.dispenser.IBlockSource
 *  net.minecraft.dispenser.IPosition
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.api.recipe.Recipes;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorScrapboxDispense
extends BehaviorDefaultDispenseItem {
    protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
        EnumFacing facing = EnumFacing.getFront((int)blockSource.getBlockMetadata());
        IPosition position = BlockDispenser.func_149939_a((IBlockSource)blockSource);
        BehaviorScrapboxDispense.doDispense((World)blockSource.getWorld(), (ItemStack)Recipes.scrapboxDrops.getDrop(stack, true), (int)6, (EnumFacing)facing, (IPosition)position);
        return stack;
    }
}

