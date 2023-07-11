/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.machine.tileentity.TileEntityElectrolyzer;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerElectrolyzer
extends ContainerFullInv<TileEntityElectrolyzer> {
    public ContainerElectrolyzer(EntityPlayer entityPlayer, TileEntityElectrolyzer tileEntity1) {
        super(entityPlayer, tileEntity1, 166);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.waterSlot, 0, 54, 35));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.hydrogenSlot, 0, 112, 35));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("energy");
        return ret;
    }
}

