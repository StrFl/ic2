/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityCropHavester;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCropHavester
extends ContainerElectricMachine<TileEntityCropHavester> {
    public ContainerCropHavester(EntityPlayer entityPlayer, TileEntityCropHavester base) {
        super(entityPlayer, base, 191, 152, 58);
        for (int y = 0; y < base.contentSlot.size() / 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                this.addSlotToContainer(new SlotInvSlot(base.contentSlot, x + y * 5, 44 + x * 18, 22 + y * 18));
            }
        }
        this.addSlotToContainer(new SlotInvSlot(base.upgradeSlot, 0, 80, 80));
        this.addSlotToContainer(new SlotInvSlot(base.cropnalyzerSlot, 0, 15, 40));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("energy");
        return ret;
    }
}

