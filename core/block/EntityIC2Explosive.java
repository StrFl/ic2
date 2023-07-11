/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityIC2Explosive
extends Entity {
    public DamageSource damageSource;
    public EntityLivingBase igniter;
    public int fuse = 80;
    public float explosivePower = 4.0f;
    public int radiationRange = 0;
    public float dropRate = 0.3f;
    public float damageVsEntitys = 1.0f;
    public Block renderBlock = Blocks.dirt;

    public EntityIC2Explosive(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(0.98f, 0.98f);
        this.yOffset = this.height / 2.0f;
    }

    public EntityIC2Explosive(World world, double d, double d1, double d2, int fuselength, float power, float rate, float damage, Block block, int RadiationRange) {
        this(world);
        this.setPosition(d, d1, d2);
        float f = (float)(Math.random() * 3.1415927410125732 * 2.0);
        this.motionX = -MathHelper.sin((float)(f * 3.141593f / 180.0f)) * 0.02f;
        this.motionY = 0.2f;
        this.motionZ = -MathHelper.cos((float)(f * 3.141593f / 180.0f)) * 0.02f;
        this.prevPosX = d;
        this.prevPosY = d1;
        this.prevPosZ = d2;
        this.fuse = fuselength;
        this.explosivePower = power;
        this.radiationRange = RadiationRange;
        this.dropRate = rate;
        this.damageVsEntitys = damage;
        this.renderBlock = block;
    }

    protected void entityInit() {
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.04;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98;
        this.motionY *= 0.98;
        this.motionZ *= 0.98;
        if (this.onGround) {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
            this.motionY *= -0.5;
        }
        if (this.fuse-- <= 0) {
            this.setDead();
            if (IC2.platform.isSimulating()) {
                this.explode();
            }
        } else {
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
        }
    }

    private void explode() {
        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, this, this.posX, this.posY, this.posZ, this.explosivePower, this.dropRate, this.radiationRange > 0 ? ExplosionIC2.Type.Nuclear : ExplosionIC2.Type.Normal, this.igniter, this.radiationRange);
        explosion.doExplosion();
    }

    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Fuse", (byte)this.fuse);
    }

    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        this.fuse = nbttagcompound.getByte("Fuse");
    }

    public float getShadowSize() {
        return 0.0f;
    }

    public EntityIC2Explosive setIgniter(EntityLivingBase igniter1) {
        this.igniter = igniter1;
        return this;
    }
}

