/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 */
package ic2.core.energy;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.core.IC2;
import ic2.core.WorldData;
import ic2.core.util.LogCategory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EventHandler {
    public EventHandler() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void onEnergyTileLoad(EnergyTileLoadEvent event) {
        if (!IC2.platform.isSimulating()) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyTileLoadEvent: posted for %s client-side, aborting", event.energyTile);
            return;
        }
        WorldData.get((World)event.world).energyNet.addTileEntity((TileEntity)event.energyTile);
    }

    @SubscribeEvent
    public void onEnergyTileUnload(EnergyTileUnloadEvent event) {
        if (!IC2.platform.isSimulating()) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyTileUnloadEvent: posted for %s client-side, aborting", event.energyTile);
            return;
        }
        WorldData.get((World)event.world).energyNet.removeTileEntity((TileEntity)event.energyTile);
    }
}

