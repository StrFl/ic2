/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.entity.RenderBoat
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.item.EntityIC2Boat;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;

@SideOnly(value=Side.CLIENT)
public class RenderIC2Boat
extends RenderBoat {
    protected ResourceLocation getEntityTexture(EntityBoat entity) {
        return new ResourceLocation(IC2.textureDomain, ((EntityIC2Boat)entity).getTexture());
    }
}

