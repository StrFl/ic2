/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.WorldData;
import ic2.core.init.InternalName;
import ic2.core.item.tool.ItemElectricTool;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemWindmeter
extends ItemElectricTool {
    public ItemWindmeter(InternalName internalName) {
        super(internalName, 50);
        this.setMaxStackSize(1);
        this.maxCharge = 10000;
        this.transferLimit = 100;
        this.tier = 1;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!IC2.platform.isSimulating()) {
            return itemstack;
        }
        if (!ElectricItem.manager.canUse(itemstack, this.operationEnergyCost)) {
            return itemstack;
        }
        ElectricItem.manager.use(itemstack, this.operationEnergyCost, (EntityLivingBase)entityplayer);
        double windStrength = WorldData.get((World)world).windSim.getWindAt(entityplayer.posY);
        if (windStrength < 0.0) {
            windStrength = 0.0;
        }
        IC2.platform.messagePlayer(entityplayer, StatCollector.translateToLocalFormatted((String)"ic2.itemwindmeter.info", (Object[])new Object[]{Float.valueOf((float)Math.round(windStrength * 100.0) / 100.0f)}), new Object[0]);
        return itemstack;
    }
}

