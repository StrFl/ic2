/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.logging.log4j.Level
 */
package ic2.core.block.reactor.tileentity;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IMetaDelegate;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.generator.block.BlockGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotReactor;
import ic2.core.block.reactor.block.BlockReactorAccessHatch;
import ic2.core.block.reactor.block.BlockReactorChamber;
import ic2.core.block.reactor.block.BlockReactorFluidPort;
import ic2.core.block.reactor.block.BlockReactorRedstonePort;
import ic2.core.block.reactor.block.BlockReactorVessel;
import ic2.core.block.reactor.container.ContainerNuclearReactor;
import ic2.core.block.reactor.gui.GuiNuclearReactor;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.reactor.ItemReactorHeatStorage;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.Level;

public class TileEntityNuclearReactorElectric
extends TileEntityInventory
implements IHasGui,
IReactor,
IEnergySource,
IMetaDelegate,
IFluidHandler {
    public float output = 0.0f;
    public int updateTicker;
    public int heat = 0;
    public int maxHeat = 10000;
    public float hem = 1.0f;
    private int EmitHeatbuffer = 0;
    public int EmitHeat = 0;
    private boolean redstone = false;
    private boolean fluidcoolreactor = false;
    public AudioSource audioSourceMain;
    public AudioSource audioSourceGeiger;
    private float lastOutput = 0.0f;
    public Block[][][] surroundings = new Block[5][5][5];
    public final FluidTank inputTank;
    public final FluidTank outputTank;
    private List<TileEntity> subTiles;
    public final InvSlotReactor reactorSlot;
    public final InvSlotOutput coolantoutputSlot;
    public final InvSlotOutput hotcoolantoutputSlot;
    public final InvSlotConsumableLiquidByList coolantinputSlot;
    public final InvSlotConsumableLiquidByTank hotcoolinputSlot;
    public boolean addedToEnergyNet = false;
    private static final float huOutputModifier = 2.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/FluidReactor/outputModifier");

    public TileEntityNuclearReactorElectric() {
        this.updateTicker = IC2.random.nextInt(this.getTickRate());
        this.inputTank = new FluidTank(10000);
        this.outputTank = new FluidTank(10000);
        this.reactorSlot = new InvSlotReactor(this, "reactor", 0, 54);
        this.coolantinputSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "coolantinputSlot", 55, InvSlot.Access.I, 1, InvSlot.InvSide.ANY, InvSlotConsumableLiquid.OpType.Drain, BlocksItems.getFluid(InternalName.fluidCoolant));
        this.hotcoolinputSlot = new InvSlotConsumableLiquidByTank(this, "hotcoolinputSlot", 56, InvSlot.Access.I, 1, InvSlot.InvSide.ANY, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
        this.coolantoutputSlot = new InvSlotOutput(this, "coolantoutputSlot", 57, 1);
        this.hotcoolantoutputSlot = new InvSlotOutput(this, "hotcoolantoutputSlot", 58, 1);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating() && !this.isFluidCooled()) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
    }

    @Override
    public void onUnloaded() {
        if (IC2.platform.isRendering()) {
            IC2.audioManager.removeSources(this);
            this.audioSourceMain = null;
            this.audioSourceGeiger = null;
        }
        if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        super.onUnloaded();
    }

    @Override
    public String getInventoryName() {
        return "Nuclear Reactor";
    }

    public int gaugeHeatScaled(int i) {
        return i * this.heat / (this.maxHeat / 100 * 85);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.heat = nbttagcompound.getInteger("heat");
        this.inputTank.readFromNBT(nbttagcompound.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbttagcompound.getCompoundTag("outputTank"));
        this.output = nbttagcompound.getShort("output");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound inputTankTag = new NBTTagCompound();
        this.inputTank.writeToNBT(inputTankTag);
        nbttagcompound.setTag("inputTank", (NBTBase)inputTankTag);
        NBTTagCompound outputTankTag = new NBTTagCompound();
        this.outputTank.writeToNBT(outputTankTag);
        nbttagcompound.setTag("outputTank", (NBTBase)outputTankTag);
        nbttagcompound.setInteger("heat", this.heat);
        nbttagcompound.setShort("output", (short)this.getReactorEnergyOutput());
    }

    @Override
    public void setRedstoneSignal(boolean redstone) {
        this.redstone = redstone;
    }

    @Override
    public void drawEnergy(double amount) {
    }

    public float sendEnergy(float send) {
        return 0.0f;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return true;
    }

    @Override
    public double getOfferedEnergy() {
        return this.getReactorEnergyOutput() * 5.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/nuclear");
    }

    @Override
    public int getSourceTier() {
        return 4;
    }

    @Override
    public double getReactorEUEnergyOutput() {
        return this.getOfferedEnergy();
    }

    @Override
    public List<TileEntity> getSubTiles() {
        if (this.subTiles == null) {
            this.subTiles = new ArrayList<TileEntity>();
            this.subTiles.add(this);
            for (Direction dir : Direction.directions) {
                TileEntity te = dir.applyToTileEntity(this);
                if (!(te instanceof TileEntityReactorChamberElectric) || te.isInvalid()) continue;
                this.subTiles.add(te);
            }
        }
        return this.subTiles;
    }

    private void processfluidsSlots() {
        RecipeOutput outputoutputSlot;
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.coolantoutputSlot.add(processResult);
        }
        if ((outputoutputSlot = this.processOutputSlot(true)) != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.hotcoolantoutputSlot.add(processResult);
        }
    }

    public void refreshChambers() {
        if (this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
        }
        this.subTiles = null;
        if (this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
        }
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.updateTicker++ % this.getTickRate() != 0) {
            return;
        }
        if (!this.worldObj.doChunksNearChunkExist(this.xCoord, this.yCoord, this.zCoord, 2)) {
            this.output = 0.0f;
        } else {
            boolean fluidcoolreactor_new;
            if (this.getReactorSize() == 9 && this.fluidcoolreactor != (fluidcoolreactor_new = this.readyforpressurizedreactor())) {
                if (fluidcoolreactor_new) {
                    if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
                        MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
                        this.addedToEnergyNet = false;
                    }
                    this.movefluidinWorld(false);
                } else {
                    if (IC2.platform.isSimulating() && !this.fluidcoolreactor) {
                        MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
                        this.addedToEnergyNet = true;
                    }
                    this.movefluidinWorld(true);
                }
                this.fluidcoolreactor = fluidcoolreactor_new;
            }
            this.dropAllUnfittingStuff();
            this.output = 0.0f;
            this.maxHeat = 10000;
            this.hem = 1.0f;
            this.processChambers();
            if (this.fluidcoolreactor) {
                this.processfluidsSlots();
                int huOtput = (int)(huOutputModifier * (float)this.EmitHeatbuffer);
                int outputroom = this.outputTank.getCapacity() - this.outputTank.getFluidAmount();
                if (outputroom > 0) {
                    FluidStack draincoolant = huOtput < outputroom ? this.inputTank.drain(huOtput, false) : this.inputTank.drain(outputroom, false);
                    if (draincoolant != null) {
                        this.EmitHeat = draincoolant.amount;
                        huOtput -= this.inputTank.drain((int)draincoolant.amount, (boolean)true).amount;
                        this.outputTank.fill(new FluidStack(BlocksItems.getFluid(InternalName.fluidHotCoolant), draincoolant.amount), true);
                    } else {
                        this.EmitHeat = 0;
                    }
                } else {
                    this.EmitHeat = 0;
                }
                this.addHeat(huOtput / 2);
            }
            this.EmitHeatbuffer = 0;
            if (this.calculateHeatEffects()) {
                return;
            }
            this.setActive(this.heat >= 1000 || this.output > 0.0f);
            this.markDirty();
        }
        IC2.network.get().updateTileEntityField(this, "output");
    }

    public void dropAllUnfittingStuff() {
        ItemStack stack;
        int i;
        for (i = 0; i < this.reactorSlot.size(); ++i) {
            stack = this.reactorSlot.get(i);
            if (stack == null || this.isUsefulItem(stack, false)) continue;
            this.reactorSlot.put(i, null);
            this.eject(stack);
        }
        for (i = this.reactorSlot.size(); i < this.reactorSlot.rawSize(); ++i) {
            stack = this.reactorSlot.get(i);
            this.reactorSlot.put(i, null);
            this.eject(stack);
        }
    }

    public boolean isUsefulItem(ItemStack stack, boolean forInsertion) {
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (forInsertion && this.fluidcoolreactor && item.getClass() == ItemReactorHeatStorage.class && ((ItemReactorHeatStorage)item).getCustomDamage(stack) > 0) {
            return false;
        }
        if (item instanceof IReactorComponent) {
            return true;
        }
        return item == Ic2Items.TritiumCell.getItem() || item == Ic2Items.reactorDepletedUraniumSimple.getItem() || item == Ic2Items.reactorDepletedUraniumDual.getItem() || item == Ic2Items.reactorDepletedUraniumQuad.getItem() || item == Ic2Items.reactorDepletedMOXSimple.getItem() || item == Ic2Items.reactorDepletedMOXDual.getItem() || item == Ic2Items.reactorDepletedMOXQuad.getItem();
    }

    public void eject(ItemStack drop) {
        if (!IC2.platform.isSimulating() || drop == null) {
            return;
        }
        float f = 0.7f;
        double d = (double)(this.worldObj.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        double d1 = (double)(this.worldObj.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        double d2 = (double)(this.worldObj.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        EntityItem entityitem = new EntityItem(this.worldObj, (double)this.xCoord + d, (double)this.yCoord + d1, (double)this.zCoord + d2, drop);
        entityitem.delayBeforeCanPickup = 10;
        this.worldObj.spawnEntityInWorld((Entity)entityitem);
    }

    public boolean calculateHeatEffects() {
        Material mat;
        Block block;
        int[] coord;
        if (this.heat < 4000 || !IC2.platform.isSimulating() || ConfigUtil.getFloat(MainConfig.get(), "protection/reactorExplosionPowerLimit") <= 0.0f) {
            return false;
        }
        float power = (float)this.heat / (float)this.maxHeat;
        if (power >= 1.0f) {
            this.explode();
            return true;
        }
        if (power >= 0.85f && this.worldObj.rand.nextFloat() <= 0.2f * this.hem && (coord = this.getRandCoord(2)) != null) {
            block = this.worldObj.getBlock(coord[0], coord[1], coord[2]);
            if (block.isAir((IBlockAccess)this.worldObj, coord[0], coord[1], coord[2])) {
                this.worldObj.setBlock(coord[0], coord[1], coord[2], (Block)Blocks.fire, 0, 7);
            } else if (block.getBlockHardness(this.worldObj, coord[0], coord[1], coord[2]) >= 0.0f && this.worldObj.getTileEntity(coord[0], coord[1], coord[2]) == null) {
                mat = block.getMaterial();
                if (mat == Material.rock || mat == Material.iron || mat == Material.lava || mat == Material.ground || mat == Material.clay) {
                    this.worldObj.setBlock(coord[0], coord[1], coord[2], (Block)Blocks.flowing_lava, 15, 7);
                } else {
                    this.worldObj.setBlock(coord[0], coord[1], coord[2], (Block)Blocks.fire, 0, 7);
                }
            }
        }
        if (power >= 0.7f) {
            List list1 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox((double)(this.xCoord - 3), (double)(this.yCoord - 3), (double)(this.zCoord - 3), (double)(this.xCoord + 4), (double)(this.yCoord + 4), (double)(this.zCoord + 4)));
            for (int l = 0; l < list1.size(); ++l) {
                Entity ent = (Entity)list1.get(l);
                ent.attackEntityFrom((DamageSource)IC2DamageSource.radiation, (float)((int)((float)this.worldObj.rand.nextInt(4) * this.hem)));
            }
        }
        if (power >= 0.5f && this.worldObj.rand.nextFloat() <= this.hem && (coord = this.getRandCoord(2)) != null && (block = this.worldObj.getBlock(coord[0], coord[1], coord[2])).getMaterial() == Material.water) {
            this.worldObj.setBlockToAir(coord[0], coord[1], coord[2]);
        }
        if (power >= 0.4f && this.worldObj.rand.nextFloat() <= this.hem && (coord = this.getRandCoord(2)) != null && this.worldObj.getTileEntity(coord[0], coord[1], coord[2]) == null && ((mat = (block = this.worldObj.getBlock(coord[0], coord[1], coord[2])).getMaterial()) == Material.wood || mat == Material.leaves || mat == Material.cloth)) {
            this.worldObj.setBlock(coord[0], coord[1], coord[2], (Block)Blocks.fire, 0, 7);
        }
        return false;
    }

    public int[] getRandCoord(int radius) {
        if (radius <= 0) {
            return null;
        }
        int[] c = new int[]{this.xCoord + this.worldObj.rand.nextInt(2 * radius + 1) - radius, this.yCoord + this.worldObj.rand.nextInt(2 * radius + 1) - radius, this.zCoord + this.worldObj.rand.nextInt(2 * radius + 1) - radius};
        if (c[0] == this.xCoord && c[1] == this.yCoord && c[2] == this.zCoord) {
            return null;
        }
        return c;
    }

    public void processChambers() {
        int size = this.getReactorSize();
        for (int pass = 0; pass < 2; ++pass) {
            for (int y = 0; y < 6; ++y) {
                for (int x = 0; x < size; ++x) {
                    ItemStack stack = this.reactorSlot.get(x, y);
                    if (stack == null || !(stack.getItem() instanceof IReactorComponent)) continue;
                    IReactorComponent comp = (IReactorComponent)stack.getItem();
                    comp.processChamber(this, stack, x, y, pass == 0);
                }
            }
        }
    }

    @Override
    public boolean produceEnergy() {
        return this.receiveredstone() && ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/generator") > 0.0f;
    }

    public boolean receiveredstone() {
        return this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord) || this.redstone;
    }

    public short getReactorSize() {
        if (this.worldObj == null) {
            return 9;
        }
        short cols = 3;
        for (Direction direction : Direction.directions) {
            TileEntity target = direction.applyToTileEntity(this);
            if (!(target instanceof TileEntityReactorChamberElectric)) continue;
            cols = (short)(cols + 1);
        }
        return cols;
    }

    @Override
    public int getTickRate() {
        return 20;
    }

    public ContainerBase<TileEntityNuclearReactorElectric> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerNuclearReactor(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiNuclearReactor(new ContainerNuclearReactor(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("output")) {
            if (this.output > 0.0f) {
                if (this.lastOutput <= 0.0f) {
                    if (this.audioSourceMain == null) {
                        this.audioSourceMain = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/NuclearReactorLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    }
                    if (this.audioSourceMain != null) {
                        this.audioSourceMain.play();
                    }
                }
                if (this.output < 40.0f) {
                    if (this.lastOutput <= 0.0f || this.lastOutput >= 40.0f) {
                        if (this.audioSourceGeiger != null) {
                            this.audioSourceGeiger.remove();
                        }
                        this.audioSourceGeiger = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/GeigerLowEU.ogg", true, false, IC2.audioManager.getDefaultVolume());
                        if (this.audioSourceGeiger != null) {
                            this.audioSourceGeiger.play();
                        }
                    }
                } else if (this.output < 80.0f) {
                    if (this.lastOutput < 40.0f || this.lastOutput >= 80.0f) {
                        if (this.audioSourceGeiger != null) {
                            this.audioSourceGeiger.remove();
                        }
                        this.audioSourceGeiger = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/GeigerMedEU.ogg", true, false, IC2.audioManager.getDefaultVolume());
                        if (this.audioSourceGeiger != null) {
                            this.audioSourceGeiger.play();
                        }
                    }
                } else if (this.output >= 80.0f && this.lastOutput < 80.0f) {
                    if (this.audioSourceGeiger != null) {
                        this.audioSourceGeiger.remove();
                    }
                    this.audioSourceGeiger = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/GeigerHighEU.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    if (this.audioSourceGeiger != null) {
                        this.audioSourceGeiger.play();
                    }
                }
            } else if (this.lastOutput > 0.0f) {
                if (this.audioSourceMain != null) {
                    this.audioSourceMain.stop();
                }
                if (this.audioSourceGeiger != null) {
                    this.audioSourceGeiger.stop();
                }
            }
            this.lastOutput = this.output;
        }
        super.onNetworkUpdate(field);
    }

    @Override
    public float getWrenchDropRate() {
        return 0.8f;
    }

    @Override
    public ChunkCoordinates getPosition() {
        return new ChunkCoordinates(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public World getWorld() {
        return this.worldObj;
    }

    @Override
    public int getHeat() {
        return this.heat;
    }

    @Override
    public void setHeat(int heat1) {
        this.heat = heat1;
    }

    @Override
    public int addHeat(int amount) {
        this.heat += amount;
        return this.heat;
    }

    @Override
    public ItemStack getItemAt(int x, int y) {
        if (x < 0 || x >= this.getReactorSize() || y < 0 || y >= 6) {
            return null;
        }
        return this.reactorSlot.get(x, y);
    }

    @Override
    public void setItemAt(int x, int y, ItemStack item) {
        if (x < 0 || x >= this.getReactorSize() || y < 0 || y >= 6) {
            return;
        }
        this.reactorSlot.put(x, y, item);
    }

    @Override
    public void explode() {
        float boomPower = 10.0f;
        float boomMod = 1.0f;
        for (int i = 0; i < this.reactorSlot.size(); ++i) {
            ItemStack stack = this.reactorSlot.get(i);
            if (stack != null && stack.getItem() instanceof IReactorComponent) {
                float f = ((IReactorComponent)stack.getItem()).influenceExplosion(this, stack);
                if (f > 0.0f && f < 1.0f) {
                    boomMod *= f;
                } else {
                    boomPower += f;
                }
            }
            this.reactorSlot.put(i, null);
        }
        IC2.log.log(LogCategory.PlayerActivity, Level.INFO, "Nuclear Reactor at %s melted (raw explosion power %f)", Util.formatPosition(this), Float.valueOf(boomPower *= this.hem * boomMod));
        boomPower = Math.min(boomPower, ConfigUtil.getFloat(MainConfig.get(), "protection/reactorExplosionPowerLimit"));
        for (Direction direction : Direction.directions) {
            TileEntity target = direction.applyToTileEntity(this);
            if (!(target instanceof TileEntityReactorChamberElectric)) continue;
            this.worldObj.setBlockToAir(target.xCoord, target.yCoord, target.zCoord);
        }
        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, boomPower, 0.01f, ExplosionIC2.Type.Nuclear);
        explosion.doExplosion();
    }

    @Override
    public void addEmitHeat(int heat) {
        this.EmitHeatbuffer += heat;
    }

    @Override
    public int getMaxHeat() {
        return this.maxHeat;
    }

    @Override
    public void setMaxHeat(int newMaxHeat) {
        this.maxHeat = newMaxHeat;
    }

    @Override
    public float getHeatEffectModifier() {
        return this.hem;
    }

    @Override
    public void setHeatEffectModifier(float newHEM) {
        this.hem = newHEM;
    }

    @Override
    public float getReactorEnergyOutput() {
        return this.output;
    }

    @Override
    public float addOutput(float energy) {
        return this.output += energy;
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.coolantinputSlot.isEmpty() && this.coolantinputSlot.transferToTank((IFluidTank)this.inputTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.coolantoutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.hotcoolinputSlot.isEmpty() && this.hotcoolinputSlot.transferFromTank((IFluidTank)this.outputTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.hotcoolantoutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    public boolean isFluidCooled() {
        return this.fluidcoolreactor;
    }

    private void movefluidinWorld(boolean out) {
        if (out) {
            if (this.inputTank.getFluidAmount() >= 1000 || this.outputTank.getFluidAmount() >= 1000) {
                for (int yoffset = 1; yoffset < 4; ++yoffset) {
                    for (int xoffset = 1; xoffset < 4; ++xoffset) {
                        for (int zoffset = 1; zoffset < 4; ++zoffset) {
                            if (!(this.surroundings[xoffset][yoffset][zoffset] instanceof BlockAir)) continue;
                            if (this.inputTank.getFluidAmount() >= 1000) {
                                this.worldObj.setBlock(xoffset + this.xCoord - 2, yoffset + this.yCoord - 2, zoffset + this.zCoord - 2, this.inputTank.getFluid().getFluid().getBlock());
                                this.inputTank.drain(1000, true);
                                continue;
                            }
                            if (this.outputTank.getFluidAmount() < 1000) continue;
                            this.worldObj.setBlock(xoffset + this.xCoord - 2, yoffset + this.yCoord - 2, zoffset + this.zCoord - 2, this.outputTank.getFluid().getFluid().getBlock());
                            this.outputTank.drain(1000, true);
                        }
                    }
                }
                if (this.inputTank.getFluidAmount() < 1000) {
                    this.inputTank.setFluid(null);
                }
                if (this.outputTank.getFluidAmount() < 1000) {
                    this.outputTank.setFluid(null);
                }
            } else {
                this.inputTank.setFluid(null);
                this.outputTank.setFluid(null);
            }
        } else {
            Fluid coolantFluid = BlocksItems.getFluid(InternalName.fluidCoolant);
            Block coolantBlock = BlocksItems.getFluidBlock(InternalName.fluidCoolant);
            Fluid hotCoolantFluid = BlocksItems.getFluid(InternalName.fluidHotCoolant);
            Block hotCoolantBlock = BlocksItems.getFluidBlock(InternalName.fluidHotCoolant);
            for (int yoffset = 1; yoffset < 4; ++yoffset) {
                for (int xoffset = 1; xoffset < 4; ++xoffset) {
                    for (int zoffset = 1; zoffset < 4; ++zoffset) {
                        if (this.surroundings[xoffset][yoffset][zoffset] == coolantBlock) {
                            this.worldObj.setBlock(xoffset + this.xCoord - 2, yoffset + this.yCoord - 2, zoffset + this.zCoord - 2, Blocks.air);
                            this.inputTank.fill(new FluidStack(coolantFluid, 1000), true);
                            continue;
                        }
                        if (this.surroundings[xoffset][yoffset][zoffset] != hotCoolantBlock) continue;
                        this.worldObj.setBlock(xoffset + this.xCoord - 2, yoffset + this.yCoord - 2, zoffset + this.zCoord - 2, Blocks.air);
                        this.outputTank.fill(new FluidStack(hotCoolantFluid, 1000), true);
                    }
                }
            }
        }
    }

    private boolean readyforpressurizedreactor() {
        int zoffset;
        int yoffset;
        int xoffset;
        Block coolantBlock = BlocksItems.getFluidBlock(InternalName.fluidCoolant);
        Block hotCoolantBlock = BlocksItems.getFluidBlock(InternalName.fluidHotCoolant);
        for (xoffset = -2; xoffset < 3; ++xoffset) {
            for (yoffset = -2; yoffset < 3; ++yoffset) {
                for (zoffset = -2; zoffset < 3; ++zoffset) {
                    Block block;
                    this.surroundings[xoffset + 2][yoffset + 2][zoffset + 2] = this.worldObj.isAirBlock(xoffset + this.xCoord, yoffset + this.yCoord, zoffset + this.zCoord) ? Blocks.air : (((block = this.worldObj.getBlock(xoffset + this.xCoord, yoffset + this.yCoord, zoffset + this.zCoord)) == coolantBlock || block == hotCoolantBlock) && this.worldObj.getBlockMetadata(xoffset + this.xCoord, yoffset + this.yCoord, zoffset + this.zCoord) != 0 ? Blocks.air : block);
                }
            }
        }
        for (xoffset = 1; xoffset < 4; ++xoffset) {
            for (yoffset = 1; yoffset < 4; ++yoffset) {
                for (zoffset = 1; zoffset < 4; ++zoffset) {
                    if (this.surroundings[xoffset][yoffset][zoffset] instanceof BlockGenerator || this.surroundings[xoffset][yoffset][zoffset] instanceof BlockReactorChamber || this.surroundings[xoffset][yoffset][zoffset] == coolantBlock || this.surroundings[xoffset][yoffset][zoffset] == hotCoolantBlock || this.surroundings[xoffset][yoffset][zoffset] instanceof BlockAir) continue;
                    return false;
                }
            }
        }
        for (xoffset = 0; xoffset < 5; ++xoffset) {
            for (yoffset = 0; yoffset < 5; ++yoffset) {
                if (!(this.surroundings[xoffset][4][yoffset] instanceof BlockReactorVessel || this.surroundings[xoffset][4][yoffset] instanceof BlockReactorAccessHatch || this.surroundings[xoffset][4][yoffset] instanceof BlockReactorRedstonePort || this.surroundings[xoffset][4][yoffset] instanceof BlockReactorFluidPort)) {
                    return false;
                }
                if (!(this.surroundings[xoffset][0][yoffset] instanceof BlockReactorVessel || this.surroundings[xoffset][0][yoffset] instanceof BlockReactorAccessHatch || this.surroundings[xoffset][0][yoffset] instanceof BlockReactorRedstonePort || this.surroundings[xoffset][0][yoffset] instanceof BlockReactorFluidPort)) {
                    return false;
                }
                if (!(this.surroundings[0][xoffset][yoffset] instanceof BlockReactorVessel || this.surroundings[0][xoffset][yoffset] instanceof BlockReactorAccessHatch || this.surroundings[0][xoffset][yoffset] instanceof BlockReactorRedstonePort || this.surroundings[0][xoffset][yoffset] instanceof BlockReactorFluidPort)) {
                    return false;
                }
                if (!(this.surroundings[4][xoffset][yoffset] instanceof BlockReactorVessel || this.surroundings[4][xoffset][yoffset] instanceof BlockReactorAccessHatch || this.surroundings[4][xoffset][yoffset] instanceof BlockReactorRedstonePort || this.surroundings[4][xoffset][yoffset] instanceof BlockReactorFluidPort)) {
                    return false;
                }
                if (!(this.surroundings[yoffset][xoffset][0] instanceof BlockReactorVessel || this.surroundings[yoffset][xoffset][0] instanceof BlockReactorAccessHatch || this.surroundings[yoffset][xoffset][0] instanceof BlockReactorRedstonePort || this.surroundings[yoffset][xoffset][0] instanceof BlockReactorFluidPort)) {
                    return false;
                }
                if (this.surroundings[yoffset][xoffset][4] instanceof BlockReactorVessel || this.surroundings[yoffset][xoffset][4] instanceof BlockReactorAccessHatch || this.surroundings[yoffset][xoffset][4] instanceof BlockReactorRedstonePort || this.surroundings[yoffset][xoffset][4] instanceof BlockReactorFluidPort) continue;
                return false;
            }
        }
        return true;
    }

    public int gaugeLiquidScaled(int i, int tank) {
        switch (tank) {
            case 0: {
                if (this.inputTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.inputTank.getFluidAmount() * i / this.inputTank.getCapacity();
            }
            case 1: {
                if (this.outputTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.outputTank.getFluidAmount() * i / this.outputTank.getCapacity();
            }
        }
        return 0;
    }

    public FluidTank getinputtank() {
        return this.inputTank;
    }

    public FluidTank getoutputtank() {
        return this.outputTank;
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{this.inputTank.getInfo(), this.outputTank.getInfo()};
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (!this.fluidcoolreactor) {
            return false;
        }
        return fluid == BlocksItems.getFluid(InternalName.fluidCoolant);
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if (!this.fluidcoolreactor) {
            return false;
        }
        FluidStack fluidStack = this.outputTank.getFluid();
        if (fluidStack == null) {
            return false;
        }
        return fluidStack.isFluidEqual(new FluidStack(fluid, 1));
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!this.canFill(from, resource.getFluid())) {
            return 0;
        }
        return this.inputTank.fill(resource, doFill);
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.outputTank.getFluid())) {
            return null;
        }
        if (!this.canDrain(from, resource.getFluid())) {
            return null;
        }
        return this.outputTank.drain(resource.amount, doDrain);
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return this.outputTank.drain(maxDrain, doDrain);
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }
}

