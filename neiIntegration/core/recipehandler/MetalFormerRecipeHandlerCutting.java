/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.lib.gui.GuiDraw
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.lib.gui.GuiDraw;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.neiIntegration.core.recipehandler.MetalFormerRecipeHandler;
import java.util.Map;

public class MetalFormerRecipeHandlerCutting
extends MetalFormerRecipeHandler {
    @Override
    public void drawExtras(int i) {
        float f = this.ticks >= 20 ? (float)((this.ticks - 20) % 20) / 20.0f : 0.0f;
        this.drawProgressBar(46, 30, 177, 14, 51, 12, f, 0);
        f = this.ticks <= 20 ? (float)this.ticks / 20.0f : 1.0f;
        this.drawProgressBar(12, 30, 176, 0, 14, 14, f, 3);
        GuiDraw.drawStringC((String)"Mode: Cutting", (int)80, (int)8, (int)0, (boolean)false);
    }

    @Override
    public Map<IRecipeInput, RecipeOutput> getRecipeList() {
        return Recipes.metalformerCutting.getRecipes();
    }
}

