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
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.machine.gui.GuiCentrifuge;
import ic2.neiIntegration.core.recipehandler.MachineRecipeHandler;
import java.awt.Rectangle;
import java.util.Map;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class CentrifugeRecipeHandler
extends MachineRecipeHandler {
    @Override
    public void drawBackground(int i) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiDraw.changeTexture((String)this.getGuiTexture());
        GuiDraw.drawTexturedModalRect((int)4, (int)0, (int)3, (int)14, (int)146, (int)68);
    }

    @Override
    public void drawExtras(int i) {
        float f = this.ticks >= 20 ? (float)((this.ticks - 20) % 20) / 20.0f : 0.0f;
        this.drawProgressBar(84, 10, 176, 50, 5, 30, f, 3);
        f = this.ticks <= 20 ? (float)this.ticks / 20.0f : 1.0f;
        this.drawProgressBar(12, 28, 176, 0, 14, 14, f, 3);
        this.drawProgressBar(93, 48, 176, 36, 14, 14, 1.0f, 0);
        this.drawProgressBar(68, 52, 176, 28, 23, 8, 1.0f, 0);
        MachineRecipeHandler.CachedIORecipe recipe = (MachineRecipeHandler.CachedIORecipe)((Object)this.arecipes.get(i));
        Integer amount = null;
        if (recipe.meta != null && recipe.meta.hasKey("minHeat")) {
            amount = recipe.meta.getInteger("minHeat");
        }
        GuiDraw.drawString((String)"heat:", (int)35, (int)30, (int)0, (boolean)false);
        GuiDraw.drawString((String)(amount + ""), (int)35, (int)46, (int)0, (boolean)false);
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
        return StatCollector.translateToLocal((String)"ic2.blockCentrifuge");
    }

    @Override
    public String getRecipeId() {
        return "ic2.blockCentrifuge";
    }

    @Override
    public String getGuiTexture() {
        return IC2.textureDomain + ":textures/gui/GUITermalCentrifuge.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "centrifuge";
    }

    @Override
    public Map<IRecipeInput, RecipeOutput> getRecipeList() {
        return Recipes.centrifuge.getRecipes();
    }

    @Override
    protected int getInputPosX() {
        return 12;
    }

    @Override
    protected int getInputPosY() {
        return 7;
    }

    @Override
    protected int getOutputPosX() {
        return 125;
    }

    @Override
    protected int getOutputPosY() {
        return 4;
    }
}

