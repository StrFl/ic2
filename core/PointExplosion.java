/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.ChunkPosition
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 */
package ic2.core;

import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.event.ExplosionEvent;
import ic2.core.util.Util;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class PointExplosion
extends Explosion {
    private final World world;
    private final Entity entity;
    private final float dropRate;
    private final int entityDamage;

    public PointExplosion(World world1, Entity entity, EntityLivingBase exploder, double x, double y, double z, float power, float dropRate1, int entityDamage1) {
        super(world1, (Entity)exploder, x, y, z, power);
        this.world = world1;
        this.entity = entity;
        this.dropRate = dropRate1;
        this.entityDamage = entityDamage1;
    }

    public void doExplosionA() {
        ExplosionEvent event = new ExplosionEvent(this.world, this.entity, this.explosionX, this.explosionY, this.explosionZ, this.explosionSize, (EntityLivingBase)this.exploder, 0, 1.0);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        for (int x = Util.roundToNegInf(this.explosionX) - 1; x <= Util.roundToNegInf(this.explosionX) + 1; ++x) {
            for (int y = Util.roundToNegInf(this.explosionY) - 1; y <= Util.roundToNegInf(this.explosionY) + 1; ++y) {
                for (int z = Util.roundToNegInf(this.explosionZ) - 1; z <= Util.roundToNegInf(this.explosionZ) + 1; ++z) {
                    Block block = this.world.getBlock(x, y, z);
                    if (!(block.getExplosionResistance(this.exploder, this.world, x, y, z, this.explosionX, this.explosionY, this.explosionZ) < this.explosionSize * 10.0f)) continue;
                    this.affectedBlockPositions.add(new ChunkPosition(x, y, z));
                }
            }
        }
        List entitiesInRange = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBox((double)(this.explosionX - 2.0), (double)(this.explosionY - 2.0), (double)(this.explosionZ - 2.0), (double)(this.explosionX + 2.0), (double)(this.explosionY + 2.0), (double)(this.explosionZ + 2.0)));
        for (Entity entity : entitiesInRange) {
            entity.attackEntityFrom(DamageSource.setExplosionSource((Explosion)this), (float)this.entityDamage);
        }
        this.explosionSize = 1.0f / this.dropRate;
    }
}

