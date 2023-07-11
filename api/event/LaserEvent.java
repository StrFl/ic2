/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Cancelable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.WorldEvent
 */
package ic2.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

@Cancelable
public class LaserEvent
extends WorldEvent {
    public final Entity lasershot;
    public EntityLivingBase owner;
    public float range;
    public float power;
    public int blockBreaks;
    public boolean explosive;
    public boolean smelt;

    public LaserEvent(World world1, Entity lasershot1, EntityLivingBase owner1, float range1, float power1, int blockBreaks1, boolean explosive1, boolean smelt1) {
        super(world1);
        this.lasershot = lasershot1;
        this.owner = owner1;
        this.range = range1;
        this.power = power1;
        this.blockBreaks = blockBreaks1;
        this.explosive = explosive1;
        this.smelt = smelt1;
    }

    public static class LaserHitsEntityEvent
    extends LaserEvent {
        public Entity hitentity;

        public LaserHitsEntityEvent(World world1, Entity lasershot1, EntityLivingBase owner1, float range1, float power1, int blockBreaks1, boolean explosive1, boolean smelt1, Entity hitentity1) {
            super(world1, lasershot1, owner1, range1, power1, blockBreaks1, explosive1, smelt1);
            this.hitentity = hitentity1;
        }
    }

    public static class LaserHitsBlockEvent
    extends LaserEvent {
        public int x;
        public int y;
        public int z;
        public int side;
        public boolean removeBlock;
        public boolean dropBlock;
        public float dropChance;

        public LaserHitsBlockEvent(World world1, Entity lasershot1, EntityLivingBase owner1, float range1, float power1, int blockBreaks1, boolean explosive1, boolean smelt1, int x1, int y1, int z1, int side1, float dropChance1, boolean removeBlock1, boolean dropBlock1) {
            super(world1, lasershot1, owner1, range1, power1, blockBreaks1, explosive1, smelt1);
            this.x = x1;
            this.y = y1;
            this.z = z1;
            this.side = side1;
            this.removeBlock = removeBlock1;
            this.dropBlock = dropBlock1;
            this.dropChance = dropChance1;
        }
    }

    public static class LaserExplodesEvent
    extends LaserEvent {
        public float explosionpower;
        public float explosiondroprate;
        public float explosionentitydamage;

        public LaserExplodesEvent(World world1, Entity lasershot1, EntityLivingBase owner1, float range1, float power1, int blockBreaks1, boolean explosive1, boolean smelt1, float explosionpower1, float explosiondroprate1, float explosionentitydamage1) {
            super(world1, lasershot1, owner1, range1, power1, blockBreaks1, explosive1, smelt1);
            this.explosionpower = explosionpower1;
            this.explosiondroprate = explosiondroprate1;
            this.explosionentitydamage = explosionentitydamage1;
        }
    }

    public static class LaserShootEvent
    extends LaserEvent {
        ItemStack laseritem;

        public LaserShootEvent(World world1, Entity lasershot1, EntityLivingBase owner1, float range1, float power1, int blockBreaks1, boolean explosive1, boolean smelt1, ItemStack laseritem1) {
            super(world1, lasershot1, owner1, range1, power1, blockBreaks1, explosive1, smelt1);
            this.laseritem = laseritem1;
        }
    }
}

