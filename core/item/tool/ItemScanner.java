/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.recipe.IRecipeInput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.PositionSpec;
import ic2.core.init.InternalName;
import ic2.core.item.BaseElectricItem;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.tool.HandHeldScanner;
import ic2.core.util.ItemStackWrapper;
import ic2.core.util.Tuple;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScanner
extends BaseElectricItem
implements IBoxable,
IHandHeldInventory {
    public ItemScanner(InternalName internalName) {
        this(internalName, 100000.0, 128.0, 1);
    }

    public ItemScanner(InternalName internalName, double maxCharge, double transferLimit, int tier) {
        super(internalName, maxCharge, transferLimit, tier);
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (this.tier == 1 && !ElectricItem.manager.use(itemstack, 50.0, (EntityLivingBase)entityplayer) || this.tier == 2 && !ElectricItem.manager.use(itemstack, 250.0, (EntityLivingBase)entityplayer)) {
            return itemstack;
        }
        if (IC2.platform.isSimulating()) {
            if (IC2.platform.launchGui(entityplayer, this.getInventory(entityplayer, itemstack))) {
                ContainerBase container = (ContainerBase)entityplayer.openContainer;
                Map<ItemStackWrapper, Integer> scanResult = this.scan(entityplayer.worldObj, Util.roundToNegInf(entityplayer.posX), Util.roundToNegInf(entityplayer.posY), Util.roundToNegInf(entityplayer.posZ), this.getScannrange());
                container.setField("scanResults", this.scanMapToSortedList(scanResult));
            }
        } else {
            IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/ODScanner.ogg", true, IC2.audioManager.getDefaultVolume());
        }
        return itemstack;
    }

    public static boolean isValuable(Block block, int metaData) {
        return ItemScanner.valueOf(block, metaData) > 0;
    }

    public static int valueOf(Block block, int metaData) {
        Item item = Item.getItemFromBlock((Block)block);
        if (item == null) {
            return 0;
        }
        ItemStack stack = new ItemStack(item, 1, metaData);
        int max = 0;
        for (Map.Entry<IRecipeInput, Integer> entry : IC2.valuableOres.entrySet()) {
            if (!entry.getKey().matches(stack)) continue;
            max = Math.max(max, entry.getValue());
        }
        return max;
    }

    public int startLayerScan(ItemStack itemStack) {
        return ElectricItem.manager.use(itemStack, 50.0, null) ? this.getScannrange() / 2 : 0;
    }

    public int getScannrange() {
        return 6;
    }

    public boolean haveChargeforScan(ItemStack itemStack) {
        return ElectricItem.manager.canUse(itemStack, 128.0);
    }

    public void discharge(ItemStack itemStack, int amount) {
        ElectricItem.manager.use(itemStack, amount, null);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public IHasGui getInventory(EntityPlayer entityPlayer, ItemStack itemStack) {
        return new HandHeldScanner(entityPlayer, itemStack);
    }

    private Map<ItemStackWrapper, Integer> scan(World world, int xStart, int yStart, int zStart, int range) {
        HashMap<ItemStackWrapper, Integer> ret = new HashMap<ItemStackWrapper, Integer>();
        for (int x = xStart - range; x <= xStart + range; ++x) {
            for (int y = yStart - range; y <= yStart + range; ++y) {
                for (int z = zStart - range; z <= zStart + range; ++z) {
                    int meta;
                    Block block = world.getBlock(x, y, z);
                    if (!ItemScanner.isValuable(block, meta = world.getBlockMetadata(x, y, z))) continue;
                    for (ItemStack drop : block.getDrops(world, x, y, z, meta, 0)) {
                        ItemStackWrapper key = new ItemStackWrapper(drop);
                        Integer count = (Integer)ret.get(key);
                        if (count == null) {
                            count = 0;
                        }
                        count = count + drop.stackSize;
                        ret.put(key, count);
                    }
                }
            }
        }
        return ret;
    }

    private List<Tuple.T2<ItemStack, Integer>> scanMapToSortedList(Map<ItemStackWrapper, Integer> map) {
        ArrayList<Tuple.T2<ItemStack, Integer>> ret = new ArrayList<Tuple.T2<ItemStack, Integer>>(map.size());
        for (Map.Entry<ItemStackWrapper, Integer> entry : map.entrySet()) {
            ret.add(new Tuple.T2<ItemStack, Integer>(entry.getKey().stack, entry.getValue()));
        }
        Collections.sort(ret, new Comparator<Tuple.T2<ItemStack, Integer>>(){

            @Override
            public int compare(Tuple.T2<ItemStack, Integer> a, Tuple.T2<ItemStack, Integer> b) {
                return (Integer)b.b - (Integer)a.b;
            }
        });
        return ret;
    }
}

