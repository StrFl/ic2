/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.nei.PositionedStack
 *  codechicken.nei.recipe.TemplateRecipeHandler
 *  codechicken.nei.recipe.TemplateRecipeHandler$CachedRecipe
 *  net.minecraft.client.Minecraft
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 *  org.lwjgl.opengl.GL11
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.api.item.ILatheItem;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.machine.gui.GuiLathe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class LatheRecipeHandler
extends TemplateRecipeHandler {
    public String getRecipeName() {
        return StatCollector.translateToLocal((String)"ic2.Lathe.gui.name");
    }

    public String getGuiTexture() {
        return IC2.textureDomain + ":textures/gui/GUILathe.png";
    }

    public void loadCraftingRecipes(ItemStack result) {
        if (result != null && result.getItem() instanceof ILatheItem) {
            this.arecipes.add(new CachedLatheRecipe(result));
        }
    }

    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient != null && ingredient.getItem() instanceof ILatheItem) {
            this.arecipes.add(new CachedLatheRecipe(ingredient));
        }
    }

    public void drawForeground(int recipe) {
        super.drawForeground(recipe);
        CachedLatheRecipe r = (CachedLatheRecipe)((Object)this.arecipes.get(recipe));
        ItemStack stack = ((CachedLatheRecipe)r).output.item;
        ILatheItem item = (ILatheItem)stack.getItem();
        int[] state = item.getCurrentState(stack);
        int segLength = 24;
        int max = item.getWidth(stack);
        GL11.glPushMatrix();
        for (int i = 0; i < 5; ++i) {
            Minecraft.getMinecraft().fontRenderer.drawString(StatCollector.translateToLocalFormatted((String)"ic2.Lathe.gui.info", (Object[])new Object[]{state[i], max}), 35 + segLength * i, 16, 0x404040);
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiLathe.renderILatheItemIntoGUI(stack, 35, 34);
        GL11.glPopMatrix();
    }

    public class CachedLatheRecipe
    extends TemplateRecipeHandler.CachedRecipe {
        private final PositionedStack output;
        private final List<PositionedStack> otherStacks;

        public PositionedStack getResult() {
            return this.output;
        }

        public List<PositionedStack> getOtherStacks() {
            return this.otherStacks;
        }

        public CachedLatheRecipe(ItemStack stack) {
            super((TemplateRecipeHandler)LatheRecipeHandler.this);
            this.otherStacks = new ArrayList<PositionedStack>();
            if (stack == null) {
                throw new NullPointerException("Stack must not be null.");
            }
            this.output = new PositionedStack((Object)stack, 5, 1);
            this.otherStacks.add(new PositionedStack((Object)Ic2Items.LathingTool, 5, 19));
            this.otherStacks.add(new PositionedStack((Object)((ILatheItem)stack.getItem()).getOutputItem(stack, 0), 5, 46));
        }
    }
}

