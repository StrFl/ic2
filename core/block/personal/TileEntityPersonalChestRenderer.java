/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block.personal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.personal.ModelPersonalChest;
import ic2.core.block.personal.TileEntityPersonalChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class TileEntityPersonalChestRenderer
extends TileEntitySpecialRenderer {
    private final ModelPersonalChest model = new ModelPersonalChest();
    private static final ResourceLocation texture = new ResourceLocation(IC2.textureDomain, "textures/models/newsafe.png");

    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick) {
        int angle;
        if (!(tile instanceof TileEntityPersonalChest)) {
            return;
        }
        TileEntityPersonalChest safe = (TileEntityPersonalChest)tile;
        this.bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glEnable((int)32826);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glTranslatef((float)((float)x), (float)((float)y + 1.0f), (float)((float)z + 1.0f));
        GL11.glScalef((float)1.0f, (float)-1.0f, (float)-1.0f);
        GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.5f);
        switch (safe.getFacing()) {
            case 2: {
                angle = 180;
                break;
            }
            case 4: {
                angle = 90;
                break;
            }
            case 5: {
                angle = -90;
                break;
            }
            default: {
                angle = 0;
            }
        }
        GL11.glRotatef((float)angle, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)-0.5f);
        float lidAngle = safe.prevLidAngle + (safe.lidAngle - safe.prevLidAngle) * partialTick;
        lidAngle = 1.0f - lidAngle;
        lidAngle = 1.0f - lidAngle * lidAngle * lidAngle;
        this.model.door.rotateAngleY = lidAngle * (float)Math.PI / 2.0f;
        this.model.renderAll();
        GL11.glDisable((int)32826);
        GL11.glPopMatrix();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

