/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.machine.tileentity.TileEntityFermenter;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerFermenter
extends ContainerFullInv<TileEntityFermenter> {
    public ContainerFermenter(EntityPlayer entityPlayer, TileEntityFermenter tileEntite) {
        super(entityPlayer, tileEntite, 184);
        this.addSlotToContainer(new SlotInvSlot(tileEntite.biomassinputSlot, 0, 14, 46));
        this.addSlotToContainer(new SlotInvSlot(tileEntite.biomassoutputSlot, 0, 14, 64));
        this.addSlotToContainer(new SlotInvSlot(tileEntite.biogasinputSlot, 0, 148, 43));
        this.addSlotToContainer(new SlotInvSlot(tileEntite.biogassoutputSlot, 0, 148, 61));
        this.addSlotToContainer(new SlotInvSlot(tileEntite.outputSlot, 0, 86, 83));
        for (int i = 0; i < 2; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntite.upgradeSlot, i, 125 + i * 18, 83));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("inputTank");
        ret.add("outputTank");
        ret.add("progress");
        ret.add("heatbuffer");
        ret.add("maxheatbuffer");
        return ret;
    }
}

