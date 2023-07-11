/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.reactor.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.reactor.tileentity.TileEntityReactorFluidPort;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerReactorFluidPort
extends ContainerFullInv<TileEntityReactorFluidPort> {
    public ContainerReactorFluidPort(EntityPlayer entityPlayer, TileEntityReactorFluidPort tileEntite) {
        super(entityPlayer, tileEntite, 166);
        this.addSlotToContainer(new SlotInvSlot(tileEntite.upgradeSlot, 0, 80, 43));
    }
}

