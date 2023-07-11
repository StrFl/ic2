/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.energy.tile.IHeatSource;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.machine.container.ContainerSteamGenerator;
import ic2.core.block.machine.gui.GuiSteamGenerator;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.util.BiomUtil;
import ic2.core.util.LiquidUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntitySteamGenerator
extends TileEntityInventory
implements IHasGui,
IFluidHandler,
INetworkClientTileEntityEventListener {
    private final int maxcalcification;
    private int calcification = 0;
    private int outputtyp;
    private final float maxsystemheat;
    private float systemheat;
    private int pressurevalve = 0;
    private int outputmb = 0;
    private int inputmb = 0;
    public FluidTank WaterTank = new FluidTank(10000);
    private int heatinput;
    private boolean newActive = false;

    public TileEntitySteamGenerator() {
        this.maxcalcification = 100000;
        this.maxsystemheat = 500.0f;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.WaterTank.readFromNBT(nbttagcompound.getCompoundTag("WaterTank"));
        this.inputmb = nbttagcompound.getInteger("inputmb");
        this.pressurevalve = nbttagcompound.getInteger("pressurevalve");
        this.systemheat = nbttagcompound.getFloat("systemheat");
        this.calcification = nbttagcompound.getInteger("calcification");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound inputTankTag = new NBTTagCompound();
        this.WaterTank.writeToNBT(inputTankTag);
        nbttagcompound.setTag("WaterTank", (NBTBase)inputTankTag);
        nbttagcompound.setInteger("inputmb", this.inputmb);
        nbttagcompound.setInteger("pressurevalve", this.pressurevalve);
        nbttagcompound.setFloat("systemheat", this.systemheat);
        nbttagcompound.setInteger("calcification", this.calcification);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.systemheat < (float)BiomUtil.gerBiomTemperature(this.worldObj, this.xCoord, this.zCoord)) {
            this.systemheat = BiomUtil.gerBiomTemperature(this.worldObj, this.xCoord, this.zCoord);
        }
        if (!this.iscalcified()) {
            this.newActive = this.work();
            if (this.getActive() != this.newActive) {
                this.setActive(this.newActive);
            }
        } else if (this.getActive()) {
            this.setActive(false);
        }
        if (!this.getActive()) {
            this.cooldown(0.01f);
        }
    }

    private boolean work() {
        FluidStack outputfluid;
        if (this.WaterTank.getFluidAmount() > 0 && this.inputmb > 0 && (outputfluid = this.getOutputfluid()) != null) {
            this.outputmb = outputfluid.amount;
            this.outputtyp = this.getoutputtyp(outputfluid);
            int amount = LiquidUtil.distribute(this, outputfluid, false);
            outputfluid.amount -= amount;
            if (outputfluid.amount > 0) {
                if ((this.outputtyp == 2 || this.outputtyp == 3) && IC2.random.nextInt(10) == 0) {
                    ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, 1.0f, 1.0f, ExplosionIC2.Type.Heat);
                    explosion.doExplosion();
                } else {
                    this.WaterTank.fill(outputfluid, true);
                }
            }
            return true;
        }
        this.outputmb = 0;
        this.outputtyp = -1;
        this.heatinput = 0;
        return this.heatupmax();
    }

    private boolean heatupmax() {
        this.heatinput = this.requestHeat(1200);
        if (this.heatinput > 0) {
            this.heatup(this.heatinput);
            return true;
        }
        return false;
    }

    private int getoutputtyp(FluidStack fluid) {
        if (fluid.getFluid().equals(BlocksItems.getFluid(InternalName.fluidSuperheatedSteam))) {
            return 3;
        }
        if (fluid.getFluid().equals(BlocksItems.getFluid(InternalName.fluidSteam))) {
            return 2;
        }
        if (fluid.getFluid().equals(BlocksItems.getFluid(InternalName.fluidDistilledWater))) {
            return 1;
        }
        if (fluid.getFluid().equals(FluidRegistry.WATER)) {
            return 0;
        }
        return -1;
    }

    private FluidStack getOutputfluid() {
        boolean cancalcification = true;
        if (this.WaterTank.getFluid() == null) {
            return null;
        }
        Fluid fluidintank = this.WaterTank.getFluid().getFluid();
        if (fluidintank.equals(BlocksItems.getFluid(InternalName.fluidDistilledWater))) {
            cancalcification = false;
        }
        if (this.systemheat < 100.0f) {
            this.heatupmax();
            return this.WaterTank.drain(this.inputmb, true);
        }
        int hu_need = 100 + Math.round((float)this.pressurevalve / 220.0f * 100.0f);
        int TargetTemp = (int)(100L + Math.round((double)((float)this.pressurevalve / 220.0f * 100.0f) * 2.74));
        if ((float)Math.round(this.systemheat * 10.0f) / 10.0f == (float)TargetTemp) {
            int heat;
            this.heatinput = heat = this.requestHeat(this.inputmb * hu_need);
            if (heat == this.inputmb * hu_need) {
                if (this.systemheat >= 374.0f) {
                    if (cancalcification) {
                        ++this.calcification;
                    }
                    this.WaterTank.drain(this.inputmb, true);
                    return new FluidStack(BlocksItems.getFluid(InternalName.fluidSuperheatedSteam), this.inputmb * 100);
                }
                if (cancalcification) {
                    ++this.calcification;
                }
                this.WaterTank.drain(this.inputmb, true);
                return new FluidStack(BlocksItems.getFluid(InternalName.fluidSteam), this.inputmb * 100);
            }
            this.heatup(heat);
            return this.WaterTank.drain(this.inputmb, true);
        }
        if (this.systemheat > (float)TargetTemp) {
            this.heatinput = 0;
            int count = this.inputmb;
            while (this.systemheat > (float)TargetTemp) {
                this.cooldown(0.1f);
                if (cancalcification) {
                    ++this.calcification;
                }
                if (--count != 0) continue;
            }
            this.WaterTank.drain(this.inputmb - count, true);
            return new FluidStack(BlocksItems.getFluid(InternalName.fluidSteam), (this.inputmb - count) * 100);
        }
        this.heatupmax();
        return this.WaterTank.drain(this.inputmb, true);
    }

    private void heatup(int heatinput) {
        this.systemheat += (float)heatinput * 5.0E-4f;
        if (this.systemheat > this.maxsystemheat) {
            this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
            ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, 10.0f, 0.01f, ExplosionIC2.Type.Heat);
            explosion.doExplosion();
        }
    }

    private void cooldown(float cool) {
        if (this.systemheat > (float)BiomUtil.gerBiomTemperature(this.worldObj, this.xCoord, this.zCoord)) {
            this.systemheat -= cool;
        }
    }

    private int requestHeat(int requestHeat) {
        int requestHeat_temp = requestHeat;
        for (Direction direction : Direction.directions) {
            TileEntity target = direction.applyToTileEntity(this);
            if (!(target instanceof IHeatSource)) continue;
            int amount = ((IHeatSource)target).requestHeat(direction.toForgeDirection().getOpposite(), requestHeat_temp);
            if (amount > 0) {
                requestHeat_temp -= amount;
            }
            if (requestHeat_temp != 0) continue;
            return requestHeat;
        }
        return requestHeat - requestHeat_temp;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (event > 2000 || event < -2000) {
            if (event > 2000) {
                this.pressurevalve += event - 2000;
            }
            if (event < -2000) {
                this.pressurevalve += event + 2000;
            }
            if (this.pressurevalve > 300) {
                this.pressurevalve = 300;
            }
            if (this.pressurevalve < 0) {
                this.pressurevalve = 0;
            }
        } else {
            this.inputmb += event;
            if (this.inputmb > 1000) {
                this.inputmb = 1000;
            }
            if (this.inputmb < 0) {
                this.inputmb = 0;
            }
        }
    }

    public int gaugeHeatScaled(int i) {
        return (int)((float)i * this.systemheat / this.maxsystemheat);
    }

    public int gaugecalcificationScaled(int i) {
        return i * this.calcification / this.maxcalcification;
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!this.canFill(from, resource.getFluid())) {
            return 0;
        }
        int amount = this.WaterTank.fill(resource, doFill);
        return amount;
    }

    public int gaugeLiquidScaled(int i, int tank) {
        switch (tank) {
            case 0: {
                if (this.WaterTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.WaterTank.getFluidAmount() * i / this.WaterTank.getCapacity();
            }
        }
        return 0;
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid.equals(BlocksItems.getFluid(InternalName.fluidDistilledWater)) || fluid.equals(FluidRegistry.WATER);
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{this.WaterTank.getInfo()};
    }

    public ContainerBase<TileEntitySteamGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerSteamGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiSteamGenerator(new ContainerSteamGenerator(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public String getInventoryName() {
        return "Steam Generator";
    }

    public int getoutputmb() {
        return this.outputmb;
    }

    public int getinputmb() {
        return this.inputmb;
    }

    public int getheatinput() {
        return this.heatinput;
    }

    public int getpressurevalve() {
        return this.pressurevalve;
    }

    public float getsystemheat() {
        return (float)Math.round(this.systemheat * 10.0f) / 10.0f;
    }

    public float getcalcification() {
        return (float)Math.round((float)this.calcification / (float)this.maxcalcification * 100.0f * 100.0f) / 100.0f;
    }

    private boolean iscalcified() {
        return this.calcification >= this.maxcalcification;
    }

    public String gtoutputfluid() {
        switch (this.outputtyp) {
            case 0: {
                return StatCollector.translateToLocal((String)"ic2.SteamGenerator.output.water");
            }
            case 1: {
                return StatCollector.translateToLocal((String)"ic2.SteamGenerator.output.destiwater");
            }
            case 2: {
                return StatCollector.translateToLocal((String)"ic2.SteamGenerator.output.steam");
            }
            case 3: {
                return StatCollector.translateToLocal((String)"ic2.SteamGenerator.output.hotsteam");
            }
        }
        return "";
    }
}

