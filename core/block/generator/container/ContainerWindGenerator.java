/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.generator.container;

import ic2.core.block.generator.container.ContainerBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityWindGenerator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerWindGenerator
extends ContainerBaseGenerator<TileEntityWindGenerator> {
    public ContainerWindGenerator(EntityPlayer entityPlayer, TileEntityWindGenerator tileEntity1) {
        super(entityPlayer, tileEntity1, 166, 80, 26);
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("subproduction");
        return ret;
    }
}

