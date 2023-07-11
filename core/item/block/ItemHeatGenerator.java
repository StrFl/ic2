/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.block;

import ic2.core.item.block.ItemBlockIC2;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemHeatGenerator
extends ItemBlockIC2 {
    public ItemHeatGenerator(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int i) {
        return i;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage();
        switch (meta) {
            case 0: {
                return "ic2.blockSolidHeatGenerator";
            }
            case 1: {
                return "ic2.blockFluidHeatGenerator";
            }
            case 2: {
                return "ic2.blockRTHeatGenerator";
            }
            case 3: {
                return "ic2.blockElectricHeatGenerator";
            }
        }
        return null;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        int meta = itemStack.getItemDamage();
        switch (meta) {
            case 0: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 20 HU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 1: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 8-32 HU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 2: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 1-32 HU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 3: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 0-100 HU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 0-100 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
            }
        }
    }
}

