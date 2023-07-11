/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.IFluidHandler
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.item.IItemHudInfo;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.upgrade.UpgradeRegistry;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

public class ItemUpgradeModule
extends ItemIC2
implements IUpgradeItem,
IItemHudInfo {
    private static final DecimalFormat decimalformat = new DecimalFormat("0.##");
    private static final List<StackUtil.AdjacentInv> emptyInvList = Arrays.asList(new StackUtil.AdjacentInv[0]);
    private final int fluidAmountPerTick;

    public ItemUpgradeModule(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.overclockerUpgrade = UpgradeRegistry.register(new ItemStack((Item)this, 1, Type.Overclocker.ordinal()));
        Ic2Items.transformerUpgrade = UpgradeRegistry.register(new ItemStack((Item)this, 1, Type.Transformer.ordinal()));
        Ic2Items.energyStorageUpgrade = UpgradeRegistry.register(new ItemStack((Item)this, 1, Type.EnergyStorage.ordinal()));
        Ic2Items.ejectorUpgrade = UpgradeRegistry.register(new ItemStack((Item)this, 1, Type.Ejector.ordinal()));
        Ic2Items.fluidEjectorUpgrade = UpgradeRegistry.register(new ItemStack((Item)this, 1, Type.FluidEjector.ordinal()));
        Ic2Items.redstoneinvUpgrade = UpgradeRegistry.register(new ItemStack((Item)this, 1, Type.RedstoneInverter.ordinal()));
        Ic2Items.pullingUpgrade = UpgradeRegistry.register(new ItemStack((Item)this, 1, Type.Pulling.ordinal()));
        this.fluidAmountPerTick = 50;
    }

    @Override
    public String getTextureFolder() {
        return "upgrade";
    }

    @Override
    public String getTextureName(int index) {
        if (index < Type.Values.length) {
            return super.getTextureName(index);
        }
        if (index < Type.Values.length + 6) {
            return InternalName.ejectorUpgrade.name() + "." + (index - Type.Values.length);
        }
        if (index < Type.Values.length + 12) {
            return InternalName.fluidEjectorUpgrade.name() + "." + (index - Type.Values.length - 6);
        }
        if (index < Type.Values.length + 18) {
            return InternalName.pullingUpgrade.name() + "." + (index - Type.Values.length - 12);
        }
        return null;
    }

    public IIcon getIcon(ItemStack stack, int pass) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return null;
        }
        switch (type) {
            case Ejector: 
            case FluidEjector: 
            case Pulling: {
                int dir = ItemUpgradeModule.getDirection(stack);
                if (dir < 1 || dir > 6) break;
                int textureIndex = Type.Values.length + dir - 1;
                switch (type) {
                    case Ejector: {
                        textureIndex += 0;
                        break;
                    }
                    case FluidEjector: {
                        textureIndex += 6;
                        break;
                    }
                    case Pulling: {
                        textureIndex += 12;
                        break;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
                return super.getIconFromDamage(textureIndex);
            }
        }
        return super.getIcon(stack, pass);
    }

    @SideOnly(value=Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add("Machine Upgrade");
        return info;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return null;
        }
        InternalName ret = null;
        switch (type) {
            case Overclocker: {
                ret = InternalName.overclockerUpgrade;
                break;
            }
            case Transformer: {
                ret = InternalName.transformerUpgrade;
                break;
            }
            case EnergyStorage: {
                ret = InternalName.energyStorageUpgrade;
                break;
            }
            case Ejector: {
                ret = InternalName.ejectorUpgrade;
                break;
            }
            case FluidEjector: {
                ret = InternalName.fluidEjectorUpgrade;
                break;
            }
            case RedstoneInverter: {
                ret = InternalName.redstoneinvUpgrade;
                break;
            }
            case Pulling: {
                ret = InternalName.pullingUpgrade;
            }
        }
        return "ic2." + ret.name();
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return;
        }
        super.addInformation(stack, player, list, par4);
        switch (type) {
            case Overclocker: {
                list.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.upgrade.overclocker.time", (Object[])new Object[]{decimalformat.format(100.0 * Math.pow(this.getProcessTimeMultiplier(stack, null), stack.stackSize))}));
                list.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.upgrade.overclocker.power", (Object[])new Object[]{decimalformat.format(100.0 * Math.pow(this.getEnergyDemandMultiplier(stack, null), stack.stackSize))}));
                break;
            }
            case Transformer: {
                list.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.upgrade.transformer", (Object[])new Object[]{this.getExtraTier(stack, null) * stack.stackSize}));
                break;
            }
            case EnergyStorage: {
                list.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.upgrade.storage", (Object[])new Object[]{this.getExtraEnergyStorage(stack, null) * stack.stackSize}));
                break;
            }
            case Ejector: 
            case FluidEjector: {
                String side = ItemUpgradeModule.getSideName(stack);
                list.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.upgrade.ejector", (Object[])new Object[]{StatCollector.translateToLocal((String)side)}));
                break;
            }
            case RedstoneInverter: {
                list.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.upgrade.redstone", (Object[])new Object[0]));
                break;
            }
            case Pulling: {
                String side = ItemUpgradeModule.getSideName(stack);
                list.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.upgrade.pulling", (Object[])new Object[]{StatCollector.translateToLocal((String)side)}));
            }
        }
    }

    private static String getSideName(ItemStack stack) {
        switch (ItemUpgradeModule.getDirection(stack) - 1) {
            case 0: {
                return "ic2.dir.west";
            }
            case 1: {
                return "ic2.dir.east";
            }
            case 2: {
                return "ic2.dir.bottom";
            }
            case 3: {
                return "ic2.dir.top";
            }
            case 4: {
                return "ic2.dir.north";
            }
            case 5: {
                return "ic2.dir.south";
            }
        }
        return "ic2.tooltip.upgrade.ejector.anyside";
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return false;
        }
        switch (type) {
            case Ejector: 
            case FluidEjector: 
            case Pulling: {
                int dir = 1 + Direction.fromSideValue(side).ordinal();
                NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
                if (nbtData.getByte("dir") == dir) {
                    nbtData.setByte("dir", (byte)0);
                } else {
                    nbtData.setByte("dir", (byte)dir);
                }
                return true;
            }
        }
        return false;
    }

    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        for (int meta = 0; meta <= Short.MAX_VALUE; ++meta) {
            ItemStack stack = new ItemStack((Item)this, 1, meta);
            if (this.getUnlocalizedName(stack) == null) continue;
            itemList.add(stack);
        }
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, Set<UpgradableProperty> types) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return false;
        }
        switch (type) {
            case Ejector: {
                return types.contains((Object)UpgradableProperty.ItemProducing);
            }
            case EnergyStorage: {
                return types.contains((Object)UpgradableProperty.EnergyStorage);
            }
            case FluidEjector: {
                return types.contains((Object)UpgradableProperty.FluidProducing);
            }
            case Overclocker: {
                return types.contains((Object)UpgradableProperty.Processing) || types.contains((Object)UpgradableProperty.Augmentable);
            }
            case Pulling: {
                return types.contains((Object)UpgradableProperty.ItemConsuming);
            }
            case RedstoneInverter: {
                return types.contains((Object)UpgradableProperty.RedstoneSensitive);
            }
            case Transformer: {
                return types.contains((Object)UpgradableProperty.Transformer);
            }
        }
        return false;
    }

    @Override
    public int getAugmentation(ItemStack stack, IUpgradableBlock parent) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return 0;
        }
        switch (type) {
            case Overclocker: {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int getExtraProcessTime(ItemStack stack, IUpgradableBlock parent) {
        return 0;
    }

    @Override
    public double getProcessTimeMultiplier(ItemStack stack, IUpgradableBlock parent) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return 1.0;
        }
        switch (type) {
            case Overclocker: {
                return 0.7;
            }
        }
        return 1.0;
    }

    @Override
    public int getExtraEnergyDemand(ItemStack stack, IUpgradableBlock parent) {
        return 0;
    }

    @Override
    public double getEnergyDemandMultiplier(ItemStack stack, IUpgradableBlock parent) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return 1.0;
        }
        switch (type) {
            case Overclocker: {
                return 1.6;
            }
        }
        return 1.0;
    }

    @Override
    public int getExtraEnergyStorage(ItemStack stack, IUpgradableBlock parent) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return 0;
        }
        switch (type) {
            case EnergyStorage: {
                return 10000;
            }
        }
        return 0;
    }

    @Override
    public double getEnergyStorageMultiplier(ItemStack stack, IUpgradableBlock parent) {
        return 1.0;
    }

    @Override
    public int getExtraTier(ItemStack stack, IUpgradableBlock parent) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return 0;
        }
        switch (type) {
            case Transformer: {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean modifiesRedstoneInput(ItemStack stack, IUpgradableBlock parent) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return false;
        }
        switch (type) {
            case RedstoneInverter: {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getRedstoneInput(ItemStack stack, IUpgradableBlock parent, int externalInput) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return externalInput;
        }
        switch (type) {
            case RedstoneInverter: {
                return 15 - externalInput;
            }
        }
        return externalInput;
    }

    @Override
    public boolean onTick(ItemStack stack, IUpgradableBlock parent) {
        Type type = this.getType(stack.getItemDamage());
        if (type == null) {
            return false;
        }
        boolean ret = false;
        block0 : switch (type) {
            case Ejector: {
                int amount = Util.saturatedCast(parent.getEnergy()) / 20;
                if (amount <= 0) break;
                for (StackUtil.AdjacentInv inv : ItemUpgradeModule.getTargetInventories(stack, (TileEntity)parent)) {
                    int cAmount = StackUtil.transfer((IInventory)parent, inv.inv, inv.dir, amount);
                    if (cAmount <= 0) continue;
                    ret = true;
                    parent.useEnergy(20 * cAmount);
                    if ((amount -= cAmount) > 0) continue;
                    break block0;
                }
                break;
            }
            case FluidEjector: {
                if (!(parent instanceof IFluidHandler)) {
                    return false;
                }
                IFluidHandler fParent = (IFluidHandler)parent;
                int rawDir = ItemUpgradeModule.getDirection(stack);
                if (rawDir < 1 || rawDir > 6) {
                    ret = LiquidUtil.distributeAll(fParent, this.fluidAmountPerTick) > 0;
                    break;
                }
                Direction dir = Direction.directions[rawDir - 1];
                TileEntity te = dir.applyToTileEntity((TileEntity)parent);
                if (!(te instanceof IFluidHandler)) {
                    return false;
                }
                IFluidHandler target = (IFluidHandler)te;
                ret = LiquidUtil.transfer(fParent, dir, target, this.fluidAmountPerTick) != null;
                break;
            }
            case Pulling: {
                int amount = Util.saturatedCast(parent.getEnergy()) / 20;
                if (amount <= 0) break;
                for (StackUtil.AdjacentInv inv : ItemUpgradeModule.getTargetInventories(stack, (TileEntity)parent)) {
                    int cAmount = StackUtil.transfer(inv.inv, (IInventory)parent, inv.dir.getInverse(), amount);
                    if (cAmount <= 0) continue;
                    ret = true;
                    parent.useEnergy(20 * cAmount);
                    if ((amount -= cAmount) > 0) continue;
                    break block0;
                }
                break;
            }
            default: {
                return false;
            }
        }
        return ret;
    }

    private static List<StackUtil.AdjacentInv> getTargetInventories(ItemStack stack, TileEntity parent) {
        int rawDir = ItemUpgradeModule.getDirection(stack);
        if (rawDir < 1 || rawDir > 6) {
            return StackUtil.getAdjacentInventories(parent);
        }
        Direction dir = Direction.directions[rawDir - 1];
        StackUtil.AdjacentInv inv = StackUtil.getAdjacentInventory(parent, dir);
        if (inv == null) {
            return emptyInvList;
        }
        return Arrays.asList(inv);
    }

    @Override
    public void onProcessEnd(ItemStack stack, IUpgradableBlock parent, List<ItemStack> output) {
    }

    private Type getType(int meta) {
        if (meta < 0 || meta >= Type.Values.length) {
            return null;
        }
        return Type.Values[meta];
    }

    private static int getDirection(ItemStack stack) {
        return StackUtil.getOrCreateNbtData(stack).getByte("dir");
    }

    private static enum Type {
        Overclocker,
        Transformer,
        EnergyStorage,
        Ejector,
        FluidEjector,
        RedstoneInverter,
        Pulling;

        public static final Type[] Values;

        static {
            Values = Type.values();
        }
    }
}

