/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.generator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ContainerBaseGenerator<T extends TileEntityBaseGenerator>
extends ContainerFullInv<T> {
    public short lastStorage = (short)-1;
    public int lastFuel = -1;

    public ContainerBaseGenerator(EntityPlayer entityPlayer, T tileEntity1, int height, int chargeX, int chargeY) {
        super(entityPlayer, tileEntity1, height);
        this.addSlotToContainer(new SlotInvSlot(((TileEntityBaseGenerator)tileEntity1).chargeSlot, 0, chargeX, chargeY));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("storage");
        ret.add("fuel");
        return ret;
    }
}

