/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.FMLClientHandler
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.particle.EffectRenderer
 *  net.minecraft.client.particle.EntityFX
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.wiring;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IEnergyStorage;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.wiring.ContainerChargepadBlock;
import ic2.core.block.wiring.GuiChargepadBlock;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.EntityIC2FX;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityChargepadBlock
extends TileEntityElectricBlock
implements IEnergySink,
IEnergySource,
IHasGui,
IEnergyStorage {
    private int updateTicker = IC2.random.nextInt(this.getTickRate());
    private EntityPlayer player = null;
    private boolean isEmittingRedstone = false;
    public boolean addedToEnergyNet = false;
    public static byte redstoneModes = (byte)2;

    public TileEntityChargepadBlock(int tier1, int output1, int maxStorage1) {
        super(tier1, output1, maxStorage1);
    }

    public void playerstandsat(EntityPlayer entity) {
        if (this.player == null) {
            this.player = entity;
        } else if (this.player.getUniqueID() != entity.getUniqueID()) {
            this.player = entity;
        }
    }

    protected int getTickRate() {
        return 2;
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        if (side == 1) {
            return false;
        }
        return this.getFacing() != side;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.updateTicker++ % this.getTickRate() != 0) {
            return;
        }
        if (this.player != null && this.energy >= 1.0) {
            if (!this.getActive()) {
                this.setActive(true);
            }
            this.getItems(this.player);
            this.player = null;
            needsInvUpdate = true;
        } else if (this.getActive()) {
            this.setActive(false);
            needsInvUpdate = true;
        }
        if (this.redstoneMode == 0 && this.getActive() || this.redstoneMode == 1 && !this.getActive()) {
            this.isEmittingRedstone = true;
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
        } else {
            this.isEmittingRedstone = false;
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    protected abstract void getItems(EntityPlayer var1);

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        if (direction == ForgeDirection.UP) {
            return false;
        }
        return !this.facingMatchesDirection(direction);
    }

    public ContainerBase<TileEntityChargepadBlock> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerChargepadBlock(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiChargepadBlock(new ContainerChargepadBlock(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public boolean isEmittingRedstone() {
        return this.isEmittingRedstone;
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(World world, int blockX, int blockY, int blockZ, Random rand) {
        if (this.getActive()) {
            EffectRenderer effect = FMLClientHandler.instance().getClient().effectRenderer;
            for (int particles = 20; particles > 0; --particles) {
                double x = (float)blockX + 0.0f + rand.nextFloat();
                double y = (float)blockY + 0.9f + rand.nextFloat();
                double z = (float)blockZ + 0.0f + rand.nextFloat();
                effect.addEffect((EntityFX)new EntityIC2FX(world, x, y, z, 60, new double[]{0.0, 0.1, 0.0}, new float[]{0.2f, 0.2f, 1.0f}));
            }
        }
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
        ItemStack ret = super.getWrenchDrop(entityPlayer);
        float energyRetainedInStorageBlockDrops = ConfigUtil.getFloat(MainConfig.get(), "balance/energyRetainedInStorageBlockDrops");
        if (energyRetainedInStorageBlockDrops > 0.0f) {
            NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(ret);
            nbttagcompound.setDouble("energy", this.energy * (double)energyRetainedInStorageBlockDrops);
        }
        return ret;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        this.redstoneMode = (byte)(this.redstoneMode + 1);
        if (this.redstoneMode >= redstoneModes) {
            this.redstoneMode = 0;
        }
        IC2.platform.messagePlayer(player, this.getredstoneMode(), new Object[0]);
    }

    @Override
    public String getredstoneMode() {
        if (this.redstoneMode > 1 || this.redstoneMode < 0) {
            return "";
        }
        return StatCollector.translateToLocal((String)("ic2.blockChargepad.gui.mod.redstone" + this.redstoneMode));
    }

    protected void chargeitems(ItemStack itemstack, int chargefactor) {
        if (!(itemstack.getItem() instanceof IElectricItem)) {
            return;
        }
        if (itemstack.getItem() == Ic2Items.debug.getItem()) {
            return;
        }
        double freeamount = ElectricItem.manager.charge(itemstack, Double.POSITIVE_INFINITY, this.tier, true, true);
        double charge = 0.0;
        if (freeamount >= 0.0) {
            charge = freeamount >= (double)(chargefactor * this.getTickRate()) ? (double)(chargefactor * this.getTickRate()) : freeamount;
            if (this.energy < charge) {
                charge = this.energy;
            }
            this.energy -= ElectricItem.manager.charge(itemstack, charge, this.tier, true, false);
        }
    }
}

