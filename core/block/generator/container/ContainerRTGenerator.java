/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.generator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.generator.tileentity.TileEntityRTGenerator;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerRTGenerator
extends ContainerFullInv<TileEntityRTGenerator> {
    public ContainerRTGenerator(EntityPlayer entityPlayer, TileEntityRTGenerator tileEntity1) {
        super(entityPlayer, tileEntity1, 166);
        int i;
        for (i = 0; i < 3; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.fuelSlot, i, 36 + i * 18, 36));
        }
        for (i = 3; i < 6; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity1.fuelSlot, i, 36 + (i - 3) * 18, 54));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("storage");
        return ret;
    }
}

