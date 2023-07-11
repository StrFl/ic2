/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTUtil
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.personal;

import com.mojang.authlib.GameProfile;
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
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotConsumableLinked;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.block.personal.ContainerEnergyOMatClosed;
import ic2.core.block.personal.ContainerEnergyOMatOpen;
import ic2.core.block.personal.GuiEnergyOMatClosed;
import ic2.core.block.personal.GuiEnergyOMatOpen;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityEnergyOMat
extends TileEntityInventory
implements IPersonalBlock,
IHasGui,
IEnergySink,
IEnergySource,
INetworkClientTileEntityEventListener,
IUpgradableBlock {
    public int euOffer = 1000;
    private GameProfile owner = null;
    private boolean addedToEnergyNet = false;
    public int paidFor;
    public double euBuffer;
    private int euBufferMax = 10000;
    private int tier = 1;
    public final InvSlot demandSlot = new InvSlot(this, "demand", 0, InvSlot.Access.NONE, 1);
    public final InvSlotConsumableLinked inputSlot = new InvSlotConsumableLinked((TileEntityInventory)this, "input", 1, 1, this.demandSlot);
    public final InvSlotCharge chargeSlot = new InvSlotCharge(this, -1, 1);
    public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, "upgrade", 2, 1);

    @Override
    public String getInventoryName() {
        return "Energy-O-Mat";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        if (nbttagcompound.hasKey("ownerGameProfile")) {
            this.owner = NBTUtil.func_152459_a((NBTTagCompound)nbttagcompound.getCompoundTag("ownerGameProfile"));
        }
        this.euOffer = nbttagcompound.getInteger("euOffer");
        this.paidFor = nbttagcompound.getInteger("paidFor");
        try {
            this.euBuffer = nbttagcompound.getDouble("euBuffer");
        }
        catch (Exception e) {
            this.euBuffer = nbttagcompound.getInteger("euBuffer");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        if (this.owner != null) {
            NBTTagCompound ownerNbt = new NBTTagCompound();
            NBTUtil.func_152460_a((NBTTagCompound)ownerNbt, (GameProfile)this.owner);
            nbttagcompound.setTag("ownerGameProfile", (NBTBase)ownerNbt);
        }
        nbttagcompound.setInteger("euOffer", this.euOffer);
        nbttagcompound.setInteger("paidFor", this.paidFor);
        nbttagcompound.setDouble("euBuffer", this.euBuffer);
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
        return this.permitsAccess(entityPlayer.getGameProfile());
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
        double sent;
        int transferred;
        ItemStack tradedIn;
        super.updateEntityServer();
        boolean invChanged = false;
        this.euBufferMax = 10000;
        this.tier = 1;
        this.chargeSlot.setTier(1);
        if (!this.upgradeSlot.isEmpty()) {
            this.euBufferMax = TileEntityStandardMachine.applyModifier(10000, this.upgradeSlot.extraEnergyStorage, this.upgradeSlot.energyStorageMultiplier);
            this.tier = 1 + this.upgradeSlot.extraTier;
            this.chargeSlot.setTier(this.tier);
        }
        if ((tradedIn = this.inputSlot.consumeLinked(true)) != null && (transferred = StackUtil.distribute(this, tradedIn, true)) == tradedIn.stackSize) {
            StackUtil.distribute(this, this.inputSlot.consumeLinked(false), false);
            this.paidFor += this.euOffer;
            invChanged = true;
        }
        if (this.euBuffer >= 1.0 && (sent = this.chargeSlot.charge(this.euBuffer)) > 0.0) {
            this.euBuffer -= sent;
            invChanged = true;
        }
        if (invChanged) {
            this.markDirty();
        }
    }

    @Override
    public boolean permitsAccess(GameProfile profile) {
        if (profile == null) {
            return this.owner == null;
        }
        if (IC2.platform.isSimulating()) {
            if (this.owner == null) {
                this.owner = profile;
                IC2.network.get().updateTileEntityField(this, "owner");
                return true;
            }
            if (MinecraftServer.getServer().getConfigurationManager().func_152596_g(profile)) {
                return true;
            }
        } else if (this.owner == null) {
            return true;
        }
        return this.owner.equals((Object)profile);
    }

    @Override
    public List<String> getNetworkedFields() {
        Vector<String> ret = new Vector<String>(1);
        ret.add("owner");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    @Override
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        return this.getFacing() != side && this.permitsAccess(entityPlayer.getGameProfile());
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return !this.facingMatchesDirection(direction);
    }

    public boolean facingMatchesDirection(ForgeDirection direction) {
        return direction.ordinal() == this.getFacing();
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return this.facingMatchesDirection(direction);
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(this.euBuffer, EnergyNet.instance.getPowerFromTier(this.getSourceTier()));
    }

    @Override
    public void drawEnergy(double amount) {
        this.euBuffer -= amount;
    }

    @Override
    public double getDemandedEnergy() {
        return Math.min((double)this.paidFor, (double)this.euBufferMax - this.euBuffer);
    }

    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
        double toAdd = Math.min(Math.min(amount, (double)this.paidFor), (double)this.euBufferMax - this.euBuffer);
        this.paidFor = (int)((double)this.paidFor - toAdd);
        this.euBuffer += toAdd;
        return amount - toAdd;
    }

    @Override
    public int getSourceTier() {
        return this.tier;
    }

    @Override
    public int getSinkTier() {
        return Integer.MAX_VALUE;
    }

    public ContainerBase<TileEntityEnergyOMat> getGuiContainer(EntityPlayer entityPlayer) {
        if (this.permitsAccess(entityPlayer.getGameProfile())) {
            return new ContainerEnergyOMatOpen(entityPlayer, this);
        }
        return new ContainerEnergyOMatClosed(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        if (isAdmin || this.permitsAccess(entityPlayer.getGameProfile())) {
            return new GuiEnergyOMatOpen(new ContainerEnergyOMatOpen(entityPlayer, this));
        }
        return new GuiEnergyOMatClosed(new ContainerEnergyOMatClosed(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (!this.permitsAccess(player.getGameProfile())) {
            return;
        }
        switch (event) {
            case 0: {
                this.attemptSet(-100000);
                break;
            }
            case 1: {
                this.attemptSet(-10000);
                break;
            }
            case 2: {
                this.attemptSet(-1000);
                break;
            }
            case 3: {
                this.attemptSet(-100);
                break;
            }
            case 4: {
                this.attemptSet(100000);
                break;
            }
            case 5: {
                this.attemptSet(10000);
                break;
            }
            case 6: {
                this.attemptSet(1000);
                break;
            }
            case 7: {
                this.attemptSet(100);
            }
        }
    }

    private void attemptSet(int amount) {
        this.euOffer += amount;
        if (this.euOffer < 100) {
            this.euOffer = 100;
        }
    }

    @Override
    public double getEnergy() {
        return this.euBuffer;
    }

    @Override
    public boolean useEnergy(double amount) {
        if (amount <= this.euBuffer) {
            amount -= this.euBuffer;
            return true;
        }
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.EnergyStorage, UpgradableProperty.Transformer);
    }
}

