/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.energy;

import ic2.api.Direction;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.NodeStats;
import ic2.core.IC2;
import ic2.core.WorldData;
import ic2.core.energy.EventHandler;
import ic2.core.util.Util;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyNetGlobal
implements IEnergyNet {
    public static final boolean replaceConflicting = System.getProperty("ic2.energynet.replaceconflicting") != null;
    public static final boolean debugTileManagement = System.getProperty("ic2.energynet.debugtilemanagement") != null;
    public static final boolean debugGrid = System.getProperty("ic2.energynet.debuggrid") != null;
    public static final boolean debugGridVerbose = debugGrid && System.getProperty("ic2.energynet.debuggrid").equals("verbose");
    public static final boolean checkApi = System.getProperty("ic2.energynet.checkapi") != null;
    public static final boolean logAll = System.getProperty("ic2.energynet.logall") != null;

    public static EnergyNetGlobal initialize() {
        return new EnergyNetGlobal();
    }

    private EnergyNetGlobal() {
        new EventHandler();
    }

    @Override
    public TileEntity getTileEntity(World world, int x, int y, int z) {
        return WorldData.get((World)world).energyNet.getTileEntity(x, y, z);
    }

    @Override
    public TileEntity getNeighbor(TileEntity te, ForgeDirection dir) {
        return WorldData.get((World)te.getWorldObj()).energyNet.getNeighbor(te, Direction.directions[(dir.ordinal() + 2) % 6]);
    }

    @Override
    public double getTotalEnergyEmitted(TileEntity te) {
        return WorldData.get((World)te.getWorldObj()).energyNet.getTotalEnergyEmitted(te);
    }

    @Override
    public double getTotalEnergySunken(TileEntity te) {
        return WorldData.get((World)te.getWorldObj()).energyNet.getTotalEnergySunken(te);
    }

    @Override
    public NodeStats getNodeStats(TileEntity te) {
        return WorldData.get((World)te.getWorldObj()).energyNet.getNodeStats(te);
    }

    @Override
    public double getPowerFromTier(int tier) {
        if (tier < 14) {
            return 8 << tier * 2;
        }
        return 8.0 * Math.pow(4.0, tier);
    }

    @Override
    public int getTierFromPower(double power) {
        if (power <= 0.0) {
            return 0;
        }
        return (int)Math.ceil(Math.log(power / 8.0) / Math.log(4.0));
    }

    public static void onTickEnd(World world) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        WorldData.get((World)world).energyNet.onTickEnd();
    }

    protected static boolean verifyGrid() {
        return Util.hasAssertions();
    }
}

