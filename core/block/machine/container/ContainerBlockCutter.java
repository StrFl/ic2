/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityBlockCutter;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerBlockCutter
extends ContainerStandardMachine<TileEntityBlockCutter> {
    public ContainerBlockCutter(EntityPlayer entityPlayer, TileEntityBlockCutter tileEntity1) {
        super(entityPlayer, tileEntity1, 166, 26, 53, 26, 17, 116, 34, 152, 8);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.cutterSlot, 0, 71, 35));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("bladetoweak");
        return ret;
    }
}

