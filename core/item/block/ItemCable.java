/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.item.block;

import ic2.api.item.IBoxable;
import ic2.core.Ic2Items;
import ic2.core.block.wiring.BlockCable;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemCable
extends ItemIC2
implements IBoxable {
    public ItemCable(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.copperCableItem = new ItemStack((Item)this, 1, 1);
        Ic2Items.insulatedCopperCableItem = new ItemStack((Item)this, 1, 0);
        Ic2Items.goldCableItem = new ItemStack((Item)this, 1, 2);
        Ic2Items.insulatedGoldCableItem = new ItemStack((Item)this, 1, 3);
        Ic2Items.doubleInsulatedGoldCableItem = new ItemStack((Item)this, 1, 4);
        Ic2Items.ironCableItem = new ItemStack((Item)this, 1, 5);
        Ic2Items.insulatedIronCableItem = new ItemStack((Item)this, 1, 6);
        Ic2Items.doubleInsulatedIronCableItem = new ItemStack((Item)this, 1, 7);
        Ic2Items.trippleInsulatedIronCableItem = new ItemStack((Item)this, 1, 8);
        Ic2Items.glassFiberCableItem = new ItemStack((Item)this, 1, 9);
        Ic2Items.tinCableItem = new ItemStack((Item)this, 1, 10);
        Ic2Items.detectorCableItem = new ItemStack((Item)this, 1, 11);
        Ic2Items.splitterCableItem = new ItemStack((Item)this, 1, 12);
        Ic2Items.insulatedTinCableItem = new ItemStack((Item)this, 1, 13);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        InternalName ret;
        int meta = itemstack.getItemDamage();
        switch (meta) {
            case 0: {
                ret = InternalName.itemCable;
                break;
            }
            case 1: {
                ret = InternalName.itemCableO;
                break;
            }
            case 2: {
                ret = InternalName.itemGoldCable;
                break;
            }
            case 3: {
                ret = InternalName.itemGoldCableI;
                break;
            }
            case 4: {
                ret = InternalName.itemGoldCableII;
                break;
            }
            case 5: {
                ret = InternalName.itemIronCable;
                break;
            }
            case 6: {
                ret = InternalName.itemIronCableI;
                break;
            }
            case 7: {
                ret = InternalName.itemIronCableII;
                break;
            }
            case 8: {
                ret = InternalName.itemIronCableIIII;
                break;
            }
            case 9: {
                ret = InternalName.itemGlassCable;
                break;
            }
            case 10: {
                ret = InternalName.itemTinCable;
                break;
            }
            case 11: {
                ret = InternalName.itemDetectorCable;
                break;
            }
            case 12: {
                ret = InternalName.itemSplitterCable;
                break;
            }
            case 13: {
                ret = InternalName.itemTinCableI;
                break;
            }
            default: {
                return null;
            }
        }
        return "ic2." + ret.name();
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        int capacity = TileEntityCable.getMaxCapacity(itemStack.getItemDamage());
        info.add(capacity + " EU/t");
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float a, float b, float c) {
        Block oldBlock = world.getBlock(x, y, z);
        if (!oldBlock.isAir((IBlockAccess)world, x, y, z)) {
            if (oldBlock == Blocks.snow_layer) {
                side = 1;
            } else if (oldBlock != Blocks.vine && oldBlock != Blocks.tallgrass && oldBlock != Blocks.deadbush && !oldBlock.isReplaceable((IBlockAccess)world, x, y, z)) {
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
                    }
                }
            }
        }
        BlockCable block = (BlockCable)StackUtil.getBlock(Ic2Items.insulatedCopperCableBlock);
        if ((oldBlock.isAir((IBlockAccess)world, x, y, z) || world.canPlaceEntityOnSide(StackUtil.getBlock(Ic2Items.insulatedCopperCableBlock), x, y, z, false, side, (Entity)entityplayer, itemstack)) && world.checkNoEntityCollision(block.getCollisionBoundingBoxFromPool(world, x, y, z, itemstack.getItemDamage())) && world.setBlock(x, y, z, (Block)block, itemstack.getItemDamage(), 3)) {
            block.onPostBlockPlaced(world, x, y, z, side);
            block.onBlockPlacedBy(world, x, y, z, (EntityLivingBase)entityplayer, itemstack);
            if (!entityplayer.capabilities.isCreativeMode) {
                --itemstack.stackSize;
            }
            return true;
        }
        return false;
    }

    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        for (int meta = 0; meta < Short.MAX_VALUE; ++meta) {
            ItemStack stack;
            if (meta == 4 || meta == 7 || meta == 8 || this.getUnlocalizedName(stack = new ItemStack((Item)this, 1, meta)) == null) continue;
            itemList.add(stack);
        }
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

