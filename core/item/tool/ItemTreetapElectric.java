/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.item.tool.ItemTreetap;
import ic2.core.util.StackUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTreetapElectric
extends ItemElectricTool {
    public ItemTreetapElectric(InternalName internalName) {
        super(internalName, 50);
        this.maxCharge = 10000;
        this.transferLimit = 100;
        this.tier = 1;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!StackUtil.equals(world.getBlock(x, y, z), Ic2Items.rubberWood) || !ElectricItem.manager.canUse(itemStack, this.operationEnergyCost)) {
            return false;
        }
        if (ItemTreetap.attemptExtract(entityPlayer, world, x, y, z, side, null)) {
            ElectricItem.manager.use(itemStack, this.operationEnergyCost, (EntityLivingBase)entityPlayer);
            return true;
        }
        return super.onItemUse(itemStack, entityPlayer, world, x, y, z, side, hitX, hitY, hitZ);
    }
}

