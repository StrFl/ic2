/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.IBlockAccess
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.fluids.IFluidBlock
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.item.ElectricItem;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.Ic2Player;
import ic2.core.InvSlotConsumableBlock;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerMiner;
import ic2.core.block.machine.gui.GuiMiner;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityPump;
import ic2.core.init.MainConfig;
import ic2.core.item.tool.ItemScanner;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.IFluidBlock;

public class TileEntityMiner
extends TileEntityElectricMachine
implements IHasGui,
IUpgradableBlock {
    private Mode lastMode = Mode.None;
    public int progress = 0;
    private int scannedLevel = -1;
    private int scanRange = 0;
    private int lastX;
    private int lastZ;
    public boolean pumpMode = false;
    public boolean canProvideLiquid = false;
    public int liquidX;
    public int liquidY;
    public int liquidZ;
    private AudioSource audioSource;
    public final InvSlot buffer;
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlotConsumable drillSlot = new InvSlotConsumableId((TileEntityInventory)this, "drill", 19, InvSlot.Access.IO, 1, InvSlot.InvSide.TOP, Ic2Items.miningDrill.getItem(), Ic2Items.diamondDrill.getItem());
    public final InvSlotConsumable pipeSlot = new InvSlotConsumableBlock(this, "pipe", 18, InvSlot.Access.IO, 1, InvSlot.InvSide.TOP);
    public final InvSlotConsumable scannerSlot = new InvSlotConsumableId((TileEntityInventory)this, "scanner", 17, InvSlot.Access.IO, 1, InvSlot.InvSide.BOTTOM, Ic2Items.odScanner.getItem(), Ic2Items.ovScanner.getItem());

    public TileEntityMiner() {
        super(1000, ConfigUtil.getInt(MainConfig.get(), "balance/minerDischargeTier"), 0, false);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 16, 1);
        this.buffer = new InvSlot(this, "buffer", 1, InvSlot.Access.IO, 15, InvSlot.InvSide.SIDE);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        this.scannedLevel = -1;
        this.lastX = this.xCoord;
        this.lastZ = this.zCoord;
        this.canProvideLiquid = false;
    }

    @Override
    public void onUnloaded() {
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.lastMode = Mode.values()[nbtTagCompound.getInteger("lastMode")];
        this.progress = nbtTagCompound.getInteger("progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("lastMode", this.lastMode.ordinal());
        nbtTagCompound.setInteger("progress", this.progress);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.chargeTools();
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
        if (this.work()) {
            this.markDirty();
            this.setActive(true);
        } else {
            this.setActive(false);
        }
    }

    private void chargeTools() {
        if (!this.scannerSlot.isEmpty()) {
            this.energy -= ElectricItem.manager.charge(this.scannerSlot.get(), this.energy, 2, false, false);
        }
        if (!this.drillSlot.isEmpty()) {
            this.energy -= ElectricItem.manager.charge(this.drillSlot.get(), this.energy, 1, false, false);
        }
    }

    private boolean work() {
        int operationHeight = this.getOperationHeight();
        if (this.drillSlot.isEmpty()) {
            return this.withDrawPipe(operationHeight);
        }
        if (operationHeight >= 0) {
            Block block = this.worldObj.getBlock(this.xCoord, operationHeight, this.zCoord);
            if (!StackUtil.equals(block, Ic2Items.miningPipeTip)) {
                if (operationHeight > 0) {
                    return this.digDown(operationHeight, false);
                }
                return false;
            }
            MineResult result = this.mineLevel(operationHeight);
            if (result == MineResult.Done) {
                return this.digDown(operationHeight - 1, true);
            }
            return result == MineResult.Working;
        }
        return false;
    }

    private int getOperationHeight() {
        for (int y = this.yCoord - 1; y >= 0; --y) {
            Block block = this.worldObj.getBlock(this.xCoord, y, this.zCoord);
            if (StackUtil.equals(block, Ic2Items.miningPipe)) continue;
            return y;
        }
        return -1;
    }

    private boolean withDrawPipe(int y) {
        if (this.lastMode != Mode.Withdraw) {
            this.lastMode = Mode.Withdraw;
            this.progress = 0;
        }
        if (y < 0 || !StackUtil.equals(this.worldObj.getBlock(this.xCoord, y, this.zCoord), Ic2Items.miningPipeTip)) {
            ++y;
        }
        if (y != this.yCoord && this.energy >= 3.0) {
            if (this.progress < 20) {
                this.energy -= 3.0;
                ++this.progress;
            } else {
                this.progress = 0;
                this.removePipe(y);
            }
            return true;
        }
        return false;
    }

    private void removePipe(int y) {
        ItemStack filler;
        Item fillerItem;
        this.worldObj.setBlockToAir(this.xCoord, y, this.zCoord);
        this.storeDrop(Ic2Items.miningPipe.copy());
        ItemStack pipe = this.pipeSlot.consume(1, true, false);
        if (pipe != null && pipe.getItem() != Ic2Items.miningPipe.getItem() && (fillerItem = (filler = this.pipeSlot.consume(1)).getItem()) instanceof ItemBlock) {
            ((ItemBlock)fillerItem).onItemUse(filler, (EntityPlayer)new Ic2Player(this.worldObj), this.worldObj, this.xCoord, y + 1, this.zCoord, 0, 0.0f, 0.0f, 0.0f);
        }
    }

    private boolean digDown(int y, boolean removeTipAbove) {
        ItemStack pipe = this.pipeSlot.consume(1, true, false);
        if (pipe == null || pipe.getItem() != Ic2Items.miningPipe.getItem()) {
            return false;
        }
        if (y < 0) {
            if (removeTipAbove) {
                this.worldObj.setBlock(this.xCoord, y + 1, this.zCoord, StackUtil.getBlock(Ic2Items.miningPipe));
            }
            return false;
        }
        MineResult result = this.mineBlock(this.xCoord, y, this.zCoord);
        if (result == MineResult.Failed_Temp || result == MineResult.Failed_Perm) {
            if (removeTipAbove) {
                this.worldObj.setBlock(this.xCoord, y + 1, this.zCoord, StackUtil.getBlock(Ic2Items.miningPipe));
            }
            return false;
        }
        if (result == MineResult.Done) {
            if (removeTipAbove) {
                this.worldObj.setBlock(this.xCoord, y + 1, this.zCoord, StackUtil.getBlock(Ic2Items.miningPipe));
            }
            this.pipeSlot.consume(1);
            this.worldObj.setBlock(this.xCoord, y, this.zCoord, StackUtil.getBlock(Ic2Items.miningPipeTip));
        }
        return true;
    }

    private MineResult mineLevel(int y) {
        if (this.scannerSlot.isEmpty()) {
            return MineResult.Done;
        }
        if (this.scannedLevel != y) {
            this.scanRange = ((ItemScanner)this.scannerSlot.get().getItem()).startLayerScan(this.scannerSlot.get());
        }
        if (this.scanRange > 0) {
            this.scannedLevel = y;
            for (int x = this.xCoord - this.scanRange; x <= this.xCoord + this.scanRange; ++x) {
                for (int z = this.zCoord - this.scanRange; z <= this.zCoord + this.scanRange; ++z) {
                    LiquidUtil.LiquidData liquid;
                    Block block = this.worldObj.getBlock(x, y, z);
                    int meta = this.worldObj.getBlockMetadata(x, y, z);
                    boolean isValidTarget = false;
                    if (ItemScanner.isValuable(block, meta) && this.canMine(x, y, z)) {
                        isValidTarget = true;
                    } else if (this.pumpMode && (liquid = LiquidUtil.getLiquid(this.worldObj, x, y, z)) != null && this.canPump(x, y, z)) {
                        isValidTarget = true;
                    }
                    if (!isValidTarget) continue;
                    MineResult result = this.mineTowards(x, y, z);
                    if (result == MineResult.Done) {
                        return MineResult.Working;
                    }
                    if (result == MineResult.Failed_Perm) continue;
                    return result;
                }
            }
            return MineResult.Done;
        }
        return MineResult.Failed_Temp;
    }

    private MineResult mineTowards(int x, int y, int z) {
        int dx = Math.abs(x - this.xCoord);
        int sx = this.xCoord < x ? 1 : -1;
        int dz = -Math.abs(z - this.zCoord);
        int sz = this.zCoord < z ? 1 : -1;
        int err = dx + dz;
        int cx = this.xCoord;
        int cz = this.zCoord;
        while (cx != x || cz != z) {
            boolean isCurrentPos = cx == this.lastX && cz == this.lastZ;
            int e2 = 2 * err;
            if (e2 > dz) {
                err += dz;
                cx += sx;
            } else if (e2 < dx) {
                err += dx;
                cz += sz;
            }
            boolean isBlocking = false;
            if (isCurrentPos) {
                isBlocking = true;
            } else {
                LiquidUtil.LiquidData liquid;
                Block block = this.worldObj.getBlock(cx, y, cz);
                if (!block.isAir((IBlockAccess)this.worldObj, cx, y, cz) && ((liquid = LiquidUtil.getLiquid(this.worldObj, cx, y, cz)) == null || liquid.isSource || this.pumpMode && this.canPump(x, y, z))) {
                    isBlocking = true;
                }
            }
            if (!isBlocking) continue;
            MineResult result = this.mineBlock(cx, y, cz);
            if (result == MineResult.Done) {
                this.lastX = cx;
                this.lastZ = cz;
            }
            return result;
        }
        this.lastX = this.xCoord;
        this.lastZ = this.zCoord;
        return MineResult.Done;
    }

    private MineResult mineBlock(int x, int y, int z) {
        int duration;
        int energyPerTick;
        Mode mode;
        Block block = this.worldObj.getBlock(x, y, z);
        boolean isAirBlock = true;
        if (!block.isAir((IBlockAccess)this.worldObj, x, y, z)) {
            isAirBlock = false;
            LiquidUtil.LiquidData liquidData = LiquidUtil.getLiquid(this.worldObj, x, y, z);
            if (liquidData != null) {
                if (liquidData.isSource || this.pumpMode && this.canPump(x, y, z)) {
                    this.liquidX = x;
                    this.liquidY = y;
                    this.liquidZ = z;
                    this.canProvideLiquid = true;
                    return this.pumpMode ? MineResult.Failed_Temp : MineResult.Failed_Perm;
                }
            } else if (!this.canMine(x, y, z)) {
                return MineResult.Failed_Perm;
            }
        }
        this.canProvideLiquid = false;
        if (isAirBlock) {
            mode = Mode.MineAir;
            energyPerTick = 3;
            duration = 20;
        } else if (this.drillSlot.get().getItem() == Ic2Items.miningDrill.getItem()) {
            mode = Mode.MineDrill;
            energyPerTick = 6;
            duration = 200;
        } else if (this.drillSlot.get().getItem() == Ic2Items.diamondDrill.getItem()) {
            mode = Mode.MineDDrill;
            energyPerTick = 20;
            duration = 50;
        } else {
            throw new IllegalStateException("invalid drill: " + this.drillSlot.get());
        }
        if (this.lastMode != mode) {
            this.lastMode = mode;
            this.progress = 0;
        }
        if (this.progress < duration) {
            if (this.energy >= (double)energyPerTick) {
                this.energy -= (double)energyPerTick;
                ++this.progress;
                return MineResult.Working;
            }
        } else if (isAirBlock || this.harvestBlock(x, y, z, block)) {
            this.progress = 0;
            return MineResult.Done;
        }
        return MineResult.Failed_Temp;
    }

    private boolean harvestBlock(int x, int y, int z, Block block) {
        int energyCost = 2 * (this.yCoord - y);
        if (this.energy >= (double)energyCost) {
            if (this.drillSlot.get().getItem() == Ic2Items.miningDrill.getItem()) {
                if (!ElectricItem.manager.use(this.drillSlot.get(), 50.0, null)) {
                    return false;
                }
            } else if (this.drillSlot.get().getItem() == Ic2Items.diamondDrill.getItem()) {
                if (!ElectricItem.manager.use(this.drillSlot.get(), 80.0, null)) {
                    return false;
                }
            } else {
                throw new IllegalStateException("invalid drill: " + this.drillSlot.get());
            }
            this.energy -= (double)energyCost;
            ArrayList drops = block.getDrops(this.worldObj, x, y, z, this.worldObj.getBlockMetadata(x, y, z), 0);
            if (drops != null) {
                for (ItemStack drop : drops) {
                    this.storeDrop(drop);
                }
            }
            this.worldObj.setBlockToAir(x, y, z);
            return true;
        }
        return false;
    }

    private void storeDrop(ItemStack stack) {
        if (StackUtil.putInInventory(this, Direction.XN, stack, true) == 0) {
            StackUtil.dropAsEntity(this.worldObj, this.xCoord, this.yCoord, this.zCoord, stack);
        } else {
            StackUtil.putInInventory(this, Direction.XN, stack, false);
        }
    }

    public boolean canPump(int x, int y, int z) {
        return false;
    }

    public boolean canMine(int x, int y, int z) {
        Block block = this.worldObj.getBlock(x, y, z);
        if (block.isAir((IBlockAccess)this.worldObj, x, y, z)) {
            return true;
        }
        int meta = this.worldObj.getBlockMetadata(x, y, z);
        if (StackUtil.equals(block, Ic2Items.miningPipe) || StackUtil.equals(block, Ic2Items.miningPipeTip) || block == Blocks.chest) {
            return false;
        }
        if (block instanceof IFluidBlock && this.isPumpConnected(x, y, z)) {
            return true;
        }
        if ((block == Blocks.water || block == Blocks.flowing_water || block == Blocks.lava || block == Blocks.flowing_lava) && this.isPumpConnected(x, y, z)) {
            return true;
        }
        if (block.getBlockHardness(this.worldObj, x, y, z) < 0.0f) {
            return false;
        }
        if (block.canCollideCheck(meta, false) && block.getMaterial().isToolNotRequired()) {
            return true;
        }
        if (block == Blocks.web) {
            return true;
        }
        if (!this.drillSlot.isEmpty()) {
            return ForgeHooks.canToolHarvestBlock((Block)block, (int)meta, (ItemStack)this.drillSlot.get()) || this.drillSlot.get().func_150998_b(block);
        }
        return false;
    }

    public boolean isPumpConnected(int x, int y, int z) {
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) instanceof TileEntityPump && ((TileEntityPump)this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord)).pump(x, y, z, true, this) != null) {
            return true;
        }
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof TileEntityPump && ((TileEntityPump)this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord)).pump(x, y, z, true, this) != null) {
            return true;
        }
        if (this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof TileEntityPump && ((TileEntityPump)this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord)).pump(x, y, z, true, this) != null) {
            return true;
        }
        if (this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof TileEntityPump && ((TileEntityPump)this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord)).pump(x, y, z, true, this) != null) {
            return true;
        }
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof TileEntityPump && ((TileEntityPump)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1)).pump(x, y, z, true, this) != null) {
            return true;
        }
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof TileEntityPump && ((TileEntityPump)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1)).pump(x, y, z, true, this) != null;
    }

    public boolean isAnyPumpConnected() {
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) instanceof TileEntityPump) {
            return true;
        }
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof TileEntityPump) {
            return true;
        }
        if (this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof TileEntityPump) {
            return true;
        }
        if (this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof TileEntityPump) {
            return true;
        }
        if (this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof TileEntityPump) {
            return true;
        }
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof TileEntityPump;
    }

    @Override
    public String getInventoryName() {
        return "Miner";
    }

    public ContainerBase<TileEntityMiner> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerMiner(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiMiner(new ContainerMiner(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active") && this.prevActive != this.getActive()) {
            if (this.audioSource == null) {
                this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Machines/MinerOp.ogg", true, false, IC2.audioManager.getDefaultVolume());
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
    public double getEnergy() {
        return this.energy;
    }

    @Override
    public boolean useEnergy(double amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
            return true;
        }
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }

    static enum MineResult {
        Working,
        Done,
        Failed_Temp,
        Failed_Perm;

    }

    static enum Mode {
        None,
        Withdraw,
        MineAir,
        MineDrill,
        MineDDrill;

    }
}

