/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.fluids.FluidRegistry
 */
package ic2.core.item.block;

import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemMachine2
extends ItemBlockIC2 {
    public ItemMachine2(Block block) {
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
                return "ic2.blockTeleporter";
            }
            case 1: {
                return "ic2.blockTesla";
            }
            case 2: {
                return "ic2.blockCropmatron";
            }
            case 3: {
                return "ic2.blockCentrifuge";
            }
            case 4: {
                return "ic2.blockMetalFormer";
            }
            case 5: {
                return "ic2.blockOreWashingPlant";
            }
            case 6: {
                return "ic2.blockPatternStorage";
            }
            case 7: {
                return "ic2.blockScanner";
            }
            case 8: {
                return "ic2.blockReplicator";
            }
            case 9: {
                return "ic2.blockSolidCanner";
            }
            case 10: {
                return "ic2.blockFluidBottler";
            }
            case 11: {
                return "ic2.blockAdvMiner";
            }
            case 12: {
                return "ic2.blockLiquidHeatExchanger";
            }
            case 13: {
                return "ic2.blockFermenter";
            }
            case 14: {
                return "ic2.blockFluidRegulator";
            }
            case 15: {
                return "ic2.blockCondenser";
            }
        }
        return null;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        int meta = itemStack.getItemDamage();
        switch (meta) {
            case 1: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 128 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 2: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 3: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 48 EU/t, 128 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 4: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 10 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 5: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 16 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 7: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 512 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 8: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 2048 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 9: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 2 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 10: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 2 EU/t, 32 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 11: {
                info.add("512 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
                break;
            }
            case 12: {
                info.add("Input: " + BlocksItems.getFluid(InternalName.fluidHotCoolant).getName() + ", " + FluidRegistry.LAVA.getName());
                break;
            }
            case 13: {
                info.add("Input 1-x HU");
                break;
            }
            case 14: {
                info.add("Output: 1-1000 mB /s or /t");
                break;
            }
            case 15: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.power") + " 0-32 EU/t, 128 EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.max"));
            }
        }
        switch (meta) {
            case 11: {
                NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemStack);
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Store") + " " + nbttagcompound.getInteger("energy") + " EU");
            }
        }
    }
}

