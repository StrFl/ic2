/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.kineticgenerator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.kineticgenerator.tileentity.TileEntitySteamKineticGenerator;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerSteamKineticGenerator
extends ContainerFullInv<TileEntitySteamKineticGenerator> {
    public ContainerSteamKineticGenerator(EntityPlayer entityPlayer, TileEntitySteamKineticGenerator tileEntity) {
        super(entityPlayer, tileEntity, 166);
        this.addSlotToContainer(new SlotInvSlot(tileEntity.upgradeSlot, 0, 152, 26));
        this.addSlotToContainer(new SlotInvSlot(tileEntity.turbineSlot, 0, 80, 26));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("distilledwaterTank");
        ret.add("SteamTank");
        ret.add("kUoutput");
        ret.add("isturbefilledupwithwater");
        return ret;
    }
}

