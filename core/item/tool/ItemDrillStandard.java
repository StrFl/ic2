/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.tool;

import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.tool.ItemDrill;
import ic2.core.item.tool.ItemElectricTool;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemDrillStandard
extends ItemDrill {
    public ItemDrillStandard(InternalName internalName) {
        super(internalName, 50, ItemElectricTool.HarvestLevel.Iron);
        this.maxCharge = 30000;
        this.transferLimit = 100;
        this.tier = 1;
        this.efficiencyOnProperMaterial = 8.0f;
        Ic2Items.miningDrill = new ItemStack((Item)this);
    }
}

