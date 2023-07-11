/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityPump;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerPump
extends ContainerElectricMachine<TileEntityPump> {
    public ContainerPump(EntityPlayer entityPlayer, TileEntityPump tileEntity1) {
        super(entityPlayer, tileEntity1, 166, 8, 44);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.containerSlot, 0, 99, 17));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, 0, 132, 34));
        for (int i = 0; i < 4; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.upgradeSlot, i, 152, 8 + i * 18));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("fluidTank");
        ret.add("guiProgress");
        return ret;
    }
}

