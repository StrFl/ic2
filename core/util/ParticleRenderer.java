/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$ClientTickEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$Phase
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.EntityFX
 *  net.minecraft.client.renderer.ActiveRenderInfo
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class ParticleRenderer {
    private static final String name = "ic2-particles";
    private boolean lazyAdd = false;
    private final List<EntityFX> particles = new ArrayList<EntityFX>();
    private final List<EntityFX> newParticles = new ArrayList<EntityFX>();

    public ParticleRenderer() {
        MinecraftForge.EVENT_BUS.register((Object)this);
        FMLCommonHandler.instance().bus().register((Object)this);
    }

    public void addEffect(EntityFX particle) {
        if (this.lazyAdd) {
            this.newParticles.add(particle);
        } else {
            this.particles.add(particle);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        this.render(event.partialTicks);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        this.particles.clear();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.update();
        }
    }

    private void update() {
        Minecraft.getMinecraft().mcProfiler.startSection("ic2-particles-update");
        this.lazyAdd = true;
        Iterator<EntityFX> it = this.particles.iterator();
        while (it.hasNext()) {
            EntityFX particle = it.next();
            particle.onUpdate();
            if (!particle.isDead) continue;
            it.remove();
        }
        this.lazyAdd = false;
        this.particles.addAll(this.newParticles);
        this.newParticles.clear();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    private void render(float partialTicks) {
        Minecraft.getMinecraft().mcProfiler.startSection("ic2-particles-render");
        float rotationX = ActiveRenderInfo.rotationX;
        float rotationZ = ActiveRenderInfo.rotationZ;
        float rotationYZ = ActiveRenderInfo.rotationYZ;
        float rotationXY = ActiveRenderInfo.rotationXY;
        float rotationXZ = ActiveRenderInfo.rotationXZ;
        EntityLivingBase player = Minecraft.getMinecraft().renderViewEntity;
        EntityFX.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
        EntityFX.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
        EntityFX.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushAttrib((int)16640);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glAlphaFunc((int)516, (float)0.003921569f);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (EntityFX particle : this.particles) {
            tessellator.setBrightness(particle.getBrightnessForRender(partialTicks));
            particle.renderParticle(tessellator, partialTicks, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
        }
        tessellator.draw();
        GL11.glPopAttrib();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }
}

