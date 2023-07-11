/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.api.item.IKineticRotor;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableClass;
import net.minecraft.item.ItemStack;

public class InvSlotConsumableKineticRotor
extends InvSlotConsumableClass {
    private IKineticRotor.GearboxType type;

    public InvSlotConsumableKineticRotor(TileEntityInventory base1, String name1, int oldStartIndex1, InvSlot.Access access1, int count, InvSlot.InvSide preferredSide1, IKineticRotor.GearboxType type) {
        super(base1, name1, oldStartIndex1, access1, count, preferredSide1, IKineticRotor.class);
        this.type = type;
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        if (super.accepts(itemStack)) {
            return ((IKineticRotor)itemStack.getItem()).isAcceptedType(itemStack, this.type);
        }
        return false;
    }
}

