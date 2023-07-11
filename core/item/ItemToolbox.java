/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.init.InternalName;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.ItemIC2;
import ic2.core.item.tool.HandHeldToolbox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemToolbox
extends ItemIC2
implements IHandHeldInventory {
    public ItemToolbox(InternalName internalName) {
        super(internalName);
        this.setMaxStackSize(1);
    }

    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (IC2.platform.isSimulating()) {
            IC2.platform.launchGui(entityPlayer, this.getInventory(entityPlayer, itemStack));
        }
        return itemStack;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public IHasGui getInventory(EntityPlayer entityPlayer, ItemStack itemStack) {
        return new HandHeldToolbox(entityPlayer, itemStack, 9);
    }
}

