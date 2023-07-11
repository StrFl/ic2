/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.machine.tileentity.TileEntityLathe;
import ic2.core.slot.SlotInvSlot;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerLathe
extends ContainerFullInv<TileEntityLathe> {
    public ContainerLathe(EntityPlayer player, TileEntityLathe base1) {
        super(player, base1, 166);
        this.addSlotToContainer(new SlotInvSlot(base1.toolSlot, 0, 10, 30));
        this.addSlotToContainer(new SlotInvSlot(base1.latheSlot, 0, 10, 12));
        this.addSlotToContainer(new SlotInvSlot(base1.outputSlot, 0, 10, 57));
    }

    @Override
    public List<String> getNetworkedFields() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("toolSlot");
        list.add("latheSlot");
        list.add("outputSlot");
        return list;
    }
}

