/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.wiring;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlotArmor;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerElectricBlock
extends ContainerFullInv<TileEntityElectricBlock> {
    public ContainerElectricBlock(EntityPlayer entityPlayer, TileEntityElectricBlock tileEntity1) {
        super(entityPlayer, tileEntity1, 196);
        for (int col = 0; col < 4; ++col) {
            this.addSlotToContainer(new InvSlotArmor(entityPlayer.inventory, col, 8 + col * 18, 84));
        }
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

