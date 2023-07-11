/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityCondenser;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCondenser
extends ContainerElectricMachine<TileEntityCondenser> {
    public ContainerCondenser(EntityPlayer entityPlayer, TileEntityCondenser tileEntite) {
        super(entityPlayer, tileEntite, 184, 8, 44);
        int i;
        this.addSlotToContainer(new SlotInvSlot(tileEntite.waterinputSlot, 0, 26, 73));
        this.addSlotToContainer(new SlotInvSlot(tileEntite.wateroutputSlot, 0, 134, 73));
        this.addSlotToContainer(new SlotInvSlot(tileEntite.upgradeSlot, 0, 152, 73));
        for (i = 0; i < 2; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntite.ventslots, i, 26 + i * 108, 26));
        }
        for (i = 2; i < 4; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntite.ventslots, i, 26 + (i - 2) * 108, 44));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("inputTank");
        ret.add("outputTank");
        ret.add("progress");
        return ret;
    }
}

