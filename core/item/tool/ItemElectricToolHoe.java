/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.block.Block
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.UseHoeEvent
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.tool.ItemElectricTool;
import java.util.EnumSet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemElectricToolHoe
extends ItemElectricTool {
    public ItemElectricToolHoe(InternalName internalName) {
        super(internalName, 50, ItemElectricTool.HarvestLevel.Iron, EnumSet.of(ItemElectricTool.ToolClass.Hoe));
        this.maxCharge = 10000;
        this.transferLimit = 100;
        this.tier = 1;
        this.efficiencyOnProperMaterial = 16.0f;
    }

    public boolean onBlockStartBreak(ItemStack itemStack, int x, int y, int z, EntityPlayer entityLiving) {
        ElectricItem.manager.use(itemStack, this.operationEnergyCost, (EntityLivingBase)entityLiving);
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!entityPlayer.canPlayerEdit(x, y, z, side, itemStack)) {
            return false;
        }
        if (!ElectricItem.manager.canUse(itemStack, this.operationEnergyCost)) {
            return false;
        }
        if (MinecraftForge.EVENT_BUS.post((Event)new UseHoeEvent(entityPlayer, itemStack, world, x, y, z))) {
            return false;
        }
        Block block = world.getBlock(x, y, z);
        if (side != 0 && world.isAirBlock(x, y + 1, z) && (block == Blocks.mycelium || block == Blocks.grass || block == Blocks.dirt)) {
            block = Blocks.farmland;
            world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, block.stepSound.getStepResourcePath(), (block.stepSound.getVolume() + 1.0f) / 2.0f, block.stepSound.getPitch() * 0.8f);
            if (IC2.platform.isSimulating()) {
                world.setBlock(x, y, z, block, 0, 3);
                ElectricItem.manager.use(itemStack, this.operationEnergyCost, (EntityLivingBase)entityPlayer);
            }
            return true;
        }
        return super.onItemUse(itemStack, entityPlayer, world, x, y, z, side, hitX, hitY, hitZ);
    }
}

