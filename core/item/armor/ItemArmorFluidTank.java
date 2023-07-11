/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidContainerItem
 */
package ic2.core.item.armor;

import ic2.api.item.IItemHudInfo;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public abstract class ItemArmorFluidTank
extends ItemArmorUtility
implements IFluidContainerItem,
IItemHudInfo {
    protected final int capacity;
    protected final Fluid allowfluid;

    public ItemArmorFluidTank(InternalName internalName, InternalName armorName, Fluid allowfluid, int capacity) {
        super(internalName, armorName, 1);
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.capacity = capacity;
        this.allowfluid = allowfluid;
    }

    public void filltank(ItemStack stack) {
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
        FluidStack fs = new FluidStack(this.allowfluid, this.getCapacity(stack));
        fs.writeToNBT(fluidTag);
        nbtTagCompound.setTag("Fluid", (NBTBase)fluidTag);
    }

    public double getCharge(ItemStack itemStack) {
        if (this.getFluid(itemStack) == null) {
            return 0.0;
        }
        double ret = this.getFluid((ItemStack)itemStack).amount;
        return ret > 0.0 ? ret : 0.0;
    }

    public double getMaxCharge(ItemStack itemStack) {
        return this.getCapacity(itemStack);
    }

    protected void Updatedamage(ItemStack itemStack) {
        itemStack.setItemDamage(itemStack.getMaxDamage() - 1 - (int)Util.map(this.getCharge(itemStack), this.getMaxCharge(itemStack), itemStack.getMaxDamage() - 2));
    }

    public FluidStack getFluid(ItemStack stack) {
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
        return FluidStack.loadFluidStackFromNBT((NBTTagCompound)fluidTag);
    }

    public boolean isEmpty(ItemStack stack) {
        return this.getFluid(stack) == null;
    }

    public int getCapacity(ItemStack container) {
        return this.capacity;
    }

    public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        if (stack.stackSize != 1) {
            return null;
        }
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
        FluidStack fs = FluidStack.loadFluidStackFromNBT((NBTTagCompound)fluidTag);
        if (fs == null) {
            return null;
        }
        maxDrain = Math.min(fs.amount, maxDrain);
        if (doDrain) {
            fs.amount -= maxDrain;
            if (fs.amount <= 0) {
                nbtTagCompound.removeTag("Fluid");
            } else {
                fs.writeToNBT(fluidTag);
                nbtTagCompound.setTag("Fluid", (NBTBase)fluidTag);
            }
        }
        this.Updatedamage(stack);
        return new FluidStack(fs, maxDrain);
    }

    public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        if (stack.stackSize != 1) {
            return 0;
        }
        if (resource == null) {
            return 0;
        }
        if (resource.getFluid() != this.allowfluid) {
            return 0;
        }
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
        FluidStack fs = FluidStack.loadFluidStackFromNBT((NBTTagCompound)fluidTag);
        if (fs == null) {
            fs = new FluidStack(resource, 0);
        }
        if (!fs.isFluidEqual(resource)) {
            return 0;
        }
        int amount = Math.min(this.capacity - fs.amount, resource.amount);
        if (doFill && amount > 0) {
            fs.amount += amount;
            fs.writeToNBT(fluidTag);
            nbtTagCompound.setTag("Fluid", (NBTBase)fluidTag);
        }
        this.Updatedamage(stack);
        return amount;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        super.addInformation(itemStack, player, info, b);
        FluidStack fs = this.getFluid(itemStack);
        if (fs != null) {
            info.add("< " + FluidRegistry.getFluidName((FluidStack)fs) + ", " + fs.amount + " mB >");
        } else {
            info.add("< Empty >");
        }
    }

    @Override
    public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        FluidStack fs = this.getFluid(itemStack);
        if (fs != null) {
            info.add("< " + FluidRegistry.getFluidName((FluidStack)fs) + ", " + fs.amount + " mB >");
        } else {
            info.add("< Empty >");
        }
        return info;
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
}

