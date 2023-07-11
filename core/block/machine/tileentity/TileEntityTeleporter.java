/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.boss.EntityDragon
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityMinecart
 *  net.minecraft.entity.monster.EntityGhast
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.world.chunk.Chunk
 */
package ic2.core.block.machine.tileentity;

import ic2.api.Direction;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.tile.IEnergyStorage;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityBlock;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;

public class TileEntityTeleporter
extends TileEntityBlock
implements INetworkTileEntityEventListener {
    public boolean targetSet = false;
    public int targetX;
    public int targetY;
    public int targetZ;
    private AudioSource audioSource = null;
    private static final int EventTeleport = 0;

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.targetSet = nbttagcompound.getBoolean("targetSet");
        this.targetX = nbttagcompound.getInteger("targetX");
        this.targetY = nbttagcompound.getInteger("targetY");
        this.targetZ = nbttagcompound.getInteger("targetZ");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("targetSet", this.targetSet);
        nbttagcompound.setInteger("targetX", this.targetX);
        nbttagcompound.setInteger("targetY", this.targetY);
        nbttagcompound.setInteger("targetZ", this.targetZ);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord) && this.targetSet) {
            boolean prevWorldChunkLoadOverride = this.worldObj.findingSpawnPoint;
            this.worldObj.findingSpawnPoint = true;
            Chunk chunk = this.worldObj.getChunkProvider().provideChunk(this.targetX >> 4, this.targetZ >> 4);
            this.worldObj.findingSpawnPoint = prevWorldChunkLoadOverride;
            if (chunk == null || !StackUtil.equals(chunk.getBlock(this.targetX & 0xF, this.targetY, this.targetZ & 0xF), Ic2Items.teleporter) || chunk.getBlockMetadata(this.targetX & 0xF, this.targetY, this.targetZ & 0xF) != Ic2Items.teleporter.getItemDamage()) {
                this.targetSet = false;
                this.setActive(false);
            } else {
                this.setActive(true);
                List entitiesNearby = this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox((double)(this.xCoord - 1), (double)this.yCoord, (double)(this.zCoord - 1), (double)(this.xCoord + 2), (double)(this.yCoord + 3), (double)(this.zCoord + 2)));
                if (!entitiesNearby.isEmpty()) {
                    double minDistanceSquared = Double.MAX_VALUE;
                    Entity closestEntity = null;
                    for (Entity entity : entitiesNearby) {
                        double distSquared;
                        if (entity.ridingEntity != null || !((distSquared = ((double)this.xCoord - entity.posX) * ((double)this.xCoord - entity.posX) + ((double)(this.yCoord + 1) - entity.posY) * ((double)(this.yCoord + 1) - entity.posY) + ((double)this.zCoord - entity.posZ) * ((double)this.zCoord - entity.posZ)) < minDistanceSquared)) continue;
                        minDistanceSquared = distSquared;
                        closestEntity = entity;
                    }
                    this.teleport(closestEntity);
                }
            }
        } else {
            this.setActive(false);
        }
    }

    @Override
    protected void updateEntityClient() {
        super.updateEntityClient();
        if (this.getActive()) {
            this.spawnBlueParticles(2, this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public void onUnloaded() {
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    public void teleport(Entity user) {
        double distance = Math.sqrt((this.xCoord - this.targetX) * (this.xCoord - this.targetX) + (this.yCoord - this.targetY) * (this.yCoord - this.targetY) + (this.zCoord - this.targetZ) * (this.zCoord - this.targetZ));
        int weight = this.getWeightOf(user);
        if (weight == 0) {
            return;
        }
        int energyCost = (int)((double)weight * Math.pow(distance + 10.0, 0.7) * 5.0);
        if (energyCost > this.getAvailableEnergy()) {
            return;
        }
        this.consumeEnergy(energyCost);
        if (user instanceof EntityPlayerMP) {
            ((EntityPlayerMP)user).setPositionAndUpdate((double)this.targetX + 0.5, (double)this.targetY + 1.5 + user.getYOffset(), (double)this.targetZ + 0.5);
        } else {
            user.setPositionAndRotation((double)this.targetX + 0.5, (double)this.targetY + 1.5 + user.getYOffset(), (double)this.targetZ + 0.5, user.rotationYaw, user.rotationPitch);
        }
        IC2.network.get().initiateTileEntityEvent(this, 0, true);
        if (user instanceof EntityPlayer && distance >= 1000.0) {
            IC2.achievements.issueAchievement((EntityPlayer)user, "teleportFarAway");
        }
    }

    public void spawnBlueParticles(int n, int x, int y, int z) {
        for (int i = 0; i < n; ++i) {
            this.worldObj.spawnParticle("reddust", (double)((float)x + this.worldObj.rand.nextFloat()), (double)((float)(y + 1) + this.worldObj.rand.nextFloat()), (double)((float)z + this.worldObj.rand.nextFloat()), -1.0, 0.0, 1.0);
            this.worldObj.spawnParticle("reddust", (double)((float)x + this.worldObj.rand.nextFloat()), (double)((float)(y + 2) + this.worldObj.rand.nextFloat()), (double)((float)z + this.worldObj.rand.nextFloat()), -1.0, 0.0, 1.0);
        }
    }

    public void consumeEnergy(int energy) {
        LinkedList<IEnergyStorage> energySources = new LinkedList<IEnergyStorage>();
        for (Direction direction : Direction.directions) {
            IEnergyStorage energySource;
            TileEntity target = direction.applyToTileEntity(this);
            if (!(target instanceof IEnergyStorage) || !(energySource = (IEnergyStorage)target).isTeleporterCompatible(direction.getInverse().toForgeDirection()) || energySource.getStored() <= 0) continue;
            energySources.add(energySource);
        }
        while (energy > 0) {
            int drain = (energy + energySources.size() - 1) / energySources.size();
            Iterator it = energySources.iterator();
            while (it.hasNext()) {
                IEnergyStorage energySource = (IEnergyStorage)it.next();
                if (drain > energy) {
                    drain = energy;
                }
                if (energySource.getStored() <= drain) {
                    energy -= energySource.getStored();
                    energySource.setStored(0);
                    it.remove();
                    continue;
                }
                energy -= drain;
                energySource.addEnergy(-drain);
            }
        }
    }

    public int getAvailableEnergy() {
        int energy = 0;
        for (Direction direction : Direction.directions) {
            IEnergyStorage storage;
            TileEntity target = direction.applyToTileEntity(this);
            if (!(target instanceof IEnergyStorage) || !(storage = (IEnergyStorage)target).isTeleporterCompatible(direction.getInverse().toForgeDirection())) continue;
            energy += storage.getStored();
        }
        return energy;
    }

    public int getWeightOf(Entity user) {
        boolean teleporterUseInventoryWeight = ConfigUtil.getBool(MainConfig.get(), "balance/teleporterUseInventoryWeight");
        int weight = 0;
        Entity ce = user;
        while (ce != null) {
            int i;
            if (ce instanceof EntityItem) {
                ItemStack is = ((EntityItem)ce).getEntityItem();
                weight += 100 * is.stackSize / is.getMaxStackSize();
            } else if (ce instanceof EntityAnimal || ce instanceof EntityMinecart || ce instanceof EntityBoat) {
                weight += 100;
            } else if (ce instanceof EntityPlayer) {
                weight += 1000;
                if (teleporterUseInventoryWeight) {
                    InventoryPlayer inv = ((EntityPlayer)ce).inventory;
                    for (i = 0; i < inv.mainInventory.length; ++i) {
                        if (inv.mainInventory[i] == null) continue;
                        weight += 100 * inv.mainInventory[i].stackSize / inv.mainInventory[i].getMaxStackSize();
                    }
                }
            } else if (ce instanceof EntityGhast) {
                weight += 2500;
            } else if (ce instanceof EntityDragon) {
                weight += 10000;
            } else if (ce instanceof EntityCreature) {
                weight += 500;
            }
            if (teleporterUseInventoryWeight && ce instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase)ce;
                int n = i = ce instanceof EntityPlayer ? 1 : 0;
                while (i <= 4) {
                    ItemStack item = living.getEquipmentInSlot(i);
                    if (item != null) {
                        weight += 100 * item.stackSize / item.getMaxStackSize();
                    }
                    ++i;
                }
            }
            ce = ce.riddenByEntity;
        }
        return weight;
    }

    public void setTarget(int x, int y, int z) {
        this.targetSet = true;
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        IC2.network.get().updateTileEntityField(this, "targetX");
        IC2.network.get().updateTileEntityField(this, "targetY");
        IC2.network.get().updateTileEntityField(this, "targetZ");
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> ret = new Vector<String>(3);
        ret.add("targetX");
        ret.add("targetY");
        ret.add("targetZ");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active") && this.prevActive != this.getActive()) {
            if (this.audioSource == null) {
                this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Machines/Teleporter/TeleChargedLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
            }
            if (this.getActive()) {
                if (this.audioSource != null) {
                    this.audioSource.play();
                }
            } else if (this.audioSource != null) {
                this.audioSource.stop();
            }
        }
        super.onNetworkUpdate(field);
    }

    @Override
    public void onNetworkEvent(int event) {
        switch (event) {
            case 0: {
                IC2.audioManager.playOnce(this, PositionSpec.Center, "Machines/Teleporter/TeleUse.ogg", true, IC2.audioManager.getDefaultVolume());
                IC2.audioManager.playOnce(new AudioPosition(this.worldObj, (float)this.targetX + 0.5f, (float)this.targetY + 0.5f, (float)this.targetZ + 0.5f), PositionSpec.Center, "Machines/Teleporter/TeleUse.ogg", true, IC2.audioManager.getDefaultVolume());
                this.spawnBlueParticles(20, this.xCoord, this.yCoord, this.zCoord);
                this.spawnBlueParticles(20, this.targetX, this.targetY, this.targetZ);
                break;
            }
            default: {
                IC2.platform.displayError("An unknown event type was received over multiplayer.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: event ID " + event + ", tile entity below)\nT: " + this + " (" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")", new Object[0]);
            }
        }
    }
}

