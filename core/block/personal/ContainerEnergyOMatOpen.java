/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.ICrafting
 */
package ic2.core.block.personal;

import ic2.core.ContainerFullInv;
import ic2.core.block.personal.TileEntityEnergyOMat;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

public class ContainerEnergyOMatOpen
extends ContainerFullInv<TileEntityEnergyOMat> {
    private int lastTier = -1;

    public ContainerEnergyOMatOpen(EntityPlayer entityPlayer, TileEntityEnergyOMat tileEntity1) {
        super(entityPlayer, tileEntity1, 166);
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.demandSlot, 0, 24, 17));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.upgradeSlot, 0, 24, 53));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.inputSlot, 0, 60, 17));
        this.addSlotToContainer(new SlotInvSlot(tileEntity1.chargeSlot, 0, 60, 53));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("paidFor");
        ret.add("euBuffer");
        ret.add("euOffer");
        return ret;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i) {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);
            if (((TileEntityEnergyOMat)this.base).chargeSlot.tier == this.lastTier) continue;
            icrafting.sendProgressBarUpdate((Container)this, 0, ((TileEntityEnergyOMat)this.base).chargeSlot.tier);
        }
        this.lastTier = ((TileEntityEnergyOMat)this.base).chargeSlot.tier;
    }

    public void updateProgressBar(int index, int value) {
        super.updateProgressBar(index, value);
        switch (index) {
            case 0: {
                ((TileEntityEnergyOMat)this.base).chargeSlot.tier = value;
            }
        }
    }
}

