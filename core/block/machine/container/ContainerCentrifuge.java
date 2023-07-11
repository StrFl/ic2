/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityCentrifuge;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCentrifuge
extends ContainerStandardMachine<TileEntityCentrifuge> {
    public ContainerCentrifuge(EntityPlayer entityPlayer, TileEntityCentrifuge tileEntity1) {
        super(entityPlayer, tileEntity1, 166, 11, 60, 11, 21, 124, 18, 152, 8);
        for (int i = 1; i < tileEntity1.outputSlot.size(); ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, i, 124, 18 + i * 18));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("workheat");
        ret.add("heat");
        return ret;
    }
}

