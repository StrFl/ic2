/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntitySortingMachine;
import ic2.core.slot.SlotHologramSlot;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class ContainerSortingMachine
extends ContainerElectricMachine<TileEntitySortingMachine> {
    public ContainerSortingMachine(EntityPlayer entityPlayer, TileEntitySortingMachine tileEntity) {
        super(entityPlayer, tileEntity, 243, 188, 219);
        int i;
        for (i = 0; i < 3; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity.upgradeSlot, i, 188, 161 + i * 18));
        }
        for (i = 0; i < 11; ++i) {
            this.addSlotToContainer(new SlotInvSlot(tileEntity.buffer, i, 8 + i * 18, 141));
        }
        for (i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; ++i) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
            ItemStack[] filterSlots = tileEntity.getFilterSlots(dir);
            for (int j = 0; j < filterSlots.length; ++j) {
                this.addSlotToContainer(new SlotHologramSlot(filterSlots, j, 80 + j * 18, 19 + i * 20, 1, null));
            }
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("defaultRoute");
        return ret;
    }
}

