/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.world.World
 */
package ic2.core.item;

import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class EntityIC2Boat
extends EntityBoat {
    private static Field field_isBoatEmpty;
    private static Field field_speedMultiplier;
    private static Field field_boatPosRotationIncrements;
    private static Field field_boatX;
    private static Field field_boatY;
    private static Field field_boatZ;
    private static Field field_boatYaw;
    private static Field field_boatPitch;

    public static void init() {
        field_isBoatEmpty = EntityIC2Boat.getField("isBoatEmpty", "field_70279_a");
        field_speedMultiplier = EntityIC2Boat.getField("speedMultiplier", "field_70276_b");
        field_boatPosRotationIncrements = EntityIC2Boat.getField("boatPosRotationIncrements", "field_70277_c");
        field_boatX = EntityIC2Boat.getField("boatX", "field_70274_d");
        field_boatY = EntityIC2Boat.getField("boatY", "field_70275_e");
        field_boatZ = EntityIC2Boat.getField("boatZ", "field_70272_f");
        field_boatYaw = EntityIC2Boat.getField("boatYaw", "field_70273_g");
        field_boatPitch = EntityIC2Boat.getField("boatPitch", "field_70281_h");
    }

    private static Field getField(String deobfName, String srgName) {
        Field ret = null;
        try {
            ret = EntityBoat.class.getDeclaredField(deobfName);
        }
        catch (Exception e) {
            try {
                ret = EntityBoat.class.getDeclaredField(srgName);
            }
            catch (Exception e1) {
                throw new RuntimeException("Can't find field " + deobfName + "/" + srgName, e1);
            }
        }
        ret.setAccessible(true);
        return ret;
    }

    public EntityIC2Boat(World par1World) {
        super(par1World);
    }

    public void onUpdate() {
        int j;
        double d4;
        double d2;
        super.onEntityUpdate();
        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
        if (this.getDamageTaken() > 0.0f) {
            this.setDamageTaken(this.getDamageTaken() - 1.0f);
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int b0 = 5;
        double d0 = 0.0;
        for (int i = 0; i < b0; ++i) {
            double d1 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i + 0) / (double)b0 - 0.125;
            double d3 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i + 1) / (double)b0 - 0.125;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double)this.boundingBox.minX, (double)d1, (double)this.boundingBox.minZ, (double)this.boundingBox.maxX, (double)d3, (double)this.boundingBox.maxZ);
            if (!this.isOnWater(axisalignedbb)) continue;
            d0 += 1.0 / (double)b0;
        }
        double d10 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (d10 > 0.26249999999999996) {
            d2 = Math.cos((double)this.rotationYaw * Math.PI / 180.0);
            d4 = Math.sin((double)this.rotationYaw * Math.PI / 180.0);
            j = 0;
            while ((double)j < 1.0 + d10 * 60.0) {
                double d9;
                double d8;
                double d5 = this.rand.nextFloat() * 2.0f - 1.0f;
                double d6 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7;
                if (this.rand.nextBoolean()) {
                    d8 = this.posX - d2 * d5 * 0.8 + d4 * d6;
                    d9 = this.posZ - d4 * d5 * 0.8 - d2 * d6;
                    this.worldObj.spawnParticle("splash", d8, this.posY - 0.125, d9, this.motionX, this.motionY, this.motionZ);
                } else {
                    d8 = this.posX + d2 + d4 * d5 * 0.7;
                    d9 = this.posZ + d4 - d2 * d5 * 0.7;
                    this.worldObj.spawnParticle("splash", d8, this.posY - 0.125, d9, this.motionX, this.motionY, this.motionZ);
                }
                ++j;
            }
        }
        try {
            double d12;
            double d11;
            if (this.worldObj.isRemote && field_isBoatEmpty.getBoolean((Object)this)) {
                int boatPosRotationIncrements = field_boatPosRotationIncrements.getInt((Object)this);
                if (boatPosRotationIncrements > 0) {
                    d2 = this.posX + (field_boatX.getDouble((Object)this) - this.posX) / (double)boatPosRotationIncrements;
                    d4 = this.posY + (field_boatY.getDouble((Object)this) - this.posY) / (double)boatPosRotationIncrements;
                    d11 = this.posZ + (field_boatZ.getDouble((Object)this) - this.posZ) / (double)boatPosRotationIncrements;
                    d12 = MathHelper.wrapAngleTo180_double((double)(field_boatYaw.getDouble((Object)this) - (double)this.rotationYaw));
                    this.rotationYaw = (float)((double)this.rotationYaw + d12 / (double)boatPosRotationIncrements);
                    this.rotationPitch = (float)((double)this.rotationPitch + (field_boatPitch.getDouble((Object)this) - (double)this.rotationPitch) / (double)boatPosRotationIncrements);
                    field_boatPosRotationIncrements.setInt((Object)this, --boatPosRotationIncrements);
                    this.setPosition(d2, d4, d11);
                    this.setRotation(this.rotationYaw, this.rotationPitch);
                } else {
                    d2 = this.posX + this.motionX;
                    d4 = this.posY + this.motionY;
                    d11 = this.posZ + this.motionZ;
                    this.setPosition(d2, d4, d11);
                    if (this.onGround) {
                        this.motionX *= 0.5;
                        this.motionY *= 0.5;
                        this.motionZ *= 0.5;
                    }
                    this.motionX *= (double)0.99f;
                    this.motionY *= (double)0.95f;
                    this.motionZ *= (double)0.99f;
                }
            } else {
                double d7;
                if (d0 < 1.0) {
                    d2 = d0 * 2.0 - 1.0;
                    this.motionY += (double)0.04f * d2;
                } else {
                    if (this.motionY < 0.0) {
                        this.motionY /= 2.0;
                    }
                    this.motionY += (double)0.007f;
                }
                double speedMultiplier = field_speedMultiplier.getDouble((Object)this);
                if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase)this.riddenByEntity;
                    float f = this.riddenByEntity.rotationYaw + -entitylivingbase.moveStrafing * 90.0f;
                    this.motionX += -Math.sin(f * (float)Math.PI / 180.0f) * speedMultiplier * (double)entitylivingbase.moveForward * (double)0.05f * this.getAccelerationFactor();
                    this.motionZ += Math.cos(f * (float)Math.PI / 180.0f) * speedMultiplier * (double)entitylivingbase.moveForward * (double)0.05f * this.getAccelerationFactor();
                }
                if ((d2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)) > this.getTopSpeed()) {
                    d4 = this.getTopSpeed() / d2;
                    this.motionX *= d4;
                    this.motionZ *= d4;
                    d2 = this.getTopSpeed();
                }
                if (d2 > d10 && speedMultiplier < 0.35) {
                    if ((speedMultiplier += (0.35 - speedMultiplier) / 35.0) > 0.35) {
                        speedMultiplier = 0.35;
                    }
                } else if ((speedMultiplier -= (speedMultiplier - 0.07) / 35.0) < 0.07) {
                    speedMultiplier = 0.07;
                }
                field_speedMultiplier.setDouble((Object)this, speedMultiplier);
                for (int l = 0; l < 4; ++l) {
                    int i1 = MathHelper.floor_double((double)(this.posX + ((double)(l % 2) - 0.5) * 0.8));
                    j = MathHelper.floor_double((double)(this.posZ + ((double)(l / 2) - 0.5) * 0.8));
                    for (int j1 = 0; j1 < 2; ++j1) {
                        int k = MathHelper.floor_double((double)this.posY) + j1;
                        Block block = this.worldObj.getBlock(i1, k, j);
                        if (block == Blocks.snow_layer) {
                            this.worldObj.setBlockToAir(i1, k, j);
                            this.isCollidedHorizontally = false;
                            continue;
                        }
                        if (block != Blocks.waterlily) continue;
                        this.worldObj.func_147480_a(i1, k, j, true);
                        this.isCollidedHorizontally = false;
                    }
                }
                if (this.onGround) {
                    this.motionX *= 0.5;
                    this.motionY *= 0.5;
                    this.motionZ *= 0.5;
                }
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                if (this.isCollidedHorizontally && d10 > this.getBreakMotion()) {
                    if (!this.worldObj.isRemote && !this.isDead) {
                        this.setDead();
                        this.breakBoat(d10);
                    }
                } else {
                    this.motionX *= (double)0.99f;
                    this.motionY *= (double)0.95f;
                    this.motionZ *= (double)0.99f;
                }
                this.rotationPitch = 0.0f;
                d4 = this.rotationYaw;
                d11 = this.prevPosX - this.posX;
                d12 = this.prevPosZ - this.posZ;
                if (d11 * d11 + d12 * d12 > 0.001) {
                    d4 = (float)(Math.atan2(d12, d11) * 180.0 / Math.PI);
                }
                if ((d7 = MathHelper.wrapAngleTo180_double((double)(d4 - (double)this.rotationYaw))) > 20.0) {
                    d7 = 20.0;
                }
                if (d7 < -20.0) {
                    d7 = -20.0;
                }
                this.rotationYaw = (float)((double)this.rotationYaw + d7);
                this.setRotation(this.rotationYaw, this.rotationPitch);
                if (!this.worldObj.isRemote) {
                    List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.expand((double)0.2f, 0.0, (double)0.2f));
                    if (list != null && !list.isEmpty()) {
                        for (int k1 = 0; k1 < list.size(); ++k1) {
                            Entity entity = (Entity)list.get(k1);
                            if (entity == this.riddenByEntity || !entity.canBePushed() || !(entity instanceof EntityBoat)) continue;
                            entity.applyEntityCollision((Entity)this);
                        }
                    }
                    if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
                        this.riddenByEntity = null;
                    }
                }
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public EntityItem func_145778_a(Item item, int meta, float yOffset) {
        if (item == Items.boat) {
            return this.entityDropItem(this.getItem(), yOffset);
        }
        return super.func_145778_a(item, meta, yOffset);
    }

    public abstract String getTexture();

    public ItemStack getPickedResult(MovingObjectPosition target) {
        return this.getItem();
    }

    protected ItemStack getItem() {
        return new ItemStack(Items.boat);
    }

    protected double getBreakMotion() {
        return 0.2;
    }

    protected void breakBoat(double motion) {
        int k;
        for (k = 0; k < 3; ++k) {
            this.entityDropItem(new ItemStack(Blocks.planks), 0.0f);
        }
        for (k = 0; k < 2; ++k) {
            this.entityDropItem(new ItemStack(Items.stick), 0.0f);
        }
    }

    protected double getAccelerationFactor() {
        return 1.0;
    }

    protected double getTopSpeed() {
        return 0.35;
    }

    protected boolean isOnWater(AxisAlignedBB aabb) {
        return this.worldObj.isAABBInMaterial(aabb, Material.water);
    }
}

