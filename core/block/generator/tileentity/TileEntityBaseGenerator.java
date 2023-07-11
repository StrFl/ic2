/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.generator.tileentity;

import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotCharge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityBaseGenerator
extends TileEntityInventory
implements IEnergySource,
IHasGui {
    public int fuel = 0;
    public double storage = 0.0;
    public int tier;
    public double power;
    public final short maxStorage;
    public int production;
    public int ticksSinceLastActiveUpdate;
    public int activityMeter = 0;
    public boolean addedToEnergyNet = false;
    public AudioSource audioSource;
    public InvSlotCharge chargeSlot;

    public TileEntityBaseGenerator(int production1, int tier, int maxStorage1) {
        this.production = production1;
        this.tier = tier;
        this.power = EnergyNet.instance.getPowerFromTier(tier);
        this.maxStorage = (short)maxStorage1;
        this.ticksSinceLastActiveUpdate = IC2.random.nextInt(256);
        this.chargeSlot = new InvSlotCharge(this, 0, 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        try {
            this.fuel = nbttagcompound.getInteger("fuel");
        }
        catch (Exception e) {
            this.fuel = nbttagcompound.getShort("fuel");
        }
        try {
            this.storage = nbttagcompound.getDouble("storage");
        }
        catch (Exception e) {
            this.storage = nbttagcompound.getShort("storage");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("fuel", this.fuel);
        nbttagcompound.setDouble("storage", this.storage);
    }

    public int gaugeStorageScaled(int i) {
        return (int)(this.storage * (double)i / (double)this.maxStorage);
    }

    public abstract int gaugeFuelScaled(int var1);

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
    }

    @Override
    public void onUnloaded() {
        if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFuel()) {
            needsInvUpdate = this.gainFuel();
        }
        boolean newActive = this.gainEnergy();
        if (this.storage > (double)this.maxStorage) {
            this.storage = this.maxStorage;
        }
        if (this.storage >= 1.0 && this.chargeSlot.get() != null) {
            double used = ElectricItem.manager.charge(this.chargeSlot.get(), this.storage, 1, false, false);
            this.storage -= used;
            if (used > 0.0) {
                needsInvUpdate = true;
            }
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (!this.delayActiveUpdate()) {
            this.setActive(newActive);
        } else {
            if (this.ticksSinceLastActiveUpdate % 256 == 0) {
                this.setActive(this.activityMeter > 0);
                this.activityMeter = 0;
            }
            this.activityMeter = newActive ? ++this.activityMeter : --this.activityMeter;
            ++this.ticksSinceLastActiveUpdate;
        }
    }

    public boolean gainEnergy() {
        if (this.isConverting()) {
            this.storage += (double)this.production;
            --this.fuel;
            return true;
        }
        return false;
    }

    public boolean isConverting() {
        return this.fuel > 0 && this.storage + (double)this.production <= (double)this.maxStorage;
    }

    public boolean needsFuel() {
        return this.fuel <= 0 && this.storage + (double)this.production <= (double)this.maxStorage;
    }

    public abstract boolean gainFuel();

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return true;
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(this.storage, this.power);
    }

    @Override
    public int getSourceTier() {
        return this.tier;
    }

    @Override
    public void drawEnergy(double amount) {
        this.storage -= amount;
    }

    @Override
    public abstract String getInventoryName();

    public String getOperationSoundFile() {
        return null;
    }

    public boolean delayActiveUpdate() {
        return false;
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active") && this.prevActive != this.getActive()) {
            if (this.audioSource == null && this.getOperationSoundFile() != null) {
                this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, this.getOperationSoundFile(), true, false, IC2.audioManager.getDefaultVolume());
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
    public float getWrenchDropRate() {
        return 0.9f;
    }
}

