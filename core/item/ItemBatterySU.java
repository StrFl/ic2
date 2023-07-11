/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IItemHudInfo;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.Util;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBatterySU
extends ItemIC2
implements IBoxable,
IItemHudInfo {
    public int capacity;
    public int tier;

    public ItemBatterySU(InternalName internalName, int capacity1, int tier1) {
        super(internalName);
        this.capacity = capacity1;
        this.tier = tier1;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        double energy = this.capacity;
        for (int i = 0; i < 9 && energy > 0.0; ++i) {
            ItemStack stack = entityplayer.inventory.mainInventory[i];
            if (stack == null || stack == itemstack) continue;
            energy -= ElectricItem.manager.charge(stack, energy, this.tier, true, false);
        }
        if (!Util.isSimilar(energy, this.capacity)) {
            --itemstack.stackSize;
        }
        return itemstack;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(this.capacity + " EU");
        return info;
    }
}

