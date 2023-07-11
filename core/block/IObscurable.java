/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 */
package ic2.core.block;

import net.minecraft.block.Block;

public interface IObscurable {
    public boolean retexture(int var1, Block var2, int var3, int var4);

    public Block getReferencedBlock(int var1);

    public int getReferencedMeta(int var1);

    public void setColorMultiplier(int var1);

    public void setRenderMask(int var1);
}

