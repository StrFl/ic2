/*
 * Decompiled with CFR 0.152.
 */
package ic2.core;

public class TickrateTracker {
    private static final int historySize = 32;
    long lastTime;
    double[] totalPeriods = new double[32];
    int currentTotalIndex = 0;
    double[] tickPeriods = new double[32];
    int currentTickIndex = 0;

    public TickrateTracker() {
        for (int i = 0; i < this.totalPeriods.length; ++i) {
            this.totalPeriods[i] = 50.0;
        }
    }

    public void onTickStart() {
        long currentTime = System.nanoTime();
        this.totalPeriods[this.currentTotalIndex] = (double)(currentTime - this.lastTime) / 1000000.0;
        this.currentTotalIndex = (this.currentTotalIndex + 1) % 32;
        this.lastTime = currentTime;
    }

    public void onTickEnd() {
        long currentTime = System.nanoTime();
        this.tickPeriods[this.currentTickIndex] = (double)(currentTime - this.lastTime) / 1000000.0;
        this.currentTickIndex = (this.currentTickIndex + 1) % 32;
    }

    public double getLastTotal() {
        return this.totalPeriods[(this.currentTotalIndex + 32 - 1) % 32];
    }

    public double getMinTotal() {
        double min = Double.POSITIVE_INFINITY;
        for (double measurement : this.totalPeriods) {
            if (!(measurement < min)) continue;
            min = measurement;
        }
        return min;
    }

    public double getMaxTotal() {
        double max = 0.0;
        for (double measurement : this.totalPeriods) {
            if (!(measurement > max)) continue;
            max = measurement;
        }
        return max;
    }

    public double getAvgTotal() {
        double sum = 0.0;
        for (double measurement : this.totalPeriods) {
            sum += measurement;
        }
        return sum / 32.0;
    }

    public double getLastTick() {
        return this.tickPeriods[(this.currentTickIndex + 32 - 1) % 32];
    }

    public double getMinTick() {
        double min = Double.POSITIVE_INFINITY;
        for (double measurement : this.tickPeriods) {
            if (!(measurement < min)) continue;
            min = measurement;
        }
        return min;
    }

    public double getMaxTick() {
        double max = 0.0;
        for (double measurement : this.tickPeriods) {
            if (!(measurement > max)) continue;
            max = measurement;
        }
        return max;
    }

    public double getAvgTick() {
        double sum = 0.0;
        for (double measurement : this.tickPeriods) {
            sum += measurement;
        }
        return sum / 32.0;
    }
}

