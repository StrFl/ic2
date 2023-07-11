/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package ic2.core;

import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.WindSim;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.network.NetworkManager;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.world.World;

public class WorldData {
    private static Map<World, WorldData> mapping = new WeakHashMap<World, WorldData>();
    public final Queue<ITickCallback> singleTickCallbacks = new ArrayDeque<ITickCallback>();
    public final Set<ITickCallback> continuousTickCallbacks = new HashSet<ITickCallback>();
    public boolean continuousTickCallbacksInUse = false;
    public final List<ITickCallback> continuousTickCallbacksToAdd = new ArrayList<ITickCallback>();
    public final List<ITickCallback> continuousTickCallbacksToRemove = new ArrayList<ITickCallback>();
    public final EnergyNetLocal energyNet;
    public final Set<NetworkManager.TileEntityField> networkedFieldsToUpdate = new HashSet<NetworkManager.TileEntityField>();
    public int ticksLeftToNetworkUpdate = 1;
    public final WindSim windSim;

    public static WorldData get(World world) {
        if (world == null) {
            throw new IllegalArgumentException("world is null");
        }
        WorldData ret = mapping.get(world);
        if (ret == null) {
            ret = new WorldData(world);
            mapping.put(world, ret);
        }
        return ret;
    }

    public static void onWorldUnload(World world) {
        mapping.remove(world);
    }

    private WorldData(World world) {
        if (IC2.platform.isSimulating()) {
            this.energyNet = new EnergyNetLocal();
            this.windSim = new WindSim(world);
        } else {
            this.energyNet = null;
            this.windSim = null;
        }
    }
}

