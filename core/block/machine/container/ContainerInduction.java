/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerInduction
extends ContainerElectricMachine<TileEntityInduction> {
    public ContainerInduction(EntityPlayer entityPlayer, TileEntityInduction tileEntity) {
        super(entityPlayer, tileEntity, 166, 56, 53);
        this.addSlotToContainer(new SlotInvSlot(tileEntity.inputSlotA, 0, 47, 17));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.inputSlotB, 0, 63, 17));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.outputSlotA, 0, 113, 35));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.outputSlotB, 0, 131, 35));
        for (int i = 0; i < 2; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity.upgradeSlot, i, 153, 26 + i * 18));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("progress");
        ret.add("heat");
        return ret;
    }
}

