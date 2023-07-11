/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.lib.gui.GuiDraw
 *  codechicken.nei.recipe.TemplateRecipeHandler$RecipeTransferRect
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.core.IC2;
import ic2.core.block.machine.gui.GuiMetalFormer;
import ic2.neiIntegration.core.recipehandler.MachineRecipeHandler;
import java.awt.Rectangle;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public abstract class MetalFormerRecipeHandler
extends MachineRecipeHandler {
    @Override
    public void drawBackground(int i) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiDraw.changeTexture((String)this.getGuiTexture());
        GuiDraw.drawTexturedModalRect((int)0, (int)0, (int)5, (int)6, (int)146, (int)70);
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(47, 33, 46, 9), this.getRecipeId(), new Object[0]));
    }

    public Class<? extends GuiContainer> getGuiClass() {
        return GuiMetalFormer.class;
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal((String)"ic2.blockMetalFormer");
    }

    @Override
    public String getRecipeId() {
        return "ic2.MetalFormer";
    }

    @Override
    public String getGuiTexture() {
        return IC2.textureDomain + ":textures/gui/GUIMetalFormer.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "metalformer";
    }

    @Override
    protected int getInputPosX() {
        return 12;
    }

    @Override
    protected int getInputPosY() {
        return 11;
    }

    @Override
    protected int getOutputPosX() {
        return 112;
    }

    @Override
    protected int getOutputPosY() {
        return 30;
    }
}

