/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.slot.SlotArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InvSlotArmor
extends SlotArmor {
    private final int armorType;

    public InvSlotArmor(InventoryPlayer inventory1, int armorType1, int xDisplayPosition1, int yDisplayPosition1) {
        super(inventory1, armorType1, xDisplayPosition1, yDisplayPosition1);
        this.armorType = armorType1;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        Item item = itemStack.getItem();
        if (item == null) {
            return false;
        }
        return item.isValidArmor(itemStack, this.armorType, (Entity)((InventoryPlayer)this.inventory).player);
    }
}

