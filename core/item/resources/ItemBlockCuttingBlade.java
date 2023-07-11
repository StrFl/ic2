/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.resources;

import ic2.api.item.IBlockCuttingBlade;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockCuttingBlade
extends ItemIC2
implements IBlockCuttingBlade {
    private final int hardness;

    public ItemBlockCuttingBlade(InternalName internalName, int hardness) {
        super(internalName);
        this.hardness = hardness;
    }

    @Override
    public int gethardness() {
        return this.hardness;
    }

    @Override
    public String getTextureFolder() {
        return "resources";
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        switch (this.hardness) {
            case 3: {
                info.add(StatCollector.translateToLocal((String)"ic2.IronBlockCuttingBlade.info"));
                break;
            }
            case 6: {
                info.add(StatCollector.translateToLocal((String)"ic2.AdvIronBlockCuttingBlade.info"));
                break;
            }
            case 9: {
                info.add(StatCollector.translateToLocal((String)"ic2.DiamondBlockCuttingBlade.info"));
            }
        }
        info.add(StatCollector.translateToLocalFormatted((String)"ic2.CuttingBlade.hardness", (Object[])new Object[]{this.gethardness()}));
    }
}

