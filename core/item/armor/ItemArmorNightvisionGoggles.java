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
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 */
package ic2.core.item.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.util.StackUtil;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemArmorNightvisionGoggles
extends ItemArmorUtility
implements IElectricItem,
IItemHudInfo {
    public ItemArmorNightvisionGoggles(InternalName internalName) {
        super(internalName, InternalName.nightvision, 0);
        this.setMaxDamage(27);
        this.setNoRepair();
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
        return 200000.0;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return 1;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return 200.0;
    }

    @Override
    public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(itemStack));
        return info;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        boolean active = nbtData.getBoolean("active");
        byte toggleTimer = nbtData.getByte("toggleTimer");
        if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
            toggleTimer = 10;
            boolean bl = active = !active;
            if (IC2.platform.isSimulating()) {
                nbtData.setBoolean("active", active);
                if (active) {
                    IC2.platform.messagePlayer(player, "Nightvision enabled.", new Object[0]);
                } else {
                    IC2.platform.messagePlayer(player, "Nightvision disabled.", new Object[0]);
                }
            }
        }
        if (IC2.platform.isSimulating() && toggleTimer > 0) {
            toggleTimer = (byte)(toggleTimer - 1);
            nbtData.setByte("toggleTimer", toggleTimer);
        }
        boolean ret = false;
        if (active && IC2.platform.isSimulating() && ElectricItem.manager.use(itemStack, 1.0, (EntityLivingBase)player)) {
            int x = MathHelper.floor_double((double)player.posX);
            int z = MathHelper.floor_double((double)player.posZ);
            int y = MathHelper.floor_double((double)player.posY);
            int skylight = player.worldObj.getBlockLightValue(x, y, z);
            if (skylight > 8) {
                IC2.platform.removePotion((EntityLivingBase)player, Potion.nightVision.id);
                player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0, true));
            } else {
                IC2.platform.removePotion((EntityLivingBase)player, Potion.blindness.id);
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, 0, true));
            }
            ret = true;
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
        ItemStack itemStack = new ItemStack((Item)this, 1);
        if (this.getChargedItem(itemStack) == this) {
            ItemStack charged = new ItemStack((Item)this, 1);
            ElectricItem.manager.charge(charged, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, false);
            itemList.add(charged);
        }
        if (this.getEmptyItem(itemStack) == this) {
            itemList.add(new ItemStack((Item)this, 1, this.getMaxDamage()));
        }
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
}

