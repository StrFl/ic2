/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemArmorSolarHelmet
extends ItemArmorUtility {
    public ItemArmorSolarHelmet(InternalName internalName) {
        super(internalName, InternalName.solar, 0);
        this.setMaxDamage(0);
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        boolean ret = false;
        if (player.inventory.armorInventory[2] != null && TileEntitySolarGenerator.isSunVisible(player.worldObj, (int)player.posX, (int)player.posY + 1, (int)player.posZ)) {
            boolean bl = ret = ElectricItem.manager.charge(player.inventory.armorInventory[2], 1.0, Integer.MAX_VALUE, true, false) > 0.0;
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }
}

