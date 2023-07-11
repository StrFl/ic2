/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.generator.container;

import ic2.core.block.generator.container.ContainerBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerWaterGenerator
extends ContainerBaseGenerator<TileEntityWaterGenerator> {
    public ContainerWaterGenerator(EntityPlayer entityPlayer, TileEntityWaterGenerator tileEntity1) {
        super(entityPlayer, tileEntity1, 166, 80, 17);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.fuelSlot, 0, 80, 53));
    }
}

