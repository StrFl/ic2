/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.RenderBlocks
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.block.EntityIC2Explosive;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderExplosiveBlock
extends Render {
    public RenderBlocks blockRenderer = new RenderBlocks();

    public RenderExplosiveBlock() {
        this.shadowSize = 0.5f;
    }

    public void render(EntityIC2Explosive entitytntprimed, double x, double y, double z, float f, float f1) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)x), (float)((float)y), (float)((float)z));
        if ((float)entitytntprimed.fuse - f1 + 1.0f < 10.0f) {
            float scale = 1.0f - ((float)entitytntprimed.fuse - f1 + 1.0f) / 10.0f;
            if (scale < 0.0f) {
                scale = 0.0f;
            }
            if (scale > 1.0f) {
                scale = 1.0f;
            }
            scale *= scale;
            scale *= scale;
            scale = 1.0f + scale * 0.3f;
            GL11.glScalef((float)scale, (float)scale, (float)scale);
        }
        float f3 = (1.0f - ((float)entitytntprimed.fuse - f1 + 1.0f) / 100.0f) * 0.8f;
        this.bindTexture(TextureMap.locationBlocksTexture);
        this.blockRenderer.renderBlockAsItem(entitytntprimed.renderBlock, 0, entitytntprimed.getBrightness(f1));
        if (entitytntprimed.fuse / 5 % 2 == 0) {
            GL11.glDisable((int)3553);
            GL11.glDisable((int)2896);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)772);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)f3);
            this.blockRenderer.renderBlockAsItem(entitytntprimed.renderBlock, 0, 1.0f);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glDisable((int)3042);
            GL11.glEnable((int)2896);
            GL11.glEnable((int)3553);
        }
        GL11.glPopMatrix();
    }

    public void doRender(Entity entity, double x, double y, double z, float f, float f1) {
        this.render((EntityIC2Explosive)entity, x, y, z, f, f1);
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}

