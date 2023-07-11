/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraftforge.fluids.FluidContainerRegistry
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.core.util;

import ic2.api.info.IEnergyValueProvider;
import ic2.api.info.IFuelValueProvider;
import ic2.core.Ic2Items;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ItemInfo
implements IEnergyValueProvider,
IFuelValueProvider {
    @Override
    public double getEnergyValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item == null) {
            return 0.0;
        }
        if (item == Ic2Items.suBattery.getItem()) {
            return 1200.0;
        }
        if (item == Items.redstone) {
            return 800.0;
        }
        if (StackUtil.isStackEqual(stack, Ic2Items.energiumDust)) {
            return 16000.0;
        }
        return 0.0;
    }

    @Override
    public int getFuelValue(ItemStack stack, boolean allowLava) {
        FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem((ItemStack)stack);
        if (liquid != null && liquid.getFluid() == FluidRegistry.LAVA) {
            if (allowLava) {
                return 2000;
            }
            return 0;
        }
        if (stack.getItem() == Ic2Items.scrap.getItem() && !ConfigUtil.getBool(MainConfig.get(), "misc/allowBurningScrap")) {
            return 0;
        }
        return TileEntityFurnace.getItemBurnTime((ItemStack)stack);
    }
}

