/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 */
package ic2.core.block.personal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(value=Side.CLIENT)
public class ModelPersonalChest
extends ModelBase {
    ModelRenderer wallR;
    ModelRenderer wallL;
    ModelRenderer wallB;
    ModelRenderer wallU;
    ModelRenderer wallD;
    public ModelRenderer door;

    public ModelPersonalChest() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.wallR = new ModelRenderer((ModelBase)this, 0, 0);
        this.wallR.addBox(0.0f, 0.0f, 0.0f, 14, 16, 1);
        this.wallR.setRotationPoint(1.0f, 0.0f, 15.0f);
        this.wallR.setTextureSize(64, 64);
        this.wallR.mirror = true;
        ModelPersonalChest.setRotation(this.wallR, 0.0f, 1.570796f, 0.0f);
        this.wallL = new ModelRenderer((ModelBase)this, 0, 0);
        this.wallL.addBox(0.0f, 0.0f, 0.0f, 14, 16, 1);
        this.wallL.setRotationPoint(15.0f, 0.0f, 1.0f);
        this.wallL.setTextureSize(64, 64);
        this.wallL.mirror = true;
        ModelPersonalChest.setRotation(this.wallL, 0.0f, -1.570796f, 0.0f);
        this.wallB = new ModelRenderer((ModelBase)this, 1, 1);
        this.wallB.addBox(1.0f, 1.0f, 0.0f, 12, 14, 1);
        this.wallB.setRotationPoint(15.0f, 0.0f, 15.0f);
        this.wallB.setTextureSize(64, 64);
        this.wallB.mirror = true;
        ModelPersonalChest.setRotation(this.wallB, 0.0f, 3.141593f, 0.0f);
        this.wallU = new ModelRenderer((ModelBase)this, 1, 17);
        this.wallU.addBox(1.0f, 0.0f, 0.0f, 12, 14, 1);
        this.wallU.setRotationPoint(1.0f, 0.0f, 15.0f);
        this.wallU.setTextureSize(64, 64);
        this.wallU.mirror = true;
        ModelPersonalChest.setRotation(this.wallU, -1.570796f, 0.0f, 0.0f);
        this.wallD = new ModelRenderer((ModelBase)this, 1, 17);
        this.wallD.addBox(1.0f, 0.0f, 0.0f, 12, 14, 1);
        this.wallD.setRotationPoint(15.0f, 15.0f, 1.0f);
        this.wallD.setTextureSize(64, 64);
        this.wallD.mirror = true;
        ModelPersonalChest.setRotation(this.wallD, -1.570796f, 3.141593f, 0.0f);
        this.door = new ModelRenderer((ModelBase)this, 30, 0);
        this.door.addBox(0.0f, 0.0f, 0.0f, 12, 14, 1);
        this.door.setRotationPoint(2.0f, 1.0f, 2.0f);
        this.door.setTextureSize(64, 64);
        this.door.mirror = true;
    }

    public void renderAll() {
        this.wallR.render(0.0625f);
        this.wallL.render(0.0625f);
        this.wallB.render(0.0625f);
        this.wallU.render(0.0625f);
        this.wallD.render(0.0625f);
        this.door.render(0.0625f);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

