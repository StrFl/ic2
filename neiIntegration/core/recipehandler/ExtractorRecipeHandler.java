/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 */
package ic2.neiIntegration.core.recipehandler;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.machine.gui.GuiExtractor;
import ic2.neiIntegration.core.recipehandler.MachineRecipeHandler;
import java.util.Map;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ExtractorRecipeHandler
extends MachineRecipeHandler {
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiExtractor.class;
    }

    @Override
    public String getRecipeName() {
        return "Extractor";
    }

    @Override
    public String getRecipeId() {
        return "ic2.extractor";
    }

    @Override
    public String getGuiTexture() {
        return IC2.textureDomain + ":textures/gui/GUIExtractor.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "extractor";
    }

    @Override
    public Map<IRecipeInput, RecipeOutput> getRecipeList() {
        return Recipes.extractor.getRecipes();
    }
}

