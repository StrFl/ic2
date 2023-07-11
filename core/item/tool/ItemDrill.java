/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.tool.ItemElectricTool;
import java.util.EnumSet;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemDrill
extends ItemElectricTool {
    protected int soundTicker = 0;

    public ItemDrill(InternalName internalName, int operationEnergyCost, ItemElectricTool.HarvestLevel harvestLevel) {
        super(internalName, operationEnergyCost, harvestLevel, EnumSet.of(ItemElectricTool.ToolClass.Pickaxe, ItemElectricTool.ToolClass.Shovel));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
            Item item;
            ItemStack torchStack = player.inventory.mainInventory[i];
            if (torchStack == null || !torchStack.getUnlocalizedName().toLowerCase(Locale.ENGLISH).contains("torch") || !((item = torchStack.getItem()) instanceof ItemBlock)) continue;
            int oldMeta = torchStack.getItemDamage();
            int oldSize = torchStack.stackSize;
            boolean result = torchStack.tryPlaceItemIntoWorld(player, world, x, y, z, side, xOffset, yOffset, zOffset);
            if (player.capabilities.isCreativeMode) {
                torchStack.setItemDamage(oldMeta);
                torchStack.stackSize = oldSize;
            } else if (torchStack.stackSize <= 0) {
                ForgeEventFactory.onPlayerDestroyItem((EntityPlayer)player, (ItemStack)torchStack);
                player.inventory.mainInventory[i] = null;
            }
            if (!result) continue;
            return true;
        }
        return super.onItemUse(stack, player, world, x, y, z, side, xOffset, yOffset, zOffset);
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        float rStrength = super.getDigSpeed(stack, block, meta);
        if (rStrength > 1.0f) {
            ++this.soundTicker;
            if (this.soundTicker % 4 == 0) {
                IC2.platform.playSoundSp(this.getRandomDrillSound(), 1.0f, 1.0f);
            }
        }
        return rStrength;
    }

    public String getRandomDrillSound() {
        switch (IC2.random.nextInt(4)) {
            default: {
                return "drill";
            }
            case 1: {
                return "drillOne";
            }
            case 2: {
                return "drillTwo";
            }
            case 3: 
        }
        return "drillThree";
    }
}

