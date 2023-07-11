/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.lib.gui.GuiDraw
 *  codechicken.nei.recipe.TemplateRecipeHandler$RecipeTransferRect
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.gui.GuiCentrifuge;
import ic2.neiIntegration.core.recipehandler.MachineRecipeHandler;
import java.awt.Rectangle;
import java.util.Map;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class BlastFurnaceRecipeHandler
extends MachineRecipeHandler {
    @Override
    public void drawBackground(int i) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderItem renderItem = new RenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        GuiDraw.changeTexture((String)this.getGuiTexture());
        GuiDraw.drawTexturedModalRect((int)4, (int)0, (int)15, (int)18, (int)155, (int)65);
        renderItem.renderItemIntoGUI(GuiDraw.fontRenderer, GuiDraw.renderEngine, Ic2Items.airCell, 15, 38);
    }

    @Override
    public void drawExtras(int i) {
        float f = this.ticks >= 20 ? (float)((this.ticks - 20) % 20) / 20.0f : 0.0f;
        this.drawProgressBar(64, 16, 176, 51, 27, 27, f, 3);
        this.drawProgressBar(84, 48, 176, 8, 14, 14, 1.0f, 0);
        this.drawProgressBar(59, 51, 176, 0, 23, 8, 1.0f, 0);
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(41, 4, 80, 42), this.getRecipeId(), new Object[0]));
    }

    public Class<? extends GuiContainer> getGuiClass() {
        return GuiCentrifuge.class;
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal((String)"ic2.blockBlastFurnace");
    }

    @Override
    public String getRecipeId() {
        return "ic2.blockBlastFurnace";
    }

    @Override
    public String getGuiTexture() {
        return IC2.textureDomain + ":textures/gui/GUIBlastFurnace.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "blastfurnace";
    }

    @Override
    public Map<IRecipeInput, RecipeOutput> getRecipeList() {
        return Recipes.blastfurance.getRecipes();
    }

    @Override
    protected int getInputPosX() {
        return 24;
    }

    @Override
    protected int getInputPosY() {
        return 15;
    }

    @Override
    protected int getOutputPosX() {
        return 123;
    }

    @Override
    protected int getOutputPosY() {
        return 38;
    }

    @Override
    protected boolean isOutputsVertical() {
        return false;
    }
}

