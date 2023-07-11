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

public class ItemPersonalBlock
extends ItemBlockIC2 {
    public ItemPersonalBlock(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int i) {
        return i;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage();
        switch (meta) {
            case 0: {
                return "ic2.blockPersonalChest";
            }
            case 1: {
                return "ic2.blockPersonalTrader";
            }
            case 2: {
                return "ic2.blockPersonalTraderEnergy";
            }
        }
        return null;
    }
}

