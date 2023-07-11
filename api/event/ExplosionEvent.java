/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Cancelable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.WorldEvent
 */
package ic2.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

@Cancelable
public class ExplosionEvent
extends WorldEvent {
    public final Entity entity;
    public double x;
    public double y;
    public double z;
    public double power;
    public final EntityLivingBase igniter;
    public final int radiationRange;
    public final double rangeLimit;

    public ExplosionEvent(World world, Entity entity, double x, double y, double z, double power, EntityLivingBase igniter, int radiationRange, double rangeLimit) {
        super(world);
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.power = power;
        this.igniter = igniter;
        this.radiationRange = radiationRange;
        this.rangeLimit = rangeLimit;
    }
}

