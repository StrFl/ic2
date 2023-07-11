/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.tool;

import ic2.api.item.IBoxable;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemToolHammer
extends ItemIC2
implements IItemHudInfo,
IBoxable {
    public ItemToolHammer(InternalName internalName) {
        super(internalName);
        this.setMaxDamage(79);
        this.setMaxStackSize(1);
        this.canRepair = false;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        info.add(StatCollector.translateToLocal((String)"ic2.item.ItemTool.tooltip.UsesLeft") + " " + (itemStack.getMaxDamage() - itemStack.getItemDamage() + 1));
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add("Use Left: " + (itemStack.getMaxDamage() - itemStack.getItemDamage()));
        return info;
    }

    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack ret = stack.copy();
        ret.attemptDamageItem(1, IC2.random);
        return ret;
    }

    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        return false;
    }
}

