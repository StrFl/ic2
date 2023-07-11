/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.wiring;

import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.tile.IWrenchable;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.Ic2Items;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityLuminator
extends TileEntity
implements IEnergySink,
IWrenchable {
    public double energy = 0.0;
    public boolean lswitch = true;
    public int ticker = -1;
    public boolean ignoreBlockStay = false;
    public boolean addedToEnergyNet = false;
    private boolean loaded = false;

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        try {
            this.energy = nbttagcompound.getDouble("energy");
        }
        catch (Exception e) {
            this.energy = nbttagcompound.getShort("energy");
        }
        this.lswitch = nbttagcompound.getBoolean("lswitch");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.energy);
        nbttagcompound.setBoolean("lswitch", this.lswitch);
    }

    public void validate() {
        super.validate();
        IC2.tickHandler.addSingleTickCallback(this.worldObj, new ITickCallback(){

            @Override
            public void tickCallback(World world) {
                if (TileEntityLuminator.this.isInvalid() || !world.blockExists(TileEntityLuminator.this.xCoord, TileEntityLuminator.this.yCoord, TileEntityLuminator.this.zCoord)) {
                    return;
                }
                TileEntityLuminator.this.onLoaded();
                if (TileEntityLuminator.this.enableUpdateEntity()) {
                    world.loadedTileEntityList.add(TileEntityLuminator.this);
                }
            }
        });
    }

    public void invalidate() {
        super.invalidate();
        if (this.loaded) {
            this.onUnloaded();
        }
    }

    public void onChunkUnload() {
        super.onChunkUnload();
        if (this.loaded) {
            this.onUnloaded();
        }
    }

    public void onLoaded() {
        if (IC2.platform.isSimulating() && !this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
        this.loaded = true;
    }

    public void onUnloaded() {
        if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
    }

    public final boolean canUpdate() {
        return false;
    }

    public boolean enableUpdateEntity() {
        return IC2.platform.isSimulating();
    }

    public boolean isRedstonePowered() {
        return this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
    }

    public void updateEntity() {
        ++this.ticker;
        if (this.ticker % 4 == 0) {
            TileEntityLuminator newLumi;
            if (this.isActive()) {
                this.energy -= 1.0;
            }
            if (StackUtil.equals(this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord), Ic2Items.activeLuminator) && !this.isActive()) {
                this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, StackUtil.getBlock(Ic2Items.luminator), this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord), 7);
                newLumi = (TileEntityLuminator)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
                newLumi.lswitch = this.lswitch;
                newLumi.energy = this.energy;
            }
            if (StackUtil.equals(this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord), Ic2Items.luminator) && this.isActive()) {
                this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, StackUtil.getBlock(Ic2Items.activeLuminator), this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord), 7);
                newLumi = (TileEntityLuminator)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
                newLumi.lswitch = this.lswitch;
                newLumi.energy = this.energy;
            }
        }
    }

    public boolean isActive() {
        if (this.energy <= 0.0) {
            return false;
        }
        return !this.lswitch && this.isRedstonePowered() || this.lswitch && !this.isRedstonePowered();
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return emitter instanceof TileEntityCable;
    }

    @Override
    public double getDemandedEnergy() {
        return (double)this.getMaxEnergy() - this.energy;
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        if (this.energy >= (double)this.getMaxEnergy() || amount <= 0.0) {
            return amount;
        }
        this.energy += amount;
        return 0.0;
    }

    @Override
    public int getSinkTier() {
        return 1;
    }

    public int getMaxEnergy() {
        return 2;
    }

    public void poof() {
        if (this.loaded) {
            this.onUnloaded();
        }
        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, 0.5 + (double)this.xCoord, 0.5 + (double)this.yCoord, 0.5 + (double)this.zCoord, 0.5f, 0.85f);
        explosion.doExplosion();
    }

    public boolean canCableConnectFrom(int x, int y, int z) {
        int facing = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        switch (facing) {
            case 0: {
                return x == this.xCoord && y == this.yCoord + 1 && z == this.zCoord;
            }
            case 1: {
                return x == this.xCoord && y == this.yCoord - 1 && z == this.zCoord;
            }
            case 2: {
                return x == this.xCoord && y == this.yCoord && z == this.zCoord + 1;
            }
            case 3: {
                return x == this.xCoord && y == this.yCoord && z == this.zCoord - 1;
            }
            case 4: {
                return x == this.xCoord + 1 && y == this.yCoord && z == this.zCoord;
            }
            case 5: {
                return x == this.xCoord - 1 && y == this.yCoord && z == this.zCoord;
            }
        }
        return false;
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return true;
    }

    @Override
    public short getFacing() {
        return 0;
    }

    @Override
    public void setFacing(short facing) {
        this.lswitch = !this.lswitch;
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
        return false;
    }

    @Override
    public float getWrenchDropRate() {
        return 0.0f;
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        return null;
    }
}

