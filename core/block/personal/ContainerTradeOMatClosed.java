/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.personal;

import ic2.core.ContainerFullInv;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.slot.SlotInvSlot;
import ic2.core.slot.SlotInvSlotReadOnly;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerTradeOMatClosed
extends ContainerFullInv<TileEntityTradeOMat> {
    public ContainerTradeOMatClosed(EntityPlayer entityPlayer, TileEntityTradeOMat tileEntity1) {
        super(entityPlayer, tileEntity1, 166);
        this.addSlotToContainer(new SlotInvSlotReadOnly(tileEntity1.demandSlot, 0, 50, 19));
        this.addSlotToContainer(new SlotInvSlotReadOnly(tileEntity1.offerSlot, 0, 50, 38));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.inputSlot, 0, 143, 19));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.outputSlot, 0, 143, 53));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("stock");
        return ret;
    }
}

