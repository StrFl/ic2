/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  codechicken.nei.api.API
 *  codechicken.nei.api.IConfigureNEI
 *  codechicken.nei.guihook.GuiContainerManager
 *  codechicken.nei.guihook.IContainerTooltipHandler
 *  codechicken.nei.recipe.ICraftingHandler
 *  codechicken.nei.recipe.IUsageHandler
 *  net.minecraft.item.ItemStack
 */
package ic2.neiIntegration.core;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerTooltipHandler;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import ic2.core.Ic2Items;
import ic2.core.block.machine.gui.GuiCompressor;
import ic2.core.block.machine.gui.GuiElecFurnace;
import ic2.core.block.machine.gui.GuiExtractor;
import ic2.core.block.machine.gui.GuiInduction;
import ic2.core.block.machine.gui.GuiIronFurnace;
import ic2.core.block.machine.gui.GuiMacerator;
import ic2.core.block.machine.gui.GuiSolidCanner;
import ic2.neiIntegration.core.ChargeTooltipHandler;
import ic2.neiIntegration.core.recipehandler.AdvRecipeHandler;
import ic2.neiIntegration.core.recipehandler.AdvShapelessRecipeHandler;
import ic2.neiIntegration.core.recipehandler.BlastFurnaceRecipeHandler;
import ic2.neiIntegration.core.recipehandler.BlockCutterRecipeHandler;
import ic2.neiIntegration.core.recipehandler.CentrifugeRecipeHandler;
import ic2.neiIntegration.core.recipehandler.CompressorRecipeHandler;
import ic2.neiIntegration.core.recipehandler.ExtractorRecipeHandler;
import ic2.neiIntegration.core.recipehandler.FluidCannerRecipeHandler;
import ic2.neiIntegration.core.recipehandler.LatheRecipeHandler;
import ic2.neiIntegration.core.recipehandler.MaceratorRecipeHandler;
import ic2.neiIntegration.core.recipehandler.MetalFormerRecipeHandlerCutting;
import ic2.neiIntegration.core.recipehandler.MetalFormerRecipeHandlerExtruding;
import ic2.neiIntegration.core.recipehandler.MetalFormerRecipeHandlerRolling;
import ic2.neiIntegration.core.recipehandler.OreWashingRecipeHandler;
import ic2.neiIntegration.core.recipehandler.ScrapboxRecipeHandler;
import ic2.neiIntegration.core.recipehandler.SolidCannerRecipeHandler;
import net.minecraft.item.ItemStack;

public class NEIIC2Config
implements IConfigureNEI {
    public void loadConfig() {
        System.out.println("IC2 NEI Submodule initialized");
        API.registerRecipeHandler((ICraftingHandler)new AdvRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new AdvRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new AdvShapelessRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new AdvShapelessRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new MaceratorRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new MaceratorRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new ExtractorRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new ExtractorRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new CompressorRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new CompressorRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new ScrapboxRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new ScrapboxRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new MetalFormerRecipeHandlerExtruding());
        API.registerUsageHandler((IUsageHandler)new MetalFormerRecipeHandlerExtruding());
        API.registerRecipeHandler((ICraftingHandler)new MetalFormerRecipeHandlerCutting());
        API.registerUsageHandler((IUsageHandler)new MetalFormerRecipeHandlerCutting());
        API.registerRecipeHandler((ICraftingHandler)new MetalFormerRecipeHandlerRolling());
        API.registerUsageHandler((IUsageHandler)new MetalFormerRecipeHandlerRolling());
        API.registerRecipeHandler((ICraftingHandler)new CentrifugeRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new CentrifugeRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new BlockCutterRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new BlockCutterRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new OreWashingRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new OreWashingRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new SolidCannerRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new SolidCannerRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new BlastFurnaceRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new BlastFurnaceRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new LatheRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new LatheRecipeHandler());
        API.registerRecipeHandler((ICraftingHandler)new FluidCannerRecipeHandler());
        API.registerUsageHandler((IUsageHandler)new FluidCannerRecipeHandler());
        API.registerGuiOverlay(GuiMacerator.class, (String)"macerator", (int)5, (int)11);
        API.registerGuiOverlay(GuiExtractor.class, (String)"extractor", (int)5, (int)11);
        API.registerGuiOverlay(GuiCompressor.class, (String)"compressor", (int)5, (int)11);
        API.registerGuiOverlay(GuiIronFurnace.class, (String)"smelting", (int)5, (int)11);
        API.registerGuiOverlay(GuiElecFurnace.class, (String)"smelting", (int)5, (int)11);
        API.registerGuiOverlay(GuiInduction.class, (String)"smelting", (int)-4, (int)11);
        API.registerGuiOverlay(GuiSolidCanner.class, (String)"solidcanner", (int)5, (int)16);
        GuiContainerManager.addTooltipHandler((IContainerTooltipHandler)new ChargeTooltipHandler());
        API.hideItem((ItemStack)Ic2Items.mugBooze);
        this.addSubsets();
    }

    public void addSubsets() {
    }

    public String getName() {
        return "IndustrialCraft 2";
    }

    public String getVersion() {
        return "2.2.828-experimental";
    }
}

