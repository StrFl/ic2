/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.item.ItemLeaves
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.ColorizerFoliage
 */
package ic2.core.item.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.ColorizerFoliage;

public class ItemRubLeaves
extends ItemLeaves {
    private final Block block;

    public ItemRubLeaves(Block block) {
        super((BlockLeaves)block);
        this.block = block;
        this.setHasSubtypes(false);
    }

    @SideOnly(value=Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
        return ColorizerFoliage.getFoliageColorBirch();
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(5);
    }

    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return this.getUnlocalizedName();
    }

    public String getItemStackDisplayName(ItemStack itemStack) {
        return StatCollector.translateToLocal((String)this.getUnlocalizedName(itemStack));
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        return this.block.getIcon(0, par1);
    }
}

