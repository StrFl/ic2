/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityOreWashing;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerOreWashing
extends ContainerStandardMachine<TileEntityOreWashing> {
    public ContainerOreWashing(EntityPlayer entityPlayer, TileEntityOreWashing tileEntity1) {
        super(entityPlayer, tileEntity1, 166, 8, 62, 104, 17, 86, 62, 152, 8);
        for (int i = 1; i < tileEntity1.outputSlot.size(); ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, i, 86 + i * 18, 62));
        }
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.fluidSlot, 0, 38, 17));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.cellSlot, 0, 38, 62));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("fluidTank");
        return ret;
    }
}

