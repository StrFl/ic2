/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.wiring;

import ic2.core.ContainerFullInv;
import ic2.core.block.wiring.TileEntityChargepadBlock;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerChargepadBlock
extends ContainerFullInv<TileEntityChargepadBlock> {
    public ContainerChargepadBlock(EntityPlayer entityPlayer, TileEntityChargepadBlock tileEntity1) {
        super(entityPlayer, tileEntity1, 162);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.chargeSlot, 0, 56, 17));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.dischargeSlot, 0, 56, 53));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("energy");
        ret.add("redstoneMode");
        ret.add("chargeSlot");
        ret.add("dischargeSlot");
        return ret;
    }
}

