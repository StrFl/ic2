/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ContainerElectricMachine<T extends TileEntityElectricMachine>
extends ContainerFullInv<T> {
    public ContainerElectricMachine(EntityPlayer entityPlayer, T base1, int height, int dischargeX, int dischargeY) {
        super(entityPlayer, base1, height);
        this.addSlotToContainer(new SlotInvSlot(((TileEntityElectricMachine)base1).dischargeSlot, 0, dischargeX, dischargeY));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("guiChargeLevel");
        ret.add("tier");
        return ret;
    }
}

