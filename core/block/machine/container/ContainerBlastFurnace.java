/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.machine.tileentity.TileEntityBlastFurnace;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerBlastFurnace
extends ContainerFullInv<TileEntityBlastFurnace> {
    public ContainerBlastFurnace(EntityPlayer entityPlayer, TileEntityBlastFurnace tileEntity) {
        super(entityPlayer, tileEntity, 166);
        this.addSlotToContainer(new SlotInvSlot(tileEntity.inputSlot, 0, 35, 33));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.outputSlot, 0, 134, 56));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.slagOutputSlot, 0, 152, 56));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.airSlot, 0, 26, 56));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.airOutputSlot, 0, 44, 56));
        for (int i = 0; i < 2; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity.upgradeSlot, i, 152, 8 + i * 18));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("heat");
        ret.add("progress");
        ret.add("outOfAir");
        return ret;
    }
}

