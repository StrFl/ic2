/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.util.IIcon
 */
package ic2.core.crop;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.crops.CropCard;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public abstract class Ic2CropCard
extends CropCard {
    @Override
    public String owner() {
        return "IC2";
    }

    @Override
    public String displayName() {
        return "ic2.crop." + this.name();
    }

    @Override
    public String discoveredBy() {
        return "IC2 Team";
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerSprites(IIconRegister iconRegister) {
        this.textures = new IIcon[this.maxSize()];
        for (int i = 1; i <= this.textures.length; ++i) {
            this.textures[i - 1] = iconRegister.registerIcon("ic2:crop/" + this.name() + "." + i);
        }
    }
}

