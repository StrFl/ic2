/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.ChunkCache
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 */
package ic2.core;

import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.event.ExplosionEvent;
import ic2.api.tile.ExplosionWhitelist;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.IC2Potion;
import ic2.core.item.armor.ItemArmorHazmat;
import ic2.core.util.ItemStackWrapper;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ExplosionIC2
extends Explosion {
    private final Random ExplosionRNG = new Random();
    private final World worldObj;
    private final int mapHeight;
    private final float power;
    private final float explosionDropRate;
    private final Type type;
    private final int radiationRange;
    private final EntityLivingBase igniter;
    private final double maxDistance;
    private final int areaSize;
    private final int areaX;
    private final int areaZ;
    private final DamageSource damageSource;
    private final List<EntityDamage> entitiesInRange = new ArrayList<EntityDamage>();
    private final long[][] destroyedBlockPositions;
    private ChunkCache chunkCache;
    private static final double dropPowerLimit = 8.0;
    private static final double damageAtDropPowerLimit = 32.0;
    private static final double accelerationAtDropPowerLimit = 0.7;
    private static final double motionLimit = 60.0;
    private static final int secondaryRayCount = 5;
    private static final int bitSetElementSize = 2;

    public ExplosionIC2(World world, Entity entity, double x, double y, double z, float power1, float drop) {
        this(world, entity, x, y, z, power1, drop, Type.Normal);
    }

    public ExplosionIC2(World world, Entity entity, double x, double y, double z, float power1, float drop, Type type1) {
        this(world, entity, x, y, z, power1, drop, type1, null, 0);
    }

    public ExplosionIC2(World world, Entity entity, double x, double y, double z, float power1, float drop, Type type1, EntityLivingBase igniter1, int radiationRange1) {
        super(world, entity, x, y, z, power1);
        this.worldObj = world;
        this.mapHeight = IC2.getWorldHeight(world);
        this.exploder = entity;
        this.power = power1;
        this.explosionDropRate = drop;
        this.explosionX = x;
        this.explosionY = y;
        this.explosionZ = z;
        this.type = type1;
        this.igniter = igniter1;
        this.radiationRange = radiationRange1;
        this.maxDistance = (double)this.power / 0.4;
        int maxDistanceInt = (int)Math.ceil(this.maxDistance);
        this.areaSize = maxDistanceInt * 2;
        this.areaX = Util.roundToNegInf(this.explosionX) - maxDistanceInt;
        this.areaZ = Util.roundToNegInf(this.explosionZ) - maxDistanceInt;
        this.damageSource = this.isNuclear() ? IC2DamageSource.getNukeSource(this) : DamageSource.setExplosionSource((Explosion)this);
        this.destroyedBlockPositions = new long[this.mapHeight][];
    }

    public void doExplosion() {
        boolean entitiesAreInRange;
        if (this.power <= 0.0f) {
            return;
        }
        ExplosionEvent event = new ExplosionEvent(this.worldObj, this.exploder, this.explosionX, this.explosionY, this.explosionZ, this.power, this.igniter, this.radiationRange, this.maxDistance);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        this.chunkCache = new ChunkCache(this.worldObj, (int)this.explosionX - this.areaSize / 2, (int)this.explosionY - this.areaSize / 2, (int)this.explosionZ - this.areaSize / 2, (int)this.explosionX + this.areaSize / 2, (int)this.explosionY + this.areaSize / 2, (int)this.explosionZ + this.areaSize / 2, 0);
        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox((double)(this.explosionX - this.maxDistance), (double)(this.explosionY - this.maxDistance), (double)(this.explosionZ - this.maxDistance), (double)(this.explosionX + this.maxDistance), (double)(this.explosionY + this.maxDistance), (double)(this.explosionZ + this.maxDistance)));
        for (Entity entity : entities) {
            if (!(entity instanceof EntityLivingBase) && !(entity instanceof EntityItem)) continue;
            int distance = (int)(Util.square(entity.posX - this.explosionX) + Util.square(entity.posY - this.explosionY) + Util.square(entity.posZ - this.explosionZ));
            double health = ExplosionIC2.getEntityHealth(entity);
            this.entitiesInRange.add(new EntityDamage(entity, distance, health));
        }
        boolean bl = entitiesAreInRange = !this.entitiesInRange.isEmpty();
        if (entitiesAreInRange) {
            Collections.sort(this.entitiesInRange, new Comparator<EntityDamage>(){

                @Override
                public int compare(EntityDamage a, EntityDamage b) {
                    return a.distance - b.distance;
                }
            });
        }
        int steps = (int)Math.ceil(Math.PI / Math.atan(1.0 / this.maxDistance));
        for (int phi_n = 0; phi_n < 2 * steps; ++phi_n) {
            for (int theta_n = 0; theta_n < steps; ++theta_n) {
                double phi = Math.PI * 2 / (double)steps * (double)phi_n;
                double theta = Math.PI / (double)steps * (double)theta_n;
                this.shootRay(this.explosionX, this.explosionY, this.explosionZ, phi, theta, this.power, entitiesAreInRange && phi_n % 8 == 0 && theta_n % 8 == 0);
            }
        }
        for (EntityDamage entry : this.entitiesInRange) {
            double motionSq;
            Entity entity = entry.entity;
            entity.attackEntityFrom(this.damageSource, (float)entry.damage);
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)entity;
                if (this.isNuclear() && this.igniter != null && entityPlayer == this.igniter && entityPlayer.getHealth() <= 0.0f) {
                    IC2.achievements.issueAchievement(entityPlayer, "dieFromOwnNuke");
                }
            }
            double reduction = (motionSq = Util.square(entry.motionX) + Util.square(entity.motionY) + Util.square(entity.motionZ)) > 3600.0 ? Math.sqrt(3600.0 / motionSq) : 1.0;
            entity.motionX += entry.motionX * reduction;
            entity.motionY += entry.motionY * reduction;
            entity.motionZ += entry.motionZ * reduction;
        }
        if (this.isNuclear() && this.radiationRange >= 1) {
            for (EntityLiving entity : this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox((double)(this.explosionX - (double)this.radiationRange), (double)(this.explosionY - (double)this.radiationRange), (double)(this.explosionZ - (double)this.radiationRange), (double)(this.explosionX + (double)this.radiationRange), (double)(this.explosionY + (double)this.radiationRange), (double)(this.explosionZ + (double)this.radiationRange)))) {
                if (ItemArmorHazmat.hasCompleteHazmat((EntityLivingBase)entity)) continue;
                double distance = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ);
                int hungerLength = (int)(120.0 * ((double)this.radiationRange - distance));
                int poisonLength = (int)(80.0 * ((double)(this.radiationRange / 3) - distance));
                if (hungerLength >= 0) {
                    entity.addPotionEffect(new PotionEffect(Potion.hunger.id, hungerLength, 0));
                }
                if (poisonLength < 0) continue;
                IC2Potion.radiation.applyTo((EntityLivingBase)entity, poisonLength, 0);
            }
        }
        IC2.network.get().initiateExplosionEffect(this.worldObj, this.explosionX, this.explosionY, this.explosionZ);
        Random rng = this.worldObj.rand;
        boolean doDrops = this.worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops");
        HashMap blocksToDrop = new HashMap();
        this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0f, (1.0f + (rng.nextFloat() - rng.nextFloat()) * 0.2f) * 0.7f);
        for (int y = 0; y < this.destroyedBlockPositions.length; ++y) {
            long[] bitSet = this.destroyedBlockPositions[y];
            if (bitSet == null) continue;
            int index = -2;
            while ((index = ExplosionIC2.nextSetIndex(index + 2, bitSet, 2)) != -1) {
                int realIndex = index / 2;
                int z = realIndex / this.areaSize;
                int x = realIndex - z * this.areaSize;
                Block block = this.chunkCache.getBlock(x += this.areaX, y, z += this.areaZ);
                if (this.power < 20.0f) {
                    double effectX = (float)x + rng.nextFloat();
                    double effectY = (float)y + rng.nextFloat();
                    double effectZ = (float)z + rng.nextFloat();
                    double d3 = effectX - this.explosionX;
                    double d4 = effectY - this.explosionY;
                    double d5 = effectZ - this.explosionZ;
                    double effectDistance = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
                    d3 /= effectDistance;
                    d4 /= effectDistance;
                    d5 /= effectDistance;
                    double d7 = 0.5 / (effectDistance / (double)this.power + 0.1);
                    this.worldObj.spawnParticle("explode", (effectX + this.explosionX) / 2.0, (effectY + this.explosionY) / 2.0, (effectZ + this.explosionZ) / 2.0, d3 *= (d7 *= (double)(rng.nextFloat() * rng.nextFloat() + 0.3f)), d4 *= d7, d5 *= d7);
                    this.worldObj.spawnParticle("smoke", effectX, effectY, effectZ, d3, d4, d5);
                }
                if (doDrops && ExplosionIC2.getAtIndex(index, bitSet, 2) == 1) {
                    int meta = this.chunkCache.getBlockMetadata(x, y, z);
                    for (ItemStack itemStack : block.getDrops(this.worldObj, x, y, z, meta, 0)) {
                        ItemStackWrapper isw;
                        Map map;
                        if (!(rng.nextFloat() <= this.explosionDropRate)) continue;
                        XZposition xZposition = new XZposition(x / 2, z / 2);
                        if (!blocksToDrop.containsKey(xZposition)) {
                            blocksToDrop.put(xZposition, new HashMap());
                        }
                        if (!(map = (Map)blocksToDrop.get(xZposition)).containsKey(isw = new ItemStackWrapper(itemStack))) {
                            map.put(isw, new DropData(itemStack.stackSize, y));
                            continue;
                        }
                        map.put(isw, ((DropData)map.get(isw)).add(itemStack.stackSize, y));
                    }
                }
                this.worldObj.setBlockToAir(x, y, z);
                block.onBlockDestroyedByExplosion(this.worldObj, x, y, z, (Explosion)this);
            }
        }
        for (Map.Entry entry : blocksToDrop.entrySet()) {
            XZposition xZposition = (XZposition)entry.getKey();
            for (Map.Entry entry2 : ((Map)entry.getValue()).entrySet()) {
                int stackSize;
                ItemStackWrapper isw = (ItemStackWrapper)entry2.getKey();
                for (int count = ((DropData)entry2.getValue()).n; count > 0; count -= stackSize) {
                    stackSize = Math.min(count, 64);
                    EntityItem entityitem = new EntityItem(this.worldObj, (double)((float)xZposition.x + this.worldObj.rand.nextFloat()) * 2.0, (double)((DropData)entry2.getValue()).maxY + 0.5, (double)((float)xZposition.z + this.worldObj.rand.nextFloat()) * 2.0, StackUtil.copyWithSize(isw.stack, stackSize));
                    entityitem.delayBeforeCanPickup = 10;
                    this.worldObj.spawnEntityInWorld((Entity)entityitem);
                }
            }
        }
    }

    public void destroy(int x, int y, int z, boolean noDrop) {
        this.destroyUnchecked(x, y, z, noDrop);
    }

    private void destroyUnchecked(int x, int y, int z, boolean noDrop) {
        int index = (z - this.areaZ) * this.areaSize + (x - this.areaX);
        index *= 2;
        long[] array = this.destroyedBlockPositions[y];
        if (array == null) {
            array = ExplosionIC2.makeArray(Util.square(this.areaSize), 2);
            this.destroyedBlockPositions[y] = array;
        }
        if (noDrop) {
            ExplosionIC2.setAtIndex(index, array, 3);
        } else {
            ExplosionIC2.setAtIndex(index, array, 1);
        }
    }

    private void shootRay(double x, double y, double z, double phi, double theta, double power1, boolean killEntities) {
        int blockY;
        double deltaX = Math.sin(theta) * Math.cos(phi);
        double deltaY = Math.cos(theta);
        double deltaZ = Math.sin(theta) * Math.sin(phi);
        int step = 0;
        while ((blockY = Util.roundToNegInf(y)) >= 0 && blockY < this.mapHeight) {
            int blockZ;
            int blockX = Util.roundToNegInf(x);
            Block block = this.chunkCache.getBlock(blockX, blockY, blockZ = Util.roundToNegInf(z));
            double absorption = this.getAbsorption(block, blockX, blockY, blockZ);
            if (absorption > 1000.0 && !ExplosionWhitelist.isBlockWhitelisted(block)) {
                absorption = 0.5;
            } else {
                if (absorption > power1) break;
                if (block == Blocks.stone || block != Blocks.air && !block.isAir((IBlockAccess)this.worldObj, blockX, blockY, blockZ)) {
                    this.destroyUnchecked(blockX, blockY, blockZ, power1 > 8.0);
                }
            }
            if (killEntities && (step + 4) % 8 == 0 && !this.entitiesInRange.isEmpty() && power1 >= 0.25) {
                this.damageEntities(x, y, z, step, power1);
            }
            if (absorption > 10.0) {
                for (int i = 0; i < 5; ++i) {
                    this.shootRay(x, y, z, this.ExplosionRNG.nextDouble() * 2.0 * Math.PI, this.ExplosionRNG.nextDouble() * Math.PI, absorption * 0.4, false);
                }
            }
            power1 -= absorption;
            x += deltaX;
            y += deltaY;
            z += deltaZ;
            ++step;
        }
    }

    private double getAbsorption(Block block, int x, int y, int z) {
        double ret = 0.5;
        if (block == Blocks.air || block.isAir((IBlockAccess)this.worldObj, x, y, z)) {
            return ret;
        }
        if ((block == Blocks.water || block == Blocks.flowing_water) && this.type != Type.Normal) {
            ret += 1.0;
        } else {
            double extra = (double)(block.getExplosionResistance(this.exploder, this.worldObj, x, y, z, this.explosionX, this.explosionY, this.explosionZ) + 4.0f) * 0.3;
            ret = this.type != Type.Heat ? (ret += extra) : (ret += extra * 6.0);
        }
        return ret;
    }

    private void damageEntities(double x, double y, double z, int step, double power) {
        int index;
        if (step != 4) {
            int distanceMin = Util.square(step - 5);
            int indexStart = 0;
            int indexEnd = this.entitiesInRange.size() - 1;
            do {
                index = (indexStart + indexEnd) / 2;
                int distance = this.entitiesInRange.get((int)index).distance;
                if (distance < distanceMin) {
                    indexStart = index + 1;
                    continue;
                }
                indexEnd = distance > distanceMin ? index - 1 : index;
            } while (indexStart < indexEnd);
        } else {
            index = 0;
        }
        int distanceMax = Util.square(step + 5);
        for (int i = index; i < this.entitiesInRange.size(); ++i) {
            EntityDamage entry = this.entitiesInRange.get(i);
            if (entry.distance >= distanceMax) break;
            Entity entity = entry.entity;
            if (!(Util.square(entity.posX - x) + Util.square(entity.posY - y) + Util.square(entity.posZ - z) <= 25.0)) continue;
            double damage = 4.0 * power;
            entry.damage += damage;
            entry.health -= damage;
            double dx = entity.posX - this.explosionX;
            double dy = entity.posY - this.explosionY;
            double dz = entity.posZ - this.explosionZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            entry.motionX += dx / distance * 0.0875 * power;
            entry.motionY += dy / distance * 0.0875 * power;
            entry.motionZ += dz / distance * 0.0875 * power;
            if (!(entry.health <= 0.0)) continue;
            entity.attackEntityFrom(this.damageSource, (float)entry.damage);
            if (entity.isEntityAlive()) continue;
            this.entitiesInRange.remove(i);
            --i;
        }
    }

    public EntityLivingBase getExplosivePlacedBy() {
        return this.igniter;
    }

    private boolean isNuclear() {
        return this.type == Type.Nuclear;
    }

    private static double getEntityHealth(Entity entity) {
        if (entity instanceof EntityItem) {
            return 5.0;
        }
        return Double.POSITIVE_INFINITY;
    }

    private static long[] makeArray(int size, int step) {
        return new long[(size * step + 8 - step) / 8];
    }

    private static int nextSetIndex(int start, long[] array, int step) {
        int offset = start % 8;
        for (int i = start / 8; i < array.length; ++i) {
            long aval = array[i];
            for (int j = offset; j < 8; j += step) {
                int val = (int)(aval >> j & (long)((1 << step) - 1));
                if (val == 0) continue;
                return i * 8 + j;
            }
            offset = 0;
        }
        return -1;
    }

    private static int getAtIndex(int index, long[] array, int step) {
        return (int)(array[index / 8] >>> index % 8 & (long)((1 << step) - 1));
    }

    private static void setAtIndex(int index, long[] array, int value) {
        int n = index / 8;
        array[n] = array[n] | (long)(value << index % 8);
    }

    private static class EntityDamage {
        final Entity entity;
        final int distance;
        double health;
        double damage;
        double motionX;
        double motionY;
        double motionZ;

        EntityDamage(Entity entity, int distance, double health) {
            this.entity = entity;
            this.distance = distance;
            this.health = health;
        }
    }

    public static enum Type {
        Normal,
        Heat,
        Nuclear;

    }

    private static class DropData {
        int n;
        int maxY;

        DropData(int n1, int y) {
            this.n = n1;
            this.maxY = y;
        }

        public DropData add(int n1, int y) {
            this.n += n1;
            if (y > this.maxY) {
                this.maxY = y;
            }
            return this;
        }
    }

    private static class XZposition {
        int x;
        int z;

        XZposition(int x1, int z1) {
            this.x = x1;
            this.z = z1;
        }

        public boolean equals(Object obj) {
            if (obj instanceof XZposition) {
                XZposition xZposition = (XZposition)obj;
                return xZposition.x == this.x && xZposition.z == this.z;
            }
            return false;
        }

        public int hashCode() {
            return this.x * 31 ^ this.z;
        }
    }
}

