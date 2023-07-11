/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.IFuelHandler
 *  cpw.mods.fml.common.IWorldGenerator
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLMissingMappingsEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.event.FMLServerStartingEvent
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$PlayerLoggedInEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$PlayerLoggedOutEvent
 *  cpw.mods.fml.common.registry.EntityRegistry
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.command.ICommand
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.monster.EntitySkeleton
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.BiomeGenBase
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraft.world.chunk.IChunkProvider
 *  net.minecraft.world.gen.feature.WorldGenMinable
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogColors
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogDensity
 *  net.minecraftforge.client.event.TextureStitchEvent$Post
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingSpawnEvent$SpecialSpawn
 *  net.minecraftforge.event.world.ChunkWatchEvent$Watch
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.oredict.OreDictionary
 *  net.minecraftforge.oredict.OreDictionary$OreRegisterEvent
 *  net.minecraftforge.oredict.RecipeSorter
 *  net.minecraftforge.oredict.RecipeSorter$Category
 *  org.lwjgl.opengl.GL11
 */
package ic2.core;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.energy.EnergyNet;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.tile.ExplosionWhitelist;
import ic2.api.util.Keys;
import ic2.core.AdvRecipe;
import ic2.core.AdvShapelessRecipe;
import ic2.core.CreativeTabIC2;
import ic2.core.ExplosionIC2;
import ic2.core.IC2Achievements;
import ic2.core.IC2BucketHandler;
import ic2.core.IC2DamageSource;
import ic2.core.IC2Loot;
import ic2.core.IC2Potion;
import ic2.core.Ic2Items;
import ic2.core.Platform;
import ic2.core.TickHandler;
import ic2.core.TickrateTracker;
import ic2.core.WorldData;
import ic2.core.audio.AudioManager;
import ic2.core.block.BlockIC2Fluid;
import ic2.core.block.BlockTextureStitched;
import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityItnt;
import ic2.core.block.EntityNuke;
import ic2.core.block.EntityStickyDynamite;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.WorldGenRubTree;
import ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import ic2.core.block.machine.tileentity.TileEntityBlastFurnace;
import ic2.core.block.machine.tileentity.TileEntityBlockCutter;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import ic2.core.block.machine.tileentity.TileEntityCentrifuge;
import ic2.core.block.machine.tileentity.TileEntityCompressor;
import ic2.core.block.machine.tileentity.TileEntityExtractor;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.block.machine.tileentity.TileEntityMacerator;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.block.machine.tileentity.TileEntityMetalFormer;
import ic2.core.block.machine.tileentity.TileEntityOreWashing;
import ic2.core.block.machine.tileentity.TileEntityRecycler;
import ic2.core.command.CommandIc2;
import ic2.core.command.CommandTps;
import ic2.core.crop.IC2Crops;
import ic2.core.energy.EnergyNetGlobal;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.init.Localization;
import ic2.core.init.MainConfig;
import ic2.core.init.Rezepte;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.EntityBoatCarbon;
import ic2.core.item.EntityBoatElectric;
import ic2.core.item.EntityBoatRubber;
import ic2.core.item.EntityIC2Boat;
import ic2.core.item.GatewayElectricItemManager;
import ic2.core.item.ItemScrapbox;
import ic2.core.item.tfbp.ItemTFBPCultivation;
import ic2.core.item.tfbp.ItemTFBPFlatification;
import ic2.core.item.tool.EntityMiningLaser;
import ic2.core.item.tool.EntityParticle;
import ic2.core.network.NetworkManager;
import ic2.core.util.ConfigUtil;
import ic2.core.util.ItemInfo;
import ic2.core.util.Keyboard;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.PriorityExecutor;
import ic2.core.util.SideGateway;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import ic2.core.uu.UuIndex;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import org.lwjgl.opengl.GL11;

