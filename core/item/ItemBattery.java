/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.BaseElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBattery
extends BaseElectricItem {
    public ItemBattery(InternalName internalName, double maxCharge, double transferLimit, int tier) {
        super(internalName, maxCharge, transferLimit, tier);
    }

    @Override
    public String getTextureName(int index) {
        if (index < 5) {
            return this.getUnlocalizedName().substring(4) + "." + index;
        }
        return null;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (meta <= 1) {
            return this.textures[4];
        }
        if (meta >= this.getMaxDamage() - 1) {
            return this.textures[0];
        }
        return this.textures[3 - 3 * (meta - 2) / (this.getMaxDamage() - 4 + 1)];
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return true;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        if (this == Ic2Items.chargedReBattery.getItem()) {
            return Ic2Items.reBattery.getItem();
        }
        return super.getEmptyItem(itemStack);
    }

    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityplayer) {
        if (IC2.platform.isSimulating() && itemStack.getItem() == Ic2Items.chargedReBattery.getItem()) {
            boolean transferred = false;
            for (int i = 0; i < 9; ++i) {
                double transfer;
                ItemStack stack = entityplayer.inventory.mainInventory[i];
                if (stack == null || stack.getItem() instanceof ItemBattery || (transfer = ElectricItem.manager.discharge(itemStack, 2.0 * this.transferLimit, Integer.MAX_VALUE, true, true, true)) <= 0.0 || (transfer = ElectricItem.manager.charge(stack, transfer, this.tier, true, false)) <= 0.0) continue;
                ElectricItem.manager.discharge(itemStack, transfer, Integer.MAX_VALUE, true, true, false);
                transferred = true;
            }
            if (transferred && !IC2.platform.isRendering()) {
                entityplayer.openContainer.detectAndSendChanges();
            }
        }
        return itemStack;
    }
}

