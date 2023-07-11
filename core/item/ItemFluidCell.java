/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidBlock
 *  net.minecraftforge.fluids.IFluidHandler
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2FluidContainer;
import ic2.core.util.LiquidUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;

public class ItemFluidCell
extends ItemIC2FluidContainer {
    public ItemFluidCell(InternalName internalName) {
        super(internalName, 1000);
    }

    @Override
    public String getTextureFolder() {
        return "cell";
    }

    @Override
    public String getTextureName(int index) {
        switch (index) {
            case 0: {
                return this.getUnlocalizedName().substring(4);
            }
            case 1: {
                return this.getUnlocalizedName().substring(4) + ".window";
            }
        }
        return null;
    }

    @SideOnly(value=Side.CLIENT)
    IIcon getWindowIcon() {
        return this.textures[1];
    }

    public boolean isRepairable() {
        return false;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        if (this.interactWithTank(stack, player, world, x, y, z, side)) {
            return true;
        }
        MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (position == null) {
            return false;
        }
        if (position.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            x = position.blockX;
            y = position.blockY;
            z = position.blockZ;
            if (!world.canMineBlock(player, x, y, z)) {
                return false;
            }
            if (!player.canPlayerEdit(x, y, z, position.sideHit, stack)) {
                return false;
            }
            if (this.collectFluidBlock(stack, player, world, x, y, z)) {
                return true;
            }
            FluidStack fs = LiquidUtil.drainContainerStack(stack, player, 1000, true);
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[position.sideHit];
            if (LiquidUtil.placeFluid(fs, world, x, y, z) || player.canPlayerEdit(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, position.sideHit, stack) && LiquidUtil.placeFluid(fs, world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
                if (!player.capabilities.isCreativeMode) {
                    LiquidUtil.drainContainerStack(stack, player, 1000, false);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canfill(Fluid fluid) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
        itemList.add(Ic2Items.FluidCell.copy());
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
            if (fluid == null) continue;
            ItemStack stack = Ic2Items.FluidCell.copy();
            this.fill(stack, new FluidStack(fluid, Integer.MAX_VALUE), true);
            itemList.add(stack);
        }
    }

    private boolean interactWithTank(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IFluidHandler)) {
            return false;
        }
        IFluidHandler handler = (IFluidHandler)te;
        ForgeDirection dir = ForgeDirection.getOrientation((int)side);
        FluidStack fs = this.getFluid(stack);
        if (fs == null || player.isSneaking() && fs.amount < this.capacity) {
            int amount = fs == null ? this.capacity : this.capacity - fs.amount;
            FluidStack input = handler.drain(dir, amount, false);
            if (input == null || input.amount <= 0) {
                return true;
            }
            amount = LiquidUtil.fillContainerStack(stack, player, input, false);
            if (amount <= 0) {
                return true;
            }
            handler.drain(dir, amount, true);
            return true;
        }
        int amount = handler.fill(dir, fs, false);
        if (amount <= 0) {
            return true;
        }
        fs = LiquidUtil.drainContainerStack(stack, player, amount, false);
        if (fs == null || fs.amount <= 0) {
            return true;
        }
        handler.fill(dir, fs, true);
        return true;
    }

    private boolean collectFluidBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof IFluidBlock) {
            FluidStack fluid;
            int amount;
            IFluidBlock liquid = (IFluidBlock)block;
            if (liquid.canDrain(world, x, y, z) && (amount = LiquidUtil.fillContainerStack(stack, player, fluid = liquid.drain(world, x, y, z, false), true)) == fluid.amount) {
                LiquidUtil.fillContainerStack(stack, player, fluid, false);
                liquid.drain(world, x, y, z, true);
                return true;
            }
        } else if (world.getBlockMetadata(x, y, z) == 0) {
            int amount;
            FluidStack fluid = null;
            if (block == Blocks.water || block == Blocks.flowing_water) {
                fluid = new FluidStack(FluidRegistry.WATER, 1000);
            } else if (block == Blocks.lava || block == Blocks.flowing_lava) {
                fluid = new FluidStack(FluidRegistry.LAVA, 1000);
            }
            if (fluid != null && (amount = LiquidUtil.fillContainerStack(stack, player, fluid, true)) == fluid.amount) {
                LiquidUtil.fillContainerStack(stack, player, fluid, false);
                world.setBlockToAir(x, y, z);
                return true;
            }
        }
        return false;
    }

    public static ItemStack getUniversalFluidCell(FluidStack fluidStack) {
        ItemStack stack = Ic2Items.FluidCell.copy();
        ((ItemFluidCell)Ic2Items.FluidCell.getItem()).fill(stack, new FluidStack(fluidStack.getFluid(), fluidStack.amount), true);
        return stack;
    }
}

