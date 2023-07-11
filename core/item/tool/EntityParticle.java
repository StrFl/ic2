/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IThrowableEntity
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.registry.IThrowableEntity;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.util.Quaternion;
import ic2.core.util.Vector3;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityParticle
extends Entity
implements IThrowableEntity {
    private double coreSize;
    private double influenceSize;
    private int lifeTime;
    private Entity owner;
    private Vector3[] radialTestVectors;

    public EntityParticle(World world) {
        super(world);
        this.noClip = true;
        this.lifeTime = 6000;
    }

    public EntityParticle(World world, EntityLivingBase owner1, float speed, double coreSize1, double influenceSize1) {
        this(world);
        this.coreSize = coreSize1;
        this.influenceSize = influenceSize1;
        this.owner = owner1;
        this.setPosition(owner1.posX, owner1.posY + (double)owner1.getEyeHeight(), owner1.posZ);
        Vector3 motion = new Vector3(owner1.getLookVec());
        Vector3 ortho = motion.copy().cross(Vector3.UP).scaleTo(influenceSize1);
        double stepAngle = Math.atan(0.5 / influenceSize1) * 2.0;
        int steps = (int)Math.ceil(Math.PI * 2 / stepAngle);
        Quaternion q = new Quaternion().setFromAxisAngle(motion, stepAngle);
        this.radialTestVectors = new Vector3[steps];
        this.radialTestVectors[0] = ortho.copy();
        for (int i = 1; i < steps; ++i) {
            q.rotate(ortho);
            this.radialTestVectors[i] = ortho.copy();
        }
        motion.scale(speed);
        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;
    }

    protected void entityInit() {
    }

    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    }

    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    }

    public Entity getThrower() {
        return this.owner;
    }

    public void setThrower(Entity entity) {
        this.owner = entity;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        Vector3 start = new Vector3(this.prevPosX, this.prevPosY, this.prevPosZ);
        Vector3 end = new Vector3(this.posX, this.posY, this.posZ);
        MovingObjectPosition hit = this.worldObj.rayTraceBlocks(start.toVec3(), end.toVec3(), true);
        if (hit != null) {
            end.set(hit.hitVec);
            this.posX = hit.hitVec.xCoord;
            this.posY = hit.hitVec.yCoord;
            this.posZ = hit.hitVec.zCoord;
        }
        List entitiesToCheck = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, AxisAlignedBB.getBoundingBox((double)this.prevPosX, (double)this.prevPosY, (double)this.prevPosZ, (double)this.posX, (double)this.posY, (double)this.posZ).expand(this.influenceSize, this.influenceSize, this.influenceSize));
        ArrayList<MovingObjectPosition> entitiesInfluences = new ArrayList<MovingObjectPosition>();
        double minDistanceSq = start.distanceSquared(end);
        for (Entity entity : entitiesToCheck) {
            double distanceSq;
            MovingObjectPosition entityInfluence;
            if (entity == this.owner || !entity.canBeCollidedWith() || (entityInfluence = entity.boundingBox.expand(this.influenceSize, this.influenceSize, this.influenceSize).calculateIntercept(start.toVec3(), end.toVec3())) == null) continue;
            entitiesInfluences.add(entityInfluence);
            MovingObjectPosition entityHit = entity.boundingBox.expand(this.coreSize, this.coreSize, this.coreSize).calculateIntercept(start.toVec3(), end.toVec3());
            if (entityHit == null || !((distanceSq = start.distanceSquared(entityHit.hitVec)) < minDistanceSq)) continue;
            hit = entityHit;
            minDistanceSq = distanceSq;
        }
        double maxInfluenceDistance = Math.sqrt(minDistanceSq) + this.influenceSize;
        for (MovingObjectPosition entityInfluence : entitiesInfluences) {
            if (!(start.distance(entityInfluence.hitVec) <= maxInfluenceDistance)) continue;
            this.onInfluence(entityInfluence);
        }
        if (this.radialTestVectors != null) {
            Vector3 vForward = end.copy().sub(start);
            double len = vForward.length();
            vForward.scale(1.0 / len);
            Vector3 origin = new Vector3(start);
            Vector3 tmp = new Vector3();
            int d = 0;
            while ((double)d < len) {
                for (int i = 0; i < this.radialTestVectors.length; ++i) {
                    origin.copy(tmp).add(this.radialTestVectors[i]);
                    MovingObjectPosition influence = this.worldObj.rayTraceBlocks(origin.toVec3(), tmp.toVec3(), true);
                    if (influence == null) continue;
                    this.onInfluence(influence);
                }
                origin.add(vForward);
                ++d;
            }
        }
        if (hit != null) {
            this.onImpact(hit);
            this.setDead();
        } else {
            --this.lifeTime;
            if (this.lifeTime <= 0) {
                this.setDead();
            }
        }
    }

    protected void onImpact(MovingObjectPosition hit) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        System.out.println("hit " + hit.typeOfHit + " " + hit.hitVec + " sim=" + IC2.platform.isSimulating());
        if (hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || IC2.platform.isSimulating()) {
            // empty if block
        }
        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, this.owner, hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, 18.0f, 0.95f, ExplosionIC2.Type.Heat);
        explosion.doExplosion();
    }

    protected void onInfluence(MovingObjectPosition hit) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        System.out.println("influenced " + hit.typeOfHit + " " + hit.hitVec + " sim=" + IC2.platform.isSimulating());
        if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && IC2.platform.isSimulating()) {
            Block block = this.worldObj.getBlock(hit.blockX, hit.blockY, hit.blockZ);
            if (block == Blocks.water || block == Blocks.flowing_water) {
                this.worldObj.setBlockToAir(hit.blockX, hit.blockY, hit.blockZ);
            } else {
                ItemStack existing = new ItemStack(block, 1, this.worldObj.getBlockMetadata(hit.blockX, hit.blockY, hit.blockZ));
                ItemStack smelted = FurnaceRecipes.smelting().getSmeltingResult(existing);
                if (smelted != null && smelted.getItem() instanceof ItemBlock) {
                    this.worldObj.setBlock(hit.blockX, hit.blockY, hit.blockZ, ((ItemBlock)smelted.getItem()).field_150939_a, smelted.getItemDamage(), 3);
                } else {
                    ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[hit.sideHit];
                    if (block.isFlammable((IBlockAccess)this.worldObj, hit.blockX, hit.blockY, hit.blockZ, side)) {
                        int x = hit.blockX - side.offsetX;
                        int y = hit.blockY - side.offsetY;
                        int z = hit.blockZ - side.offsetZ;
                        this.worldObj.setBlock(x, y, z, (Block)Blocks.fire);
                    }
                }
            }
        }
    }
}

