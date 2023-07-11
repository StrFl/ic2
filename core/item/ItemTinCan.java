/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTinCan
extends ItemIC2 {
    public ItemTinCan(InternalName internalName) {
        super(internalName);
    }

    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (IC2.platform.isSimulating() && entityPlayer.getFoodStats().needFood()) {
            return this.onEaten(entityPlayer, itemStack);
        }
        return itemStack;
    }

    public ItemStack onEaten(EntityPlayer player, ItemStack itemStack) {
        int needfood = 20 - player.getFoodStats().getFoodLevel();
        if (needfood > 0) {
            if (itemStack.stackSize >= needfood) {
                if (StackUtil.storeInventoryItem(new ItemStack(Ic2Items.tinCan.getItem(), needfood), player, true)) {
                    player.getFoodStats().addStats(needfood, (float)needfood);
                    itemStack.stackSize -= needfood;
                    StackUtil.storeInventoryItem(new ItemStack(Ic2Items.tinCan.getItem(), needfood), player, false);
                    IC2.platform.playSoundSp("Tools/eat.ogg", 1.0f, 1.0f);
                }
            } else if (StackUtil.storeInventoryItem(new ItemStack(Ic2Items.tinCan.getItem(), itemStack.stackSize), player, true)) {
                player.getFoodStats().addStats(itemStack.stackSize, (float)itemStack.stackSize);
                StackUtil.storeInventoryItem(new ItemStack(Ic2Items.tinCan.getItem(), itemStack.stackSize), player, false);
                itemStack.stackSize = 0;
                IC2.platform.playSoundSp("Tools/eat.ogg", 1.0f, 1.0f);
            }
        }
        return itemStack;
    }
}

