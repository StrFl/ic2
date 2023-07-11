/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidContainerRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidContainerItem
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.invslot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.util.StackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class InvSlotConsumableLiquid
extends InvSlotConsumable {
    private OpType opType;

    public InvSlotConsumableLiquid(TileEntityInventory base1, String name1, int oldStartIndex1, int count) {
        this(base1, name1, oldStartIndex1, InvSlot.Access.I, count, InvSlot.InvSide.TOP, OpType.Drain);
    }

    public InvSlotConsumableLiquid(TileEntityInventory base1, String name1, int oldStartIndex1, InvSlot.Access access1, int count, InvSlot.InvSide preferredSide1, OpType opType1) {
        super(base1, name1, oldStartIndex1, access1, count, preferredSide1);
        this.opType = opType1;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        block10: {
            IFluidContainerItem containerItem;
            FluidStack prevFluid;
            Item item;
            block11: {
                item = stack.getItem();
                if (item == null) {
                    return false;
                }
                if (this.opType == OpType.Drain || this.opType == OpType.Both) {
                    Object containerFluid = null;
                    if (FluidContainerRegistry.isFilledContainer((ItemStack)stack)) {
                        containerFluid = FluidContainerRegistry.getFluidForFilledItem((ItemStack)stack);
                    } else if (item instanceof IFluidContainerItem) {
                        containerFluid = ((IFluidContainerItem)item).getFluid(stack);
                    }
                    if (containerFluid != null && ((FluidStack)containerFluid).amount > 0 && this.acceptsLiquid(containerFluid.getFluid())) {
                        return true;
                    }
                }
                if (this.opType != OpType.Fill && this.opType != OpType.Both) break block10;
                if (!FluidContainerRegistry.isEmptyContainer((ItemStack)stack)) break block11;
                if (this.getPossibleFluids() == null) {
                    return true;
                }
                for (Fluid fluid : this.getPossibleFluids()) {
                    if (FluidContainerRegistry.fillFluidContainer((FluidStack)new FluidStack(fluid, Integer.MAX_VALUE), (ItemStack)stack) == null) continue;
                    return true;
                }
                break block10;
            }
            if (!(item instanceof IFluidContainerItem) || (prevFluid = (containerItem = (IFluidContainerItem)item).getFluid(stack)) != null && containerItem.getCapacity(stack) <= prevFluid.amount) break block10;
            if (this.getPossibleFluids() == null) {
                return true;
            }
            ItemStack singleStack = StackUtil.copyWithSize(stack, 1);
            for (Fluid fluid : this.getPossibleFluids()) {
                if (containerItem.fill(singleStack, new FluidStack(fluid, Integer.MAX_VALUE), false) <= 0) continue;
                return true;
            }
        }
        return false;
    }

    public FluidStack drain(Fluid fluid, int maxAmount, MutableObject<ItemStack> output, boolean simulate) {
        output.setValue(null);
        if (this.opType != OpType.Drain && this.opType != OpType.Both) {
            return null;
        }
        ItemStack stack = this.get();
        if (stack == null) {
            return null;
        }
        if (FluidContainerRegistry.isFilledContainer((ItemStack)stack)) {
            FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem((ItemStack)stack);
            if (fluidStack == null || fluid != null && fluid != fluidStack.getFluid()) {
                return null;
            }
            if (!this.acceptsLiquid(fluidStack.getFluid())) {
                return null;
            }
            if (fluidStack.amount <= 0 || fluidStack.amount > maxAmount) {
                return null;
            }
            if (stack.getItem().hasContainerItem(stack)) {
                output.setValue((Object)stack.getItem().getContainerItem(stack));
            }
            if (!simulate) {
                --stack.stackSize;
                if (stack.stackSize <= 0) {
                    this.put(null);
                }
            }
            return fluidStack;
        }
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem)stack.getItem();
            if (container.getFluid(stack) == null) {
                return null;
            }
            if (fluid != null && container.getFluid(stack).getFluid() != fluid) {
                return null;
            }
            if (!this.acceptsLiquid(container.getFluid(stack).getFluid())) {
                return null;
            }
            ItemStack singleStack = StackUtil.copyWithSize(stack, 1);
            FluidStack fluidStack = container.drain(singleStack, maxAmount, true);
            if (fluidStack == null || fluidStack.amount <= 0) {
                return null;
            }
            if (singleStack.stackSize <= 0) {
                if (!simulate) {
                    --stack.stackSize;
                }
            } else if (container.getFluid(singleStack) == null) {
                output.setValue((Object)singleStack);
                if (!simulate) {
                    --stack.stackSize;
                }
            } else {
                if (stack.stackSize > 1) {
                    return null;
                }
                if (!simulate) {
                    this.put(singleStack);
                }
            }
            if (stack.stackSize <= 0) {
                this.put(null);
            }
            return fluidStack;
        }
        return null;
    }

    public int fill(Fluid fluid, int maxAmount, MutableObject<ItemStack> output, boolean simulate) {
        if (fluid == null) {
            throw new NullPointerException("fluid is null");
        }
        output.setValue(null);
        if (this.opType != OpType.Fill && this.opType != OpType.Both) {
            return 0;
        }
        ItemStack stack = this.get();
        if (stack == null) {
            return 0;
        }
        if (FluidContainerRegistry.isEmptyContainer((ItemStack)stack)) {
            ItemStack filled = FluidContainerRegistry.fillFluidContainer((FluidStack)new FluidStack(fluid, maxAmount), (ItemStack)stack);
            if (filled == null) {
                return 0;
            }
            output.setValue((Object)filled);
            if (!simulate) {
                --stack.stackSize;
                if (stack.stackSize <= 0) {
                    this.put(null);
                }
            }
            return FluidContainerRegistry.getFluidForFilledItem((ItemStack)filled).amount;
        }
        if (stack.getItem() instanceof IFluidContainerItem) {
            ItemStack singleStack;
            IFluidContainerItem container = (IFluidContainerItem)stack.getItem();
            int amount = container.fill(singleStack = StackUtil.copyWithSize(stack, 1), new FluidStack(fluid, maxAmount), true);
            if (amount <= 0) {
                return 0;
            }
            assert (singleStack.stackSize == 1);
            if (container.getFluid((ItemStack)singleStack).amount == container.getCapacity(singleStack)) {
                output.setValue((Object)singleStack);
                if (!simulate) {
                    --stack.stackSize;
                }
            } else {
                if (stack.stackSize > 1) {
                    return 0;
                }
                if (!simulate) {
                    this.put(singleStack);
                }
            }
            if (stack.stackSize <= 0) {
                this.put(null);
            }
            return amount;
        }
        return 0;
    }

    public boolean transferToTank(IFluidTank tank, MutableObject<ItemStack> output, boolean simulate) {
        FluidStack fluid;
        int space = tank.getCapacity();
        Fluid fluidRequired = null;
        FluidStack tankFluid = tank.getFluid();
        if (tankFluid != null) {
            space -= tankFluid.amount;
            fluidRequired = tankFluid.getFluid();
        }
        if ((fluid = this.drain(fluidRequired, space, output, true)) == null) {
            return false;
        }
        int amount = tank.fill(fluid, !simulate);
        if (amount <= 0) {
            return false;
        }
        if (!simulate) {
            this.drain(fluidRequired, amount, output, false);
        }
        return true;
    }

    public boolean transferFromTank(IFluidTank tank, MutableObject<ItemStack> output, boolean simulate) {
        FluidStack tankFluid = tank.drain(tank.getFluidAmount(), false);
        if (tankFluid == null || tankFluid.amount <= 0) {
            return false;
        }
        int amount = this.fill(tankFluid.getFluid(), tankFluid.amount, output, simulate);
        if (amount <= 0) {
            return false;
        }
        if (!simulate) {
            tank.drain(amount, true);
        }
        return true;
    }

    public void setOpType(OpType opType1) {
        this.opType = opType1;
    }

    protected boolean acceptsLiquid(Fluid fluid) {
        return true;
    }

    protected Iterable<Fluid> getPossibleFluids() {
        return null;
    }

    public static enum OpType {
        Drain,
        Fill,
        Both,
        None;

    }
}

