/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.wiring;

import ic2.core.ContainerFullInv;
import ic2.core.block.wiring.TileEntityTransformer;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerTransformer
extends ContainerFullInv<TileEntityTransformer> {
    public ContainerTransformer(EntityPlayer entityPlayer, TileEntityTransformer tileEntity1, int height) {
        super(entityPlayer, tileEntity1, height);
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("mode");
        ret.add("inputflow");
        ret.add("outputflow");
        return ret;
    }
}

