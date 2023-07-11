/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRubWood
extends BlockMetaData {
    private static final int textureIndexNormal = 0;
    private static final int textureIndexWet = 1;
    private static final int textureIndexDry = 2;

    public BlockRubWood(InternalName internalName1) {
        super(internalName1, Material.wood, ItemBlockIC2.class);
        this.setTickRandomly(true);
        this.setHardness(1.0f);
        this.setStepSound(soundTypeWood);
        Ic2Items.rubberWood = new ItemStack((Block)this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        return super.getIcon(iBlockAccess, x, y, z, side);
    }

    @Override
    public String getTextureName(int index) {
        if (index == 0) {
            return this.getUnlocalizedName();
        }
        if (index == 1) {
            return this.getUnlocalizedName() + "." + InternalName.wet.name();
        }
        if (index == 2) {
            return this.getUnlocalizedName() + "." + InternalName.dry.name();
        }
        return null;
    }

    @Override
    public int getTextureIndex(int meta) {
        if (meta % 6 >= 2) {
            if (meta >= 6) {
                return 2;
            }
            return 1;
        }
        return 0;
    }

    @Override
    public int getFacing(int meta) {
        int ret = meta % 6;
        return ret < 2 ? super.getFacing(meta) : ret;
    }

    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        int count = this.quantityDropped(world.rand);
        for (int j1 = 0; j1 < count; ++j1) {
            if (world.rand.nextFloat() > chance) continue;
            Item item = this.getItemDropped(meta, world.rand, fortune);
            if (item != null) {
                this.dropBlockAsItem(world, x, y, z, new ItemStack(item, 1, 0));
            }
            if (meta == 0 || world.rand.nextInt(6) != 0) continue;
            this.dropBlockAsItem(world, x, y, z, new ItemStack(Ic2Items.resin.getItem()));
        }
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int b) {
        int range = 4;
        int l = range + 1;
        if (world.checkChunksExist(x - l, y - l, z - l, x + l, y + l, z + l)) {
            for (int xOffset = -range; xOffset <= range; ++xOffset) {
                for (int yOffset = -range; yOffset <= range; ++yOffset) {
                    for (int zOffset = -range; zOffset <= range; ++zOffset) {
                        int meta;
                        Block neighbor = world.getBlock(x + xOffset, y + yOffset, z + zOffset);
                        if (!StackUtil.equals(neighbor, Ic2Items.rubberLeaves) || ((meta = world.getBlockMetadata(x + xOffset, y + yOffset, z + zOffset)) & 8) != 0) continue;
                        world.setBlockMetadataWithNotify(x + xOffset, y + yOffset, z + zOffset, meta | 8, 3);
                    }
                }
            }
        }
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.isRemote) {
            return;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 6) {
            return;
        }
        if (random.nextInt(200) == 0) {
            world.setBlockMetadataWithNotify(x, y, z, meta % 6, 7);
        } else {
            world.scheduleBlockUpdate(x, y, z, (Block)this, this.tickRate(world));
        }
    }

    public int tickRate(World world) {
        return 100;
    }

    public int getMobilityFlag() {
        return 2;
    }

    public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    public boolean isWood(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 4;
    }

    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 20;
    }
}

