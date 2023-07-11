/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.item.tool;

import ic2.core.Ic2Items;
import ic2.core.item.ContainerHandHeldInventory;
import ic2.core.item.tool.HandHeldCropnalyzer;
import ic2.core.slot.SlotCustom;
import ic2.core.slot.SlotDischarge;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCropnalyzer
extends ContainerHandHeldInventory<HandHeldCropnalyzer> {
    public ContainerCropnalyzer(EntityPlayer player, HandHeldCropnalyzer cropnalyzer1, int height) {
        super(cropnalyzer1);
        this.addSlotToContainer(new SlotCustom(cropnalyzer1, Ic2Items.cropSeed.getItem(), 0, 8, 7));
        this.addSlotToContainer(new SlotCustom(cropnalyzer1, null, 1, 41, 7));
        this.addSlotToContainer(new SlotDischarge(cropnalyzer1, 2, 152, 7));
        this.addPlayerInventorySlots(player, height);
    }
}

