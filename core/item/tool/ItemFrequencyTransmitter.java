/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 */
package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.tileentity.TileEntityTeleporter;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ItemFrequencyTransmitter
extends ItemIC2 {
    public ItemFrequencyTransmitter(InternalName internalName) {
        super(internalName);
        this.maxStackSize = 1;
        this.setMaxDamage(0);
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (IC2.platform.isSimulating()) {
            if (itemstack.getItemDamage() == 0) {
                NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
                if (nbtData.getBoolean("targetSet")) {
                    nbtData.setBoolean("targetSet", false);
                    IC2.platform.messagePlayer(entityplayer, "Frequency Transmitter unlinked", new Object[0]);
                }
            } else {
                itemstack.setItemDamage(0);
            }
        }
        return itemstack;
    }

    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = world.getTileEntity(i, j, k);
        if (tileEntity instanceof TileEntityTeleporter && IC2.platform.isSimulating()) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
            boolean targetSet = nbtData.getBoolean("targetSet");
            int targetX = nbtData.getInteger("targetX");
            int targetY = nbtData.getInteger("targetY");
            int targetZ = nbtData.getInteger("targetZ");
            TileEntityTeleporter tp = (TileEntityTeleporter)tileEntity;
            if (targetSet) {
                boolean prevWorldChunkLoadOverride = world.findingSpawnPoint;
                world.findingSpawnPoint = true;
                Chunk chunk = world.getChunkProvider().provideChunk(targetX >> 4, targetZ >> 4);
                world.findingSpawnPoint = prevWorldChunkLoadOverride;
                if (chunk == null || !StackUtil.equals(chunk.getBlock(targetX & 0xF, targetY, targetZ & 0xF), Ic2Items.teleporter) || chunk.getBlockMetadata(targetX & 0xF, targetY, targetZ & 0xF) != Ic2Items.teleporter.getItemDamage()) {
                    targetSet = false;
                }
            }
            if (!targetSet) {
                targetSet = true;
                targetX = tp.xCoord;
                targetY = tp.yCoord;
                targetZ = tp.zCoord;
                IC2.platform.messagePlayer(entityplayer, "Frequency Transmitter linked to Teleporter.", new Object[0]);
            } else if (tp.xCoord == targetX && tp.yCoord == targetY && tp.zCoord == targetZ) {
                IC2.platform.messagePlayer(entityplayer, "Can't link Teleporter to itself.", new Object[0]);
            } else if (tp.targetSet && tp.targetX == targetX && tp.targetY == targetY && tp.targetZ == targetZ) {
                IC2.platform.messagePlayer(entityplayer, "Teleportation link unchanged.", new Object[0]);
            } else {
                tp.setTarget(targetX, targetY, targetZ);
                TileEntity te = world.getTileEntity(targetX, targetY, targetZ);
                if (te instanceof TileEntityTeleporter) {
                    TileEntityTeleporter tp2 = (TileEntityTeleporter)te;
                    if (!tp2.targetSet) {
                        tp2.setTarget(tp.xCoord, tp.yCoord, tp.zCoord);
                    }
                }
                IC2.platform.messagePlayer(entityplayer, "Teleportation link established.", new Object[0]);
            }
            nbtData.setBoolean("targetSet", targetSet);
            nbtData.setInteger("targetX", targetX);
            nbtData.setInteger("targetY", targetY);
            nbtData.setInteger("targetZ", targetZ);
            itemstack.setItemDamage(1);
            return false;
        }
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        if (nbtData.getBoolean("targetSet")) {
            list.add(StatCollector.translateToLocalFormatted((String)"ic2.itemFreq.tooltip.target", (Object[])new Object[]{nbtData.getInteger("targetX"), nbtData.getInteger("targetY"), nbtData.getInteger("targetZ")}));
        } else {
            list.add(StatCollector.translateToLocal((String)"ic2.itemFreq.tooltip.blank"));
        }
    }
}

