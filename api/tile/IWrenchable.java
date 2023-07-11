/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package ic2.api.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IWrenchable {
    public boolean wrenchCanSetFacing(EntityPlayer var1, int var2);

    public short getFacing();

    public void setFacing(short var1);

    public boolean wrenchCanRemove(EntityPlayer var1);

    public float getWrenchDropRate();

    public ItemStack getWrenchDrop(EntityPlayer var1);
}

