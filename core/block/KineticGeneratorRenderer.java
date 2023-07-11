/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  org.lwjgl.opengl.GL11
 */
package ic2.core.block;

import ic2.core.block.KineticGeneratorRotor;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWindKineticGenerator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class KineticGeneratorRenderer
extends TileEntitySpecialRenderer {
    private static final Map<Integer, ModelBase> rotorModels = new HashMap<Integer, ModelBase>();

    public void renderBlockRotor(TileEntityInventory tileEntity, World world, int posX, int posY, int posZ) {
        ResourceLocation rotorRL;
        float angle;
        int diameter;
        TileEntityInventory te;
        if (tileEntity instanceof TileEntityWindKineticGenerator) {
            te = (TileEntityWindKineticGenerator)tileEntity;
            diameter = ((TileEntityWindKineticGenerator)te).getRotorDiameter();
            angle = ((TileEntityWindKineticGenerator)te).getAngle();
            rotorRL = ((TileEntityWindKineticGenerator)te).getRotorRenderTexture();
        } else if (tileEntity instanceof TileEntityWaterKineticGenerator) {
            te = (TileEntityWaterKineticGenerator)tileEntity;
            diameter = ((TileEntityWaterKineticGenerator)te).getRotorDiameter();
            angle = ((TileEntityWaterKineticGenerator)te).getAngle();
            rotorRL = ((TileEntityWaterKineticGenerator)te).getRotorRenderTexture();
        } else {
            return;
        }
        if (diameter == 0) {
            return;
        }
        ModelBase model = rotorModels.get(diameter);
        if (model == null) {
            model = new KineticGeneratorRotor(diameter);
            rotorModels.put(diameter, model);
        }
        Tessellator tessellator = Tessellator.instance;
        short facing = tileEntity.getFacing();
        float brightness = world.getBlockLightValue(posX, posY, posZ);
        int skyBrightness = 0;
        switch (facing) {
            case 2: {
                skyBrightness = world.getLightBrightnessForSkyBlocks(posX, posY, posZ - 1, 0);
                break;
            }
            case 3: {
                skyBrightness = world.getLightBrightnessForSkyBlocks(posX, posY, posZ + 1, 0);
                break;
            }
            case 4: {
                skyBrightness = world.getLightBrightnessForSkyBlocks(posX - 1, posY, posZ, 0);
                break;
            }
            case 5: {
                skyBrightness = world.getLightBrightnessForSkyBlocks(posX + 1, posY, posZ, 0);
            }
        }
        int skyBrightness1 = skyBrightness % 65536;
        int skyBrightness2 = skyBrightness / 65536;
        tessellator.setColorOpaque_F(brightness, brightness, brightness);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)skyBrightness1, (float)skyBrightness2);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.5f);
        if (facing == 2 || facing == 3 || facing == 4 || facing == 5) {
            int dir = facing == 4 ? 0 : (facing == 2 ? 1 : (facing == 5 ? 2 : (int)facing));
            GL11.glRotatef((float)((float)dir * -90.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        } else if (facing == 1) {
            GL11.glRotatef((float)-90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        }
        GL11.glRotatef((float)angle, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glTranslatef((float)-0.2f, (float)0.0f, (float)0.0f);
        this.bindTexture(rotorRL);
        model.render(null, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }

    public void renderTileEntityAt(TileEntity tileEntity, double posX, double posY, double posZ, float partialTickTime) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)((float)posX), (float)((float)posY), (float)((float)posZ));
        TileEntityInventory tileEntityWindmill = (TileEntityInventory)tileEntity;
        this.renderBlockRotor(tileEntityWindmill, tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        GL11.glPopMatrix();
    }
}

