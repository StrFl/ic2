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

public class ItemMachine
extends ItemBlockIC2 {
    public ItemMachine(Block block) {
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
                return "ic2.blockMachine";
            }
            case 1: {
                return "ic2.blockIronFurnace";
            }
            case 2: {
                return "ic2.blockElecFurnace";
            }
            case 3: {
                return "ic2.blockMacerator";
            }
            case 4: {
                return "ic2.blockExtractor";
            }
            case 5: {
                return "ic2.blockCompressor";
            }
            case 6: {
                return "ic2.blockCanner";
            }
            case 7: {
                return "ic2.blockMiner";
            }
            case 8: {
                return "ic2.blockPump";
            }
            case 9: {
                return "ic2.blockMagnetizer";
            }
            case 10: {
                return "ic2.blockElectrolyzer";
            }
            case 11: {
                return "ic2.blockRecycler";
            }
            case 12: {
                return "ic2.blockAdvMachine";
            }
            case 13: {
                return "ic2.blockInduction";
            }
            case 14: {
                return "ic2.blockMatter";
            }
            case 15: {
                return "ic2.blockTerra";
            }
        }
        return null;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        int meta = itemStack.getItemDamage();
        switch (meta) {
            case 2: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 3 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 3: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 2 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 4: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 2 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 5: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 2 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 6: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 4 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 8: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 4 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 9: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 11: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 1 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 13: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 1 EU/t, 128 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 14: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 512 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 15: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 512 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
            }
        }
    }
}

