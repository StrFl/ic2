/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.machine.tileentity.TileEntityPatternStorage;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerPatternStorage
extends ContainerFullInv<TileEntityPatternStorage> {
    public ContainerPatternStorage(EntityPlayer entityPlayer, TileEntityPatternStorage tileEntity1) {
        super(entityPlayer, tileEntity1, 166);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.diskSlot, 0, 18, 20));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("index");
        ret.add("maxIndex");
        ret.add("pattern");
        ret.add("patternUu");
        ret.add("patternEu");
        return ret;
    }
}

