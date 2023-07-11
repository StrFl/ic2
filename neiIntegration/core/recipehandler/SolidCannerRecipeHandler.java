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
 *  net.minecraft.item.ItemStack
 *  org.lwjgl.opengl.GL11
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.machine.gui.GuiSolidCanner;
import ic2.core.util.StackUtil;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class SolidCannerRecipeHandler
extends TemplateRecipeHandler {
    int ticks;

    public Class<? extends GuiContainer> getGuiClass() {
        return GuiSolidCanner.class;
    }

    public String getRecipeName() {
        return "Canning Machine";
    }

    public String getRecipeId() {
        return "ic2.solidcanner";
    }

    public String getGuiTexture() {
        return IC2.textureDomain + ":textures/gui/GUISolidCanner.png";
    }

    public String getOverlayIdentifier() {
        return "solidcanner";
    }

    public Map<ICannerBottleRecipeManager.Input, RecipeOutput> getRecipeList() {
        return Recipes.cannerBottle.getRecipes();
    }

    public void drawBackground(int i) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiDraw.changeTexture((String)this.getGuiTexture());
        GuiDraw.drawTexturedModalRect((int)0, (int)0, (int)5, (int)16, (int)140, (int)65);
    }

    public void drawExtras(int i) {
        float f = this.ticks >= 20 ? (float)((this.ticks - 20) % 20) / 20.0f : 0.0f;
        this.drawProgressBar(83, 19, 176, 14, 25, 16, f, 0);
        f = this.ticks <= 20 ? (float)this.ticks / 20.0f : 1.0f;
        this.drawProgressBar(3, 29, 176, 0, 14, 14, f, 3);
    }

    public void onUpdate() {
        super.onUpdate();
        ++this.ticks;
    }

    public void loadTransferRects() {
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(84, 19, 25, 16), this.getRecipeId(), new Object[0]));
    }

    public void loadCraftingRecipes(String outputId, Object ... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (Map.Entry<ICannerBottleRecipeManager.Input, RecipeOutput> entry : this.getRecipeList().entrySet()) {
                this.arecipes.add(new CachedSolidCannerRecipe(entry.getKey().container, entry.getKey().fill, entry.getValue()));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(ItemStack result) {
        block0: for (Map.Entry<ICannerBottleRecipeManager.Input, RecipeOutput> entry : this.getRecipeList().entrySet()) {
            for (ItemStack output : entry.getValue().items) {
                if (!NEIServerUtils.areStacksSameTypeCrafting((ItemStack)output, (ItemStack)result)) continue;
                this.arecipes.add(new CachedSolidCannerRecipe(entry.getKey().container, entry.getKey().fill, entry.getValue()));
                continue block0;
            }
        }
    }

    public void loadUsageRecipes(ItemStack ingredient) {
        for (Map.Entry<ICannerBottleRecipeManager.Input, RecipeOutput> entry : this.getRecipeList().entrySet()) {
            if (!entry.getKey().container.matches(ingredient) && !entry.getKey().fill.matches(ingredient)) continue;
            this.arecipes.add(new CachedSolidCannerRecipe(entry.getKey().container, entry.getKey().fill, entry.getValue()));
        }
    }

    public class CachedSolidCannerRecipe
    extends TemplateRecipeHandler.CachedRecipe {
        public PositionedStack output;
        public List<PositionedStack> ingredients;

        public List<PositionedStack> getIngredients() {
            return this.getCycledIngredients(SolidCannerRecipeHandler.this.cycleticks / 20, this.ingredients);
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public CachedSolidCannerRecipe(IRecipeInput container, IRecipeInput fill, RecipeOutput output1) {
            super((TemplateRecipeHandler)SolidCannerRecipeHandler.this);
            this.ingredients = new ArrayList<PositionedStack>();
            ArrayList<ItemStack> containerItems = new ArrayList<ItemStack>();
            ArrayList<ItemStack> fillItems = new ArrayList<ItemStack>();
            for (ItemStack item : container.getInputs()) {
                containerItems.add(StackUtil.copyWithSize(item, container.getAmount()));
            }
            for (ItemStack item : fill.getInputs()) {
                fillItems.add(StackUtil.copyWithSize(item, fill.getAmount()));
            }
            this.ingredients.add(new PositionedStack(containerItems, 62, 20));
            this.ingredients.add(new PositionedStack(fillItems, 32, 20));
            this.output = new PositionedStack((Object)output1.items.get(0), 111, 20);
        }
    }
}

