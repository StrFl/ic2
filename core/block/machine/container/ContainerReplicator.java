/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityReplicator;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerReplicator
extends ContainerElectricMachine<TileEntityReplicator> {
    public ContainerReplicator(EntityPlayer entityPlayer, TileEntityReplicator tileEntity1) {
        super(entityPlayer, tileEntity1, 184, 152, 83);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, 0, 90, 59));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.fluidSlot, 0, 8, 27));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.cellSlot, 0, 8, 72));
        for (int i = 0; i < 4; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.upgradeSlot, i, 152, 8 + i * 18));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("fluidTank");
        ret.add("uuProcessed");
        ret.add("pattern");
        ret.add("mode");
        ret.add("index");
        ret.add("maxIndex");
        ret.add("patternUu");
        ret.add("patternEu");
        return ret;
    }
}

