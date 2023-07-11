/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.core.item.tool;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.ContainerFullInv;
import ic2.core.IC2;
import ic2.core.item.tool.HandHeldMeter;
import ic2.core.network.ClientModifiable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerMeter
extends ContainerFullInv<HandHeldMeter> {
    private TileEntity uut;
    private double resultAvg;
    private double resultMin;
    private double resultMax;
    private int resultCount = 0;
    @ClientModifiable
    private Mode mode = Mode.EnergyIn;

    public ContainerMeter(EntityPlayer player, HandHeldMeter meter) {
        super(player, meter, 218);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (this.uut == null) {
            return;
        }
        NodeStats stats = EnergyNet.instance.getNodeStats(this.uut);
        double result = 0.0;
        switch (this.mode) {
            case EnergyIn: {
                result = stats.getEnergyIn();
                break;
            }
            case EnergyOut: {
                result = stats.getEnergyOut();
                break;
            }
            case EnergyGain: {
                result = stats.getEnergyIn() - stats.getEnergyOut();
                break;
            }
            case Voltage: {
                result = stats.getVoltage();
            }
        }
        if (this.resultCount == 0) {
            this.resultMin = this.resultMax = result;
            this.resultAvg = this.resultMax;
        } else {
            if (result < this.resultMin) {
                this.resultMin = result;
            }
            if (result > this.resultMax) {
                this.resultMax = result;
            }
            this.resultAvg = (this.resultAvg * (double)this.resultCount + result) / (double)(this.resultCount + 1);
        }
        ++this.resultCount;
        IC2.network.get().sendContainerFields(this, "resultAvg", "resultMin", "resultMax", "resultCount");
    }

    public double getResultAvg() {
        return this.resultAvg;
    }

    public double getResultMin() {
        return this.resultMin;
    }

    public double getResultMax() {
        return this.resultMax;
    }

    public int getResultCount() {
        return this.resultCount;
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        IC2.network.get().sendContainerField(this, "mode");
        this.reset();
    }

    public void reset() {
        if (IC2.platform.isSimulating()) {
            this.resultCount = 0;
        } else {
            IC2.network.get().sendContainerEvent(this, "reset");
        }
    }

    public void setUut(TileEntity uut) {
        assert (this.uut == null);
        assert (uut instanceof IEnergyTile);
        this.uut = uut;
    }

    @Override
    public void onContainerEvent(String event) {
        super.onContainerEvent(event);
        if ("reset".equals(event)) {
            this.reset();
        }
    }

    public static enum Mode {
        EnergyIn,
        EnergyOut,
        EnergyGain,
        Voltage;

    }
}

