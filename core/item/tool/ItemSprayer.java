/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.world.ChunkPosition
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidBlock
 */
package ic2.core.item.tool;

import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2FluidContainer;
import ic2.core.item.armor.ItemArmorCFPack;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class ItemSprayer
extends ItemIC2FluidContainer
implements IBoxable {
    public ItemSprayer(InternalName internalName) {
        super(internalName, 8000);
        this.setMaxStackSize(1);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.platform.isSimulating() && IC2.keyboard.isModeSwitchKeyDown(player)) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
            int mode = nbtData.getInteger("mode");
            mode = mode == 0 ? 1 : 0;
            nbtData.setInteger("mode", mode);
            String sMode = mode == 0 ? "ic2.tooltip.mode.normal" : "ic2.tooltip.mode.single";
            IC2.platform.messagePlayer(player, "ic2.tooltip.mode", sMode);
        }
        return super.onItemRightClick(stack, world, player);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        Target target;
        ItemStack pack;
        FluidStack fluid;
        int amount;
        IFluidBlock liquid;
        int fz;
        int fy;
        int fx;
        Block block;
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            return false;
        }
        if (!IC2.platform.isSimulating()) {
            return true;
        }
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (movingobjectposition == null) {
            return false;
        }
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && (block = world.getBlock(fx = movingobjectposition.blockX, fy = movingobjectposition.blockY, fz = movingobjectposition.blockZ)) instanceof IFluidBlock && (liquid = (IFluidBlock)block).canDrain(world, fx, fy, fz) && (amount = LiquidUtil.fillContainerStack(stack, player, fluid = liquid.drain(world, fx, fy, fz, false), true)) == fluid.amount) {
            LiquidUtil.fillContainerStack(stack, player, fluid, false);
            liquid.drain(world, fx, fy, fz, true);
            return true;
        }
        int maxFoamBlocks = 0;
        FluidStack fluid2 = this.getFluid(stack);
        if (fluid2 != null && fluid2.amount > 0) {
            maxFoamBlocks += fluid2.amount / this.getFluidPerFoam();
        }
        if ((pack = player.inventory.armorInventory[2]) != null && pack.getItem() == Ic2Items.cfPack.getItem()) {
            fluid2 = ((ItemArmorCFPack)pack.getItem()).getFluid(pack);
            if (fluid2 != null && fluid2.amount > 0) {
                maxFoamBlocks += fluid2.amount / this.getFluidPerFoam();
            } else {
                pack = null;
            }
        } else {
            pack = null;
        }
        if (maxFoamBlocks == 0) {
            return false;
        }
        maxFoamBlocks = Math.min(maxFoamBlocks, this.getMaxFoamBlocks(stack));
        ChunkPosition pos = new ChunkPosition(x, y, z);
        if (ItemSprayer.canPlaceFoam(world, pos, Target.Scaffold)) {
            target = Target.Scaffold;
        } else if (ItemSprayer.canPlaceFoam(world, pos, Target.Cable)) {
            target = Target.Cable;
        } else {
            switch (side) {
                case 0: {
                    --y;
                    break;
                }
                case 1: {
                    ++y;
                    break;
                }
                case 2: {
                    --z;
                    break;
                }
                case 3: {
                    ++z;
                    break;
                }
                case 4: {
                    --x;
                    break;
                }
                case 5: {
                    ++x;
                    break;
                }
                default: {
                    assert (false);
                    break;
                }
            }
            target = Target.Any;
        }
        int amount2 = this.sprayFoam(world, x, y, z, ItemSprayer.calculateDirectionsFromPlayer(player), target, maxFoamBlocks);
        if ((amount2 *= this.getFluidPerFoam()) > 0) {
            if (pack != null && (fluid2 = ((ItemArmorCFPack)pack.getItem()).drainfromCFpack(player, pack, amount2)) != null) {
                amount2 -= fluid2.amount;
            }
            if (amount2 > 0) {
                this.drain(stack, amount2, true);
            }
            return true;
        }
        return false;
    }

    public static boolean[] calculateDirectionsFromPlayer(EntityPlayer player) {
        float yaw = player.rotationYaw % 360.0f;
        float pitch = player.rotationPitch;
        boolean[] r = new boolean[]{true, true, true, true, true, true};
        if (pitch >= -65.0f && pitch <= 65.0f) {
            if (yaw >= 300.0f && yaw <= 360.0f || yaw >= 0.0f && yaw <= 60.0f) {
                r[2] = false;
            }
            if (yaw >= 30.0f && yaw <= 150.0f) {
                r[5] = false;
            }
            if (yaw >= 120.0f && yaw <= 240.0f) {
                r[3] = false;
            }
            if (yaw >= 210.0f && yaw <= 330.0f) {
                r[4] = false;
            }
        }
        if (pitch <= -40.0f) {
            r[0] = false;
        }
        if (pitch >= 40.0f) {
            r[1] = false;
        }
        return r;
    }

    public int sprayFoam(World world, int x, int y, int z, boolean[] directions, Target target, int maxFoamBlocks) {
        ChunkPosition startPos = new ChunkPosition(x, y, z);
        if (!ItemSprayer.canPlaceFoam(world, startPos, target)) {
            return 0;
        }
        ArrayList<ChunkPosition> check = new ArrayList<ChunkPosition>();
        ArrayList<ChunkPosition> place = new ArrayList<ChunkPosition>();
        int foamBlocks = 0;
        check.add(new ChunkPosition(x, y, z));
        for (int i = 0; i < check.size() && foamBlocks < maxFoamBlocks; ++i) {
            ChunkPosition set = (ChunkPosition)check.get(i);
            if (!ItemSprayer.canPlaceFoam(world, set, target)) continue;
            this.considerAddingCoord(set, place);
            this.addAdjacentSpacesOnList(set.chunkPosX, set.chunkPosY, set.chunkPosZ, check, directions, target != Target.Any);
            ++foamBlocks;
        }
        for (ChunkPosition pos : place) {
            Block targetBlock = world.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
            if (StackUtil.equals(targetBlock, Ic2Items.scaffold)) {
                StackUtil.getBlock(Ic2Items.scaffold).dropBlockAsItem(world, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, world.getBlockMetadata(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ), 0);
                world.setBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, StackUtil.getBlock(Ic2Items.constructionFoam), 0, 3);
                continue;
            }
            if (StackUtil.equals(targetBlock, Ic2Items.ironScaffold)) {
                world.setBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, StackUtil.getBlock(Ic2Items.constructionreinforcedFoam), 0, 3);
                continue;
            }
            if (StackUtil.equals(targetBlock, Ic2Items.copperCableBlock)) {
                TileEntity te = world.getTileEntity(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
                if (!(te instanceof TileEntityCable)) continue;
                ((TileEntityCable)te).changeFoam((byte)1);
                continue;
            }
            world.setBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, StackUtil.getBlock(Ic2Items.constructionFoam), 0, 3);
        }
        return foamBlocks;
    }

    public void addAdjacentSpacesOnList(int x, int y, int z, ArrayList<ChunkPosition> foam, boolean[] directions, boolean ignoreDirections) {
        int[] order = this.generateRngSpread(IC2.random);
        block8: for (int i = 0; i < order.length; ++i) {
            if (!ignoreDirections && !directions[order[i]]) continue;
            switch (order[i]) {
                case 0: {
                    this.considerAddingCoord(new ChunkPosition(x, y - 1, z), foam);
                    continue block8;
                }
                case 1: {
                    this.considerAddingCoord(new ChunkPosition(x, y + 1, z), foam);
                    continue block8;
                }
                case 2: {
                    this.considerAddingCoord(new ChunkPosition(x, y, z - 1), foam);
                    continue block8;
                }
                case 3: {
                    this.considerAddingCoord(new ChunkPosition(x, y, z + 1), foam);
                    continue block8;
                }
                case 4: {
                    this.considerAddingCoord(new ChunkPosition(x - 1, y, z), foam);
                    continue block8;
                }
                case 5: {
                    this.considerAddingCoord(new ChunkPosition(x + 1, y, z), foam);
                }
            }
        }
    }

    public void considerAddingCoord(ChunkPosition coord, ArrayList<ChunkPosition> list) {
        for (int i = 0; i < list.size(); ++i) {
            ChunkPosition entry = list.get(i);
            if (entry.chunkPosX != coord.chunkPosX || entry.chunkPosY != coord.chunkPosY || entry.chunkPosZ != coord.chunkPosZ) continue;
            return;
        }
        list.add(coord);
    }

    public int[] generateRngSpread(Random random) {
        int[] re = new int[]{0, 1, 2, 3, 4, 5};
        for (int i = 0; i < 16; ++i) {
            int first = random.nextInt(6);
            int second = random.nextInt(6);
            int save = re[first];
            re[first] = re[second];
            re[second] = save;
        }
        return re;
    }

    protected int getMaxFoamBlocks(ItemStack stack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        if (nbtData.getInteger("mode") == 0) {
            return 10;
        }
        return 1;
    }

    protected int getFluidPerFoam() {
        return 100;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean canfill(Fluid fluid) {
        return fluid == BlocksItems.getFluid(InternalName.fluidConstructionFoam);
    }

    private static boolean canPlaceFoam(World world, ChunkPosition pos, Target target) {
        int x = pos.chunkPosX;
        int y = pos.chunkPosY;
        int z = pos.chunkPosZ;
        switch (target) {
            case Any: {
                return StackUtil.getBlock(Ic2Items.constructionFoam).canPlaceBlockAt(world, x, y, z);
            }
            case Scaffold: {
                Block block = world.getBlock(x, y, z);
                return StackUtil.equals(block, Ic2Items.scaffold) || StackUtil.equals(block, Ic2Items.ironScaffold);
            }
            case Cable: {
                Block block = world.getBlock(x, y, z);
                if (!StackUtil.equals(block, Ic2Items.copperCableBlock)) {
                    return false;
                }
                TileEntity te = world.getTileEntity(x, y, z);
                if (!(te instanceof TileEntityCable)) break;
                return !((TileEntityCable)te).isFoamed();
            }
            default: {
                assert (false);
                break;
            }
        }
        return false;
    }

    static enum Target {
        Any,
        Scaffold,
        Cable;

    }
}

