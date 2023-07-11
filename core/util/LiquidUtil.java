/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.BlockFluidBase
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidBlock
 *  net.minecraftforge.fluids.IFluidContainerItem
 *  net.minecraftforge.fluids.IFluidHandler
 */
package ic2.core.util;

import ic2.api.Direction;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class LiquidUtil {
    public static LiquidData getLiquid(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        Fluid liquid = null;
        boolean isSource = false;
        if (block instanceof IFluidBlock) {
            IFluidBlock fblock = (IFluidBlock)block;
            liquid = fblock.getFluid();
            isSource = fblock.canDrain(world, x, y, z);
        } else if (block == Blocks.water || block == Blocks.flowing_water) {
            liquid = FluidRegistry.WATER;
            isSource = world.getBlockMetadata(x, y, z) == 0;
        } else if (block == Blocks.lava || block == Blocks.flowing_lava) {
            liquid = FluidRegistry.LAVA;
            boolean bl = isSource = world.getBlockMetadata(x, y, z) == 0;
        }
        if (liquid != null) {
            return new LiquidData(liquid, isSource);
        }
        return null;
    }

    public static int fillContainerStack(ItemStack stack, EntityPlayer player, FluidStack fluid, boolean simulate) {
        Item item = stack.getItem();
        if (!(item instanceof IFluidContainerItem)) {
            return 0;
        }
        IFluidContainerItem container = (IFluidContainerItem)item;
        if (stack.stackSize == 1) {
            return container.fill(stack, fluid, !simulate);
        }
        ItemStack testStack = StackUtil.copyWithSize(stack, 1);
        int amount = container.fill(testStack, fluid, true);
        if (amount <= 0) {
            return 0;
        }
        if (StackUtil.storeInventoryItem(testStack, player, simulate)) {
            if (!simulate) {
                --stack.stackSize;
            }
            return amount;
        }
        return 0;
    }

    public static FluidStack drainContainerStack(ItemStack stack, EntityPlayer player, int maxAmount, boolean simulate) {
        Item item = stack.getItem();
        if (!(item instanceof IFluidContainerItem)) {
            return null;
        }
        IFluidContainerItem container = (IFluidContainerItem)item;
        if (stack.stackSize == 1) {
            return container.drain(stack, maxAmount, !simulate);
        }
        ItemStack testStack = StackUtil.copyWithSize(stack, 1);
        FluidStack ret = container.drain(testStack, maxAmount, true);
        if (ret == null || ret.amount <= 0) {
            return null;
        }
        if (StackUtil.storeInventoryItem(testStack, player, simulate)) {
            if (!simulate) {
                --stack.stackSize;
            }
            return ret;
        }
        return null;
    }

    public static List<AdjacentFluidHandler> getAdjacentHandlers(TileEntity source) {
        ArrayList<AdjacentFluidHandler> ret = new ArrayList<AdjacentFluidHandler>();
        for (Direction dir : Direction.directions) {
            TileEntity te = dir.applyToTileEntity(source);
            if (!(te instanceof IFluidHandler)) continue;
            ret.add(new AdjacentFluidHandler((IFluidHandler)te, dir));
        }
        return ret;
    }

    public static int distribute(TileEntity source, FluidStack stack, boolean simulate) {
        int transferred = 0;
        for (AdjacentFluidHandler handler : LiquidUtil.getAdjacentHandlers(source)) {
            int amount = LiquidUtil.distributeTo(handler.handler, stack, handler.dir, simulate);
            transferred += amount;
            stack.amount -= amount;
            if (stack.amount > 0) continue;
            break;
        }
        stack.amount += transferred;
        return transferred;
    }

    public static int distributeTo(IFluidHandler target, FluidStack stack, Direction dirTo, boolean simulate) {
        int amount = target.fill(dirTo.getInverse().toForgeDirection(), stack, !simulate);
        return amount;
    }

    public static int distributeAll(IFluidHandler source, int amount) {
        if (!(source instanceof TileEntity)) {
            throw new IllegalArgumentException("source has to be a tile entity");
        }
        TileEntity srcTe = (TileEntity)source;
        int transferred = 0;
        for (Direction dir : Direction.directions) {
            FluidStack stack;
            TileEntity te = dir.applyToTileEntity(srcTe);
            if (!(te instanceof IFluidHandler) || (stack = LiquidUtil.transfer(source, dir, (IFluidHandler)te, amount)) == null) continue;
            transferred += stack.amount;
            if ((amount -= stack.amount) <= 0) break;
        }
        return transferred;
    }

    public static FluidStack transfer(IFluidHandler source, Direction dir, IFluidHandler target, int amount) {
        FluidStack ret;
        int cAmount;
        do {
            if ((ret = source.drain(dir.toForgeDirection(), amount, false)) == null || ret.amount <= 0) {
                return null;
            }
            if (ret.amount > amount) {
                throw new IllegalStateException("The fluid handler " + source + " drained more than the requested amount.");
            }
            cAmount = target.fill(dir.getInverse().toForgeDirection(), ret, false);
            if (cAmount <= amount) continue;
            throw new IllegalStateException("The fluid handler " + target + " filled more than the requested amount.");
        } while ((amount = cAmount) != ret.amount && amount > 0);
        if (amount <= 0) {
            return null;
        }
        ret = source.drain(dir.toForgeDirection(), amount, true);
        if (ret.amount != amount) {
            throw new IllegalStateException("The fluid handler " + source + " drained inconsistently. Expected " + amount + ", got " + ret.amount + ".");
        }
        amount = target.fill(dir.getInverse().toForgeDirection(), ret, true);
        if (amount != ret.amount) {
            throw new IllegalStateException("The fluid handler " + target + " filled inconsistently. Expected " + ret.amount + ", got " + amount + ".");
        }
        return ret;
    }

    public static boolean check(FluidStack fs) {
        return fs.getFluid() != null;
    }

    public static boolean placeFluid(FluidStack fs, World world, int x, int y, int z) {
        if (fs == null || fs.amount < 1000) {
            return false;
        }
        Fluid fluid = fs.getFluid();
        Object block = world.getBlock(x, y, z);
        if (!(!block.isAir((IBlockAccess)world, x, y, z) && block.getMaterial().isSolid() || !fluid.canBePlacedInWorld() || block == fluid.getBlock() && LiquidUtil.isFullFluidBlock(world, x, y, z, block))) {
            if (world.provider.isHellWorld && fluid == FluidRegistry.WATER) {
                world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.fizz", 0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f);
                for (int i = 0; i < 8; ++i) {
                    world.spawnParticle("largesmoke", (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0, 0.0, 0.0);
                }
            } else {
                int meta;
                if (!(world.isRemote || block.getMaterial().isSolid() || block.getMaterial().isLiquid())) {
                    world.func_147480_a(x, y, z, true);
                }
                block = fluid == FluidRegistry.WATER ? Blocks.flowing_water : (fluid == FluidRegistry.LAVA ? Blocks.flowing_lava : fluid.getBlock());
                int n = meta = block instanceof BlockFluidBase ? ((BlockFluidBase)block).getMaxRenderHeightMeta() : 0;
                if (!world.setBlock(x, y, z, block, meta, 3)) {
                    return false;
                }
            }
            fs.amount -= 1000;
            return true;
        }
        return false;
    }

    private static boolean isFullFluidBlock(World world, int x, int y, int z, Block block) {
        if (block instanceof IFluidBlock) {
            IFluidBlock fBlock = (IFluidBlock)block;
            FluidStack drained = fBlock.drain(world, x, y, z, false);
            return drained != null && drained.amount >= 1000;
        }
        if (block == Blocks.water || block == Blocks.flowing_water || block == Blocks.lava || block == Blocks.flowing_lava) {
            return world.getBlockMetadata(x, y, z) == 0;
        }
        return false;
    }

    public static class AdjacentFluidHandler {
        public final IFluidHandler handler;
        public final Direction dir;

        private AdjacentFluidHandler(IFluidHandler handler, Direction dir) {
            this.handler = handler;
            this.dir = dir;
        }
    }

    public static class LiquidData {
        public final Fluid liquid;
        public final boolean isSource;

        LiquidData(Fluid liquid1, boolean isSource1) {
            this.liquid = liquid1;
            this.isSource = isSource1;
        }
    }
}

