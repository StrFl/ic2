/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  org.ejml.simple.SimpleBase
 *  org.ejml.simple.SimpleMatrix
 */
package ic2.core;

import ic2.core.IC2;
import net.minecraft.world.World;
import org.ejml.simple.SimpleBase;
import org.ejml.simple.SimpleMatrix;

public class WindSim {
    private int windStrength = 10 + IC2.random.nextInt(10);
    private int windDirection = IC2.random.nextInt(360);
    public int windTicker;
    private final World world;
    private final SimpleMatrix windHeightCoefficients;

    public WindSim(World world) {
        this.world = world;
        int height = IC2.getWorldHeight(world);
        int seaLevel = IC2.getSeaLevel(world);
        double sh = seaLevel + (height - seaLevel) / 2;
        double fh = height * 9 / 8;
        SimpleMatrix a = new SimpleMatrix(3, 3);
        SimpleMatrix b = new SimpleMatrix(3, 1);
        a.setRow(0, 0, new double[]{sh * sh, sh * sh * sh, sh * sh * sh * sh});
        b.set(0, 1.0);
        a.setRow(1, 0, new double[]{fh * fh, fh * fh * fh, fh * fh * fh * fh});
        b.set(1, 0.0);
        a.setRow(2, 0, new double[]{2.0 * sh, 3.0 * sh * sh, 4.0 * sh * sh * sh});
        b.set(2, 0.0);
        this.windHeightCoefficients = (SimpleMatrix)a.solve((SimpleBase)b);
    }

    public void updateWind() {
        if (this.windTicker++ % 128 != 0) {
            return;
        }
        int upChance = 10;
        int downChance = 10;
        if (this.windStrength > 20) {
            upChance -= this.windStrength - 20;
        } else if (this.windStrength < 10) {
            downChance -= 10 - this.windStrength;
        }
        if (IC2.random.nextInt(100) < upChance) {
            ++this.windStrength;
        } else if (IC2.random.nextInt(100) < downChance) {
            --this.windStrength;
        }
        switch (IC2.random.nextInt(3)) {
            case 0: {
                this.windDirection = this.chancewindDirection(-18);
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                this.windDirection = this.chancewindDirection(18);
            }
        }
    }

    public double getWindAt(double height) {
        double ret = this.windStrength;
        SimpleMatrix x = new SimpleMatrix(1, 3);
        x.setRow(0, 0, new double[]{height * height, height * height * height, height * height * height * height});
        ret *= ((SimpleMatrix)x.mult((SimpleBase)this.windHeightCoefficients)).get(0);
        if (this.world.isThundering()) {
            ret *= 1.5;
        } else if (this.world.isRaining()) {
            ret *= 1.25;
        }
        return ret *= 2.4;
    }

    private int chancewindDirection(int amount) {
        this.windDirection += amount;
        if (this.windDirection < 0) {
            return 359 - this.windDirection;
        }
        if (this.windDirection > 359) {
            return 0 + (this.windDirection - 359);
        }
        return this.windDirection;
    }
}

