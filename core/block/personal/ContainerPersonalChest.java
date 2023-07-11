/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.personal;

import ic2.core.ContainerFullInv;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerPersonalChest
extends ContainerFullInv<TileEntityPersonalChest> {
    public ContainerPersonalChest(EntityPlayer entityPlayer, TileEntityPersonalChest tileEntity1) {
        super(entityPlayer, tileEntity1, 222);
        tileEntity1.openInventory();
        for (int y = 0; y < tileEntity1.contentSlot.size() / 9; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlotToContainer(new SlotInvSlot(tileEntity1.contentSlot, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }
    }

    public void onContainerClosed(EntityPlayer entityplayer) {
        ((TileEntityPersonalChest)this.base).closeInventory();
    }
}

