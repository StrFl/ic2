/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntitySolidCanner;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerSolidCanner
extends ContainerStandardMachine<TileEntitySolidCanner> {
    public ContainerSolidCanner(EntityPlayer entityPlayer, TileEntitySolidCanner tileEntity1) {
        super(entityPlayer, tileEntity1, 166, 8, 62, 37, 36, 116, 36, 152, 8);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.canInputSlot, 0, 67, 36));
    }
}

