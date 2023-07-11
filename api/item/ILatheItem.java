/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 */
package ic2.api.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ICustomDamageItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface ILatheItem {
    public int getWidth(ItemStack var1);

    public int[] getCurrentState(ItemStack var1);

    public void setState(ItemStack var1, int var2, int var3);

    public ItemStack getOutputItem(ItemStack var1, int var2);

    public float getOutputChance(ItemStack var1, int var2);

    @SideOnly(value=Side.CLIENT)
    public ResourceLocation getTexture(ItemStack var1);

    public int getHardness(ItemStack var1);

    public static interface ILatheTool
    extends ICustomDamageItem {
        public int getHardness(ItemStack var1);
    }
}

