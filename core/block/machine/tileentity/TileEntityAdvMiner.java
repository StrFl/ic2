/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.IBlockAccess
 *  net.minecraftforge.fluids.BlockFluidClassic
 *  net.minecraftforge.fluids.IFluidBlock
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.IRecipeInput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.Ic2Player;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerAdvMiner;
import ic2.core.block.machine.gui.GuiAdvMiner;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.init.MainConfig;
import ic2.core.item.tool.ItemScanner;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.IFluidBlock;

public class TileEntityAdvMiner
extends TileEntityElectricMachine
implements IHasGui,
INetworkClientTileEntityEventListener,
IUpgradableBlock {
    private final List<ItemStack> itemstack = new ArrayList<ItemStack>();
    private int currectblockscanncount;
    private int blockscanncount;
    public final int defaultTier;
    public final int workTick;
    public boolean blacklist = true;
    public boolean silktouch = false;
    public boolean redstonePowered = false;
    public int energyConsume = 512;
    public int xcounter = 99;
    public int zcounter = 99;
    private int minelayer = -1;
    private int minetargetX = -1;
    private int minetargetZ = -1;
    private short ticker = 0;
    public final InvSlotConsumableId scannerSlot = new InvSlotConsumableId((TileEntityInventory)this, "scanner", 1, InvSlot.Access.IO, 1, InvSlot.InvSide.BOTTOM, Ic2Items.odScanner.getItem(), Ic2Items.ovScanner.getItem());
    public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, "upgrade", 3, 4);
    public final InvSlot ListSlot = new InvSlot(this, "list", 8, null, 15);
    protected final Redstone redstone = this.addComponent(new Redstone(this));

    public TileEntityAdvMiner() {
        super(4000000, 3, 0);
        this.defaultTier = 3;
        this.workTick = 20;
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            if (this.minelayer < 0) {
                this.minelayer = this.yCoord - 1;
            }
            this.setUpgradestat();
        }
    }

    private void chargeTool() {
        if (!this.scannerSlot.isEmpty()) {
            this.energy -= ElectricItem.manager.charge(this.scannerSlot.get(), this.energy, 2, false, false);
        }
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.chargeTool();
        this.setUpgradestat();
        if (this.work()) {
            this.markDirty();
            if (!this.getActive()) {
                this.setActive(true);
            }
        } else if (this.getActive()) {
            this.setActive(false);
        }
    }

    private boolean work() {
        if (this.energy < (double)this.energyConsume) {
            return false;
        }
        if (this.redstone.hasRedstoneInput()) {
            return false;
        }
        if (this.minelayer == 0) {
            return false;
        }
        if (this.scannerSlot.isEmpty()) {
            return false;
        }
        if (this.scannerSlot.get().getItem() instanceof ItemScanner && !((ItemScanner)this.scannerSlot.get().getItem()).haveChargeforScan(this.scannerSlot.get())) {
            return false;
        }
        int range = 0;
        if (this.scannerSlot.get().getItem() == Ic2Items.odScanner.getItem()) {
            range = 16;
        }
        if (this.scannerSlot.get().getItem() == Ic2Items.ovScanner.getItem()) {
            range = 32;
        }
        if (this.ticker == this.workTick) {
            this.currectblockscanncount = this.blockscanncount;
            while (this.minelayer > 0 && this.currectblockscanncount > 0) {
                Block block;
                if (this.xcounter == 99) {
                    this.xcounter = 0 - range / 2;
                }
                if (this.zcounter == 99) {
                    this.zcounter = 0 - range / 2;
                }
                if (this.xcounter <= range / 2) {
                    if (this.zcounter < range / 2) {
                        this.minetargetX = this.xCoord - this.xcounter;
                        this.minetargetZ = this.zCoord - this.zcounter;
                        ++this.zcounter;
                    } else {
                        this.minetargetX = this.xCoord - this.xcounter;
                        this.minetargetZ = this.zCoord - this.zcounter;
                        ++this.xcounter;
                        this.zcounter = 0 - range / 2;
                    }
                } else {
                    --this.minelayer;
                    this.xcounter = 0 - range / 2;
                    this.zcounter = 0 - range / 2;
                    continue;
                }
                if (this.scannerSlot.get().getItem() instanceof ItemScanner) {
                    ((ItemScanner)this.scannerSlot.get().getItem()).discharge(this.scannerSlot.get(), 64);
                }
                if (!(block = this.worldObj.getBlock(this.minetargetX, this.minelayer, this.minetargetZ)).isAir((IBlockAccess)this.worldObj, this.minetargetX, this.minelayer, this.minetargetZ) && this.canMine(this.minetargetX, this.minelayer, this.minetargetZ, block)) {
                    this.doMine(block);
                    break;
                }
                --this.currectblockscanncount;
            }
            this.ticker = 0;
        } else {
            this.ticker = (short)(this.ticker + 1);
        }
        return true;
    }

    public void doMine(Block block) {
        if (this.silktouch && block.canSilkHarvest(this.worldObj, (EntityPlayer)new Ic2Player(this.worldObj), this.minetargetX, this.minelayer, this.minetargetZ, this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ))) {
            if (Item.getItemFromBlock((Block)block) != null && StackUtil.check(new ItemStack(Item.getItemFromBlock((Block)block), 1, this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ)))) {
                StackUtil.distribute(this, new ItemStack(Item.getItemFromBlock((Block)block), 1, this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ)), false);
            }
        } else {
            StackUtil.distributeDrop(this, block.getDrops(this.worldObj, this.minetargetX, this.minelayer, this.minetargetZ, this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ), 0));
        }
        this.worldObj.setBlockToAir(this.minetargetX, this.minelayer, this.minetargetZ);
        this.energy -= (double)this.energyConsume;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean canMine(int x, int y, int z, Block block) {
        this.itemstack.clear();
        if (block.hasTileEntity(this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ))) {
            ItemStack stack = new ItemStack(block, 1, this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ));
            int max = 0;
            for (Map.Entry<IRecipeInput, Integer> entry : IC2.valuableOres.entrySet()) {
                if (!entry.getKey().matches(stack)) continue;
                ++max;
            }
            if (max == 0) {
                return false;
            }
        }
        if (block instanceof IFluidBlock || block instanceof BlockFluidClassic || block instanceof BlockStaticLiquid || block instanceof BlockDynamicLiquid) {
            return false;
        }
        if (block.getBlockHardness(this.worldObj, x, y, z) < 0.0f) {
            return false;
        }
        if (this.silktouch && block.canSilkHarvest(this.worldObj, (EntityPlayer)new Ic2Player(this.worldObj), this.minetargetX, this.minelayer, this.minetargetZ, this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ))) {
            if (Item.getItemFromBlock((Block)block) == null) return false;
            if (!StackUtil.check(new ItemStack(Item.getItemFromBlock((Block)block), 1, this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ)))) return false;
            this.itemstack.add(new ItemStack(Item.getItemFromBlock((Block)block), 1, this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ)));
        } else {
            this.itemstack.addAll(block.getDrops(this.worldObj, this.minetargetX, this.minelayer, this.minetargetZ, this.worldObj.getBlockMetadata(this.minetargetX, this.minelayer, this.minetargetZ), 0));
        }
        if (this.itemstack.isEmpty()) return false;
        if (this.blacklist) {
            for (int i = 0; i < this.ListSlot.size(); ++i) {
                if (this.ListSlot.get(i) == null || !StackUtil.isStackEqual(this.itemstack.get(0), this.ListSlot.get(i))) continue;
                return false;
            }
            return true;
        } else {
            for (int i = 0; i < this.ListSlot.size(); ++i) {
                if (this.ListSlot.get(i) == null || !StackUtil.isStackEqual(this.itemstack.get(0), this.ListSlot.get(i))) continue;
                return true;
            }
            return false;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.minelayer = nbtTagCompound.getInteger("minelayer");
        this.xcounter = nbtTagCompound.getInteger("xcounter");
        this.zcounter = nbtTagCompound.getInteger("zcounter");
        this.blacklist = nbtTagCompound.getBoolean("blacklist");
        this.silktouch = nbtTagCompound.getBoolean("silktouch");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("minelayer", this.minelayer);
        nbtTagCompound.setInteger("xcounter", this.xcounter);
        nbtTagCompound.setInteger("zcounter", this.zcounter);
        nbtTagCompound.setBoolean("blacklist", this.blacklist);
        nbtTagCompound.setBoolean("silktouch", this.silktouch);
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                if (this.getActive()) break;
                this.minelayer = this.yCoord - 1;
                this.xcounter = 99;
                this.zcounter = 99;
                break;
            }
            case 1: {
                if (this.getActive()) break;
                this.blacklist = !this.blacklist;
                break;
            }
            case 2: {
                if (this.getActive()) break;
                this.silktouch = !this.silktouch;
            }
        }
    }

    public void setUpgradestat() {
        this.upgradeSlot.onChanged();
        this.setTier(TileEntityAdvMiner.applyModifier(this.defaultTier, this.upgradeSlot.extraTier, 1.0));
        this.blockscanncount = 5 * (this.upgradeSlot.augmentation + 1);
    }

    private static int applyModifier(int base, int extra, double multiplier) {
        double ret = Math.round(((double)base + (double)extra) * multiplier);
        return ret > 2.147483647E9 ? Integer.MAX_VALUE : (int)ret;
    }

    public ContainerBase<TileEntityAdvMiner> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerAdvMiner(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiAdvMiner(new ContainerAdvMiner(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
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
    public String getInventoryName() {
        return "AdvMiner";
    }

    public int getminelayer() {
        return this.minelayer;
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
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Augmentable, UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer);
    }
}

