/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item;

import ic2.api.item.IBoxable;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import net.minecraft.item.ItemStack;

public class ItemFertilizer
extends ItemIC2
implements IBoxable {
    public ItemFertilizer(InternalName internalName) {
        super(internalName);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

