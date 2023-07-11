/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item;

import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import ic2.core.uu.UuIndex;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class ItemCrystalMemory
extends ItemIC2 {
    public ItemCrystalMemory(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
    }

    public boolean isRepairable() {
        return false;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        super.addInformation(itemStack, player, info, b);
        ItemStack recorded = this.readItemStack(itemStack);
        if (recorded != null) {
            info.add(StatCollector.translateToLocal((String)"ic2.item.CrystalMemory.tooltip.Item") + " " + recorded.getDisplayName());
            info.add(StatCollector.translateToLocal((String)"ic2.item.CrystalMemory.tooltip.UU-Matter") + " " + Util.toSiString(UuIndex.instance.getInBuckets(recorded), 4) + "B");
        } else {
            info.add(StatCollector.translateToLocal((String)"ic2.item.CrystalMemory.tooltip.Empty"));
        }
    }

    public ItemStack readItemStack(ItemStack stack) {
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound contentTag = nbtTagCompound.getCompoundTag("Pattern");
        ItemStack Item2 = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
        return Item2;
    }

    public void writecontentsTag(ItemStack stack, ItemStack recorded) {
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound contentTag = new NBTTagCompound();
        recorded.writeToNBT(contentTag);
        nbtTagCompound.setTag("Pattern", (NBTBase)contentTag);
    }

    @Override
    public String getTextureFolder() {
        return "resources";
    }
}

