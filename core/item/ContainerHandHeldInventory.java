/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.item.tool.HandHeldInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class ContainerHandHeldInventory<T extends HandHeldInventory>
extends ContainerBase<T> {
    public ContainerHandHeldInventory(T inventory) {
        super(inventory);
    }

    public ItemStack slotClick(int slot, int button, int par3, EntityPlayer player) {
        ItemStack stack;
        if (player instanceof EntityPlayerMP && IC2.platform.isSimulating() && slot == -999 && (button == 0 || button == 1) && ((HandHeldInventory)this.base).isThisContainer(stack = player.inventory.getItemStack())) {
            ((EntityPlayerMP)player).closeScreen();
        }
        return super.slotClick(slot, button, par3, player);
    }

    public void onContainerClosed(EntityPlayer player) {
        ((HandHeldInventory)this.base).onGuiClosed(player);
        super.onContainerClosed(player);
    }
}

