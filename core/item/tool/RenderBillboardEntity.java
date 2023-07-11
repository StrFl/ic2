/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.util.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBillboardEntity
extends Render {
    private final ResourceLocation texture = new ResourceLocation(IC2.textureDomain, "textures/models/beam.png");

    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        Vector3 vPlayer = new Vector3(player.prevPosX + (player.posX - player.prevPosX) * (double)partialTickTime, player.prevPosY + (player.posY - player.prevPosY) * (double)partialTickTime, player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTickTime);
        Vector3 vEntity = new Vector3(entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTickTime, entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTickTime + (double)entity.getEyeHeight(), entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTickTime);
        Vector3 vForward = vEntity.copy().sub(vPlayer);
        Vector3 vRight = vForward.copy().cross(Vector3.UP).normalize();
        Vector3 vUp = vRight.copy().cross(vForward).normalize();
        double radius = 0.4;
        vRight.scale(radius);
        vUp.scale(radius);
        Vector3 a = vForward.copy().sub(vUp).sub(vRight);
        Vector3 b = vForward.copy().sub(vUp).add(vRight);
        Vector3 c = vForward.copy().add(vUp).add(vRight);
        Vector3 d = vForward.copy().add(vUp).sub(vRight);
        double u = 0.0;
        double v = 0.0;
        double uvSize = 1.0;
        this.bindTexture(this.getEntityTexture(entity));
        GL11.glPushAttrib((int)16640);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)1);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(a.x, a.y, a.z, u, v);
        tessellator.addVertexWithUV(b.x, b.y, b.z, u, v + uvSize);
        tessellator.addVertexWithUV(c.x, c.y, c.z, u + uvSize, v + uvSize);
        tessellator.addVertexWithUV(d.x, d.y, d.z, u + uvSize, v);
        tessellator.draw();
        GL11.glPopAttrib();
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.texture;
    }
}

