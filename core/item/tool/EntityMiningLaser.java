/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.common.registry.IThrowableEntity
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.boss.EntityDragon
 *  net.minecraft.entity.boss.EntityDragonPart
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.EntityDamageSourceIndirect
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.registry.IThrowableEntity;
import ic2.api.event.LaserEvent;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.MaterialIC2TNT;
import ic2.core.util.StackUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EntityMiningLaser
extends Entity
implements IThrowableEntity {
    public float range = 0.0f;
    public float power = 0.0f;
    public int blockBreaks = 0;
    public boolean explosive = false;
    public static Set<Block> unmineableBlocks = new HashSet<Block>(Arrays.asList(Blocks.brick_block, Blocks.obsidian, Blocks.lava, Blocks.flowing_lava, Blocks.water, Blocks.flowing_water, Blocks.bedrock, StackUtil.getBlock(Ic2Items.reinforcedStone), StackUtil.getBlock(Ic2Items.reinforcedDoorBlock)));
    public static final double laserSpeed = 1.0;
    public EntityLivingBase owner;
    public boolean headingSet = false;
    public boolean smelt = false;
    private int ticksInAir = 0;

    public EntityMiningLaser(World world) {
        super(world);
        this.setSize(0.8f, 0.8f);
        this.yOffset = 0.0f;
    }

    public EntityMiningLaser(World world, EntityLivingBase entityliving, float range, float power, int blockBreaks, boolean explosive) {
        this(world, entityliving, range, power, blockBreaks, explosive, entityliving.rotationYaw, entityliving.rotationPitch);
    }

    public EntityMiningLaser(World world, EntityLivingBase entityliving, float range, float power, int blockBreaks, boolean explosive, boolean smelt) {
        this(world, entityliving, range, power, blockBreaks, explosive, entityliving.rotationYaw, entityliving.rotationPitch);
        this.smelt = smelt;
    }

    public EntityMiningLaser(World world, EntityLivingBase entityliving, float range, float power, int blockBreaks, boolean explosive, double yawDeg, double pitchDeg) {
        this(world, entityliving, range, power, blockBreaks, explosive, yawDeg, pitchDeg, entityliving.posY + (double)entityliving.getEyeHeight() - 0.1);
    }

    public EntityMiningLaser(World world, EntityLivingBase entityliving, float range, float power, int blockBreaks, boolean explosive, double yawDeg, double pitchDeg, double y) {
        super(world);
        this.owner = entityliving;
        this.setSize(0.8f, 0.8f);
        this.yOffset = 0.0f;
        double yaw = Math.toRadians(yawDeg);
        double pitch = Math.toRadians(pitchDeg);
        double x = entityliving.posX - Math.cos(yaw) * 0.16;
        double z = entityliving.posZ - Math.sin(yaw) * 0.16;
        double startMotionX = -Math.sin(yaw) * Math.cos(pitch);
        double startMotionY = -Math.sin(pitch);
        double startMotionZ = Math.cos(yaw) * Math.cos(pitch);
        this.setPosition(x, y, z);
        this.setLaserHeading(startMotionX, startMotionY, startMotionZ, 1.0);
        this.range = range;
        this.power = power;
        this.blockBreaks = blockBreaks;
        this.explosive = explosive;
    }

    protected void entityInit() {
    }

    public void setLaserHeading(double motionX, double motionY, double motionZ, double speed) {
        double currentSpeed = MathHelper.sqrt_double((double)(motionX * motionX + motionY * motionY + motionZ * motionZ));
        this.motionX = motionX / currentSpeed * speed;
        this.motionY = motionY / currentSpeed * speed;
        this.motionZ = motionZ / currentSpeed * speed;
        this.prevRotationYaw = this.rotationYaw = (float)Math.toDegrees(Math.atan2(motionX, motionZ));
        this.prevRotationPitch = this.rotationPitch = (float)Math.toDegrees(Math.atan2(motionY, MathHelper.sqrt_double((double)(motionX * motionX + motionZ * motionZ))));
        this.headingSet = true;
    }

    public void setVelocity(double motionX, double motionY, double motionZ) {
        this.setLaserHeading(motionX, motionY, motionZ, 1.0);
    }

    public void onUpdate() {
        super.onUpdate();
        if (IC2.platform.isSimulating() && (this.range < 1.0f || this.power <= 0.0f || this.blockBreaks <= 0)) {
            if (this.explosive) {
                this.explode();
            }
            this.setDead();
            return;
        }
        ++this.ticksInAir;
        Vec3 oldPosition = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
        Vec3 newPosition = Vec3.createVectorHelper((double)(this.posX + this.motionX), (double)(this.posY + this.motionY), (double)(this.posZ + this.motionZ));
        MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(oldPosition, newPosition, false, true, false);
        oldPosition = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
        newPosition = movingobjectposition != null ? Vec3.createVectorHelper((double)movingobjectposition.hitVec.xCoord, (double)movingobjectposition.hitVec.yCoord, (double)movingobjectposition.hitVec.zCoord) : Vec3.createVectorHelper((double)(this.posX + this.motionX), (double)(this.posY + this.motionY), (double)(this.posZ + this.motionZ));
        Entity entity = null;
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
        double d = 0.0;
        for (int l = 0; l < list.size(); ++l) {
            double d1;
            float f4;
            AxisAlignedBB axisalignedbb1;
            MovingObjectPosition movingobjectposition1;
            Entity entity1 = (Entity)list.get(l);
            if (!entity1.canBeCollidedWith() || entity1 == this.owner && this.ticksInAir < 5 || (movingobjectposition1 = (axisalignedbb1 = entity1.boundingBox.expand((double)(f4 = 0.3f), (double)f4, (double)f4)).calculateIntercept(oldPosition, newPosition)) == null || !((d1 = oldPosition.distanceTo(movingobjectposition1.hitVec)) < d) && d != 0.0) continue;
            entity = entity1;
            d = d1;
        }
        if (entity != null) {
            movingobjectposition = new MovingObjectPosition(entity);
        }
        if (movingobjectposition != null && IC2.platform.isSimulating()) {
            if (this.explosive) {
                this.explode();
                this.setDead();
                return;
            }
            if (movingobjectposition.entityHit != null) {
                LaserEvent.LaserHitsEntityEvent tEvent = new LaserEvent.LaserHitsEntityEvent(this.worldObj, this, this.owner, this.range, this.power, this.blockBreaks, this.explosive, this.smelt, movingobjectposition.entityHit);
                MinecraftForge.EVENT_BUS.post((Event)tEvent);
                if (this.takeDataFromEvent(tEvent)) {
                    int damage = (int)this.power;
                    if (damage > 0) {
                        if (entity != null) {
                            entity.setFire(damage * (this.smelt ? 2 : 1));
                        }
                        if (tEvent.hitentity.attackEntityFrom(new EntityDamageSourceIndirect("arrow", (Entity)this, (Entity)this.owner).setProjectile(), (float)damage) && this.owner instanceof EntityPlayer && (tEvent.hitentity instanceof EntityDragon && ((EntityDragon)tEvent.hitentity).getHealth() <= 0.0f || tEvent.hitentity instanceof EntityDragonPart && ((EntityDragonPart)tEvent.hitentity).entityDragonObj instanceof EntityDragon && ((EntityLivingBase)((EntityDragonPart)tEvent.hitentity).entityDragonObj).getHealth() <= 0.0f)) {
                            IC2.achievements.issueAchievement((EntityPlayer)this.owner, "killDragonMiningLaser");
                        }
                    }
                    this.setDead();
                }
            } else {
                Block tBlock;
                LaserEvent.LaserHitsBlockEvent tEvent = new LaserEvent.LaserHitsBlockEvent(this.worldObj, this, this.owner, this.range, this.power, this.blockBreaks, this.explosive, this.smelt, movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ, movingobjectposition.sideHit, 0.9f, true, true);
                MinecraftForge.EVENT_BUS.post((Event)tEvent);
                if (this.takeDataFromEvent(tEvent) && (tBlock = this.worldObj.getBlock(tEvent.x, tEvent.y, tEvent.z)) != null && tBlock != Blocks.glass && tBlock != Blocks.glass_pane && !StackUtil.equals(tBlock, Ic2Items.reinforcedGlass)) {
                    if (!this.canMine(tBlock)) {
                        this.setDead();
                    } else if (IC2.platform.isSimulating()) {
                        float resis = 0.0f;
                        resis = tBlock.getExplosionResistance((Entity)this, this.worldObj, tEvent.x, tEvent.y, tEvent.z, this.posX, this.posY, this.posZ) + 0.3f;
                        this.power -= resis / 10.0f;
                        if (this.power >= 0.0f) {
                            if (tBlock.getMaterial() == Material.tnt || tBlock.getMaterial() == MaterialIC2TNT.instance) {
                                tBlock.onBlockDestroyedByExplosion(this.worldObj, tEvent.x, tEvent.y, tEvent.z, new Explosion(this.worldObj, (Entity)this, (double)tEvent.x, (double)tEvent.y, (double)tEvent.z, 1.0f));
                            } else if (this.smelt) {
                                if (tBlock.getMaterial() == Material.wood) {
                                    tEvent.dropBlock = false;
                                } else {
                                    for (ItemStack isa : tBlock.getDrops(this.worldObj, tEvent.x, tEvent.y, tEvent.z, this.worldObj.getBlockMetadata(tEvent.x, tEvent.y, tEvent.z), 0)) {
                                        ItemStack is = FurnaceRecipes.smelting().getSmeltingResult(isa);
                                        if (is == null) continue;
                                        Block newBlock = StackUtil.getBlock(is);
                                        if (newBlock != null && newBlock != tBlock) {
                                            tEvent.removeBlock = false;
                                            tEvent.dropBlock = false;
                                            this.worldObj.setBlock(tEvent.x, tEvent.y, tEvent.z, newBlock, is.getItemDamage(), 7);
                                        } else {
                                            tEvent.dropBlock = false;
                                            float var6 = 0.7f;
                                            double var7 = (double)(this.worldObj.rand.nextFloat() * var6) + (double)(1.0f - var6) * 0.5;
                                            double var9 = (double)(this.worldObj.rand.nextFloat() * var6) + (double)(1.0f - var6) * 0.5;
                                            double var11 = (double)(this.worldObj.rand.nextFloat() * var6) + (double)(1.0f - var6) * 0.5;
                                            EntityItem var13 = new EntityItem(this.worldObj, (double)tEvent.x + var7, (double)tEvent.y + var9, (double)tEvent.z + var11, is.copy());
                                            var13.delayBeforeCanPickup = 10;
                                            this.worldObj.spawnEntityInWorld((Entity)var13);
                                        }
                                        this.power = 0.0f;
                                    }
                                }
                            }
                            if (tEvent.removeBlock) {
                                if (tEvent.dropBlock) {
                                    tBlock.dropBlockAsItemWithChance(this.worldObj, tEvent.x, tEvent.y, tEvent.z, this.worldObj.getBlockMetadata(tEvent.x, tEvent.y, tEvent.z), tEvent.dropChance, 0);
                                }
                                this.worldObj.setBlockToAir(tEvent.x, tEvent.y, tEvent.z);
                                if (this.worldObj.rand.nextInt(10) == 0 && tBlock.getMaterial().getCanBurn()) {
                                    this.worldObj.setBlock(tEvent.x, tEvent.y, tEvent.z, (Block)Blocks.fire, 0, 7);
                                }
                            }
                            --this.blockBreaks;
                        }
                    }
                }
            }
        } else {
            this.power -= 0.5f;
        }
        this.setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        this.range = (float)((double)this.range - Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
        if (this.isInWater()) {
            this.setDead();
        }
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    }

    public float getShadowSize() {
        return 0.0f;
    }

    public boolean takeDataFromEvent(LaserEvent aEvent) {
        this.owner = aEvent.owner;
        this.range = aEvent.range;
        this.power = aEvent.power;
        this.blockBreaks = aEvent.blockBreaks;
        this.explosive = aEvent.explosive;
        this.smelt = aEvent.smelt;
        if (aEvent.isCanceled()) {
            this.setDead();
            return false;
        }
        return true;
    }

    public void explode() {
        if (IC2.platform.isSimulating()) {
            LaserEvent.LaserExplodesEvent tEvent = new LaserEvent.LaserExplodesEvent(this.worldObj, this, this.owner, this.range, this.power, this.blockBreaks, this.explosive, this.smelt, 5.0f, 0.85f, 0.55f);
            MinecraftForge.EVENT_BUS.post((Event)tEvent);
            if (this.takeDataFromEvent(tEvent)) {
                ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, this.posX, this.posY, this.posZ, tEvent.explosionpower, tEvent.explosiondroprate);
                explosion.doExplosion();
            }
        }
    }

    public boolean canMine(Block block) {
        return !unmineableBlocks.contains(block);
    }

    public Entity getThrower() {
        return this.owner;
    }

    public void setThrower(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            this.owner = (EntityLivingBase)entity;
        }
    }
}

