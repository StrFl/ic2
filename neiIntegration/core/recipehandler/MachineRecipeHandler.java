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
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  org.lwjgl.opengl.GL11
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.core.util.StackUtil;
import ic2.neiIntegration.core.PositionedStackIc2;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

public abstract class MachineRecipeHandler
extends TemplateRecipeHandler {
    protected int ticks;

    public abstract String getRecipeName();

    public abstract String getRecipeId();

    public abstract String getGuiTexture();

    public abstract String getOverlayIdentifier();

    public abstract Map<IRecipeInput, RecipeOutput> getRecipeList();

    public void drawBackground(int i) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiDraw.changeTexture((String)this.getGuiTexture());
        GuiDraw.drawTexturedModalRect((int)0, (int)0, (int)5, (int)11, (int)140, (int)65);
    }

    public void drawExtras(int i) {
        float f = this.ticks >= 20 ? (float)((this.ticks - 20) % 20) / 20.0f : 0.0f;
        this.drawProgressBar(74, 23, 176, 14, 25, 16, f, 0);
        f = this.ticks <= 20 ? (float)this.ticks / 20.0f : 1.0f;
        this.drawProgressBar(51, 25, 176, 0, 14, 14, f, 3);
    }

    public void onUpdate() {
        super.onUpdate();
        ++this.ticks;
    }

    public void loadTransferRects() {
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(74, 23, 25, 16), this.getRecipeId(), new Object[0]));
    }

    public void loadCraftingRecipes(String outputId, Object ... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (Map.Entry<IRecipeInput, RecipeOutput> entry : this.getRecipeList().entrySet()) {
                this.arecipes.add(new CachedIORecipe(entry.getKey(), entry.getValue()));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(ItemStack result) {
        block0: for (Map.Entry<IRecipeInput, RecipeOutput> entry : this.getRecipeList().entrySet()) {
            for (ItemStack output : entry.getValue().items) {
                if (!NEIServerUtils.areStacksSameTypeCrafting((ItemStack)output, (ItemStack)result)) continue;
                this.arecipes.add(new CachedIORecipe(entry.getKey(), entry.getValue()));
                continue block0;
            }
        }
    }

    public void loadUsageRecipes(ItemStack ingredient) {
        for (Map.Entry<IRecipeInput, RecipeOutput> entry : this.getRecipeList().entrySet()) {
            if (!entry.getKey().matches(ingredient)) continue;
            this.arecipes.add(new CachedIORecipe(entry.getKey(), entry.getValue()));
        }
    }

    protected int getInputPosX() {
        return 51;
    }

    protected int getInputPosY() {
        return 6;
    }

    protected int getOutputPosX() {
        return 111;
    }

    protected int getOutputPosY() {
        return 24;
    }

    protected boolean isOutputsVertical() {
        return true;
    }

    public class CachedIORecipe
    extends TemplateRecipeHandler.CachedRecipe {
        private final List<PositionedStack> ingredients;
        private final PositionedStack output;
        private final List<PositionedStack> otherStacks;
        final NBTTagCompound meta;

        public List<PositionedStack> getIngredients() {
            return this.getCycledIngredients(MachineRecipeHandler.this.cycleticks / 20, this.ingredients);
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public List<PositionedStack> getOtherStacks() {
            return this.otherStacks;
        }

        public CachedIORecipe(ItemStack input, ItemStack output1) {
            super((TemplateRecipeHandler)MachineRecipeHandler.this);
            this.ingredients = new ArrayList<PositionedStack>();
            this.otherStacks = new ArrayList<PositionedStack>();
            if (input == null) {
                throw new NullPointerException("Input must not be null (recipe " + input + " -> " + output1 + ").");
            }
            if (output1 == null) {
                throw new NullPointerException("Output must not be null (recipe " + input + " -> " + output1 + ").");
            }
            this.ingredients.add(new PositionedStack((Object)input, MachineRecipeHandler.this.getInputPosX(), MachineRecipeHandler.this.getInputPosY()));
            this.output = new PositionedStack((Object)output1, MachineRecipeHandler.this.getOutputPosX(), MachineRecipeHandler.this.getOutputPosY());
            this.meta = null;
        }

        public CachedIORecipe(IRecipeInput input, RecipeOutput output1) {
            super((TemplateRecipeHandler)MachineRecipeHandler.this);
            this.ingredients = new ArrayList<PositionedStack>();
            this.otherStacks = new ArrayList<PositionedStack>();
            if (input == null) {
                throw new NullPointerException("Input must not be null (recipe " + input + " -> " + output1 + ").");
            }
            if (output1 == null) {
                throw new NullPointerException("Output must not be null (recipe " + input + " -> " + output1 + ").");
            }
            if (output1.items.isEmpty()) {
                throw new IllegalArgumentException("Output must not be empty (recipe " + input + " -> " + output1 + ").");
            }
            if (output1.items.contains(null)) {
                throw new IllegalArgumentException("Output must not contain null (recipe " + input + " -> " + output1 + ").");
            }
            ArrayList<ItemStack> items = new ArrayList<ItemStack>();
            for (ItemStack item : input.getInputs()) {
                items.add(StackUtil.copyWithSize(item, input.getAmount()));
            }
            this.ingredients.add(new PositionedStackIc2(items, MachineRecipeHandler.this.getInputPosX(), MachineRecipeHandler.this.getInputPosY()));
            this.output = new PositionedStackIc2(output1.items.get(0), MachineRecipeHandler.this.getOutputPosX(), MachineRecipeHandler.this.getOutputPosY());
            for (int i = 1; i < output1.items.size(); ++i) {
                if (MachineRecipeHandler.this.isOutputsVertical()) {
                    this.otherStacks.add(new PositionedStack((Object)output1.items.get(i), MachineRecipeHandler.this.getOutputPosX(), MachineRecipeHandler.this.getOutputPosY() + i * 18));
                    continue;
                }
                this.otherStacks.add(new PositionedStack((Object)output1.items.get(i), MachineRecipeHandler.this.getOutputPosX() + i * 18, MachineRecipeHandler.this.getOutputPosY()));
            }
            this.meta = output1.metadata;
        }
    }
}

