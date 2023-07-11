/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.core.item.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorFluidTank;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ItemArmorCFPack
extends ItemArmorFluidTank {
    public ItemArmorCFPack(InternalName internalName) {
        super(internalName, InternalName.batpack, BlocksItems.getFluid(InternalName.fluidConstructionFoam), 80000);
    }

    public FluidStack drainfromCFpack(EntityPlayer player, ItemStack pack, int amount) {
        if (this.isEmpty(pack)) {
            return null;
        }
        if (this.drain((ItemStack)pack, (int)amount, (boolean)false).amount < amount) {
            return null;
        }
        FluidStack fluid = this.drain(pack, amount, true);
        this.Updatedamage(pack);
        player.inventoryContainer.detectAndSendChanges();
        return fluid;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
        ItemStack itemStack = new ItemStack(Ic2Items.cfPack.getItem(), 1);
        this.filltank(itemStack);
        itemStack.setItemDamage(1);
        itemList.add(itemStack);
        itemStack = new ItemStack(Ic2Items.cfPack.getItem(), 1);
        itemStack.setItemDamage(this.getMaxDamage());
        itemList.add(itemStack);
    }
}

