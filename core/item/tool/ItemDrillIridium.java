/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.tool.ItemDrill;
import ic2.core.item.tool.ItemElectricTool;
import java.util.HashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDrillIridium
extends ItemDrill {
    public ItemDrillIridium(InternalName internalName) {
        super(internalName, 800, ItemElectricTool.HarvestLevel.Iridium);
        this.maxCharge = 300000;
        this.transferLimit = 1000;
        this.tier = 3;
        this.efficiencyOnProperMaterial = 24.0f;
        Ic2Items.iridiumDrill = this.getItemStack(0.0);
    }

    @Override
    protected ItemStack getItemStack(double charge) {
        ItemStack ret = super.getItemStack(charge);
        HashMap<Integer, Integer> enchantmentMap = new HashMap<Integer, Integer>();
        enchantmentMap.put(Enchantment.fortune.effectId, 3);
        EnchantmentHelper.setEnchantments(enchantmentMap, (ItemStack)ret);
        return ret;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!IC2.platform.isSimulating()) {
            return super.onItemRightClick(stack, world, player);
        }
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            HashMap<Integer, Integer> enchantmentMap = new HashMap<Integer, Integer>();
            enchantmentMap.put(Enchantment.fortune.effectId, 3);
            if (EnchantmentHelper.getEnchantmentLevel((int)Enchantment.silkTouch.effectId, (ItemStack)stack) == 0) {
                enchantmentMap.put(Enchantment.silkTouch.effectId, 1);
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.silkTouch");
            } else {
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.normal");
            }
            EnchantmentHelper.setEnchantments(enchantmentMap, (ItemStack)stack);
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            return false;
        }
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }
}

