/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class ItemRemote
extends ItemIC2 {
    public ItemRemote(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, float a, float b, float c) {
        Block block = world.getBlock(x, y, z);
        if (!IC2.platform.isSimulating()) {
            return StackUtil.equals(block, Ic2Items.dynamiteStick) || StackUtil.equals(block, Ic2Items.dynamiteStickWithRemote);
        }
        if (StackUtil.equals(block, Ic2Items.dynamiteStick)) {
            ItemRemote.addRemote(x, y, z, itemstack);
            entityplayer.openContainer.detectAndSendChanges();
            world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.dynamiteStickWithRemote), 0, 7);
            return true;
        }
        if (StackUtil.equals(block, Ic2Items.dynamiteStickWithRemote)) {
            int index = ItemRemote.hasRemote(x, y, z, itemstack);
            if (index > -1) {
                world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.dynamiteStick), 0, 7);
                ItemRemote.removeRemote(index, itemstack);
                entityplayer.openContainer.detectAndSendChanges();
            } else {
                IC2.platform.messagePlayer(entityplayer, "This dynamite stick is not linked to this remote, cannot unlink.", new Object[0]);
            }
            return true;
        }
        return true;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!IC2.platform.isSimulating()) {
            return itemstack;
        }
        IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/dynamiteomote.ogg", true, IC2.audioManager.getDefaultVolume());
        ItemRemote.launchRemotes(world, itemstack, entityplayer);
        entityplayer.openContainer.detectAndSendChanges();
        return itemstack;
    }

    public static void addRemote(int x, int y, int z, ItemStack freq) {
        NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
        if (!compound.hasKey("coords")) {
            compound.setTag("coords", (NBTBase)new NBTTagList());
        }
        NBTTagList coords = compound.getTagList("coords", 10);
        NBTTagCompound coord = new NBTTagCompound();
        coord.setInteger("x", x);
        coord.setInteger("y", y);
        coord.setInteger("z", z);
        coords.appendTag((NBTBase)coord);
        compound.setTag("coords", (NBTBase)coords);
        freq.setItemDamage(coords.tagCount());
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debugTooltips) {
        if (stack.getItemDamage() > 0) {
            info.add("Linked to " + stack.getItemDamage() + " dynamite");
        }
    }

    public static void launchRemotes(World world, ItemStack freq, EntityPlayer player) {
        NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
        if (!compound.hasKey("coords")) {
            return;
        }
        NBTTagList coords = compound.getTagList("coords", 10);
        for (int i = 0; i < coords.tagCount(); ++i) {
            int z;
            int y;
            NBTTagCompound coord = coords.getCompoundTagAt(i);
            int x = coord.getInteger("x");
            if (!StackUtil.equals(world.getBlock(x, y = coord.getInteger("y"), z = coord.getInteger("z")), Ic2Items.dynamiteStickWithRemote)) continue;
            StackUtil.getBlock(Ic2Items.dynamiteStickWithRemote).removedByPlayer(world, player, x, y, z, false);
            world.setBlockToAir(x, y, z);
        }
        compound.setTag("coords", (NBTBase)new NBTTagList());
        freq.setItemDamage(0);
    }

    public static int hasRemote(int x, int y, int z, ItemStack freq) {
        NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
        if (!compound.hasKey("coords")) {
            return -1;
        }
        NBTTagList coords = compound.getTagList("coords", 10);
        for (int i = 0; i < coords.tagCount(); ++i) {
            NBTTagCompound coord = coords.getCompoundTagAt(i);
            if (coord.getInteger("x") != x || coord.getInteger("y") != y || coord.getInteger("z") != z) continue;
            return i;
        }
        return -1;
    }

    public static void removeRemote(int index, ItemStack freq) {
        NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
        if (!compound.hasKey("coords")) {
            return;
        }
        NBTTagList coords = compound.getTagList("coords", 10);
        NBTTagList newCoords = new NBTTagList();
        for (int i = 0; i < coords.tagCount(); ++i) {
            if (i == index) continue;
            newCoords.appendTag((NBTBase)coords.getCompoundTagAt(i));
        }
        compound.setTag("coords", (NBTBase)newCoords);
        freq.setItemDamage(newCoords.tagCount());
    }
}

