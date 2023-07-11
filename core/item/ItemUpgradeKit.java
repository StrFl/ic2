/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.wiring.TileEntityChargepadMFE;
import ic2.core.block.wiring.TileEntityChargepadMFSU;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.block.wiring.TileEntityElectricMFE;
import ic2.core.block.wiring.TileEntityElectricMFSU;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemUpgradeKit
extends ItemIC2 {
    public ItemUpgradeKit(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.mfsukit = new ItemStack((Item)this, 1, 0);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        InternalName ret;
        int meta = itemstack.getItemDamage();
        switch (meta) {
            case 0: {
                ret = InternalName.itemMFSUupgradekit;
                break;
            }
            default: {
                return null;
            }
        }
        return "ic2." + ret.name();
    }

    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        int meta = stack.getItemDamage();
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        switch (meta) {
            case 0: {
                int i;
                ItemStack[] items;
                byte mode;
                short facing;
                double eustored;
                TileEntityElectricBlock mfe;
                if (tileEntity instanceof TileEntityElectricMFE) {
                    mfe = (TileEntityElectricMFE)tileEntity;
                    eustored = ((TileEntityElectricMFE)mfe).energy;
                    facing = mfe.getFacing();
                    mode = ((TileEntityElectricMFE)mfe).redstoneMode;
                    items = new ItemStack[mfe.getSizeInventory()];
                    for (i = 0; i < items.length; ++i) {
                        items[i] = mfe.getStackInSlot(i);
                    }
                    world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.mfsUnit), 2, 2);
                    tileEntity = world.getTileEntity(x, y, z);
                    if (tileEntity instanceof TileEntityElectricMFSU) {
                        TileEntityElectricMFSU mfsu = (TileEntityElectricMFSU)tileEntity;
                        mfsu.setFacing(facing);
                        mfsu.setStored((int)eustored);
                        mfsu.redstoneMode = mode;
                        for (int i2 = 0; i2 < items.length; ++i2) {
                            mfsu.setInventorySlotContents(i2, items[i2]);
                        }
                        mfsu.markDirty();
                        --stack.stackSize;
                        if (stack.stackSize < 0) {
                            stack = null;
                        }
                        return true;
                    }
                }
                if (!(tileEntity instanceof TileEntityChargepadMFE)) break;
                mfe = (TileEntityChargepadMFE)tileEntity;
                eustored = ((TileEntityChargepadMFE)mfe).energy;
                facing = mfe.getFacing();
                mode = ((TileEntityChargepadMFE)mfe).redstoneMode;
                items = new ItemStack[mfe.getSizeInventory()];
                for (i = 0; i < items.length; ++i) {
                    items[i] = mfe.getStackInSlot(i);
                }
                world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.ChargepadmfsUnit), 3, 2);
                tileEntity = world.getTileEntity(x, y, z);
                if (!(tileEntity instanceof TileEntityChargepadMFSU)) break;
                TileEntityChargepadMFSU mfsu = (TileEntityChargepadMFSU)tileEntity;
                mfsu.setFacing(facing);
                mfsu.setStored((int)eustored);
                mfsu.redstoneMode = mode;
                for (int i3 = 0; i3 < items.length; ++i3) {
                    mfsu.setInventorySlotContents(i3, items[i3]);
                }
                mfsu.markDirty();
                --stack.stackSize;
                if (stack.stackSize < 0) {
                    stack = null;
                }
                return true;
            }
        }
        return false;
    }

    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        ItemStack stack;
        for (int meta = 0; meta < Short.MAX_VALUE && this.getUnlocalizedName(stack = new ItemStack((Item)this, 1, meta)) != null; ++meta) {
            itemList.add(stack);
        }
    }

    public void addInformation(ItemStack itemStack, EntityPlayer player, List info, boolean b) {
        super.addInformation(itemStack, player, info, b);
        int meta = itemStack.getItemDamage();
        switch (meta) {
            case 0: {
                info.add(StatCollector.translateToLocal((String)"ic2.itemMFSUupgradekit.info"));
            }
        }
    }
}

