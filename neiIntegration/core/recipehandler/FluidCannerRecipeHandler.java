/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.lib.gui.GuiDraw
 *  codechicken.nei.PositionedStack
 *  codechicken.nei.recipe.GuiRecipe
 *  codechicken.nei.recipe.TemplateRecipeHandler
 *  codechicken.nei.recipe.TemplateRecipeHandler$CachedRecipe
 *  codechicken.nei.recipe.TemplateRecipeHandler$RecipeTransferRect
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraftforge.fluids.BlockFluidBase
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidContainerItem
 *  org.lwjgl.opengl.GL11
 */
package ic2.neiIntegration.core.recipehandler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ic2.api.recipe.ICannerEnrichRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.machine.gui.GuiCanner;
import ic2.core.item.ItemFluidCell;
import ic2.core.util.DrawUtil;
import ic2.core.util.GuiTooltipHelper;
import ic2.core.util.StackUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import org.lwjgl.opengl.GL11;

public class FluidCannerRecipeHandler
extends TemplateRecipeHandler {
    int ticks;

    public Class<? extends GuiContainer> getGuiClass() {
        return GuiCanner.class;
    }

    public String getRecipeName() {
        return "Fluid Canning Machine";
    }

    public String getRecipeId() {
        return "ic2.fluidcanner";
    }

    public String getGuiTexture() {
        return IC2.textureDomain + ":textures/gui/GUICanner.png";
    }

    public String getOverlayIdentifier() {
        return "fluidcanner";
    }

    public Map<ICannerEnrichRecipeManager.Input, FluidStack> getRecipeList() {
        return Recipes.cannerEnrich.getRecipes();
    }

    public void drawBackground(int i) {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiDraw.changeTexture((String)this.getGuiTexture());
        GuiDraw.drawTexturedModalRect((int)0, (int)20, (int)5, (int)16, (int)140, (int)85);
        CachedFluidCannerRecipe recipe = (CachedFluidCannerRecipe)((Object)this.arecipes.get(i));
        this.drawLiquid(recipe.fluidInput, 38, i);
        this.drawLiquid(recipe.fluidOutput, 116, i);
    }

    private void drawLiquid(FluidStack stack, int x, int recipe) {
        int y = 50;
        IIcon fluidIcon = stack.getFluid().getIcon(stack);
        GuiDraw.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        int liquidHeight = (int)((float)stack.amount / 8000.0f * 47.0f);
        DrawUtil.drawRepeated(fluidIcon, x, 97 - liquidHeight, 12.0, liquidHeight, GuiDraw.gui.getZLevel());
        GuiDraw.changeTexture((String)this.getGuiTexture());
        GuiDraw.drawTexturedModalRect((int)x, (int)50, (int)176, (int)69, (int)12, (int)47);
    }

    public void drawExtras(int i) {
        float f = this.ticks >= 20 ? (float)((this.ticks - 20) % 20) / 20.0f : 0.0f;
        this.drawProgressBar(68, 26, 233, 0, 25, 16, f, 0);
        f = this.ticks <= 20 ? (float)this.ticks / 20.0f : 1.0f;
        this.drawProgressBar(3, 65, 176, 0, 14, 14, f, 3);
        CachedFluidCannerRecipe recipe = (CachedFluidCannerRecipe)((Object)this.arecipes.get(i));
        this.drawLiquidTooltip(recipe.fluidInput, 38, i);
        this.drawLiquidTooltip(recipe.fluidOutput, 116, i);
    }

    private void drawLiquidTooltip(FluidStack stack, int x, int recipe) {
        int y = 55;
        GuiRecipe gui = (GuiRecipe)Minecraft.getMinecraft().currentScreen;
        Point mouse = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipe);
        String tooltip = stack.getLocalizedName() + " (" + stack.amount + "mb)";
        GuiTooltipHelper.drawAreaTooltip(mouse.x - (gui.width - 176) / 2 - offset.x, mouse.y - (gui.height - 176) / 2 - offset.y, tooltip, x, 55, x + 12, 102);
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
            for (Map.Entry<ICannerEnrichRecipeManager.Input, FluidStack> entry : this.getRecipeList().entrySet()) {
                this.arecipes.add(new CachedFluidCannerRecipe(entry.getKey().additive, entry.getKey().fluid, entry.getValue()));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(ItemStack result) {
        FluidStack stack = null;
        if (result.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem)result.getItem();
            stack = container.getFluid(result);
        } else if (result.getItem() instanceof ItemBlock && Block.getBlockFromItem((Item)result.getItem()) instanceof BlockFluidBase) {
            stack = new FluidStack(((BlockFluidBase)Block.getBlockFromItem((Item)result.getItem())).getFluid(), 1000);
        }
        if (stack != null && stack.getFluid() != null) {
            for (Map.Entry<ICannerEnrichRecipeManager.Input, FluidStack> entry : this.getRecipeList().entrySet()) {
                if (!stack.isFluidEqual(entry.getValue())) continue;
                this.arecipes.add(new CachedFluidCannerRecipe(entry.getKey().additive, entry.getKey().fluid, entry.getValue()));
            }
        }
    }

    public void loadUsageRecipes(ItemStack ingredient) {
        FluidStack stack = null;
        if (ingredient.getItem() instanceof IFluidContainerItem) {
            stack = ((IFluidContainerItem)ingredient.getItem()).getFluid(ingredient);
        } else if (ingredient.getItem() instanceof ItemBlock && Block.getBlockFromItem((Item)ingredient.getItem()) instanceof BlockFluidBase) {
            stack = new FluidStack(((BlockFluidBase)Block.getBlockFromItem((Item)ingredient.getItem())).getFluid(), 1000);
        }
        for (Map.Entry<ICannerEnrichRecipeManager.Input, FluidStack> entry : this.getRecipeList().entrySet()) {
            if (!entry.getKey().additive.matches(ingredient) && (stack == null || stack.getFluid() == null || !stack.getFluid().equals(entry.getKey().fluid.getFluid()))) continue;
            this.arecipes.add(new CachedFluidCannerRecipe(entry.getKey().additive, entry.getKey().fluid, entry.getValue()));
        }
    }

    public int recipiesPerPage() {
        return 1;
    }

    public class CachedFluidCannerRecipe
    extends TemplateRecipeHandler.CachedRecipe {
        public PositionedStack output;
        public List<PositionedStack> ingredients;
        private FluidStack fluidInput;
        private FluidStack fluidOutput;

        public List<PositionedStack> getIngredients() {
            return this.getCycledIngredients(FluidCannerRecipeHandler.this.cycleticks / 20, this.ingredients);
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public CachedFluidCannerRecipe(IRecipeInput container, FluidStack fluid, FluidStack output1) {
            super((TemplateRecipeHandler)FluidCannerRecipeHandler.this);
            this.ingredients = new ArrayList<PositionedStack>();
            ArrayList<ItemStack> containerItems = new ArrayList<ItemStack>();
            for (ItemStack item : container.getInputs()) {
                containerItems.add(StackUtil.copyWithSize(item, container.getAmount()));
            }
            this.ingredients.add(new PositionedStack((Object)ItemFluidCell.getUniversalFluidCell(new FluidStack(fluid, fluid.amount)), 36, 21));
            this.ingredients.add(new PositionedStack(containerItems, 75, 48));
            this.output = new PositionedStack((Object)ItemFluidCell.getUniversalFluidCell(new FluidStack(output1, output1.amount)), 114, 21);
            this.fluidInput = fluid;
            this.fluidOutput = output1;
        }
    }
}

