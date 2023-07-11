/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityCropmatron;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCropmatron
extends ContainerElectricMachine<TileEntityCropmatron> {
    public ContainerCropmatron(EntityPlayer entityPlayer, TileEntityCropmatron base) {
        super(entityPlayer, base, 191, 134, 80);
        int i;
        for (i = 0; i < base.fertilizerSlot.size(); ++i) {
            this.addSlotToContainer(new SlotInvSlot(base.fertilizerSlot, i, 26 + i * 18, 50));
        }
        for (i = 0; i < base.weedExSlot.size(); ++i) {
            this.addSlotToContainer(new SlotInvSlot(base.weedExSlot, i, 26 + i * 18, 27));
        }
        this.addSlotToContainer(new SlotInvSlot(base.wasserinputSlot, 0, 26, 71));
        this.addSlotToContainer(new SlotInvSlot(base.wasseroutputSlot, 0, 26, 89));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("energy");
        ret.add("fluidTank");
        return ret;
    }
}

