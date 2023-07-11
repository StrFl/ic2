/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.beam;

import ic2.core.block.beam.TileEmitter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityParticle
extends Entity {
    private static final double initialVelocity = 0.5;
    private static final double slowdown = 0.99;

    public EntityParticle(World world) {
        super(world);
        this.noClip = true;
    }

    public EntityParticle(TileEmitter emitter) {
        this(emitter.getWorldObj());
        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[emitter.getFacing()];
        double x = (double)emitter.xCoord + 0.5 + (double)dir.offsetX * 0.5;
        double y = (double)emitter.yCoord + 0.5 + (double)dir.offsetY * 0.5;
        double z = (double)emitter.zCoord + 0.5 + (double)dir.offsetZ * 0.5;
        this.setPosition(x, y, z);
        this.motionX = (double)dir.offsetX * 0.5;
        this.motionY = (double)dir.offsetY * 0.5;
        this.motionZ = (double)dir.offsetZ * 0.5;
        this.setSize(0.2f, 0.2f);
    }

    protected void entityInit() {
    }

    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    }

    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.99;
        this.motionY *= 0.99;
        this.motionZ *= 0.99;
        if (this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 1.0E-4) {
            this.setDead();
        }
    }
}

