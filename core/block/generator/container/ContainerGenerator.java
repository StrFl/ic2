/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.generator.container;

import ic2.core.block.generator.container.ContainerBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityGenerator;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerGenerator
extends ContainerBaseGenerator<TileEntityGenerator> {
    public ContainerGenerator(EntityPlayer entityPlayer, TileEntityGenerator tileEntity1) {
        super(entityPlayer, tileEntity1, 166, 65, 17);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.fuelSlot, 0, 65, 53));
    }
}

