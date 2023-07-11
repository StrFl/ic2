/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.comp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.Direction;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotDischarge;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class Energy
extends TileEntityComponent {
    private double capacity;
    private double storage;
    private int sinkTier;
    private int sourceTier;
    private Set<Direction> sinkDirections;
    private Set<Direction> sourceDirections;
    private List<InvSlot> managedSlots;
    private EnergyNetDelegate delegate;
    private boolean loaded;

    public static Energy asBasicSink(TileEntityBlock parent, double capacity) {
        return Energy.asBasicSink(parent, capacity, 1);
    }

    public static Energy asBasicSink(TileEntityBlock parent, double capacity, int tier) {
        return new Energy(parent, capacity, Direction.allDirections, Direction.noDirections, tier);
    }

    public static Energy asBasicSource(TileEntityBlock parent, double capacity) {
        return Energy.asBasicSource(parent, capacity, 1);
    }

    public static Energy asBasicSource(TileEntityBlock parent, double capacity, int tier) {
        return new Energy(parent, capacity, Direction.noDirections, Direction.allDirections, tier);
    }

    public Energy(TileEntityBlock parent, double capacity) {
        this(parent, capacity, Direction.noDirections, Direction.noDirections, 1);
    }

    public Energy(TileEntityBlock parent, double capacity, Set<Direction> sinkDirections, Set<Direction> sourceDirections, int tier) {
        this(parent, capacity, sinkDirections, sourceDirections, tier, tier);
    }

    public Energy(TileEntityBlock parent, double capacity, Set<Direction> sinkDirections, Set<Direction> sourceDirections, int sinkTier, int sourceTier) {
        super(parent);
        this.capacity = capacity;
        this.sinkTier = sinkTier;
        this.sourceTier = sourceTier;
        this.sinkDirections = sinkDirections;
        this.sourceDirections = sourceDirections;
    }

    public Energy addManagedSlot(InvSlot slot) {
        if (slot instanceof InvSlotCharge || slot instanceof InvSlotDischarge) {
            if (this.managedSlots == null) {
                this.managedSlots = new ArrayList<InvSlot>(4);
            }
        } else {
            throw new IllegalArgumentException("No charge/discharge slot.");
        }
        this.managedSlots.add(slot);
        return this;
    }

    @Override
    public String getDefaultName() {
        return "energy";
    }

    @Override
    public void readFromNbt(NBTTagCompound nbt) {
        this.storage = nbt.getDouble("storage");
    }

    @Override
    public NBTTagCompound writeToNbt() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setDouble("storage", this.storage);
        return ret;
    }

    @Override
    public void onLoaded() {
        assert (this.delegate == null);
        if (!(this.sinkDirections.isEmpty() && this.sourceDirections.isEmpty() || FMLCommonHandler.instance().getEffectiveSide().isClient())) {
            this.delegate = new EnergyNetDelegate();
            this.delegate.setWorldObj(this.parent.getWorldObj());
            this.delegate.xCoord = this.parent.xCoord;
            this.delegate.yCoord = this.parent.yCoord;
            this.delegate.zCoord = this.parent.zCoord;
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this.delegate));
        }
        this.loaded = true;
    }

    @Override
    public void onUnloaded() {
        if (this.delegate != null) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this.delegate));
            this.delegate = null;
        }
        this.loaded = false;
    }

    @Override
    public void onContainerUpdate(String name, EntityPlayerMP player) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(data);
        try {
            os.writeDouble(this.capacity);
            os.writeDouble(this.storage);
            os.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        IC2.network.get().sendComponentUpdate(this.parent, name, player, data);
    }

    @Override
    public void onNetworkUpdate(DataInput is) throws IOException {
        this.capacity = is.readDouble();
        this.storage = is.readDouble();
    }

    @Override
    public boolean enableWorldTick() {
        return !this.parent.getWorldObj().isRemote && this.managedSlots != null;
    }

    @Override
    public void onWorldTick() {
        for (InvSlot slot : this.managedSlots) {
            double space;
            if (slot instanceof InvSlotCharge) {
                if (!(this.storage > 0.0)) continue;
                this.storage -= ((InvSlotCharge)slot).charge(this.storage);
                continue;
            }
            if (!(slot instanceof InvSlotDischarge) || !((space = this.capacity - this.storage) > 0.0)) continue;
            this.storage += ((InvSlotDischarge)slot).discharge(space, false);
        }
    }

    public double getCapacity() {
        return this.capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getEnergy() {
        return this.storage;
    }

    public double getFreeEnergy() {
        return Math.max(0.0, this.capacity - this.storage);
    }

    public double getFillRatio() {
        return this.storage / this.capacity;
    }

    public double addEnergy(double amount) {
        amount = Math.min(this.capacity - this.storage, amount);
        this.storage += amount;
        return amount;
    }

    public boolean useEnergy(double amount) {
        if (this.storage >= amount) {
            this.storage -= amount;
            return true;
        }
        return false;
    }

    public int getSinkTier() {
        return this.sinkTier;
    }

    public void setSinkTier(int tier) {
        this.sinkTier = tier;
    }

    public int getSourceTier() {
        return this.sourceTier;
    }

    public void setSourceTier(int tier) {
        this.sourceTier = tier;
    }

    public void setDirections(Set<Direction> sinkDirections, Set<Direction> sourceDirections) {
        if (sinkDirections.equals(this.sinkDirections) && sourceDirections.equals(this.sourceDirections)) {
            return;
        }
        if (this.delegate != null) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this.delegate));
        }
        this.sinkDirections = sinkDirections;
        this.sourceDirections = sourceDirections;
        if (sinkDirections.isEmpty() && sourceDirections.isEmpty()) {
            this.delegate = null;
        } else if (this.delegate == null && this.loaded) {
            this.delegate = new EnergyNetDelegate();
        }
        if (this.delegate != null) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this.delegate));
        }
    }

    private class EnergyNetDelegate
    extends TileEntity
    implements IEnergySink,
    IEnergySource {
        private EnergyNetDelegate() {
        }

        @Override
        public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
            return Energy.this.sinkDirections.contains((Object)Direction.fromForgeDirection(direction));
        }

        @Override
        public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
            return Energy.this.sourceDirections.contains((Object)Direction.fromForgeDirection(direction));
        }

        @Override
        public double getDemandedEnergy() {
            return !Energy.this.sinkDirections.isEmpty() && Energy.this.storage < Energy.this.capacity ? Energy.this.capacity - Energy.this.storage : 0.0;
        }

        @Override
        public double getOfferedEnergy() {
            return !Energy.this.sourceDirections.isEmpty() && Energy.this.storage > 0.0 ? Math.min(Energy.this.storage, EnergyNet.instance.getPowerFromTier(Energy.this.sourceTier)) : 0.0;
        }

        @Override
        public int getSinkTier() {
            return Energy.this.sinkTier;
        }

        @Override
        public int getSourceTier() {
            return Energy.this.sourceTier;
        }

        @Override
        public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
            Energy.this.storage = Energy.this.storage + amount;
            return 0.0;
        }

        @Override
        public void drawEnergy(double amount) {
            Energy.this.storage = Energy.this.storage - amount;
        }
    }
}

