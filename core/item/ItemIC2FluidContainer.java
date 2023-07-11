/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidContainerItem
 */
package ic2.core.item;

import ic2.api.item.IItemHudInfo;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public abstract class ItemIC2FluidContainer
extends ItemIC2
implements IFluidContainerItem,
IItemHudInfo {
    protected final int capacity;

    public ItemIC2FluidContainer(InternalName internalName, int capacity1) {
        super(internalName);
        this.capacity = capacity1;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        super.addInformation(itemStack, player, info, b);
        FluidStack fs = this.getFluid(itemStack);
        if (fs != null) {
            info.add("< " + FluidRegistry.getFluidName((FluidStack)fs) + ", " + fs.amount + " mB >");
        } else {
            info.add(StatCollector.translateToLocal((String)"ic2.item.FluidContainer.Empty"));
        }
    }

    @Override
    public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        FluidStack fs = this.getFluid(itemStack);
        if (fs != null) {
            info.add("< " + FluidRegistry.getFluidName((FluidStack)fs) + ", " + fs.amount + " mB >");
        } else {
            info.add(StatCollector.translateToLocal((String)"ic2.item.FluidContainer.Empty"));
        }
        return info;
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

    public abstract boolean canfill(Fluid var1);

    public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        if (stack.stackSize != 1) {
            return 0;
        }
        if (resource == null) {
            return 0;
        }
        if (!this.canfill(resource.getFluid())) {
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
        return amount;
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
        return new FluidStack(fs, maxDrain);
    }
}

