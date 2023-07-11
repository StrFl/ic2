/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.block;

import ic2.core.item.block.ItemBlockIC2;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockMetal
extends ItemBlockIC2 {
    public ItemBlockMetal(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int i) {
        return i;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        switch (itemstack.getItemDamage()) {
            case 0: {
                return "ic2.blockMetalCopper";
            }
            case 1: {
                return "ic2.blockMetalTin";
            }
            case 2: {
                return "ic2.blockMetalBronze";
            }
            case 3: {
                return "ic2.blockMetalUranium";
            }
            case 4: {
                return "ic2.blockMetalLead";
            }
            case 5: {
                return "ic2.blockMetalAdvIron";
            }
        }
        return null;
    }
}

