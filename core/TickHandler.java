/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$ClientTickEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$Phase
 *  cpw.mods.fml.common.gameevent.TickEvent$ServerTickEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$WorldTickEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityEnderChest
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 */
package ic2.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.WorldData;
import ic2.core.energy.EnergyNetGlobal;
import ic2.core.init.MainConfig;
import ic2.core.item.tool.ItemNanoSaber;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import ic2.core.util.Util;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TickHandler {
    private static final boolean debugTickCallback = System.getProperty("ic2.debugtickcallback") != null;
    private static final Map<ITickCallback, Throwable> debugTraces = debugTickCallback ? new WeakHashMap() : null;
    private static Throwable lastDebugTrace;
    private static final Field updateEntityTick;

    public TickHandler() {
        FMLCommonHandler.instance().bus().register((Object)this);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world;
        block9: {
            world = event.world;
            if (IC2.platform.isSimulating()) {
                try {
                    if (world instanceof WorldServer && world.playerEntities.isEmpty() && world.getPersistentChunks().isEmpty() && updateEntityTick.getInt(world) >= 1200) {
                        return;
                    }
                    break block9;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (Minecraft.getMinecraft().isGamePaused()) {
                return;
            }
        }
        if (event.phase == TickEvent.Phase.START) {
            IC2.platform.profilerStartSection("Wind");
            WorldData.get((World)world).windSim.updateWind();
            IC2.platform.profilerEndStartSection("TickCallbacks");
            TickHandler.processTickCallbacks(world);
            if (IC2.platform.isSimulating() && ConfigUtil.getBool(MainConfig.get(), "balance/disableEnderChest")) {
                IC2.platform.profilerEndStartSection("EnderChestCheck");
                for (int i = 0; i < world.loadedTileEntityList.size(); ++i) {
                    TileEntity te = (TileEntity)world.loadedTileEntityList.get(i);
                    if (!(te instanceof TileEntityEnderChest) || te.isInvalid() || !world.blockExists(te.xCoord, te.yCoord, te.zCoord)) continue;
                    world.setBlockToAir(te.xCoord, te.yCoord, te.zCoord);
                    IC2.log.info(LogCategory.General, "Removed vanilla ender chest at %s.", Util.formatPosition(te));
                }
            }
            IC2.platform.profilerEndSection();
        } else {
            IC2.platform.profilerStartSection("EnergyNet");
            EnergyNetGlobal.onTickEnd(world);
            IC2.platform.profilerEndStartSection("Networking");
            IC2.network.get().onTickEnd(world);
            IC2.platform.profilerEndSection();
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ++ItemNanoSaber.ticker;
            IC2.getInstance().tickrateTracker.onTickStart();
        } else {
            IC2.getInstance().tickrateTracker.onTickEnd();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            IC2.platform.profilerStartSection("Keyboard");
            IC2.keyboard.sendKeyUpdate();
            IC2.platform.profilerEndStartSection("AudioManager");
            IC2.audioManager.onTick();
            IC2.platform.profilerEndStartSection("TickCallbacks");
            if (IC2.platform.getPlayerInstance() != null && IC2.platform.getPlayerInstance().worldObj != null) {
                TickHandler.processTickCallbacks(IC2.platform.getPlayerInstance().worldObj);
            }
            IC2.platform.profilerEndSection();
        }
    }

    public void addSingleTickCallback(World world, ITickCallback tickCallback) {
        WorldData worldData = WorldData.get(world);
        worldData.singleTickCallbacks.add(tickCallback);
        if (debugTickCallback) {
            debugTraces.put(tickCallback, new Throwable());
        }
    }

    public void addContinuousTickCallback(World world, ITickCallback tickCallback) {
        WorldData worldData = WorldData.get(world);
        if (!worldData.continuousTickCallbacksInUse) {
            worldData.continuousTickCallbacks.add(tickCallback);
        } else {
            worldData.continuousTickCallbacksToRemove.remove(tickCallback);
            worldData.continuousTickCallbacksToAdd.add(tickCallback);
        }
        if (debugTickCallback) {
            debugTraces.put(tickCallback, new Throwable());
        }
    }

    public void removeContinuousTickCallback(World world, ITickCallback tickCallback) {
        WorldData worldData = WorldData.get(world);
        if (!worldData.continuousTickCallbacksInUse) {
            worldData.continuousTickCallbacks.remove(tickCallback);
        } else {
            worldData.continuousTickCallbacksToAdd.remove(tickCallback);
            worldData.continuousTickCallbacksToRemove.add(tickCallback);
        }
    }

    public static Throwable getLastDebugTrace() {
        return lastDebugTrace;
    }

    private static void processTickCallbacks(World world) {
        WorldData worldData = WorldData.get(world);
        IC2.platform.profilerStartSection("SingleTickCallback");
        ITickCallback tickCallback = worldData.singleTickCallbacks.poll();
        while (tickCallback != null) {
            if (debugTickCallback) {
                lastDebugTrace = debugTraces.remove(tickCallback);
            }
            IC2.platform.profilerStartSection(tickCallback.getClass().getName());
            tickCallback.tickCallback(world);
            IC2.platform.profilerEndSection();
            tickCallback = worldData.singleTickCallbacks.poll();
        }
        IC2.platform.profilerEndStartSection("ContTickCallback");
        worldData.continuousTickCallbacksInUse = true;
        for (ITickCallback tickCallback2 : worldData.continuousTickCallbacks) {
            if (debugTickCallback) {
                lastDebugTrace = debugTraces.remove(tickCallback2);
            }
            IC2.platform.profilerStartSection(tickCallback2.getClass().getName());
            tickCallback2.tickCallback(world);
            IC2.platform.profilerEndSection();
        }
        worldData.continuousTickCallbacksInUse = false;
        if (debugTickCallback) {
            lastDebugTrace = null;
        }
        worldData.continuousTickCallbacks.addAll(worldData.continuousTickCallbacksToAdd);
        worldData.continuousTickCallbacksToAdd.clear();
        worldData.continuousTickCallbacks.removeAll(worldData.continuousTickCallbacksToRemove);
        worldData.continuousTickCallbacksToRemove.clear();
        IC2.platform.profilerEndSection();
    }

    static {
        updateEntityTick = ReflectionUtil.getField(WorldServer.class, "field_80004_Q", "updateEntityTick");
    }
}

