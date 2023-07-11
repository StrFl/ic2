/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.tool.EntityParticle;
import ic2.core.item.tool.ItemElectricTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PlasmaLauncher
extends ItemElectricTool {
    public PlasmaLauncher(InternalName internalName) {
        super(internalName, 100);
        this.maxCharge = 40000;
        this.transferLimit = 128;
        this.tier = 3;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!IC2.platform.isSimulating()) {
            return stack;
        }
        EntityParticle particle = new EntityParticle(world, (EntityLivingBase)player, 8.0f, 1.0, 2.0);
        world.spawnEntityInWorld((Entity)particle);
        return super.onItemRightClick(stack, world, player);
    }
}

