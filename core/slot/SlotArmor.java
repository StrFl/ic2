/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 */
package ic2.core.slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class SlotArmor
extends Slot {
    private final int armorType;

    public SlotArmor(InventoryPlayer inventory1, int armorType1, int xDisplayPosition1, int yDisplayPosition1) {
        super((IInventory)inventory1, 36 + (3 - armorType1), xDisplayPosition1, yDisplayPosition1);
        this.armorType = armorType1;
    }

    public boolean isItemValid(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item == null) {
            return false;
        }
        return item.isValidArmor(itemStack, this.armorType, (Entity)((InventoryPlayer)this.inventory).player);
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getBackgroundIconIndex() {
        return ItemArmor.func_94602_b((int)this.armorType);
    }
}

