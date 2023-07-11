/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.machine.tileentity.TileEntityNuke;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerNuke
extends ContainerFullInv<TileEntityNuke> {
    public ContainerNuke(EntityPlayer entityPlayer, TileEntityNuke base1) {
        super(entityPlayer, base1, 219);
        this.addSlotToContainer(new SlotInvSlot(base1.insideSlot, 0, 79, 62));
        this.addSlotToContainer(new SlotInvSlot(base1.outsideSlot, 0, 52, 8));
        this.addSlotToContainer(new SlotInvSlot(base1.outsideSlot, 0, 106, 8));
        this.addSlotToContainer(new SlotInvSlot(base1.outsideSlot, 0, 26, 35));
        this.addSlotToContainer(new SlotInvSlot(base1.outsideSlot, 0, 133, 35));
        this.addSlotToContainer(new SlotInvSlot(base1.outsideSlot, 0, 26, 89));
        this.addSlotToContainer(new SlotInvSlot(base1.outsideSlot, 0, 133, 89));
        this.addSlotToContainer(new SlotInvSlot(base1.outsideSlot, 0, 52, 116));
        this.addSlotToContainer(new SlotInvSlot(base1.outsideSlot, 0, 106, 116));
    }
}

