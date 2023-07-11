/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.nei.ItemList
 *  codechicken.nei.PositionedStack
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 */
package ic2.neiIntegration.core;

import codechicken.nei.ItemList;
import codechicken.nei.PositionedStack;
import ic2.api.item.ICustomDamageItem;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class PositionedStackIc2
extends PositionedStack {
    private boolean permutated;

    public PositionedStackIc2(Object object, int x, int y, boolean genPerms) {
        super(object, x, y, genPerms);
    }

    public PositionedStackIc2(Object object, int x, int y) {
        super(object, x, y);
    }

    public void generatePermutations() {
        if (this.permutated) {
            return;
        }
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        for (ItemStack item : this.items) {
            if (item == null || item.getItem() == null) continue;
            if (item.getItemDamage() == Short.MAX_VALUE) {
                List permutations = ItemList.itemMap.get((Object)item.getItem());
                if (!permutations.isEmpty()) {
                    for (ItemStack stack : permutations) {
                        stacks.add(StackUtil.copyWithSize(stack, item.stackSize));
                    }
                    continue;
                }
                ItemStack base = new ItemStack(item.getItem(), item.stackSize);
                base.setTagCompound(item.getTagCompound());
                stacks.add(base);
                continue;
            }
            stacks.add(item.copy());
        }
        this.items = stacks.toArray(new ItemStack[0]);
        if (this.items.length == 0) {
            this.items = new ItemStack[]{new ItemStack((Block)Blocks.fire)};
        }
        this.permutated = true;
        this.setPermutationToRender(0);
    }

    public void setPermutationToRender(int index) {
        ItemStack stack = this.items[index];
        if (stack.getItemDamage() == Short.MAX_VALUE && stack.getItem() instanceof ICustomDamageItem) {
            this.item = stack.copy();
            ((ICustomDamageItem)this.item.getItem()).setCustomDamage(this.item, 0);
        } else {
            super.setPermutationToRender(index);
        }
    }
}

