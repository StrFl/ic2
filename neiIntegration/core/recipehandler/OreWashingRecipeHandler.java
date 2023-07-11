/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.lib.gui.GuiDraw
 *  codechicken.nei.recipe.GuiRecipe
 *  codechicken.nei.recipe.TemplateRecipeHandler$RecipeTransferRect
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  org.lwjgl.opengl.GL11
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.machine.gui.GuiOreWashing;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import ic2.neiIntegration.core.recipehandler.MachineRecipeHandler;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class OreWashingRecipeHandler
extends MachineRecipeHandler {
    @Override
    public void drawBackground(int i) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiDraw.changeTexture((String)this.getGuiTexture());
        GuiDraw.drawTexturedModalRect((int)70, (int)0, (int)58, (int)14, (int)88, (int)65);
        Integer reqWater = this.getreqWater(i);
        if (reqWater != null) {
            this.drawLiquid(new FluidStack(FluidRegistry.WATER, reqWater.intValue()), 76, i);
        }
    }

    private void drawLiquid(FluidStack stack, int x, int recipe) {
        int y = 10;
        IIcon fluidIcon = stack.getFluid().getIcon(stack);
        GuiDraw.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        int liquidHeight = (int)((float)stack.amount / 8000.0f * 47.0f);
        DrawUtil.drawRepeated(fluidIcon, x, 57 - liquidHeight, 12.0, liquidHeight, GuiDraw.gui.getZLevel());
        GuiDraw.changeTexture((String)this.getGuiTexture());
        GuiDraw.drawTexturedModalRect((int)x, (int)9, (int)176, (int)69, (int)12, (int)47);
    }

    private Integer getreqWater(int i) {
        MachineRecipeHandler.CachedIORecipe recipe = (MachineRecipeHandler.CachedIORecipe)((Object)this.arecipes.get(i));
        if (recipe.meta != null && recipe.meta.hasKey("amount")) {
            return recipe.meta.getInteger("amount");
        }
        return null;
    }

    @Override
    public void drawExtras(int i) {
        float f = (float)(this.ticks % 20) / 20.0f;
        this.drawProgressBar(114, 24, 176, 117, 20, 19, f, 0);
        GuiDraw.drawStringC((String)"require: ", (int)35, (int)20, (int)0, (boolean)false);
        GuiDraw.drawStringC((String)"water", (int)35, (int)33, (int)0, (boolean)false);
        Integer reqWater = this.getreqWater(i);
        GuiDraw.drawStringC((String)(reqWater + "mb"), (int)35, (int)46, (int)0, (boolean)false);
        if (reqWater != null) {
            this.drawLiquidTooltip(new FluidStack(FluidRegistry.WATER, reqWater.intValue()), 76, i);
        }
    }

    private void drawLiquidTooltip(FluidStack stack, int x, int recipe) {
        int y = 15;
        GuiRecipe gui = (GuiRecipe)Minecraft.getMinecraft().currentScreen;
        Point mouse = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipe);
        String tooltip = stack.getLocalizedName() + " (" + stack.amount + "mb)";
        GuiTooltipHelper.drawAreaTooltip(mouse.x - (gui.width - 176) / 2 - offset.x, mouse.y - (gui.height - 176) / 2 - offset.y, tooltip, x, 15, x + 12, 62);
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(113, 23, 22, 21), this.getRecipeId(), new Object[0]));
    }

    public Class<? extends GuiContainer> getGuiClass() {
        return GuiOreWashing.class;
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal((String)"ic2.blockOreWashingPlant");
    }

    @Override
    public String getRecipeId() {
        return "ic2.blockOreWashingPlant";
    }

    @Override
    public String getGuiTexture() {
        return IC2.textureDomain + ":textures/gui/GUIOreWashingPlant.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "oreWashing";
    }

    @Override
    public Map<IRecipeInput, RecipeOutput> getRecipeList() {
        return Recipes.oreWashing.getRecipes();
    }

    @Override
    protected int getInputPosX() {
        return 116;
    }

    @Override
    protected int getInputPosY() {
        return 3;
    }

    @Override
    protected int getOutputPosX() {
        return 98;
    }

    @Override
    protected int getOutputPosY() {
        return 48;
    }

    @Override
    protected boolean isOutputsVertical() {
        return false;
    }
}

