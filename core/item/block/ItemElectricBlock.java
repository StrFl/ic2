/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class ItemElectricBlock
extends ItemBlockIC2 {
    public ItemElectricBlock(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    public int getMetadata(int i) {
        return i;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage();
        switch (meta) {
            case 0: {
                return "ic2.blockBatBox";
            }
            case 1: {
                return "ic2.blockMFE";
            }
            case 2: {
                return "ic2.blockMFSU";
            }
            case 3: {
                return "ic2.blockTransformerLV";
            }
            case 4: {
                return "ic2.blockTransformerMV";
            }
            case 5: {
                return "ic2.blockTransformerHV";
            }
            case 6: {
                return "ic2.blockTransformerEV";
            }
            case 7: {
                return "ic2.blockCESU";
            }
        }
        return null;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        int meta = itemStack.getItemDamage();
        switch (meta) {
            case 0: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Output") + " 32EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.Capacity") + " 40k EU ");
                break;
            }
            case 1: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Output") + " 512EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.Capacity") + " 4m EU");
                break;
            }
            case 2: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Output") + " 2048EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.Capacity") + " 40m EU");
                break;
            }
            case 7: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Output") + " 128EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.Capacity") + " 300k EU");
                break;
            }
            case 3: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Low") + " 32EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.High") + " 128EU/t ");
                break;
            }
            case 4: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Low") + " 128EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.High") + " 512EU/t ");
                break;
            }
            case 5: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Low") + " 512EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.High") + " 2048EU/t ");
                break;
            }
            case 6: {
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Low") + " 2048EU/t " + StatCollector.translateToLocal((String)"ic2.item.tooltip.High") + " 8192EU/t ");
            }
        }
        switch (meta) {
            case 0: 
            case 1: 
            case 2: 
            case 7: {
                NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemStack);
                info.add(StatCollector.translateToLocal((String)"ic2.item.tooltip.Store") + " " + nbttagcompound.getInteger("energy") + " EU");
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
        itemList.add(Ic2Items.lvTransformer);
        itemList.add(Ic2Items.mvTransformer);
        itemList.add(Ic2Items.hvTransformer);
        itemList.add(Ic2Items.evTransformer);
        itemList.add(Ic2Items.batBox);
        itemList.add(Ic2Items.cesuUnit);
        itemList.add(Ic2Items.mfeUnit);
        itemList.add(Ic2Items.mfsUnit);
        ItemStack itemStack = new ItemStack(Ic2Items.batBox.getItem(), 1);
        itemStack.setItemDamage(0);
        NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemStack);
        nbttagcompound.setDouble("energy", 40000.0);
        itemList.add(itemStack);
        itemStack = new ItemStack(Ic2Items.cesuUnit.getItem(), 1);
        itemStack.setItemDamage(7);
        nbttagcompound = StackUtil.getOrCreateNbtData(itemStack);
        nbttagcompound.setDouble("energy", 300000.0);
        itemList.add(itemStack);
        itemStack = new ItemStack(Ic2Items.mfeUnit.getItem(), 1);
        itemStack.setItemDamage(1);
        nbttagcompound = StackUtil.getOrCreateNbtData(itemStack);
        nbttagcompound.setDouble("energy", 4000000.0);
        itemList.add(itemStack);
        itemStack = new ItemStack(Ic2Items.mfsUnit.getItem(), 1);
        itemStack.setItemDamage(2);
        nbttagcompound = StackUtil.getOrCreateNbtData(itemStack);
        nbttagcompound.setDouble("energy", 4.0E7);
        itemList.add(itemStack);
    }
}

