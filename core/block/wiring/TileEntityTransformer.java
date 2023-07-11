/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.wiring;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.wiring.ContainerTransformer;
import ic2.core.block.wiring.GuiTransformer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityTransformer
extends TileEntityInventory
implements IEnergySink,
IEnergySource,
IHasGui,
INetworkClientTileEntityEventListener {
    public int mode;
    public final int tier;
    public double power;
    public final double maxStorage;
    public double energy = 0.0;
    public boolean redstone = false;
    private boolean needrefresh = false;
    private double inputflow = 0.0;
    private double outputflow = 0.0;
    public boolean addedToEnergyNet = false;

    public TileEntityTransformer(int tier) {
        this.tier = tier;
        this.power = EnergyNet.instance.getPowerFromTier(tier);
        this.maxStorage = this.power * 8.0;
    }

    public String getTyp() {
        switch (this.tier) {
            case 1: {
                return "LV";
            }
            case 2: {
                return "MV";
            }
            case 3: {
                return "HV";
            }
            case 4: {
                return "EV";
            }
        }
        return "";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.mode = nbttagcompound.getInteger("mode");
        try {
            this.energy = nbttagcompound.getDouble("energy");
        }
        catch (Exception e) {
            this.energy = nbttagcompound.getInteger("energy");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setDouble("energy", this.energy);
        nbttagcompound.setInteger("mode", this.mode);
    }

    public int getMode() {
        return this.mode;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                if (this.mode == 0) break;
                this.mode = 0;
                break;
            }
            case 1: {
                if (this.mode == 1) break;
                this.mode = 1;
                break;
            }
            case 2: {
                if (this.mode == 2) break;
                this.mode = 2;
                break;
            }
            case 3: {
                this.outputflow = EnergyNet.instance.getPowerFromTier(this.getSourceTier());
                this.inputflow = EnergyNet.instance.getPowerFromTier(this.getSinkTier());
                this.needrefresh = true;
            }
        }
    }

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
        super.onUnloaded();
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.updateRedstone();
    }

    public void updateRedstone() {
        boolean red;
        switch (this.mode) {
            case 0: {
                red = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
                break;
            }
            case 1: {
                red = false;
                break;
            }
            case 2: {
                red = true;
                break;
            }
            default: {
                throw new RuntimeException("invalid mode: " + this.mode);
            }
        }
        if (red != this.redstone) {
            if (this.addedToEnergyNet) {
                MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            }
            this.addedToEnergyNet = false;
            this.energy = 0.0;
            this.redstone = red;
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
            this.setActive(this.redstone);
            this.power = EnergyNet.instance.getPowerFromTier(this.getSourceTier());
        }
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        if (this.redstone) {
            return !this.facingMatchesDirection(direction);
        }
        return this.facingMatchesDirection(direction);
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        if (this.redstone) {
            return this.facingMatchesDirection(direction);
        }
        return !this.facingMatchesDirection(direction);
    }

    public boolean facingMatchesDirection(ForgeDirection direction) {
        return direction.ordinal() == this.getFacing();
    }

    @Override
    public double getOfferedEnergy() {
        return this.energy >= this.power ? this.power : 0.0;
    }

    @Override
    public void drawEnergy(double amount) {
        this.outputflow = amount;
        this.energy -= amount;
    }

    @Override
    public double getDemandedEnergy() {
        if (this.needrefresh) {
            this.needrefresh = false;
            return 1.0;
        }
        return this.maxStorage - this.energy;
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        this.inputflow = amount;
        this.energy += amount;
        return 0.0;
    }

    @Override
    public int getSourceTier() {
        if (this.redstone) {
            return this.tier + 1;
        }
        return this.tier;
    }

    @Override
    public int getSinkTier() {
        if (this.redstone) {
            return this.tier;
        }
        if (this.tier < 4) {
            return this.tier + 1;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return this.getFacing() != side;
    }

    @Override
    public void setFacing(short side) {
        if (this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
        }
        this.energy = 0.0;
        super.setFacing(side);
        if (this.addedToEnergyNet) {
            this.addedToEnergyNet = false;
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
    }

    public ContainerBase<TileEntityTransformer> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerTransformer(entityPlayer, this, 219);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiTransformer(new ContainerTransformer(entityPlayer, this, 219));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    public double getinputflow() {
        if (!this.redstone) {
            return this.inputflow;
        }
        return this.outputflow;
    }

    public double getoutputflow() {
        if (this.redstone) {
            return this.inputflow;
        }
        return this.outputflow;
    }
}

