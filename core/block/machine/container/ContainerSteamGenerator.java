/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerBase;
import ic2.core.block.machine.tileentity.TileEntitySteamGenerator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerSteamGenerator
extends ContainerBase<TileEntitySteamGenerator> {
    public ContainerSteamGenerator(EntityPlayer entityPlayer, TileEntitySteamGenerator tileEntite) {
        super(tileEntite);
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("WaterTank");
        ret.add("heatinput");
        ret.add("inputmb");
        ret.add("outputmb");
        ret.add("pressurevalve");
        ret.add("systemheat");
        ret.add("outputtyp");
        ret.add("calcification");
        return ret;
    }
}

