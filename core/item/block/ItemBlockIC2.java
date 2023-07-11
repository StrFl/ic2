/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 */
package ic2.core.item.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import ic2.core.block.BlockBase;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockIC2
extends ItemBlock {
    protected final Block block;

    public ItemBlockIC2(Block block) {
        super(block);
        this.block = block;
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName();
    }

    public String getUnlocalizedName(ItemStack itemStack) {
        return this.getUnlocalizedName();
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return StatCollector.translateToLocal((String)this.getUnlocalizedName(stack));
    }

    public float getDigSpeed(ItemStack stack, Block par2Block, int meta) {
        return this.canHarvestBlock(par2Block, stack) ? 1.01f : 1.0f;
    }

    public boolean canHarvestBlock(Block aBlock, ItemStack stack) {
        return StackUtil.equals(aBlock, Ic2Items.scaffold) || StackUtil.equals(aBlock, Ic2Items.ironScaffold);
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        if (this.block instanceof BlockBase) {
            return ((BlockBase)this.block).getRarity(stack);
        }
        return super.getRarity(stack);
    }
}