@Mod(modid="IC2", name="IndustrialCraft 2", version="2.2.828-experimental", useMetadata=true, certificateFingerprint="de041f9f6187debbc77034a344134053277aa3b0", dependencies="required-after:Forge@[10.13.0.1200,)")
public class IC2
implements IWorldGenerator,
IFuelHandler {
    public static final String VERSION = "2.2.828-experimental";
    public static final String MODID = "IC2";
    private static IC2 instance;
    @SidedProxy(clientSide="ic2.core.PlatformClient", serverSide="ic2.core.Platform")
    public static Platform platform;
    public static SideGateway<NetworkManager> network;
    @SidedProxy(clientSide="ic2.core.util.KeyboardClient", serverSide="ic2.core.util.Keyboard")
    public static Keyboard keyboard;
    @SidedProxy(clientSide="ic2.core.audio.AudioManagerClient", serverSide="ic2.core.audio.AudioManager")
    public static AudioManager audioManager;
    public static Log log;
    public static IC2Achievements achievements;
    public static TickHandler tickHandler;
    public static int cableRenderId;
    public static int fenceRenderId;
    public static int miningPipeRenderId;
    public static int luminatorRenderId;
    public static int cropRenderId;
    public static Random random;
    public static final Map<IRecipeInput, Integer> valuableOres;
    public static boolean suddenlyHoes;
    public static boolean seasonal;
    public static boolean initialized;
    public static final CreativeTabIC2 tabIC2;
    public static final String textureDomain;
    public static final int setBlockNotify = 1;
    public static final int setBlockUpdate = 2;
    public static final int setBlockNoUpdateFromClient = 4;
    public final TickrateTracker tickrateTracker = new TickrateTracker();
    public final PriorityExecutor threadPool = new PriorityExecutor(Math.max(Runtime.getRuntime().availableProcessors(), 2));

    public IC2() {
        instance = this;
        Info.ic2ModInstance = instance;
    }

    public static IC2 getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void load(FMLPreInitializationEvent event) {
        ItemStack tStack;
        long startTime = System.nanoTime();
        log = new Log(event.getModLog());
        log.debug(LogCategory.General, "Starting pre-init.");
        MainConfig.load();
        Localization.preInit(event.getSourceFile());
        tickHandler = new TickHandler();
        for (IRecipeInput input : ConfigUtil.asRecipeInputList(MainConfig.get(), "misc/additionalValuableOres")) {
            IC2.addValuableOre(input, 1);
        }
        audioManager.initialize();
        ElectricItem.manager = new GatewayElectricItemManager();
        ElectricItem.rawManager = new ElectricItemManager();
        ItemInfo itemInfo = new ItemInfo();
        Info.itemEnergy = itemInfo;
        Info.itemFuel = itemInfo;
        Keys.instance = keyboard;
        BlocksItems.init();
        Blocks.obsidian.setResistance(60.0f);
        Blocks.enchanting_table.setResistance(60.0f);
        Blocks.ender_chest.setResistance(60.0f);
        Blocks.anvil.setResistance(60.0f);
        Blocks.water.setResistance(30.0f);
        Blocks.flowing_water.setResistance(30.0f);
        Blocks.lava.setResistance(30.0f);
        ExplosionWhitelist.addWhitelistedBlock(Blocks.bedrock);
        FurnaceRecipes furnaceRecipes = FurnaceRecipes.smelting();
        if (Ic2Items.rubberWood != null) {
            furnaceRecipes.func_151394_a(Ic2Items.rubberWood, new ItemStack(Blocks.log, 1, 3), 0.1f);
        }
        if (Ic2Items.tinOre != null) {
            furnaceRecipes.func_151394_a(Ic2Items.tinOre, Ic2Items.tinIngot, 0.5f);
        }
        if (Ic2Items.copperOre != null) {
            furnaceRecipes.func_151394_a(Ic2Items.copperOre, Ic2Items.copperIngot, 0.5f);
        }
        if (Ic2Items.leadOre != null) {
            furnaceRecipes.func_151394_a(Ic2Items.leadOre, Ic2Items.leadIngot, 0.5f);
        }
        furnaceRecipes.func_151394_a(Ic2Items.ironDust, new ItemStack(Items.iron_ingot, 1), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.goldDust, new ItemStack(Items.gold_ingot, 1), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.tinDust, Ic2Items.tinIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.copperDust, Ic2Items.copperIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.leadDust, Ic2Items.leadIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.silverDust, Ic2Items.silverIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.hydratedCoalDust, Ic2Items.coalDust.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.bronzeDust, Ic2Items.bronzeIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.resin, Ic2Items.rubber.copy(), 0.3f);
        furnaceRecipes.func_151396_a(Ic2Items.mugCoffee.getItem(), new ItemStack(Ic2Items.mugCoffee.getItem(), 1, 1), 0.1f);
        furnaceRecipes.func_151394_a(Ic2Items.crushedIronOre, new ItemStack(Items.iron_ingot, 1), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.crushedGoldOre, new ItemStack(Items.gold_ingot, 1), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.crushedCopperOre, Ic2Items.copperIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.crushedTinOre, Ic2Items.tinIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.crushedLeadOre, Ic2Items.leadIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.crushedSilverOre, Ic2Items.silverIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.purifiedCrushedIronOre, new ItemStack(Items.iron_ingot, 1), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.purifiedCrushedGoldOre, new ItemStack(Items.gold_ingot, 1), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.purifiedCrushedCopperOre, Ic2Items.copperIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.purifiedCrushedTinOre, Ic2Items.tinIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.purifiedCrushedLeadOre, Ic2Items.leadIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.purifiedCrushedSilverOre, Ic2Items.silverIngot.copy(), 0.0f);
        furnaceRecipes.func_151394_a(Ic2Items.rawcrystalmemory, Ic2Items.crystalmemory.copy(), 0.0f);
        ItemScrapbox.init();
        ItemTFBPCultivation.init();
        ItemTFBPFlatification.init();
        TileEntityCanner.init();
        TileEntityCompressor.init();
        TileEntityExtractor.init();
        TileEntityMacerator.init();
        TileEntityRecycler.init();
        TileEntityCentrifuge.init();
        TileEntityMatter.init();
        TileEntityMetalFormer.init();
        TileEntitySemifluidGenerator.init();
        TileEntityOreWashing.init();
        TileEntityFluidHeatGenerator.init();
        TileEntityBlockCutter.init();
        TileEntityBlastFurnace.init();
        TileEntityLiquidHeatExchanger.init();
        EntityIC2Boat.init();
        StackUtil.getBlock(Ic2Items.reinforcedStone).setHarvestLevel("pickaxe", 2);
        StackUtil.getBlock(Ic2Items.reinforcedDoorBlock).setHarvestLevel("pickaxe", 2);
        StackUtil.getBlock(Ic2Items.insulatedCopperCableBlock).setHarvestLevel("axe", 0);
        StackUtil.getBlock(Ic2Items.constructionFoamWall).setHarvestLevel("pickaxe", 1);
        if (Ic2Items.copperOre != null) {
            StackUtil.getBlock(Ic2Items.copperOre).setHarvestLevel("pickaxe", 1);
        }
        if (Ic2Items.tinOre != null) {
            StackUtil.getBlock(Ic2Items.tinOre).setHarvestLevel("pickaxe", 1);
        }
        if (Ic2Items.uraniumOre != null) {
            StackUtil.getBlock(Ic2Items.uraniumOre).setHarvestLevel("pickaxe", 2);
        }
        if (Ic2Items.leadOre != null) {
            StackUtil.getBlock(Ic2Items.leadOre).setHarvestLevel("pickaxe", 1);
        }
        if (Ic2Items.rubberWood != null) {
            StackUtil.getBlock(Ic2Items.rubberWood).setHarvestLevel("axe", 0);
        }
        MinecraftForge.EVENT_BUS.register((Object)this);
        RecipeSorter.register((String)"ic2:shaped", AdvRecipe.class, (RecipeSorter.Category)RecipeSorter.Category.SHAPED, (String)"after:minecraft:shapeless");
        RecipeSorter.register((String)"ic2:shapeless", AdvShapelessRecipe.class, (RecipeSorter.Category)RecipeSorter.Category.SHAPELESS, (String)"after:ic2:shaped");
        for (String oreName : OreDictionary.getOreNames()) {
            for (ItemStack ore : OreDictionary.getOres((String)oreName)) {
                this.registerOre(new OreDictionary.OreRegisterEvent(oreName, ore));
            }
        }
        assert (Ic2Items.bronzeIngot != null);
        assert (Ic2Items.copperIngot != null);
        assert (Ic2Items.tinIngot != null);
        assert (Ic2Items.leadIngot != null);
        assert (Ic2Items.rubber != null);
        if (Ic2Items.copperOre != null) {
            OreDictionary.registerOre((String)"oreCopper", (ItemStack)Ic2Items.copperOre.copy());
        }
        if (Ic2Items.tinOre != null) {
            OreDictionary.registerOre((String)"oreTin", (ItemStack)Ic2Items.tinOre.copy());
        }
        if (Ic2Items.uraniumOre != null) {
            OreDictionary.registerOre((String)"oreUranium", (ItemStack)Ic2Items.uraniumOre.copy());
        }
        if (Ic2Items.leadOre != null) {
            OreDictionary.registerOre((String)"oreLead", (ItemStack)Ic2Items.leadOre.copy());
        }
        if (Ic2Items.rubberLeaves != null) {
            tStack = Ic2Items.rubberLeaves.copy();
            tStack.setItemDamage(Short.MAX_VALUE);
            OreDictionary.registerOre((String)"treeLeaves", (ItemStack)tStack);
        }
        if (Ic2Items.rubberSapling != null) {
            tStack = Ic2Items.rubberSapling.copy();
            tStack.setItemDamage(Short.MAX_VALUE);
            OreDictionary.registerOre((String)"treeSapling", (ItemStack)tStack);
        }
        if (Ic2Items.rubberWood != null) {
            tStack = Ic2Items.rubberWood.copy();
            tStack.setItemDamage(Short.MAX_VALUE);
            OreDictionary.registerOre((String)"woodRubber", (ItemStack)tStack);
        }
        OreDictionary.registerOre((String)"dustStone", (ItemStack)Ic2Items.stoneDust.copy());
        OreDictionary.registerOre((String)"dustBronze", (ItemStack)Ic2Items.bronzeDust.copy());
        OreDictionary.registerOre((String)"dustClay", (ItemStack)Ic2Items.clayDust.copy());
        OreDictionary.registerOre((String)"dustCoal", (ItemStack)Ic2Items.coalDust.copy());
        OreDictionary.registerOre((String)"dustCopper", (ItemStack)Ic2Items.copperDust.copy());
        OreDictionary.registerOre((String)"dustGold", (ItemStack)Ic2Items.goldDust.copy());
        OreDictionary.registerOre((String)"dustIron", (ItemStack)Ic2Items.ironDust.copy());
        OreDictionary.registerOre((String)"dustSilver", (ItemStack)Ic2Items.silverDust.copy());
        OreDictionary.registerOre((String)"dustTin", (ItemStack)Ic2Items.tinDust.copy());
        OreDictionary.registerOre((String)"dustLead", (ItemStack)Ic2Items.leadDust.copy());
        OreDictionary.registerOre((String)"dustObsidian", (ItemStack)Ic2Items.obsidianDust.copy());
        OreDictionary.registerOre((String)"dustLapis", (ItemStack)Ic2Items.lapiDust.copy());
        OreDictionary.registerOre((String)"dustSulfur", (ItemStack)Ic2Items.sulfurDust.copy());
        OreDictionary.registerOre((String)"dustLithium", (ItemStack)Ic2Items.lithiumDust.copy());
        OreDictionary.registerOre((String)"dustDiamond", (ItemStack)Ic2Items.diamondDust.copy());
        OreDictionary.registerOre((String)"dustSiliconDioxide", (ItemStack)Ic2Items.silicondioxideDust.copy());
        OreDictionary.registerOre((String)"dustHydratedCoal", (ItemStack)Ic2Items.hydratedCoalDust.copy());
        OreDictionary.registerOre((String)"dustAshes", (ItemStack)Ic2Items.AshesDust.copy());
        OreDictionary.registerOre((String)"dustTinyCopper", (ItemStack)Ic2Items.smallCopperDust.copy());
        OreDictionary.registerOre((String)"dustTinyGold", (ItemStack)Ic2Items.smallGoldDust.copy());
        OreDictionary.registerOre((String)"dustTinyIron", (ItemStack)Ic2Items.smallIronDust.copy());
        OreDictionary.registerOre((String)"dustTinySilver", (ItemStack)Ic2Items.smallSilverDust.copy());
        OreDictionary.registerOre((String)"dustTinyTin", (ItemStack)Ic2Items.smallTinDust.copy());
        OreDictionary.registerOre((String)"dustTinyLead", (ItemStack)Ic2Items.smallLeadDust.copy());
        OreDictionary.registerOre((String)"dustTinySulfur", (ItemStack)Ic2Items.smallSulfurDust.copy());
        OreDictionary.registerOre((String)"dustTinyLithium", (ItemStack)Ic2Items.smallLithiumDust.copy());
        OreDictionary.registerOre((String)"dustTinyBronze", (ItemStack)Ic2Items.smallBronzeDust.copy());
        OreDictionary.registerOre((String)"dustTinyLapis", (ItemStack)Ic2Items.smallLapiDust.copy());
        OreDictionary.registerOre((String)"dustTinyObsidian", (ItemStack)Ic2Items.smallObsidianDust.copy());
        OreDictionary.registerOre((String)"itemRubber", (ItemStack)Ic2Items.rubber.copy());
        OreDictionary.registerOre((String)"ingotBronze", (ItemStack)Ic2Items.bronzeIngot.copy());
        OreDictionary.registerOre((String)"ingotCopper", (ItemStack)Ic2Items.copperIngot.copy());
        OreDictionary.registerOre((String)"ingotSteel", (ItemStack)Ic2Items.advIronIngot.copy());
        OreDictionary.registerOre((String)"ingotLead", (ItemStack)Ic2Items.leadIngot.copy());
        OreDictionary.registerOre((String)"ingotTin", (ItemStack)Ic2Items.tinIngot.copy());
        OreDictionary.registerOre((String)"ingotSilver", (ItemStack)Ic2Items.silverIngot.copy());
        OreDictionary.registerOre((String)"plateIron", (ItemStack)Ic2Items.plateiron.copy());
        OreDictionary.registerOre((String)"plateGold", (ItemStack)Ic2Items.plategold.copy());
        OreDictionary.registerOre((String)"plateCopper", (ItemStack)Ic2Items.platecopper.copy());
        OreDictionary.registerOre((String)"plateTin", (ItemStack)Ic2Items.platetin.copy());
        OreDictionary.registerOre((String)"plateLead", (ItemStack)Ic2Items.platelead.copy());
        OreDictionary.registerOre((String)"plateLapis", (ItemStack)Ic2Items.platelapi.copy());
        OreDictionary.registerOre((String)"plateObsidian", (ItemStack)Ic2Items.plateobsidian.copy());
        OreDictionary.registerOre((String)"plateBronze", (ItemStack)Ic2Items.platebronze.copy());
        OreDictionary.registerOre((String)"plateSteel", (ItemStack)Ic2Items.plateadviron.copy());
        OreDictionary.registerOre((String)"plateDenseSteel", (ItemStack)Ic2Items.denseplateadviron.copy());
        OreDictionary.registerOre((String)"plateDenseIron", (ItemStack)Ic2Items.denseplateiron.copy());
        OreDictionary.registerOre((String)"plateDenseGold", (ItemStack)Ic2Items.denseplategold.copy());
        OreDictionary.registerOre((String)"plateDenseCopper", (ItemStack)Ic2Items.denseplatecopper.copy());
        OreDictionary.registerOre((String)"plateDenseTin", (ItemStack)Ic2Items.denseplatetin.copy());
        OreDictionary.registerOre((String)"plateDenseLead", (ItemStack)Ic2Items.denseplatelead.copy());
        OreDictionary.registerOre((String)"plateDenseLapis", (ItemStack)Ic2Items.denseplatelapi.copy());
        OreDictionary.registerOre((String)"plateDenseObsidian", (ItemStack)Ic2Items.denseplateobsidian.copy());
        OreDictionary.registerOre((String)"plateDenseBronze", (ItemStack)Ic2Items.denseplatebronze.copy());
        OreDictionary.registerOre((String)"crushedIron", (ItemStack)Ic2Items.crushedIronOre.copy());
        OreDictionary.registerOre((String)"crushedGold", (ItemStack)Ic2Items.crushedGoldOre.copy());
        OreDictionary.registerOre((String)"crushedSilver", (ItemStack)Ic2Items.crushedSilverOre.copy());
        OreDictionary.registerOre((String)"crushedLead", (ItemStack)Ic2Items.crushedLeadOre.copy());
        OreDictionary.registerOre((String)"crushedCopper", (ItemStack)Ic2Items.crushedCopperOre.copy());
        OreDictionary.registerOre((String)"crushedTin", (ItemStack)Ic2Items.crushedTinOre.copy());
        OreDictionary.registerOre((String)"crushedUranium", (ItemStack)Ic2Items.crushedUraniumOre.copy());
        OreDictionary.registerOre((String)"crushedPurifiedIron", (ItemStack)Ic2Items.purifiedCrushedIronOre.copy());
        OreDictionary.registerOre((String)"crushedPurifiedGold", (ItemStack)Ic2Items.purifiedCrushedGoldOre.copy());
        OreDictionary.registerOre((String)"crushedPurifiedSilver", (ItemStack)Ic2Items.purifiedCrushedSilverOre.copy());
        OreDictionary.registerOre((String)"crushedPurifiedLead", (ItemStack)Ic2Items.purifiedCrushedLeadOre.copy());
        OreDictionary.registerOre((String)"crushedPurifiedCopper", (ItemStack)Ic2Items.purifiedCrushedCopperOre.copy());
        OreDictionary.registerOre((String)"crushedPurifiedTin", (ItemStack)Ic2Items.purifiedCrushedTinOre.copy());
        OreDictionary.registerOre((String)"crushedPurifiedUranium", (ItemStack)Ic2Items.purifiedCrushedUraniumOre.copy());
        OreDictionary.registerOre((String)"blockBronze", (ItemStack)Ic2Items.bronzeBlock.copy());
        OreDictionary.registerOre((String)"blockCopper", (ItemStack)Ic2Items.copperBlock.copy());
        OreDictionary.registerOre((String)"blockTin", (ItemStack)Ic2Items.tinBlock.copy());
        OreDictionary.registerOre((String)"blockUranium", (ItemStack)Ic2Items.uraniumBlock.copy());
        OreDictionary.registerOre((String)"blockLead", (ItemStack)Ic2Items.leadBlock.copy());
        OreDictionary.registerOre((String)"blockSteel", (ItemStack)Ic2Items.advironblock.copy());
        OreDictionary.registerOre((String)"circuitBasic", (ItemStack)Ic2Items.electronicCircuit.copy());
        OreDictionary.registerOre((String)"circuitAdvanced", (ItemStack)Ic2Items.advancedCircuit.copy());
        OreDictionary.registerOre((String)"gemDiamond", (ItemStack)Ic2Items.industrialDiamond.copy());
        OreDictionary.registerOre((String)"gemDiamond", (Item)Items.diamond);
        OreDictionary.registerOre((String)"craftingToolForgeHammer", (ItemStack)new ItemStack(Ic2Items.ForgeHammer.getItem(), 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"craftingToolWireCutter", (ItemStack)new ItemStack(Ic2Items.cutter.getItem(), 1, Short.MAX_VALUE));
        EnergyNet.instance = EnergyNetGlobal.initialize();
        IC2Crops.init();
        Info.DMG_ELECTRIC = IC2DamageSource.electricity;
        Info.DMG_NUKE_EXPLOSION = IC2DamageSource.nuke;
        Info.DMG_RADIATION = IC2DamageSource.radiation;
        IC2Potion.init();
        new IC2Loot();
        achievements = new IC2Achievements();
        EntityRegistry.registerModEntity(EntityMiningLaser.class, (String)"MiningLaser", (int)0, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity(EntityDynamite.class, (String)"Dynamite", (int)1, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity(EntityStickyDynamite.class, (String)"StickyDynamite", (int)2, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity(EntityItnt.class, (String)"Itnt", (int)3, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity(EntityNuke.class, (String)"Nuke", (int)4, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity(EntityBoatCarbon.class, (String)"BoatCarbon", (int)5, (Object)this, (int)80, (int)3, (boolean)true);
        EntityRegistry.registerModEntity(EntityBoatRubber.class, (String)"BoatRubber", (int)6, (Object)this, (int)80, (int)3, (boolean)true);
        EntityRegistry.registerModEntity(EntityBoatElectric.class, (String)"BoatElectric", (int)7, (Object)this, (int)80, (int)3, (boolean)true);
        EntityRegistry.registerModEntity(EntityParticle.class, (String)"Particle", (int)8, (Object)this, (int)160, (int)1, (boolean)true);
        int d = Integer.parseInt(new SimpleDateFormat("Mdd").format(new Date()));
        suddenlyHoes = (double)d > Math.cbrt(6.4E7) && (double)d < Math.cbrt(6.5939264E7);
        seasonal = (double)d > Math.cbrt(1.089547389E9) && (double)d < Math.cbrt(1.338273208E9);
        GameRegistry.registerWorldGenerator((IWorldGenerator)this, (int)0);
        GameRegistry.registerFuelHandler((IFuelHandler)this);
        MinecraftForge.EVENT_BUS.register((Object)new IC2BucketHandler());
        initialized = true;
        log.debug(LogCategory.General, "Finished pre-init after %d ms.", (System.nanoTime() - startTime) / 1000000L);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        long startTime = System.nanoTime();
        log.debug(LogCategory.General, "Starting init.");
        Rezepte.loadRecipes();
        log.debug(LogCategory.General, "Finished init after %d ms.", (System.nanoTime() - startTime) / 1000000L);
    }

    @Mod.EventHandler
    public void modsLoaded(FMLPostInitializationEvent event) {
        long startTime = System.nanoTime();
        log.debug(LogCategory.General, "Starting post-init.");
        if (!initialized) {
            platform.displayError("IndustrialCraft 2 has failed to initialize properly.", new Object[0]);
        }
        Rezepte.loadFailedRecipes();
        Localization.postInit();
        if (IC2.loadSubModule("bcIntegration")) {
            log.debug(LogCategory.SubModule, "BuildCraft integration module loaded.");
        }
        ArrayList<IRecipeInput> purgedRecipes = new ArrayList<IRecipeInput>();
        purgedRecipes.addAll(ConfigUtil.asRecipeInputList(MainConfig.get(), "recipes/purge"));
        if (ConfigUtil.getBool(MainConfig.get(), "balance/disableEnderChest")) {
            purgedRecipes.add(new RecipeInputItemStack(new ItemStack(Blocks.ender_chest)));
        }
        Iterator it = CraftingManager.getInstance().getRecipeList().iterator();
        block0: while (it.hasNext()) {
            IRecipe recipe = (IRecipe)it.next();
            ItemStack itemStack = recipe.getRecipeOutput();
            if (itemStack == null) continue;
            for (IRecipeInput input : purgedRecipes) {
                if (!input.matches(itemStack)) continue;
                it.remove();
                continue block0;
            }
        }
        if (ConfigUtil.getBool(MainConfig.get(), "recipes/smeltToIc2Items")) {
            Map smeltingMap = FurnaceRecipes.smelting().getSmeltingList();
            block2: for (Map.Entry entry : smeltingMap.entrySet()) {
                boolean found = false;
                for (int oreId : OreDictionary.getOreIDs((ItemStack)((ItemStack)entry.getValue()))) {
                    String oreName = OreDictionary.getOreName((int)oreId);
                    for (ItemStack ore : OreDictionary.getOres((String)oreName)) {
                        if (ore.getItem() == null || !Item.itemRegistry.getNameForObject((Object)ore.getItem()).startsWith("IC2:")) continue;
                        entry.setValue(StackUtil.copyWithSize(ore, ((ItemStack)entry.getValue()).stackSize));
                        found = true;
                        break;
                    }
                    if (found) continue block2;
                }
            }
        }
        TileEntityRecycler.initLate();
        GameRegistry.registerTileEntity(TileEntityBlock.class, (String)"Empty Management TileEntity");
        UuIndex.instance.init();
        UuIndex.instance.refresh(true);
        platform.onPostInit();
        platform.registerRenderers();
        log.debug(LogCategory.General, "Finished post-init after %d ms.", (System.nanoTime() - startTime) / 1000000L);
        log.info(LogCategory.General, "%s version %s loaded.", MODID, VERSION);
    }

    private static boolean loadSubModule(String name) {
        log.debug(LogCategory.SubModule, "Loading %s submodule: %s.", MODID, name);
        try {
            Class<?> subModuleClass = IC2.class.getClassLoader().loadClass("ic2." + name + ".SubModule");
            return (Boolean)subModuleClass.getMethod("init", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Throwable t) {
            log.debug(LogCategory.SubModule, "Submodule %s not loaded.", name);
            return false;
        }
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand((ICommand)new CommandIc2());
        event.registerServerCommand((ICommand)new CommandTps());
    }

    @Mod.EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        BlocksItems.onMissingMappings(event);
    }

    public int getBurnTime(ItemStack stack) {
        if (stack != null) {
            if (Ic2Items.rubberSapling != null && stack.isItemEqual(Ic2Items.rubberSapling)) {
                return 80;
            }
            if (stack.getItem() == Items.reeds) {
                return 50;
            }
            if (StackUtil.equals(Blocks.cactus, stack)) {
                return 50;
            }
            if (stack.isItemEqual(Ic2Items.scrap)) {
                return 350;
            }
            if (stack.isItemEqual(Ic2Items.scrapBox)) {
                return 3150;
            }
            if (stack.isItemEqual(Ic2Items.lavaCell)) {
                return TileEntityFurnace.getItemBurnTime((ItemStack)new ItemStack(Items.lava_bucket));
            }
        }
        return 0;
    }

    public void generate(Random random1, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        int z;
        int y;
        int x;
        int n;
        int count;
        int baseCount;
        BiomeGenBase biomegenbase;
        if (ConfigUtil.getBool(MainConfig.get(), "worldgen/rubberTree") && (biomegenbase = world.getWorldChunkManager().getBiomeGenAt(chunkX * 16 + 16, chunkZ * 16 + 16)) != null && biomegenbase.biomeName != null) {
            int rubbertrees = 0;
            if (BiomeDictionary.isBiomeOfType((BiomeGenBase)biomegenbase, (BiomeDictionary.Type)BiomeDictionary.Type.SWAMP)) {
                rubbertrees += random1.nextInt(10) + 5;
            }
            if (BiomeDictionary.isBiomeOfType((BiomeGenBase)biomegenbase, (BiomeDictionary.Type)BiomeDictionary.Type.FOREST) || BiomeDictionary.isBiomeOfType((BiomeGenBase)biomegenbase, (BiomeDictionary.Type)BiomeDictionary.Type.JUNGLE)) {
                rubbertrees += random1.nextInt(5) + 1;
            }
            if (random1.nextInt(100) + 1 <= rubbertrees * 2) {
                new WorldGenRubTree().generate(world, random1, chunkX * 16 + random1.nextInt(16), rubbertrees, chunkZ * 16 + random1.nextInt(16));
            }
        }
        int baseHeight = IC2.getSeaLevel(world) + 1;
        int baseScale = Math.round((float)baseHeight * ConfigUtil.getFloat(MainConfig.get(), "worldgen/oreDensityFactor"));
        if (ConfigUtil.getBool(MainConfig.get(), "worldgen/copperOre") && Ic2Items.copperOre != null) {
            baseCount = 15 * baseScale / 64;
            count = (int)Math.round(random1.nextGaussian() * Math.sqrt(baseCount) + (double)baseCount);
            for (n = 0; n < count; ++n) {
                x = chunkX * 16 + random1.nextInt(16);
                y = random1.nextInt(40 * baseHeight / 64) + random1.nextInt(20 * baseHeight / 64) + 10 * baseHeight / 64;
                z = chunkZ * 16 + random1.nextInt(16);
                new WorldGenMinable(StackUtil.getBlock(Ic2Items.copperOre), Ic2Items.copperOre.getItemDamage(), 10, Blocks.stone).generate(world, random1, x, y, z);
            }
        }
        if (ConfigUtil.getBool(MainConfig.get(), "worldgen/tinOre") && Ic2Items.tinOre != null) {
            baseCount = 25 * baseScale / 64;
            count = (int)Math.round(random1.nextGaussian() * Math.sqrt(baseCount) + (double)baseCount);
            for (n = 0; n < count; ++n) {
                x = chunkX * 16 + random1.nextInt(16);
                y = random1.nextInt(40 * baseHeight / 64);
                z = chunkZ * 16 + random1.nextInt(16);
                new WorldGenMinable(StackUtil.getBlock(Ic2Items.tinOre), Ic2Items.tinOre.getItemDamage(), 6, Blocks.stone).generate(world, random1, x, y, z);
            }
        }
        if (ConfigUtil.getBool(MainConfig.get(), "worldgen/uraniumOre") && Ic2Items.uraniumOre != null) {
            baseCount = 20 * baseScale / 64;
            count = (int)Math.round(random1.nextGaussian() * Math.sqrt(baseCount) + (double)baseCount);
            for (n = 0; n < count; ++n) {
                x = chunkX * 16 + random1.nextInt(16);
                y = random1.nextInt(64 * baseHeight / 64);
                z = chunkZ * 16 + random1.nextInt(16);
                new WorldGenMinable(StackUtil.getBlock(Ic2Items.uraniumOre), Ic2Items.uraniumOre.getItemDamage(), 3, Blocks.stone).generate(world, random1, x, y, z);
            }
        }
        if (ConfigUtil.getBool(MainConfig.get(), "worldgen/leadOre") && Ic2Items.leadOre != null) {
            baseCount = 8 * baseScale / 64;
            count = (int)Math.round(random1.nextGaussian() * Math.sqrt(baseCount) + (double)baseCount);
            for (n = 0; n < count; ++n) {
                x = chunkX * 16 + random1.nextInt(16);
                y = random1.nextInt(64 * baseHeight / 64);
                z = chunkZ * 16 + random1.nextInt(16);
                new WorldGenMinable(StackUtil.getBlock(Ic2Items.leadOre), Ic2Items.leadOre.getItemDamage(), 4, Blocks.stone).generate(world, random1, x, y, z);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (platform.isSimulating()) {
            keyboard.removePlayerReferences(event.player);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        WorldData.onWorldUnload(event.world);
    }

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitchPost(TextureStitchEvent.Post event) {
        BlockTextureStitched.onPostStitch();
    }

    @SubscribeEvent
    public void onChunkWatchEvent(ChunkWatchEvent.Watch event) {
        Chunk chunk = event.player.worldObj.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);
        for (TileEntity tileEntity : chunk.chunkTileEntityMap.values()) {
            network.get().sendInitialData(tileEntity, event.player);
        }
    }

    public static void explodeMachineAt(World world, int x, int y, int z, boolean noDrop) {
        ExplosionIC2 explosion = new ExplosionIC2(world, null, 0.5 + (double)x, 0.5 + (double)y, 0.5 + (double)z, 2.5f, 0.75f);
        explosion.destroy(x, y, z, noDrop);
        explosion.doExplosion();
    }

    public static int getSeaLevel(World world) {
        return world.provider.getAverageGroundLevel();
    }

    public static int getWorldHeight(World world) {
        return world.getHeight();
    }

    public static void addValuableOre(Block block, int value) {
        IC2.addValuableOre(new RecipeInputItemStack(new ItemStack(block)), value);
    }

    public static void addValuableOre(IRecipeInput input, int value) {
        if (input == null) {
            throw new NullPointerException("input is null");
        }
        valuableOres.put(input, value);
    }

    @SubscribeEvent
    public void registerOre(OreDictionary.OreRegisterEvent event) {
        String oreClass = event.Name;
        ItemStack ore = event.Ore;
        if (!(ore.getItem() instanceof ItemBlock)) {
            return;
        }
        if (oreClass.startsWith("dense")) {
            oreClass = oreClass.substring("dense".length());
        }
        int value = 0;
        if (oreClass.equals("oreCoal")) {
            value = 1;
        } else if (oreClass.equals("oreCopper") || oreClass.equals("oreTin") || oreClass.equals("oreLead") || oreClass.equals("oreQuartz")) {
            value = 2;
        } else if (oreClass.equals("oreIron") || oreClass.equals("oreGold") || oreClass.equals("oreRedstone") || oreClass.equals("oreLapis") || oreClass.equals("oreSilver")) {
            value = 3;
        } else if (oreClass.equals("oreUranium") || oreClass.equals("oreGemRuby") || oreClass.equals("oreGemGreenSapphire") || oreClass.equals("oreGemSapphire") || oreClass.equals("oreRuby") || oreClass.equals("oreGreenSapphire") || oreClass.equals("oreSapphire")) {
            value = 4;
        } else if (oreClass.equals("oreDiamond") || oreClass.equals("oreEmerald") || oreClass.equals("oreTungsten")) {
            value = 5;
        } else if (oreClass.startsWith("ore")) {
            value = 1;
        }
        if (value > 0) {
            if (event.Name.startsWith("dense")) {
                value *= 3;
            }
            IC2.addValuableOre(new RecipeInputItemStack(ore), value);
        }
    }

    @SubscribeEvent
    public void onLivingSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (seasonal && (event.entityLiving instanceof EntityZombie || event.entityLiving instanceof EntitySkeleton) && event.entityLiving.worldObj.rand.nextFloat() < 0.1f) {
            EntityLiving entity = (EntityLiving)event.entityLiving;
            for (int i = 0; i <= 4; ++i) {
                entity.setEquipmentDropChance(i, Float.NEGATIVE_INFINITY);
            }
            if (entity instanceof EntityZombie) {
                entity.setCurrentItemOrArmor(0, Ic2Items.nanoSaber.copy());
            }
            if (event.entityLiving.worldObj.rand.nextFloat() < 0.1f) {
                entity.setCurrentItemOrArmor(1, Ic2Items.quantumHelmet.copy());
                entity.setCurrentItemOrArmor(2, Ic2Items.quantumBodyarmor.copy());
                entity.setCurrentItemOrArmor(3, Ic2Items.quantumLeggings.copy());
                entity.setCurrentItemOrArmor(4, Ic2Items.quantumBoots.copy());
            } else {
                entity.setCurrentItemOrArmor(1, Ic2Items.nanoHelmet.copy());
                entity.setCurrentItemOrArmor(2, Ic2Items.nanoBodyarmor.copy());
                entity.setCurrentItemOrArmor(3, Ic2Items.nanoLeggings.copy());
                entity.setCurrentItemOrArmor(4, Ic2Items.nanoBoots.copy());
            }
        }
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void textureHook(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() == 0) {
            for (InternalName name : BlocksItems.getIc2FluidNames()) {
                Block block = BlocksItems.getFluidBlock(name);
                Fluid fluid = BlocksItems.getFluid(name);
                fluid.setIcons(block.getBlockTextureFromSide(1), block.getBlockTextureFromSide(2));
            }
        }
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void onViewRenderFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (!(event.block instanceof BlockIC2Fluid)) {
            return;
        }
        event.setCanceled(true);
        Fluid fluid = ((BlockIC2Fluid)event.block).getFluid();
        GL11.glFogi((int)2917, (int)2048);
        event.density = (float)Util.map(Math.abs(fluid.getDensity()), 20000.0, 2.0);
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void onViewRenderFogColors(EntityViewRenderEvent.FogColors event) {
        if (!(event.block instanceof BlockIC2Fluid)) {
            return;
        }
        int color = ((BlockIC2Fluid)event.block).getColor();
        event.red = (float)(color >>> 16 & 0xFF) / 255.0f;
        event.green = (float)(color >>> 8 & 0xFF) / 255.0f;
        event.blue = (float)(color & 0xFF) / 255.0f;
    }

    static {
        try {
            new ChunkCoordinates(1, 2, 3).getDistanceSquared(2, 3, 4);
        }
        catch (Throwable t) {
            throw new Error("IC2 is incompatible with this environment, use the normal IC2 version, not the dev one.", t);
        }
        instance = null;
        network = new SideGateway("ic2.core.network.NetworkManager", "ic2.core.network.NetworkManagerClient");
        random = new Random();
        valuableOres = new HashMap<IRecipeInput, Integer>();
        suddenlyHoes = false;
        seasonal = false;
        initialized = false;
        tabIC2 = new CreativeTabIC2();
        textureDomain = MODID.toLowerCase(Locale.ENGLISH);
    }
}

