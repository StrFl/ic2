/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.energy;

import ic2.core.energy.Node;
import net.minecraftforge.common.util.ForgeDirection;

class Change {
    Node node;
    final ForgeDirection dir;
    private double amount;
    private double voltage;

    Change(Node node, ForgeDirection dir, double amount, double voltage) {
        this.node = node;
        this.dir = dir;
        this.setAmount(amount);
        this.setVoltage(voltage);
    }

    public String toString() {
        return this.node + "@" + this.dir + " " + this.amount + " EU / " + this.voltage + " V";
    }

    double getAmount() {
        return this.amount;
    }

    void setAmount(double amount) {
        double intAmount = Math.rint(amount);
        if (Math.abs(amount - intAmount) < 0.001) {
            amount = intAmount;
        }
        assert (!Double.isInfinite(amount) && !Double.isNaN(amount));
        this.amount = amount;
    }

    double getVoltage() {
        return this.voltage;
    }

    private void setVoltage(double voltage) {
        double intVoltage = Math.rint(this.amount);
        if (Math.abs(voltage - intVoltage) < 0.001) {
            voltage = intVoltage;
        }
        assert (!Double.isInfinite(voltage) && !Double.isNaN(voltage));
        this.voltage = voltage;
    }
}

