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

public class ItemBeam
extends ItemBlockIC2 {
    public ItemBeam(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int i) {
        return i;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        int meta = itemStack.getItemDamage();
        switch (meta) {
            case 0: {
                return "ic2.blockParticleEmitter";
            }
            case 1: {
                return "ic2.blockParticleAccelerator";
            }
        }
        return null;
    }
}

