/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.machine.tileentity.TileEntityIronFurnace;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerIronFurnace
extends ContainerFullInv<TileEntityIronFurnace> {
    public ContainerIronFurnace(EntityPlayer entityPlayer, TileEntityIronFurnace tileEntity) {
        super(entityPlayer, tileEntity, 166);
        this.addSlotToContainer(new SlotInvSlot(tileEntity.inputSlot, 0, 56, 17));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.fuelSlot, 0, 56, 53));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.outputSlot, 0, 116, 35));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("progress");
        ret.add("fuel");
        ret.add("maxFuel");
        return ret;
    }
}

