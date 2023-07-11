/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IProjectile
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.PointExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityDynamite
extends Entity
implements IProjectile {
    public boolean sticky = false;
    public static final int netId = 142;
    public int stickX;
    public int stickY;
    public int stickZ;
    public int fuse = 100;
    private boolean inGround = false;
    public EntityLivingBase owner;
    private int ticksInGround;

    public EntityDynamite(World world, double x, double y, double z) {
        super(world);
        this.setSize(0.5f, 0.5f);
        this.setPosition(x, y, z);
        this.yOffset = 0.0f;
    }

    public EntityDynamite(World world, double x, double y, double z, boolean sticky1) {
        this(world, x, y, z);
        this.sticky = sticky1;
    }

    public EntityDynamite(World world) {
        this(world, 0.0, 0.0, 0.0);
    }

    public EntityDynamite(World world, EntityLivingBase entityliving) {
        super(world);
        this.owner = entityliving;
        this.setSize(0.5f, 0.5f);
        this.setLocationAndAngles(entityliving.posX, entityliving.posY + (double)entityliving.getEyeHeight(), entityliving.posZ, entityliving.rotationYaw, entityliving.rotationPitch);
        this.posX -= (double)(MathHelper.cos((float)(this.rotationYaw / 180.0f * 3.141593f)) * 0.16f);
        this.posY -= (double)0.1f;
        this.posZ -= (double)(MathHelper.sin((float)(this.rotationYaw / 180.0f * 3.141593f)) * 0.16f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        this.motionX = -MathHelper.sin((float)(this.rotationYaw / 180.0f * 3.141593f)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * 3.141593f));
        this.motionZ = MathHelper.cos((float)(this.rotationYaw / 180.0f * 3.141593f)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * 3.141593f));
        this.motionY = -MathHelper.sin((float)(this.rotationPitch / 180.0f * 3.141593f));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, 1.0f, 1.0f);
    }

    protected void entityInit() {
    }

    public void setThrowableHeading(double d, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt_double((double)(d * d + d1 * d1 + d2 * d2));
        d /= (double)f2;
        d1 /= (double)f2;
        d2 /= (double)f2;
        d += this.rand.nextGaussian() * (double)0.0075f * (double)f1;
        d1 += this.rand.nextGaussian() * (double)0.0075f * (double)f1;
        d2 += this.rand.nextGaussian() * (double)0.0075f * (double)f1;
        this.motionX = d *= (double)f;
        this.motionY = d1 *= (double)f;
        this.motionZ = d2 *= (double)f;
        float f3 = MathHelper.sqrt_double((double)(d * d + d2 * d2));
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(d, d2) * 180.0 / 3.1415927410125732);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(d1, f3) * 180.0 / 3.1415927410125732);
        this.ticksInGround = 0;
    }

    public void setVelocity(double d, double d1, double d2) {
        this.motionX = d;
        this.motionY = d1;
        this.motionZ = d2;
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double((double)(d * d + d2 * d2));
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(d, d2) * 180.0 / 3.1415927410125732);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(d1, f) * 180.0 / 3.1415927410125732);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / 3.1415927410125732);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0 / 3.1415927410125732);
        }
        if (this.fuse-- <= 0) {
            if (IC2.platform.isSimulating()) {
                this.setDead();
                this.explode();
            } else {
                this.setDead();
            }
        } else if (this.fuse < 100 && this.fuse % 2 == 0) {
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
        }
        if (this.inGround) {
            ++this.ticksInGround;
            if (this.ticksInGround >= 200) {
                this.setDead();
            }
            if (this.sticky) {
                this.fuse -= 3;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
                if (!this.worldObj.isAirBlock(this.stickX, this.stickY, this.stickZ)) {
                    return;
                }
            }
        }
        Vec3 vec3d = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
        Vec3 vec3d1 = Vec3.createVectorHelper((double)(this.posX + this.motionX), (double)(this.posY + this.motionY), (double)(this.posZ + this.motionZ));
        MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec3d, vec3d1, false, true, false);
        vec3d = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
        vec3d1 = Vec3.createVectorHelper((double)(this.posX + this.motionX), (double)(this.posY + this.motionY), (double)(this.posZ + this.motionZ));
        if (movingobjectposition != null) {
            vec3d1 = Vec3.createVectorHelper((double)movingobjectposition.hitVec.xCoord, (double)movingobjectposition.hitVec.yCoord, (double)movingobjectposition.hitVec.zCoord);
            float remainX = (float)(movingobjectposition.hitVec.xCoord - this.posX);
            float remainY = (float)(movingobjectposition.hitVec.yCoord - this.posY);
            float remainZ = (float)(movingobjectposition.hitVec.zCoord - this.posZ);
            float f1 = MathHelper.sqrt_double((double)(remainX * remainX + remainY * remainY + remainZ * remainZ));
            this.stickX = movingobjectposition.blockX;
            this.stickY = movingobjectposition.blockY;
            this.stickZ = movingobjectposition.blockZ;
            this.posX -= (double)remainX / (double)f1 * (double)0.05f;
            this.posY -= (double)remainY / (double)f1 * (double)0.05f;
            this.posZ -= (double)remainZ / (double)f1 * (double)0.05f;
            this.posX += (double)remainX;
            this.posY += (double)remainY;
            this.posZ += (double)remainZ;
            this.motionX *= (double)(0.75f - this.rand.nextFloat());
            this.motionY *= (double)-0.3f;
            this.motionZ *= (double)(0.75f - this.rand.nextFloat());
            this.inGround = true;
        } else {
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.inGround = false;
        }
        float f2 = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / 3.1415927410125732);
        this.rotationPitch = (float)(Math.atan2(this.motionY, f2) * 180.0 / 3.1415927410125732);
        while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
            this.prevRotationPitch -= 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f;
        }
        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;
        float f3 = 0.98f;
        float f5 = 0.04f;
        if (this.isInWater()) {
            this.fuse += 2000;
            for (int i1 = 0; i1 < 4; ++i1) {
                float f6 = 0.25f;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f6, this.posY - this.motionY * (double)f6, this.posZ - this.motionZ * (double)f6, this.motionX, this.motionY, this.motionZ);
            }
            f3 = 0.75f;
        }
        this.motionX *= (double)f3;
        this.motionY *= (double)f3;
        this.motionZ *= (double)f3;
        this.motionY -= (double)f5;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        this.inGround = nbttagcompound.getByte("inGround") == 1;
    }

    public float getShadowSize() {
        return 0.0f;
    }

    public void explode() {
        PointExplosion explosion = new PointExplosion(this.worldObj, this, this.owner, this.posX, this.posY, this.posZ, 1.0f, 1.0f, 20);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
    }
}

