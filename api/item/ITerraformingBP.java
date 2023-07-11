/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package ic2.api.item;

import net.minecraft.world.World;

public interface ITerraformingBP {
    public int getConsume();

    public int getRange();

    public boolean terraform(World var1, int var2, int var3, int var4);
}

