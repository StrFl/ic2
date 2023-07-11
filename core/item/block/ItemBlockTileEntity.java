/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.block;

import ic2.core.block.BlockTileEntity;
import ic2.core.item.block.ItemBlockIC2;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockTileEntity
extends ItemBlockIC2 {
    public ItemBlockTileEntity(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int i) {
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "ic2." + ((BlockTileEntity)this.block).getName(stack.getItemDamage());
    }
}

