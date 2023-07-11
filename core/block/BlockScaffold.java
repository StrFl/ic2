/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMetaData;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockScaffold
extends BlockMetaData {
    private static final IRecipeInput stickInput = new RecipeInputOreDict("stickWood");
    public static final int standardStrength = 2;
    public static final int standardIronStrength = 5;
    public static final int reinforcedStrength = 5;
    public static final int reinforcedIronStrength = 12;
    public static final int tickDelay = 1;
    private static final int textureIndexNormal = 0;
    private static final int textureIndexReinforced = 1;

    public BlockScaffold(InternalName internalName1) {
        super(internalName1, internalName1 == InternalName.blockIronScaffold ? Material.iron : Material.wood, ItemBlockIC2.class);
        if (internalName1 == InternalName.blockIronScaffold) {
            this.setHardness(0.8f);
            this.setResistance(10.0f);
            this.setStepSound(soundTypeMetal);
            Ic2Items.ironScaffold = new ItemStack((Block)this);
        } else {
            this.setHardness(0.5f);
            this.setResistance(0.2f);
            this.setStepSound(soundTypeWood);
            Ic2Items.scaffold = new ItemStack((Block)this);
        }
    }

    @Override
    public String getTextureName(int index) {
        if (index == 0) {
            return this.getUnlocalizedName();
        }
        if (index == 1) {
            return this.getUnlocalizedName() + "." + InternalName.reinforced.name();
        }
        return null;
    }

    @Override
    public int getTextureIndex(int meta) {
        if (meta == this.getReinforcedStrength()) {
            return 1;
        }
        return 0;
    }

    public int getStandardStrength() {
        if (this.blockMaterial == Material.iron) {
            return 5;
        }
        return 2;
    }

    public int getReinforcedStrength() {
        if (this.blockMaterial == Material.iron) {
            return 12;
        }
        return 5;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isNormalCube() {
        return false;
    }

    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            player.fallDistance = 0.0f;
            if (player.motionY < -0.15) {
                player.motionY = -0.15;
            }
            if (IC2.keyboard.isForwardKeyDown(player) && player.motionY < 0.2) {
                player.motionY = 0.2;
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        double border = 0.0625;
        return AxisAlignedBB.getBoundingBox((double)((double)x + border), (double)y, (double)((double)z + border), (double)((double)(x + 1) - border), (double)(y + 1), (double)((double)(z + 1) - border));
    }

    public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
        return side == 0 || side == 1;
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1));
    }

    public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int meta, int fortune) {
        ArrayList<ItemStack> tr = new ArrayList<ItemStack>();
        tr.add(new ItemStack((Block)this, 1));
        if (meta == this.getReinforcedStrength()) {
            if (this.blockMaterial == Material.iron) {
                tr.add(new ItemStack(Ic2Items.ironFence.getItem(), 1));
            }
            if (this.blockMaterial == Material.wood) {
                tr.add(new ItemStack(Items.stick, 2));
            }
        }
        return tr;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float a, float b, float c) {
        if (entityplayer.isSneaking()) {
            return false;
        }
        ItemStack sticks = entityplayer.inventory.getCurrentItem();
        if (sticks == null || this.blockMaterial == Material.wood && (!stickInput.matches(sticks) || sticks.stackSize < 2) || this.blockMaterial == Material.iron && sticks.getItem() != Ic2Items.ironFence.getItem()) {
            return false;
        }
        if (world.getBlockMetadata(i, j, k) == this.getReinforcedStrength() || !this.isPillar(world, i, j, k)) {
            return false;
        }
        sticks.stackSize = this.blockMaterial == Material.wood ? (sticks.stackSize -= 2) : --sticks.stackSize;
        if (entityplayer.getCurrentEquippedItem().stackSize <= 0) {
            entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
        }
        world.setBlockMetadataWithNotify(i, j, k, this.getReinforcedStrength(), 7);
        world.markBlockRangeForRenderUpdate(i, j, k, i, j, k);
        return true;
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if (player.getCurrentEquippedItem() != null && StackUtil.equals(this, player.getCurrentEquippedItem())) {
            while (world.getBlock(x, y, z) == this) {
                ++y;
            }
            if (this.canPlaceBlockAt(world, x, y, z) && y < IC2.getWorldHeight(world)) {
                world.setBlock(x, y, z, (Block)this, 0, 7);
                this.onPostBlockPlaced(world, x, y, z, 0);
                if (!player.capabilities.isCreativeMode) {
                    --player.getCurrentEquippedItem().stackSize;
                    if (player.getCurrentEquippedItem().stackSize <= 0) {
                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                    }
                }
            }
        }
    }

    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        if (this.getStrengthFrom(world, i, j, k) <= -1) {
            return false;
        }
        return super.canPlaceBlockAt(world, i, j, k);
    }

    public boolean isPillar(World world, int i, int j, int k) {
        while (world.getBlock(i, j, k) == this) {
            --j;
        }
        return world.isBlockNormalCubeDefault(i, j, k, false);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        this.updateSupportStatus(world, x, y, z);
    }

    public void onPostBlockPlaced(World world, int i, int j, int k, int l) {
        this.updateTick(world, i, j, k, null);
    }

    public void updateTick(World world, int i, int j, int k, Random random) {
        int ownStrength = world.getBlockMetadata(i, j, k);
        if (ownStrength >= this.getReinforcedStrength()) {
            if (!this.isPillar(world, i, j, k)) {
                ownStrength = this.getStrengthFrom(world, i, j, k);
                ItemStack drop = new ItemStack(Items.stick, 2);
                if (this.blockMaterial == Material.iron) {
                    drop = new ItemStack(Ic2Items.ironFence.getItem());
                }
                this.dropBlockAsItem(world, i, j, k, drop);
            }
        } else {
            ownStrength = this.getStrengthFrom(world, i, j, k);
        }
        if (ownStrength <= -1) {
            world.setBlockToAir(i, j, k);
            this.dropBlockAsItem(world, i, j, k, new ItemStack((Block)this));
        } else if (ownStrength != world.getBlockMetadata(i, j, k)) {
            world.setBlockMetadataWithNotify(i, j, k, ownStrength, 7);
            world.markBlockRangeForRenderUpdate(i, j, k, i, j, k);
        }
    }

    public int getStrengthFrom(World world, int i, int j, int k) {
        int strength = 0;
        if (this.isPillar(world, i, j - 1, k)) {
            strength = this.getStandardStrength() + 1;
        }
        strength = this.compareStrengthTo(world, i, j - 1, k, strength);
        strength = this.compareStrengthTo(world, i + 1, j, k, strength);
        strength = this.compareStrengthTo(world, i - 1, j, k, strength);
        strength = this.compareStrengthTo(world, i, j, k + 1, strength);
        strength = this.compareStrengthTo(world, i, j, k - 1, strength);
        return strength - 1;
    }

    public int compareStrengthTo(World world, int i, int j, int k, int strength) {
        int s = 0;
        if (world.getBlock(i, j, k) == this && (s = world.getBlockMetadata(i, j, k)) > this.getReinforcedStrength()) {
            s = this.getReinforcedStrength();
        }
        if (s > strength) {
            return s;
        }
        return strength;
    }

    public void updateSupportStatus(World world, int i, int j, int k) {
        world.scheduleBlockUpdate(i, j, k, (Block)this, 1);
    }

    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        if (StackUtil.equals(this, Ic2Items.scaffold)) {
            return 8;
        }
        return super.getFireSpreadSpeed(world, x, y, z, face);
    }

    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        if (StackUtil.equals(this, Ic2Items.scaffold)) {
            return 20;
        }
        return super.getFlammability(world, x, y, z, face);
    }
}

