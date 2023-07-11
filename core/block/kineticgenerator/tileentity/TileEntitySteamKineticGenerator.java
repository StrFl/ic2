/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 */
package ic2.core.block.kineticgenerator.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.energy.tile.IKineticSource;
import ic2.core.ContainerBase;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.kineticgenerator.container.ContainerSteamKineticGenerator;
import ic2.core.block.kineticgenerator.gui.GuSteamKineticGenerator;
import ic2.core.block.machine.tileentity.TileEntityCondenser;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntitySteamKineticGenerator
extends TileEntityInventory
implements IKineticSource,
IFluidHandler,
IHasGui,
IUpgradableBlock {
    private boolean isturbefilledupwithwater = false;
    private float condensationprogress = 0.0f;
    private int updateTicker;
    private int kUoutput;
    protected final FluidTank SteamTank;
    protected final FluidTank distilledwaterTank = new FluidTank(1000);
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlotConsumable turbineSlot;
    private static final float outputModifier = ConfigUtil.getFloat(MainConfig.get(), "balance/energy/kineticgenerator/steam");

    public TileEntitySteamKineticGenerator() {
        this.SteamTank = new FluidTank(21000);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 1, 1);
        this.turbineSlot = new InvSlotConsumableId((TileEntityInventory)this, "Turbineslot", 0, 1, Ic2Items.steamturbine.getItem());
        this.updateTicker = IC2.random.nextInt(this.getTickRate());
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.distilledwaterTank.getCapacity() - this.distilledwaterTank.getFluidAmount() >= 1 && this.isturbefilledupwithwater) {
            this.isturbefilledupwithwater = false;
        }
        if (this.SteamTank.getFluidAmount() > 10 && !this.isturbefilledupwithwater && !this.turbineSlot.isEmpty()) {
            if (!this.getActive()) {
                this.setActive(true);
                needsInvUpdate = true;
            }
            boolean turbinework = this.turbinework();
            if (this.updateTicker++ >= this.getTickRate()) {
                if (turbinework) {
                    if (!this.isHotSteam()) {
                        this.turbineSlot.damage(2, false);
                    } else {
                        this.turbineSlot.damage(1, false);
                    }
                }
                this.updateTicker = 0;
            }
        } else if (this.getActive()) {
            this.setActive(false);
            needsInvUpdate = true;
            this.kUoutput = 0;
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    private float Steamhandling(int amount) {
        float KUWorkbuffer = 0.0f;
        float Steamfactor = 1.0f;
        if (this.isHotSteam()) {
            Steamfactor = 2.0f;
        }
        this.SteamTank.drain(amount, true);
        KUWorkbuffer = (float)(amount * 2) * Steamfactor;
        if (this.isHotSteam()) {
            this.Steamoutput(amount);
        } else {
            this.condensationprogress += (float)amount / 100.0f * 10.0f;
            this.Steamoutput((float)amount / 100.0f * 90.0f);
        }
        return KUWorkbuffer;
    }

    private boolean turbinework() {
        float KUWorkbuffer = 0.0f;
        int Steamaount = this.SteamTank.getFluidAmount();
        KUWorkbuffer = Steamaount > 18000 ? this.Steamhandling(1000) : (Steamaount > 16000 ? this.Steamhandling(800) : (Steamaount > 12000 ? this.Steamhandling(600) : (Steamaount > 8000 ? this.Steamhandling(400) : (Steamaount > 4000 ? this.Steamhandling(200) : (Steamaount > 2000 ? this.Steamhandling(100) : (Steamaount > 1000 ? this.Steamhandling(50) : (Steamaount > 800 ? this.Steamhandling(40) : (Steamaount > 600 ? this.Steamhandling(30) : (Steamaount > 400 ? this.Steamhandling(20) : (Steamaount > 10 ? this.Steamhandling(10) : 0.0f))))))))));
        if (this.condensationprogress >= 100.0f) {
            if (this.distilledwaterTank.fill(new FluidStack(BlocksItems.getFluid(InternalName.fluidDistilledWater), 1), false) == 1) {
                this.condensationprogress -= 100.0f;
                this.distilledwaterTank.fill(new FluidStack(BlocksItems.getFluid(InternalName.fluidDistilledWater), 1), true);
            } else {
                this.isturbefilledupwithwater = true;
            }
        }
        this.kUoutput = (int)(KUWorkbuffer * (100.0f - (float)this.distilledwaterTank.getFluidAmount() / (float)this.distilledwaterTank.getCapacity() * 100.0f) / 100.0f * outputModifier);
        return KUWorkbuffer > 0.0f;
    }

    private void Steamoutput(float amount) {
        for (Direction direction : Direction.directions) {
            TileEntity target = direction.applyToTileEntity(this);
            if (!this.isHotSteam() ? !(target instanceof TileEntityCondenser) : !(target instanceof TileEntityCondenser) && !(target instanceof TileEntitySteamKineticGenerator)) continue;
            int transamount = ((IFluidHandler)target).fill(direction.toForgeDirection().getOpposite(), new FluidStack(BlocksItems.getFluid(InternalName.fluidSteam), (int)amount), false);
            if (transamount <= 0) continue;
            if (amount > (float)transamount) {
                ((IFluidHandler)target).fill(direction.toForgeDirection().getOpposite(), new FluidStack(BlocksItems.getFluid(InternalName.fluidSteam), (int)amount), true);
                amount -= (float)transamount;
            } else {
                ((IFluidHandler)target).fill(direction.toForgeDirection().getOpposite(), new FluidStack(BlocksItems.getFluid(InternalName.fluidSteam), (int)amount), true);
                amount = 0.0f;
            }
            if (amount == 0.0f) break;
        }
        if (amount > 0.0f && IC2.random.nextInt(10) == 0) {
            ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, 1.0f, 1.0f, ExplosionIC2.Type.Heat);
            explosion.doExplosion();
        }
    }

    public int gethUoutput() {
        return this.kUoutput;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.condensationprogress = nbttagcompound.getFloat("condensationprogress");
        this.distilledwaterTank.readFromNBT(nbttagcompound.getCompoundTag("distilledwaterTank"));
        this.SteamTank.readFromNBT(nbttagcompound.getCompoundTag("SteamTank"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setFloat("condensationprogress", this.condensationprogress);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.distilledwaterTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("distilledwaterTank", (NBTBase)fluidTankTag);
        NBTTagCompound SteamTankTag = new NBTTagCompound();
        this.SteamTank.writeToNBT(fluidTankTag);
        nbttagcompound.setTag("SteamTank", (NBTBase)SteamTankTag);
    }

    public ContainerBase<TileEntitySteamKineticGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerSteamKineticGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuSteamKineticGenerator(new ContainerSteamKineticGenerator(entityPlayer, this));
    }

    public boolean facingMatchesDirection(ForgeDirection direction) {
        return direction.ordinal() == this.getFacing();
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
        if (side == 0 || side == 1) {
            return false;
        }
        return this.getFacing() != side;
    }

    @Override
    public void setFacing(short side) {
        super.setFacing(side);
    }

    @Override
    public int maxrequestkineticenergyTick(ForgeDirection directionFrom) {
        if (this.facingMatchesDirection(directionFrom)) {
            return this.kUoutput;
        }
        return 0;
    }

    @Override
    public int requestkineticenergy(ForgeDirection directionFrom, int requestkineticenergy) {
        if (this.facingMatchesDirection(directionFrom)) {
            return this.kUoutput;
        }
        return 0;
    }

    @Override
    public String getInventoryName() {
        return "Steam Kinetic Generator";
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!this.canFill(from, resource.getFluid())) {
            return 0;
        }
        if (resource.getFluid() == BlocksItems.getFluid(InternalName.fluidSteam) || resource.getFluid() == BlocksItems.getFluid(InternalName.fluidSuperheatedSteam)) {
            if (this.SteamTank.getFluid() != null && this.SteamTank.getFluid().getFluid() != resource.getFluid()) {
                this.SteamTank.drain(this.SteamTank.getFluidAmount(), true);
            }
            return this.SteamTank.fill(resource, doFill);
        }
        if (resource.getFluid() == FluidRegistry.WATER || resource.getFluid() == BlocksItems.getFluid(InternalName.fluidDistilledWater)) {
            if (this.distilledwaterTank.getFluid() != null && this.distilledwaterTank.getFluid().getFluid() != resource.getFluid()) {
                this.distilledwaterTank.drain(this.distilledwaterTank.getFluidAmount(), true);
            }
            return this.distilledwaterTank.fill(resource, doFill);
        }
        return 0;
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.distilledwaterTank.getFluid())) {
            return null;
        }
        if (!this.canDrain(from, resource.getFluid())) {
            return null;
        }
        return this.distilledwaterTank.drain(resource.amount, doDrain);
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return this.distilledwaterTank.drain(maxDrain, doDrain);
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (from.getOpposite().ordinal() == this.getFacing() || from.ordinal() == this.getFacing()) {
            return false;
        }
        if (fluid == BlocksItems.getFluid(InternalName.fluidSteam) || fluid == BlocksItems.getFluid(InternalName.fluidSuperheatedSteam)) {
            return this.SteamTank.getFluidAmount() < this.SteamTank.getCapacity();
        }
        if (fluid == FluidRegistry.WATER || fluid == BlocksItems.getFluid(InternalName.fluidDistilledWater)) {
            return this.distilledwaterTank.getFluidAmount() < this.distilledwaterTank.getCapacity();
        }
        return false;
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if (from.getOpposite().ordinal() == this.getFacing() || from.ordinal() == this.getFacing()) {
            return false;
        }
        FluidStack fs = this.distilledwaterTank.getFluid();
        return fs != null && fs.getFluid() == fluid;
    }

    public int gaugeLiquidScaled(int i, int tank) {
        switch (tank) {
            case 0: {
                if (this.distilledwaterTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.distilledwaterTank.getFluidAmount() * i / this.distilledwaterTank.getCapacity();
            }
        }
        return 0;
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{this.distilledwaterTank.getInfo(), this.SteamTank.getInfo()};
    }

    @Override
    public double getEnergy() {
        return 0.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return false;
    }

    public int getdistilledwaterTank() {
        return this.distilledwaterTank.getFluidAmount();
    }

    public FluidTank getTank() {
        return this.distilledwaterTank;
    }

    public boolean isHotSteam() {
        return this.SteamTank.getFluid() != null && this.SteamTank.getFluid().getFluid() == BlocksItems.getFluid(InternalName.fluidSuperheatedSteam);
    }

    public boolean isturbine() {
        return !this.turbineSlot.isEmpty();
    }

    public boolean isturbefilledupwithwater() {
        return this.isturbefilledupwithwater;
    }

    public int getTickRate() {
        return 20;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }
}

