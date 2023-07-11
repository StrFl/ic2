/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotProcessableSolidCanner;
import ic2.core.block.machine.tileentity.TileEntitySolidCanner;
import net.minecraft.item.ItemStack;

public class InvSlotConsumableSolidCanner
extends InvSlotConsumableLiquid {
    public InvSlotConsumableSolidCanner(TileEntitySolidCanner base1, String name1, int oldStartIndex1, int count) {
        super(base1, name1, oldStartIndex1, count);
    }

    @Override
    public boolean accepts(ItemStack itemStack) {
        return ((InvSlotProcessableSolidCanner)((TileEntitySolidCanner)this.base).inputSlot).getOutput(itemStack, ((TileEntitySolidCanner)this.base).inputSlot.get(), false, true) != null;
    }
}

