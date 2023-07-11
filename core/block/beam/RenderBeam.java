/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.renderer.ActiveRenderInfo
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.beam;

import ic2.core.IC2;
import ic2.core.block.beam.EntityParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBeam
extends Render {
    private final ResourceLocation texture = new ResourceLocation(IC2.textureDomain, "textures/models/beam.png");

    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        if (!(entity instanceof EntityParticle)) {
            return;
        }
        EntityParticle particle = (EntityParticle)entity;
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        double playerX = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTickTime;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * (double)partialTickTime;
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTickTime;
        double particleX = particle.prevPosX + (particle.posX - particle.prevPosX) * (double)partialTickTime - playerX;
        double particleY = particle.prevPosY + (particle.posY - particle.prevPosY) * (double)partialTickTime - playerY;
        double particleZ = particle.prevPosZ + (particle.posZ - particle.prevPosZ) * (double)partialTickTime - playerZ;
        double u1 = 0.0;
        double u2 = 1.0;
        double v1 = 0.0;
        double v2 = 1.0;
        double scale = 0.1;
        this.bindTexture(this.getEntityTexture(entity));
        Tessellator tessellator = Tessellator.instance;
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)3042);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(particleX - (double)(ActiveRenderInfo.rotationX + ActiveRenderInfo.rotationYZ) * scale, particleY - (double)ActiveRenderInfo.rotationXZ * scale, particleZ - (double)(ActiveRenderInfo.rotationZ + ActiveRenderInfo.rotationXY) * scale, u2, v2);
        tessellator.addVertexWithUV(particleX - (double)(ActiveRenderInfo.rotationX - ActiveRenderInfo.rotationYZ) * scale, particleY + (double)ActiveRenderInfo.rotationXZ * scale, particleZ - (double)(ActiveRenderInfo.rotationZ - ActiveRenderInfo.rotationXY) * scale, u2, v1);
        tessellator.addVertexWithUV(particleX + (double)(ActiveRenderInfo.rotationX + ActiveRenderInfo.rotationYZ) * scale, particleY + (double)ActiveRenderInfo.rotationXZ * scale, particleZ + (double)(ActiveRenderInfo.rotationZ + ActiveRenderInfo.rotationXY) * scale, u1, v1);
        tessellator.addVertexWithUV(particleX + (double)(ActiveRenderInfo.rotationX - ActiveRenderInfo.rotationYZ) * scale, particleY - (double)ActiveRenderInfo.rotationXZ * scale, particleZ + (double)(ActiveRenderInfo.rotationZ - ActiveRenderInfo.rotationXY) * scale, u1, v2);
        tessellator.draw();
        GL11.glDisable((int)3042);
        GL11.glDepthMask((boolean)true);
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.texture;
    }
}

