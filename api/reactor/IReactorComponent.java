/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.api.reactor;

import ic2.api.reactor.IReactor;
import net.minecraft.item.ItemStack;

public interface IReactorComponent {
    public void processChamber(IReactor var1, ItemStack var2, int var3, int var4, boolean var5);

    public boolean acceptUraniumPulse(IReactor var1, ItemStack var2, ItemStack var3, int var4, int var5, int var6, int var7, boolean var8);

    public boolean canStoreHeat(IReactor var1, ItemStack var2, int var3, int var4);

    public int getMaxHeat(IReactor var1, ItemStack var2, int var3, int var4);

    public int getCurrentHeat(IReactor var1, ItemStack var2, int var3, int var4);

    public int alterHeat(IReactor var1, ItemStack var2, int var3, int var4, int var5);

    public float influenceExplosion(IReactor var1, ItemStack var2);
}

