/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCanner
extends ContainerStandardMachine<TileEntityCanner> {
    public ContainerCanner(EntityPlayer entityPlayer, TileEntityCanner tileEntity1) {
        super(entityPlayer, tileEntity1, 184, 8, 80, 80, 44, 119, 17, 152, 26);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.canInputSlot, 0, 41, 17));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("mode");
        ret.add("inputTank");
        ret.add("outputTank");
        return ret;
    }
}

