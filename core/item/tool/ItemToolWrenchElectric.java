/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.util.StackUtil;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemToolWrenchElectric
extends ItemToolWrench
implements IElectricItem,
IItemHudInfo {
    public ItemToolWrenchElectric(InternalName internalName) {
        super(internalName);
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        String mode = nbtData.getBoolean("losslessMode") ? StatCollector.translateToLocal((String)"ic2.tooltip.mode.lossless") : StatCollector.translateToLocal((String)"ic2.tooltip.mode.normal");
        list.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.mode", (Object[])new Object[]{mode}));
    }

    @Override
    public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        String mode = nbtData.getBoolean("losslessMode") ? StatCollector.translateToLocal((String)"ic2.tooltip.mode.lossless") : StatCollector.translateToLocal((String)"ic2.tooltip.mode.normal");
        info.add(ElectricItem.manager.getToolTip(itemStack));
        info.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.mode", (Object[])new Object[]{mode}));
        return info;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (IC2.platform.isSimulating() && IC2.keyboard.isModeSwitchKeyDown(entityplayer)) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
            boolean newValue = !nbtData.getBoolean("losslessMode");
            nbtData.setBoolean("losslessMode", newValue);
            if (newValue) {
                IC2.platform.messagePlayer(entityplayer, "Lossless wrench mode enabled", new Object[0]);
            } else {
                IC2.platform.messagePlayer(entityplayer, "Lossless wrench mode disabled", new Object[0]);
            }
        }
        return itemstack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (IC2.keyboard.isModeSwitchKeyDown(entityPlayer)) {
            return false;
        }
        return super.onItemUseFirst(itemstack, entityPlayer, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean canTakeDamage(ItemStack stack, int amount) {
        return ElectricItem.manager.getCharge(stack) >= (double)(amount *= 50);
    }

    @Override
    public void damage(ItemStack itemStack, int amount, EntityPlayer player) {
        ElectricItem.manager.use(itemStack, 50 * amount, (EntityLivingBase)player);
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return 12000.0;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return 1;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return 250.0;
    }

    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        ItemStack charged = new ItemStack((Item)this, 1);
        ElectricItem.manager.charge(charged, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, false);
        itemList.add(charged);
        itemList.add(new ItemStack((Item)this, 1, this.getMaxDamage()));
    }

    @Override
    public boolean overrideWrenchSuccessRate(ItemStack itemStack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        return nbtData.getBoolean("losslessMode");
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
}

