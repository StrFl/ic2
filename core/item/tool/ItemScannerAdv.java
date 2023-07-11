/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.core.init.InternalName;
import ic2.core.item.tool.ItemScanner;
import net.minecraft.item.ItemStack;

public class ItemScannerAdv
extends ItemScanner {
    public ItemScannerAdv(InternalName internalName) {
        super(internalName, 1000000.0, 512.0, 2);
    }

    @Override
    public int startLayerScan(ItemStack itemStack) {
        return ElectricItem.manager.use(itemStack, 250.0, null) ? this.getScannrange() / 2 : 0;
    }

    @Override
    public int getScannrange() {
        return 12;
    }
}

