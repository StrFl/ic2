/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.init.InternalName;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.ItemIC2;
import ic2.core.item.tool.ContainerMeter;
import ic2.core.item.tool.HandHeldMeter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemToolMeter
extends ItemIC2
implements IBoxable,
IHandHeldInventory {
    public ItemToolMeter(InternalName internalName) {
        super(internalName);
        this.maxStackSize = 1;
        this.setMaxDamage(0);
    }

    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        TileEntity tileEntity = EnergyNet.instance.getTileEntity(world, x, y, z);
        if (tileEntity instanceof IEnergySource || tileEntity instanceof IEnergyConductor || tileEntity instanceof IEnergySink) {
            if (IC2.platform.launchGui(player, this.getInventory(player, stack))) {
                ContainerMeter container = (ContainerMeter)player.openContainer;
                container.setUut(tileEntity);
                return true;
            }
        } else {
            IC2.platform.messagePlayer(player, "Not an energy net tile", new Object[0]);
        }
        return true;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public IHasGui getInventory(EntityPlayer player, ItemStack stack) {
        return new HandHeldMeter(player, stack);
    }
}

