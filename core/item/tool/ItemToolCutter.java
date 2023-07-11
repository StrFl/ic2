/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.item.IBoxable;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.PositionSpec;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemToolCutter
extends ItemIC2
implements IBoxable,
IItemHudInfo {
    public ItemToolCutter(InternalName internalName) {
        super(internalName);
        this.setMaxDamage(59);
        this.setMaxStackSize(1);
        this.canRepair = false;
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
        TileEntityCable cable;
        TileEntity te = world.getTileEntity(i, j, k);
        if (te instanceof TileEntityCable && (cable = (TileEntityCable)te).tryAddInsulation()) {
            if (entityplayer.inventory.consumeInventoryItem(Ic2Items.rubber.getItem())) {
                if (IC2.platform.isSimulating()) {
                    ItemToolCutter.damageCutter(itemstack, 1);
                }
                return true;
            }
            cable.tryRemoveInsulation();
        }
        return false;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        info.add(StatCollector.translateToLocal((String)"ic2.item.ItemTool.tooltip.UsesLeft") + " " + (itemStack.getMaxDamage() - itemStack.getItemDamage() + 1));
    }

    public static void cutInsulationFrom(ItemStack itemstack, World world, int i, int j, int k) {
        TileEntityCable cable;
        TileEntity te = world.getTileEntity(i, j, k);
        if (te instanceof TileEntityCable && (cable = (TileEntityCable)te).tryRemoveInsulation()) {
            if (IC2.platform.isSimulating()) {
                double d = (double)world.rand.nextFloat() * 0.7 + 0.15;
                double d1 = (double)world.rand.nextFloat() * 0.7 + 0.15;
                double d2 = (double)world.rand.nextFloat() * 0.7 + 0.15;
                EntityItem entityitem = new EntityItem(world, (double)i + d, (double)j + d1, (double)k + d2, Ic2Items.rubber.copy());
                entityitem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld((Entity)entityitem);
                ItemToolCutter.damageCutter(itemstack, 3);
            }
            if (IC2.platform.isRendering()) {
                IC2.audioManager.playOnce(new AudioPosition(world, (float)i + 0.5f, (float)j + 0.5f, (float)k + 0.5f), PositionSpec.Center, "Tools/InsulationCutters.ogg", true, IC2.audioManager.getDefaultVolume());
            }
        }
    }

    public static void damageCutter(ItemStack itemStack, int damage) {
        if (!itemStack.isItemStackDamageable()) {
            return;
        }
        itemStack.setItemDamage(itemStack.getItemDamage() + damage);
        if (itemStack.getItemDamage() > itemStack.getMaxDamage()) {
            --itemStack.stackSize;
            if (itemStack.stackSize < 0) {
                itemStack.stackSize = 0;
            }
            itemStack.setItemDamage(0);
        }
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
        if (ret.attemptDamageItem(1, IC2.random)) {
            return null;
        }
        return ret;
    }

    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        return false;
    }
}

