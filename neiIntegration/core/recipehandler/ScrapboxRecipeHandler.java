/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.lib.gui.GuiDraw
 *  codechicken.nei.NEIServerUtils
 *  codechicken.nei.PositionedStack
 *  codechicken.nei.recipe.TemplateRecipeHandler
 *  codechicken.nei.recipe.TemplateRecipeHandler$CachedRecipe
 *  codechicken.nei.recipe.TemplateRecipeHandler$RecipeTransferRect
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.ItemStack
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class ScrapboxRecipeHandler
extends TemplateRecipeHandler {
    public static DecimalFormat liquidAmountFormat = new DecimalFormat("0.##%");
    public static PositionedStack scrapboxPositionedStack = new PositionedStack((Object)Ic2Items.scrapBox, 51, 24);

    public String getRecipeName() {
        return "Scrapbox";
    }

    public String getRecipeId() {
        return "ic2.scrapbox";
    }

    public String getGuiTexture() {
        return IC2.textureDomain + ":textures/gui/ScrapboxRecipes.png";
    }

    public void drawExtras(int recipe) {
        Float chance = ((CachedScrapboxRecipe)((Object)this.arecipes.get((int)recipe))).chance;
        String costString = liquidAmountFormat.format(chance);
        GuiDraw.drawStringC((String)costString, (int)85, (int)11, (int)-8355712, (boolean)false);
    }

    public void loadTransferRects() {
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(74, 23, 25, 16), this.getRecipeId(), new Object[0]));
    }

    protected List<Map.Entry<ItemStack, Float>> getRecipeList() {
        Vector<Map.Entry<ItemStack, Float>> result = new Vector<Map.Entry<ItemStack, Float>>();
        Map<ItemStack, Float> input = Recipes.scrapboxDrops.getDrops();
        for (Map.Entry<ItemStack, Float> entry : input.entrySet()) {
            result.add(new AbstractMap.SimpleEntry<ItemStack, Float>(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    public void loadCraftingRecipes(String outputId, Object ... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (Map.Entry<ItemStack, Float> irecipe : this.getRecipeList()) {
                this.arecipes.add(new CachedScrapboxRecipe(irecipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(ItemStack result) {
        for (Map.Entry<ItemStack, Float> irecipe : this.getRecipeList()) {
            if (!NEIServerUtils.areStacksSameTypeCrafting((ItemStack)irecipe.getKey(), (ItemStack)result)) continue;
            this.arecipes.add(new CachedScrapboxRecipe(irecipe));
        }
    }

    public void loadUsageRecipes(ItemStack ingredient) {
        if (NEIServerUtils.areStacksSameTypeCrafting((ItemStack)Ic2Items.scrapBox, (ItemStack)ingredient)) {
            for (Map.Entry<ItemStack, Float> irecipe : this.getRecipeList()) {
                this.arecipes.add(new CachedScrapboxRecipe(irecipe));
            }
        }
    }

    public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
        return false;
    }

    public class CachedScrapboxRecipe
    extends TemplateRecipeHandler.CachedRecipe {
        public PositionedStack output;
        public Float chance;

        public PositionedStack getIngredient() {
            return scrapboxPositionedStack;
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public CachedScrapboxRecipe(Map.Entry<ItemStack, Float> entry) {
            super((TemplateRecipeHandler)ScrapboxRecipeHandler.this);
            this.output = new PositionedStack((Object)entry.getKey(), 111, 24);
            this.chance = entry.getValue();
        }
    }
}

