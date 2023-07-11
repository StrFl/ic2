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

public class ItemKineticGenerator
extends ItemBlockIC2 {
    public ItemKineticGenerator(Block block) {
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
                return "ic2.blockWindKineticGenerator";
            }
            case 1: {
                return "ic2.blockSteamKineticGenerator";
            }
            case 2: {
                return "ic2.blockElectricKineticGenerator";
            }
            case 3: {
                return "ic2.blockManualKineticGenerator";
            }
            case 4: {
                return "ic2.blockWaterKineticGenerator";
            }
            case 5: {
                return "ic2.blockStirlingKineticGenerator";
            }
        }
        return null;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        int meta = itemStack.getItemDamage();
        switch (meta) {
            case 0: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 0-x KU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 1: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 0-4000 KU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 2: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 0-1000 KU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 0-500 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 3: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 400 KU " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 4: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 0-x KU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 5: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.PowerOutput") + " 0-2000 KU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
            }
        }
    }
}

