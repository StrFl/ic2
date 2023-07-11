/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.event.FMLMissingMappingsEvent
 *  cpw.mods.fml.common.event.FMLMissingMappingsEvent$MissingMapping
 *  cpw.mods.fml.common.registry.GameData
 *  cpw.mods.fml.common.registry.GameRegistry$Type
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.material.MaterialLiquid
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.common.util.EnumHelper
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 */
package ic2.core.init;

import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.info.Info;
import ic2.core.IC2;
import ic2.core.IC2Potion;
import ic2.core.Ic2Fluid;
import ic2.core.Ic2Items;
import ic2.core.block.BlockBarrel;
import ic2.core.block.BlockDynamite;
import ic2.core.block.BlockFoam;
import ic2.core.block.BlockIC2Door;
import ic2.core.block.BlockIC2Fluid;
import ic2.core.block.BlockITNT;
import ic2.core.block.BlockMetaData;
import ic2.core.block.BlockMetal;
import ic2.core.block.BlockPoleFence;
import ic2.core.block.BlockReinforcedFoam;
import ic2.core.block.BlockResin;
import ic2.core.block.BlockRubLeaves;
import ic2.core.block.BlockRubSapling;
import ic2.core.block.BlockRubWood;
import ic2.core.block.BlockRubberSheet;
import ic2.core.block.BlockScaffold;
import ic2.core.block.BlockTexGlass;
import ic2.core.block.BlockWall;
import ic2.core.block.generator.block.BlockGenerator;
import ic2.core.block.heatgenerator.block.BlockHeatGenerator;
import ic2.core.block.kineticgenerator.block.BlockKineticGenerator;
import ic2.core.block.machine.BlockMachine;
import ic2.core.block.machine.BlockMachine2;
import ic2.core.block.machine.BlockMachine3;
import ic2.core.block.machine.BlockMiningPipe;
import ic2.core.block.machine.BlockMiningTip;
import ic2.core.block.personal.BlockPersonal;
import ic2.core.block.reactor.block.BlockReactorAccessHatch;
import ic2.core.block.reactor.block.BlockReactorChamber;
import ic2.core.block.reactor.block.BlockReactorFluidPort;
import ic2.core.block.reactor.block.BlockReactorRedstonePort;
import ic2.core.block.reactor.block.BlockReactorVessel;
import ic2.core.block.wiring.BlockCable;
import ic2.core.block.wiring.BlockChargepad;
import ic2.core.block.wiring.BlockElectric;
import ic2.core.block.wiring.BlockLuminator;
import ic2.core.crop.BlockCrop;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.ItemBattery;
import ic2.core.item.ItemBatteryChargeHotbar;
import ic2.core.item.ItemBatteryDischarged;
import ic2.core.item.ItemBatterySU;
import ic2.core.item.ItemBooze;
import ic2.core.item.ItemContainmentbox;
import ic2.core.item.ItemCropSeed;
import ic2.core.item.ItemCrystalMemory;
import ic2.core.item.ItemFertilizer;
import ic2.core.item.ItemFluidCell;
import ic2.core.item.ItemFoamPowder;
import ic2.core.item.ItemGradual;
import ic2.core.item.ItemGradualInt;
import ic2.core.item.ItemIC2;
import ic2.core.item.ItemIC2Boat;
import ic2.core.item.ItemMug;
import ic2.core.item.ItemMugCoffee;
import ic2.core.item.ItemRadioactive;
import ic2.core.item.ItemResin;
import ic2.core.item.ItemScrapbox;
import ic2.core.item.ItemTerraWart;
import ic2.core.item.ItemTinCan;
import ic2.core.item.ItemToolbox;
import ic2.core.item.ItemUpgradeKit;
import ic2.core.item.ItemUpgradeModule;
import ic2.core.item.armor.ItemArmorAdvBatpack;
import ic2.core.item.armor.ItemArmorBatpack;
import ic2.core.item.armor.ItemArmorCFPack;
import ic2.core.item.armor.ItemArmorEnergypack;
import ic2.core.item.armor.ItemArmorHazmat;
import ic2.core.item.armor.ItemArmorIC2;
import ic2.core.item.armor.ItemArmorJetpack;
import ic2.core.item.armor.ItemArmorJetpackElectric;
import ic2.core.item.armor.ItemArmorNanoSuit;
import ic2.core.item.armor.ItemArmorNightvisionGoggles;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.item.armor.ItemArmorSolarHelmet;
import ic2.core.item.armor.ItemArmorStaticBoots;
import ic2.core.item.block.ItemBarrel;
import ic2.core.item.block.ItemCable;
import ic2.core.item.block.ItemDynamite;
import ic2.core.item.block.ItemIC2Door;
import ic2.core.item.reactor.ItemReactorCondensator;
import ic2.core.item.reactor.ItemReactorHeatStorage;
import ic2.core.item.reactor.ItemReactorHeatSwitch;
import ic2.core.item.reactor.ItemReactorLithiumCell;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorPlating;
import ic2.core.item.reactor.ItemReactorReflector;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.reactor.ItemReactorVent;
import ic2.core.item.reactor.ItemReactorVentSpread;
import ic2.core.item.resources.ItemBlockCuttingBlade;
import ic2.core.item.resources.ItemCasing;
import ic2.core.item.resources.ItemCell;
import ic2.core.item.resources.ItemCrushedOre;
import ic2.core.item.resources.ItemDensePlate;
import ic2.core.item.resources.ItemDust;
import ic2.core.item.resources.ItemDust2;
import ic2.core.item.resources.ItemIngot;
import ic2.core.item.resources.ItemLatheDefault;
import ic2.core.item.resources.ItemPlate;
import ic2.core.item.resources.ItemPurifiedCrushedOre;
import ic2.core.item.resources.ItemRecipePart;
import ic2.core.item.resources.ItemWindRotor;
import ic2.core.item.resources.ItemsmallDust;
import ic2.core.item.tfbp.ItemTFBPChilling;
import ic2.core.item.tfbp.ItemTFBPCultivation;
import ic2.core.item.tfbp.ItemTFBPDesertification;
import ic2.core.item.tfbp.ItemTFBPFlatification;
import ic2.core.item.tfbp.ItemTFBPIrrigation;
import ic2.core.item.tfbp.ItemTFBPMushroom;
import ic2.core.item.tool.ItemCropnalyzer;
import ic2.core.item.tool.ItemDebug;
import ic2.core.item.tool.ItemDrillDiamond;
import ic2.core.item.tool.ItemDrillIridium;
import ic2.core.item.tool.ItemDrillStandard;
import ic2.core.item.tool.ItemElectricToolChainsaw;
import ic2.core.item.tool.ItemElectricToolHoe;
import ic2.core.item.tool.ItemFrequencyTransmitter;
import ic2.core.item.tool.ItemIC2Axe;
import ic2.core.item.tool.ItemIC2Hoe;
import ic2.core.item.tool.ItemIC2Pickaxe;
import ic2.core.item.tool.ItemIC2Spade;
import ic2.core.item.tool.ItemIC2Sword;
import ic2.core.item.tool.ItemLathingTool;
import ic2.core.item.tool.ItemNanoSaber;
import ic2.core.item.tool.ItemObscurator;
import ic2.core.item.tool.ItemRemote;
import ic2.core.item.tool.ItemScanner;
import ic2.core.item.tool.ItemScannerAdv;
import ic2.core.item.tool.ItemSprayer;
import ic2.core.item.tool.ItemToolCutter;
import ic2.core.item.tool.ItemToolHammer;
import ic2.core.item.tool.ItemToolMeter;
import ic2.core.item.tool.ItemToolMiningLaser;
import ic2.core.item.tool.ItemToolPainter;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.item.tool.ItemToolWrenchElectric;
import ic2.core.item.tool.ItemTreetap;
import ic2.core.item.tool.ItemTreetapElectric;
import ic2.core.item.tool.ItemWeedingTrowel;
import ic2.core.item.tool.ItemWindmeter;
import ic2.core.item.tool.PlasmaLauncher;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlocksItems {
    private static Map<InternalName, Fluid> fluids = new EnumMap<InternalName, Fluid>(InternalName.class);
    private static Map<InternalName, Block> fluidBlocks = new EnumMap<InternalName, Block>(InternalName.class);
    private static Map<String, InternalName> renames = new HashMap<String, InternalName>();
    private static Set<String> dropped = new HashSet<String>();

    public static void init() {
        BlocksItems.initPotions();
        BlocksItems.initBlocks();
        BlocksItems.initFluids();
        BlocksItems.initItems();
        BlocksItems.initMigration();
    }

    private static void initPotions() {
        IC2Potion.radiation = IC2Potion.registerPotion(ConfigUtil.getInt(MainConfig.get(), "misc/radiationPotionID"), true, 5149489, new ItemStack[0]);
        Info.POTION_RADIATION = IC2Potion.radiation;
    }

    private static void initBlocks() {
        Ic2Items.copperOre = new ItemStack(new BlockMetaData(InternalName.blockOreCopper, Material.rock).setHardness(3.0f).setResistance(5.0f));
        Ic2Items.tinOre = new ItemStack(new BlockMetaData(InternalName.blockOreTin, Material.rock).setHardness(3.0f).setResistance(5.0f));
        Ic2Items.uraniumOre = new ItemStack(new BlockMetaData(InternalName.blockOreUran, Material.rock).setHardness(4.0f).setResistance(6.0f));
        Ic2Items.leadOre = new ItemStack(new BlockMetaData(InternalName.blockOreLead, Material.rock).setHardness(2.0f).setResistance(4.0f));
        new BlockRubWood(InternalName.blockRubWood);
        new BlockRubLeaves(InternalName.blockRubLeaves);
        new BlockRubSapling(InternalName.blockRubSapling);
        new BlockResin(InternalName.blockHarz);
        new BlockRubberSheet(InternalName.blockRubber);
        new BlockPoleFence(InternalName.blockFenceIron);
        Ic2Items.reinforcedStone = new ItemStack(new BlockMetaData(InternalName.blockAlloy, Material.iron).setHardness(80.0f).setResistance(180.0f).setStepSound(Block.soundTypeMetal));
        Ic2Items.basaltBlock = new ItemStack(new BlockMetaData(InternalName.blockBasalt, Material.rock).setHardness(20.0f).setResistance(45.0f).setStepSound(Block.soundTypeStone));
        Ic2Items.reinforcedGlass = new ItemStack((Block)new BlockTexGlass(InternalName.blockAlloyGlass));
        Ic2Items.reinforcedDoorBlock = new ItemStack((Block)new BlockIC2Door(InternalName.blockDoorAlloy));
        new BlockReinforcedFoam(InternalName.blockReinforcedFoam);
        new BlockFoam(InternalName.blockFoam);
        new BlockWall(InternalName.blockWall);
        new BlockScaffold(InternalName.blockScaffold);
        new BlockScaffold(InternalName.blockIronScaffold);
        new BlockMetal(InternalName.blockMetal);
        new BlockCable(InternalName.blockCable);
        new BlockKineticGenerator(InternalName.blockKineticGenerator);
        new BlockHeatGenerator(InternalName.blockHeatGenerator);
        new BlockGenerator(InternalName.blockGenerator);
        new BlockReactorChamber(InternalName.blockReactorChamber);
        new BlockReactorFluidPort(InternalName.blockReactorFluidPort);
        new BlockReactorAccessHatch(InternalName.blockReactorAccessHatch);
        new BlockReactorRedstonePort(InternalName.blockReactorRedstonePort);
        new BlockReactorVessel(InternalName.blockreactorvessel);
        new BlockElectric(InternalName.blockElectric);
        new BlockChargepad(InternalName.blockChargepad);
        new BlockMachine(InternalName.blockMachine);
        new BlockMachine2(InternalName.blockMachine2);
        new BlockMachine3(InternalName.blockMachine3);
        Ic2Items.luminator = new ItemStack((Block)new BlockLuminator(InternalName.blockLuminatorDark));
        Ic2Items.activeLuminator = new ItemStack((Block)new BlockLuminator(InternalName.blockLuminator));
        new BlockMiningPipe(InternalName.blockMiningPipe);
        new BlockMiningTip(InternalName.blockMiningTip);
        new BlockPersonal(InternalName.blockPersonal);
        Ic2Items.industrialTnt = new ItemStack((Block)new BlockITNT(InternalName.blockITNT));
        Ic2Items.nuke = new ItemStack((Block)new BlockITNT(InternalName.blockNuke));
        Ic2Items.dynamiteStick = new ItemStack((Block)new BlockDynamite(InternalName.blockDynamite));
        Ic2Items.dynamiteStickWithRemote = new ItemStack((Block)new BlockDynamite(InternalName.blockDynamiteRemote));
        new BlockCrop(InternalName.blockCrop);
        new BlockBarrel(InternalName.blockBarrel);
    }

    private static void initFluids() {
        MaterialLiquid steam = new MaterialLiquid(MapColor.silverColor);
        BlocksItems.registerIC2fluid(InternalName.fluidUuMatter, Material.water, 3867955, 3000, 3000, 0, 300, false);
        BlocksItems.registerIC2fluid(InternalName.fluidConstructionFoam, Material.water, 0x202020, 10000, 50000, 0, 300, false);
        BlocksItems.registerIC2fluid(InternalName.fluidCoolant, Material.water, 1333866, 1000, 3000, 0, 300, false);
        BlocksItems.registerIC2fluid(InternalName.fluidHotCoolant, Material.lava, 11872308, 1000, 3000, 0, 1200, false);
        BlocksItems.registerIC2fluid(InternalName.fluidPahoehoeLava, Material.lava, 8090732, 50000, 250000, 10, 1200, false);
        BlocksItems.registerIC2fluid(InternalName.fluidBiomass, Material.water, 3632933, 1000, 3000, 0, 300, false);
        BlocksItems.registerIC2fluid(InternalName.fluidBiogas, Material.water, 10983500, 1000, 3000, 0, 300, true);
        BlocksItems.registerIC2fluid(InternalName.fluidDistilledWater, Material.water, 4413173, 1000, 1000, 0, 300, false);
        BlocksItems.registerIC2fluid(InternalName.fluidSuperheatedSteam, (Material)steam, 13291985, -3000, 100, 0, 600, true);
        BlocksItems.registerIC2fluid(InternalName.fluidSteam, (Material)steam, 0xBCBCBC, -800, 300, 0, 420, true);
        BlocksItems.registerIC2fluid(InternalName.fluidHotWater, Material.water, 4644607, 1000, 1000, 0, 350, false);
    }

    private static void initItems() {
        EnumHelper.addToolMaterial((String)"IC2_BRONZE", (int)2, (int)350, (float)6.0f, (float)2.0f, (int)13);
        ItemArmor.ArmorMaterial bronzeArmorMaterial = EnumHelper.addArmorMaterial((String)"IC2_BRONZE", (int)15, (int[])new int[]{2, 6, 5, 2}, (int)9);
        ItemArmor.ArmorMaterial alloyArmorMaterial = EnumHelper.addArmorMaterial((String)"IC2_ALLOY", (int)50, (int[])new int[]{4, 9, 7, 4}, (int)12);
        Ic2Items.resin = new ItemStack((Item)new ItemResin(InternalName.itemHarz));
        Ic2Items.rubber = new ItemStack((Item)new ItemIC2(InternalName.itemRubber));
        Ic2Items.FluidCell = new ItemStack((Item)new ItemFluidCell(InternalName.itemFluidCell));
        new ItemUpgradeKit(InternalName.itemupgradekit);
        new ItemRecipePart(InternalName.itemRecipePart);
        new ItemCasing(InternalName.itemCasing);
        new ItemCrushedOre(InternalName.itemCrushedOre);
        new ItemPurifiedCrushedOre(InternalName.itemPurifiedCrushedOre);
        new ItemPlate(InternalName.itemPlates);
        new ItemDensePlate(InternalName.itemDensePlates);
        Ic2Items.turningBlankIron = new ItemStack((Item)new ItemLatheDefault(ItemLatheDefault.LatheMaterial.IRON));
        Ic2Items.turningBlankWood = new ItemStack((Item)new ItemLatheDefault(ItemLatheDefault.LatheMaterial.WOOD));
        new ItemsmallDust(InternalName.itemDustSmall);
        new ItemDust(InternalName.itemDust);
        new ItemDust2(InternalName.itemDust2);
        new ItemIngot(InternalName.itemIngot);
        Ic2Items.reactorLithiumCell = new ItemStack((Item)new ItemReactorLithiumCell(InternalName.reactorLithiumCell));
        Ic2Items.TritiumCell = new ItemStack((Item)new ItemIC2(InternalName.itemTritiumCell));
        Ic2Items.UranFuel = new ItemStack((Item)new ItemRadioactive(InternalName.itemUran, 60, 100));
        Ic2Items.MOXFuel = new ItemStack((Item)new ItemRadioactive(InternalName.itemMOX, 300, 100));
        Ic2Items.Plutonium = new ItemStack((Item)new ItemRadioactive(InternalName.itemPlutonium, 150, 100));
        Ic2Items.smallPlutonium = new ItemStack((Item)new ItemRadioactive(InternalName.itemPlutoniumSmall, 150, 100));
        Ic2Items.Uran235 = new ItemStack((Item)new ItemRadioactive(InternalName.itemUran235, 150, 100));
        Ic2Items.smallUran235 = new ItemStack((Item)new ItemRadioactive(InternalName.itemUran235small, 150, 100));
        Ic2Items.Uran238 = new ItemStack((Item)new ItemRadioactive(InternalName.itemUran238, 10, 90));
        Ic2Items.fuelRod = new ItemStack((Item)new ItemIC2(InternalName.itemFuelRod));
        Ic2Items.RTGPellets = new ItemStack(new ItemRadioactive(InternalName.itemRTGPellet, 2, 90).setMaxStackSize(1));
        Ic2Items.electronicCircuit = new ItemStack((Item)new ItemIC2(InternalName.itemPartCircuit));
        Ic2Items.advancedCircuit = new ItemStack(new ItemIC2(InternalName.itemPartCircuitAdv).setRarity(1).setUnlocalizedName("itemPartCircuitAdv").setCreativeTab((CreativeTabs)IC2.tabIC2));
        Ic2Items.advancedAlloy = new ItemStack((Item)new ItemIC2(InternalName.itemPartAlloy));
        Ic2Items.carbonFiber = new ItemStack((Item)new ItemIC2(InternalName.itemPartCarbonFibre));
        Ic2Items.carbonMesh = new ItemStack((Item)new ItemIC2(InternalName.itemPartCarbonMesh));
        Ic2Items.carbonPlate = new ItemStack((Item)new ItemIC2(InternalName.itemPartCarbonPlate));
        Ic2Items.iridiumOre = new ItemStack(new ItemIC2(InternalName.itemOreIridium).setRarity(2).setUnlocalizedName("itemOreIridium").setCreativeTab((CreativeTabs)IC2.tabIC2));
        Ic2Items.iridiumPlate = new ItemStack(new ItemIC2(InternalName.itemPartIridium).setRarity(2).setUnlocalizedName("itemPartIridium").setCreativeTab((CreativeTabs)IC2.tabIC2));
        Ic2Items.iridiumShard = new ItemStack(new ItemIC2(InternalName.itemShardIridium).setRarity(2).setUnlocalizedName("itemShardIridium").setCreativeTab((CreativeTabs)IC2.tabIC2));
        Ic2Items.treetap = new ItemStack((Item)new ItemTreetap(InternalName.itemTreetap));
        Ic2Items.bronzePickaxe = new ItemStack((Item)new ItemIC2Pickaxe(InternalName.itemToolBronzePickaxe, Item.ToolMaterial.IRON, 5.0f, "ingotBronze"));
        Ic2Items.bronzeAxe = new ItemStack((Item)new ItemIC2Axe(InternalName.itemToolBronzeAxe, Item.ToolMaterial.IRON, 5.0f, "ingotBronze"));
        Ic2Items.bronzeSword = new ItemStack((Item)new ItemIC2Sword(InternalName.itemToolBronzeSword, Item.ToolMaterial.IRON, 7, "ingotBronze"));
        Ic2Items.bronzeShovel = new ItemStack((Item)new ItemIC2Spade(InternalName.itemToolBronzeSpade, Item.ToolMaterial.IRON, 5.0f, "ingotBronze"));
        Ic2Items.bronzeHoe = new ItemStack((Item)new ItemIC2Hoe(InternalName.itemToolBronzeHoe, Item.ToolMaterial.IRON, "ingotBronze"));
        Ic2Items.wrench = new ItemStack((Item)new ItemToolWrench(InternalName.itemToolWrench));
        Ic2Items.cutter = new ItemStack((Item)new ItemToolCutter(InternalName.itemToolCutter));
        Ic2Items.constructionFoamSprayer = new ItemStack((Item)new ItemSprayer(InternalName.itemFoamSprayer));
        Ic2Items.toolbox = new ItemStack((Item)new ItemToolbox(InternalName.itemToolbox));
        Ic2Items.containmentbox = new ItemStack((Item)new ItemContainmentbox(InternalName.itemContainmentbox));
        Ic2Items.ForgeHammer = new ItemStack((Item)new ItemToolHammer(InternalName.itemToolForgeHammer));
        Ic2Items.LathingTool = new ItemStack((Item)new ItemLathingTool(InternalName.itemLathingTool));
        Ic2Items.crystalmemory = new ItemStack((Item)new ItemCrystalMemory(InternalName.itemcrystalmemory));
        new ItemDrillStandard(InternalName.itemToolDrill);
        new ItemDrillDiamond(InternalName.itemToolDDrill);
        new ItemDrillIridium(InternalName.itemToolIridiumDrill);
        Ic2Items.chainsaw = new ItemStack((Item)new ItemElectricToolChainsaw(InternalName.itemToolChainsaw));
        Ic2Items.electricWrench = new ItemStack((Item)new ItemToolWrenchElectric(InternalName.itemToolWrenchElectric));
        Ic2Items.electricTreetap = new ItemStack((Item)new ItemTreetapElectric(InternalName.itemTreetapElectric));
        Ic2Items.miningLaser = new ItemStack((Item)new ItemToolMiningLaser(InternalName.itemToolMiningLaser));
        Ic2Items.ecMeter = new ItemStack((Item)new ItemToolMeter(InternalName.itemToolMEter));
        Ic2Items.odScanner = new ItemStack((Item)new ItemScanner(InternalName.itemScanner));
        Ic2Items.ovScanner = new ItemStack((Item)new ItemScannerAdv(InternalName.itemScannerAdv));
        Ic2Items.obscurator = new ItemStack((Item)new ItemObscurator(InternalName.obscurator));
        Ic2Items.frequencyTransmitter = new ItemStack((Item)new ItemFrequencyTransmitter(InternalName.itemFreq));
        Ic2Items.nanoSaber = new ItemStack((Item)new ItemNanoSaber(InternalName.itemNanoSaber));
        Ic2Items.plasmaLauncher = new ItemStack((Item)new PlasmaLauncher(InternalName.plasmaLauncher));
        Ic2Items.windmeter = new ItemStack((Item)new ItemWindmeter(InternalName.windmeter));
        Ic2Items.hazmatHelmet = new ItemStack((Item)new ItemArmorHazmat(InternalName.itemArmorHazmatHelmet, 0));
        Ic2Items.hazmatChestplate = new ItemStack((Item)new ItemArmorHazmat(InternalName.itemArmorHazmatChestplate, 1));
        Ic2Items.hazmatLeggings = new ItemStack((Item)new ItemArmorHazmat(InternalName.itemArmorHazmatLeggings, 2));
        Ic2Items.hazmatBoots = new ItemStack((Item)new ItemArmorHazmat(InternalName.itemArmorRubBoots, 3));
        Ic2Items.bronzeHelmet = new ItemStack((Item)new ItemArmorIC2(InternalName.itemArmorBronzeHelmet, bronzeArmorMaterial, InternalName.bronze, 0, "ingotBronze"));
        Ic2Items.bronzeChestplate = new ItemStack((Item)new ItemArmorIC2(InternalName.itemArmorBronzeChestplate, bronzeArmorMaterial, InternalName.bronze, 1, "ingotBronze"));
        Ic2Items.bronzeLeggings = new ItemStack((Item)new ItemArmorIC2(InternalName.itemArmorBronzeLegs, bronzeArmorMaterial, InternalName.bronze, 2, "ingotBronze"));
        Ic2Items.bronzeBoots = new ItemStack((Item)new ItemArmorIC2(InternalName.itemArmorBronzeBoots, bronzeArmorMaterial, InternalName.bronze, 3, "ingotBronze"));
        Ic2Items.compositeArmor = new ItemStack((Item)new ItemArmorIC2(InternalName.itemArmorAlloyChestplate, alloyArmorMaterial, InternalName.alloy, 1, Ic2Items.advancedAlloy));
        Ic2Items.nanoHelmet = new ItemStack((Item)new ItemArmorNanoSuit(InternalName.itemArmorNanoHelmet, 0));
        Ic2Items.nanoBodyarmor = new ItemStack((Item)new ItemArmorNanoSuit(InternalName.itemArmorNanoChestplate, 1));
        Ic2Items.nanoLeggings = new ItemStack((Item)new ItemArmorNanoSuit(InternalName.itemArmorNanoLegs, 2));
        Ic2Items.nanoBoots = new ItemStack((Item)new ItemArmorNanoSuit(InternalName.itemArmorNanoBoots, 3));
        Ic2Items.quantumHelmet = new ItemStack((Item)new ItemArmorQuantumSuit(InternalName.itemArmorQuantumHelmet, 0));
        Ic2Items.quantumBodyarmor = new ItemStack((Item)new ItemArmorQuantumSuit(InternalName.itemArmorQuantumChestplate, 1));
        Ic2Items.quantumLeggings = new ItemStack((Item)new ItemArmorQuantumSuit(InternalName.itemArmorQuantumLegs, 2));
        Ic2Items.quantumBoots = new ItemStack((Item)new ItemArmorQuantumSuit(InternalName.itemArmorQuantumBoots, 3));
        Ic2Items.jetpack = new ItemStack((Item)new ItemArmorJetpack(InternalName.itemArmorJetpack));
        Ic2Items.electricJetpack = new ItemStack((Item)new ItemArmorJetpackElectric(InternalName.itemArmorJetpackElectric));
        Ic2Items.batPack = new ItemStack((Item)new ItemArmorBatpack(InternalName.itemArmorBatpack));
        Ic2Items.advbatPack = new ItemStack((Item)new ItemArmorAdvBatpack(InternalName.itemArmorAdvBatpack));
        Ic2Items.lapPack = Ic2Items.energyPack = new ItemStack((Item)new ItemArmorEnergypack(InternalName.itemArmorEnergypack));
        Ic2Items.cfPack = new ItemStack((Item)new ItemArmorCFPack(InternalName.itemArmorCFPack));
        Ic2Items.solarHelmet = new ItemStack((Item)new ItemArmorSolarHelmet(InternalName.itemSolarHelmet));
        Ic2Items.staticBoots = new ItemStack((Item)new ItemArmorStaticBoots(InternalName.itemStaticBoots));
        Ic2Items.nightvisionGoggles = new ItemStack((Item)new ItemArmorNightvisionGoggles(InternalName.itemNightvisionGoggles));
        Ic2Items.reBattery = new ItemStack((Item)new ItemBatteryDischarged(InternalName.itemBatREDischarged, 10000, 100, 1));
        Ic2Items.chargedReBattery = new ItemStack((Item)new ItemBattery(InternalName.itemBatRE, 10000.0, 32.0, 1));
        Ic2Items.advBattery = new ItemStack((Item)new ItemBattery(InternalName.itemAdvBat, 100000.0, 256.0, 2));
        Ic2Items.energyCrystal = new ItemStack((Item)new ItemBattery(InternalName.itemBatCrystal, 1000000.0, 2048.0, 3));
        Ic2Items.lapotronCrystal = new ItemStack((Item)new ItemBattery(InternalName.itemBatLamaCrystal, 1.0E7, 8092.0, 4).setRarity(1));
        Ic2Items.suBattery = new ItemStack((Item)new ItemBatterySU(InternalName.itemBatSU, 1200, 1));
        Ic2Items.chargingREBattery = new ItemStack((Item)new ItemBatteryChargeHotbar(InternalName.itemBatChargeRE, 40000.0, 128.0, 1));
        Ic2Items.chargingAdvBattery = new ItemStack((Item)new ItemBatteryChargeHotbar(InternalName.itemBatChargeAdv, 400000.0, 1024.0, 2));
        Ic2Items.chargingEnergyCrystal = new ItemStack((Item)new ItemBatteryChargeHotbar(InternalName.itemBatChargeCrystal, 4000000.0, 8192.0, 3));
        Ic2Items.chargingLapotronCrystal = new ItemStack((Item)new ItemBatteryChargeHotbar(InternalName.itemBatChargeLamaCrystal, 4.0E7, 32768.0, 4).setRarity(1));
        new ItemCable(InternalName.itemCable);
        Ic2Items.cell = new ItemStack((Item)new ItemCell(InternalName.itemCellEmpty));
        Ic2Items.tinCan = new ItemStack((Item)new ItemIC2(InternalName.itemTinCan));
        Ic2Items.filledTinCan = new ItemStack((Item)new ItemTinCan(InternalName.itemTinCanFilled));
        Ic2Items.reactorMOXSimple = new ItemStack((Item)new ItemReactorMOX(InternalName.reactorMOXSimple, 1));
        Ic2Items.reactorMOXDual = new ItemStack((Item)new ItemReactorMOX(InternalName.reactorMOXDual, 2));
        Ic2Items.reactorMOXQuad = new ItemStack((Item)new ItemReactorMOX(InternalName.reactorMOXQuad, 4));
        Ic2Items.reactorUraniumSimple = new ItemStack((Item)new ItemReactorUranium(InternalName.reactorUraniumSimple, 1));
        Ic2Items.reactorUraniumDual = new ItemStack((Item)new ItemReactorUranium(InternalName.reactorUraniumDual, 2));
        Ic2Items.reactorUraniumQuad = new ItemStack((Item)new ItemReactorUranium(InternalName.reactorUraniumQuad, 4));
        Ic2Items.reactorDepletedMOXSimple = new ItemStack((Item)new ItemRadioactive(InternalName.reactorMOXSimpledepleted, 10, 100));
        Ic2Items.reactorDepletedMOXDual = new ItemStack((Item)new ItemRadioactive(InternalName.reactorMOXDualdepleted, 10, 100));
        Ic2Items.reactorDepletedMOXQuad = new ItemStack((Item)new ItemRadioactive(InternalName.reactorMOXQuaddepleted, 10, 100));
        Ic2Items.reactorDepletedUraniumSimple = new ItemStack((Item)new ItemRadioactive(InternalName.reactorUraniumSimpledepleted, 10, 100));
        Ic2Items.reactorDepletedUraniumDual = new ItemStack((Item)new ItemRadioactive(InternalName.reactorUraniumDualdepleted, 10, 100));
        Ic2Items.reactorDepletedUraniumQuad = new ItemStack((Item)new ItemRadioactive(InternalName.reactorUraniumQuaddepleted, 10, 100));
        Ic2Items.reactorCoolantSimple = new ItemStack((Item)new ItemReactorHeatStorage(InternalName.reactorCoolantSimple, 10000));
        Ic2Items.reactorCoolantTriple = new ItemStack((Item)new ItemReactorHeatStorage(InternalName.reactorCoolantTriple, 30000));
        Ic2Items.reactorCoolantSix = new ItemStack((Item)new ItemReactorHeatStorage(InternalName.reactorCoolantSix, 60000));
        Ic2Items.reactorPlating = new ItemStack((Item)new ItemReactorPlating(InternalName.reactorPlating, 1000, 0.95f));
        Ic2Items.reactorPlatingHeat = new ItemStack((Item)new ItemReactorPlating(InternalName.reactorPlatingHeat, 2000, 0.99f));
        Ic2Items.reactorPlatingExplosive = new ItemStack((Item)new ItemReactorPlating(InternalName.reactorPlatingExplosive, 500, 0.9f));
        Ic2Items.reactorHeatSwitch = new ItemStack((Item)new ItemReactorHeatSwitch(InternalName.reactorHeatSwitch, 2500, 12, 4));
        Ic2Items.reactorHeatSwitchCore = new ItemStack((Item)new ItemReactorHeatSwitch(InternalName.reactorHeatSwitchCore, 5000, 0, 72));
        Ic2Items.reactorHeatSwitchSpread = new ItemStack((Item)new ItemReactorHeatSwitch(InternalName.reactorHeatSwitchSpread, 5000, 36, 0));
        Ic2Items.reactorHeatSwitchDiamond = new ItemStack((Item)new ItemReactorHeatSwitch(InternalName.reactorHeatSwitchDiamond, 10000, 24, 8));
        Ic2Items.reactorVent = new ItemStack((Item)new ItemReactorVent(InternalName.reactorVent, 1000, 6, 0));
        Ic2Items.reactorVentCore = new ItemStack((Item)new ItemReactorVent(InternalName.reactorVentCore, 1000, 5, 5));
        Ic2Items.reactorVentGold = new ItemStack((Item)new ItemReactorVent(InternalName.reactorVentGold, 1000, 20, 36));
        Ic2Items.reactorVentSpread = new ItemStack((Item)new ItemReactorVentSpread(InternalName.reactorVentSpread, 4));
        Ic2Items.reactorVentDiamond = new ItemStack((Item)new ItemReactorVent(InternalName.reactorVentDiamond, 1000, 12, 0));
        Ic2Items.reactorReflector = new ItemStack((Item)new ItemReactorReflector(InternalName.reactorReflector, 10000));
        Ic2Items.reactorReflectorThick = new ItemStack((Item)new ItemReactorReflector(InternalName.reactorReflectorThick, 40000));
        Ic2Items.reactorCondensator = new ItemStack((Item)new ItemReactorCondensator(InternalName.reactorCondensator, 20000));
        Ic2Items.reactorCondensatorLap = new ItemStack((Item)new ItemReactorCondensator(InternalName.reactorCondensatorLap, 100000));
        Ic2Items.terraformerBlueprint = new ItemStack((Item)new ItemIC2(InternalName.itemTFBP));
        Ic2Items.cultivationTerraformerBlueprint = new ItemStack((Item)new ItemTFBPCultivation(InternalName.itemTFBPCultivation));
        Ic2Items.irrigationTerraformerBlueprint = new ItemStack((Item)new ItemTFBPIrrigation(InternalName.itemTFBPIrrigation));
        Ic2Items.chillingTerraformerBlueprint = new ItemStack((Item)new ItemTFBPChilling(InternalName.itemTFBPChilling));
        Ic2Items.desertificationTerraformerBlueprint = new ItemStack((Item)new ItemTFBPDesertification(InternalName.itemTFBPDesertification));
        Ic2Items.flatificatorTerraformerBlueprint = new ItemStack((Item)new ItemTFBPFlatification(InternalName.itemTFBPFlatification));
        Ic2Items.mushroomTerraformerBlueprint = new ItemStack((Item)new ItemTFBPMushroom(InternalName.itemTFBPMushroom));
        Ic2Items.coalBall = new ItemStack((Item)new ItemIC2(InternalName.itemPartCoalBall));
        Ic2Items.compressedCoalBall = new ItemStack((Item)new ItemIC2(InternalName.itemPartCoalBlock));
        Ic2Items.coalChunk = new ItemStack((Item)new ItemIC2(InternalName.itemPartCoalChunk));
        Ic2Items.industrialDiamond = new ItemStack(new ItemIC2(InternalName.itemPartIndustrialDiamond).setUnlocalizedName("itemPartIndustrialDiamond"));
        Ic2Items.slag = new ItemStack((Item)new ItemIC2(InternalName.itemSlag));
        Ic2Items.scrap = new ItemStack((Item)new ItemIC2(InternalName.itemScrap));
        Ic2Items.scrapBox = new ItemStack((Item)new ItemScrapbox(InternalName.itemScrapbox));
        Ic2Items.plantBall = new ItemStack((Item)new ItemIC2(InternalName.itemFuelPlantBall));
        Ic2Items.biochaff = new ItemStack((Item)new ItemIC2(InternalName.itemBiochaff));
        Ic2Items.painter = new ItemStack((Item)new ItemIC2(InternalName.itemToolPainter));
        Ic2Items.blackPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterBlack, 0));
        Ic2Items.redPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterRed, 1));
        Ic2Items.greenPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterGreen, 2));
        Ic2Items.brownPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterBrown, 3));
        Ic2Items.bluePainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterBlue, 4));
        Ic2Items.purplePainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterPurple, 5));
        Ic2Items.cyanPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterCyan, 6));
        Ic2Items.lightGreyPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterLightGrey, 7));
        Ic2Items.darkGreyPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterDarkGrey, 8));
        Ic2Items.pinkPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterPink, 9));
        Ic2Items.limePainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterLime, 10));
        Ic2Items.yellowPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterYellow, 11));
        Ic2Items.cloudPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterCloud, 12));
        Ic2Items.magentaPainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterMagenta, 13));
        Ic2Items.orangePainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterOrange, 14));
        Ic2Items.whitePainter = new ItemStack((Item)new ItemToolPainter(InternalName.itemToolPainterWhite, 15));
        Ic2Items.dynamite = new ItemStack((Item)new ItemDynamite(InternalName.itemDynamite, false));
        Ic2Items.stickyDynamite = new ItemStack((Item)new ItemDynamite(InternalName.itemDynamiteSticky, true));
        Ic2Items.remote = new ItemStack((Item)new ItemRemote(InternalName.itemRemote));
        new ItemUpgradeModule(InternalName.upgradeModule);
        Ic2Items.coin = new ItemStack((Item)new ItemIC2(InternalName.itemCoin));
        Ic2Items.reinforcedDoor = new ItemStack((Item)new ItemIC2Door(InternalName.itemDoorAlloy, StackUtil.getBlock(Ic2Items.reinforcedDoorBlock)));
        Ic2Items.constructionFoamPowder = new ItemStack((Item)new ItemFoamPowder(InternalName.itemPartCFPowder));
        Ic2Items.grinPowder = new ItemStack((Item)new ItemIC2(InternalName.itemGrinPowder));
        Ic2Items.debug = new ItemStack((Item)new ItemDebug(InternalName.itemDebug));
        new ItemIC2Boat(InternalName.itemBoat);
        Ic2Items.weedingTrowel = new ItemStack((Item)new ItemWeedingTrowel(InternalName.itemWeedingTrowel));
        Ic2Items.weed = new ItemStack((Item)new ItemIC2(InternalName.itemWeed));
        Ic2Items.cropSeed = new ItemStack((Item)new ItemCropSeed(InternalName.itemCropSeed));
        Ic2Items.cropnalyzer = new ItemStack((Item)new ItemCropnalyzer(InternalName.itemCropnalyzer));
        Ic2Items.fertilizer = new ItemStack((Item)new ItemFertilizer(InternalName.itemFertilizer));
        Ic2Items.hydratingCell = new ItemStack((Item)new ItemGradual(InternalName.itemCellHydrant));
        Ic2Items.electricHoe = new ItemStack((Item)new ItemElectricToolHoe(InternalName.itemToolHoe));
        Ic2Items.terraWart = new ItemStack((Item)new ItemTerraWart(InternalName.itemTerraWart));
        Ic2Items.weedEx = new ItemStack(new ItemIC2(InternalName.itemWeedEx).setMaxStackSize(1).setMaxDamage(64));
        Ic2Items.mugEmpty = new ItemStack((Item)new ItemMug(InternalName.itemMugEmpty));
        Ic2Items.coffeeBeans = new ItemStack((Item)new ItemIC2(InternalName.itemCofeeBeans));
        Ic2Items.coffeePowder = new ItemStack((Item)new ItemIC2(InternalName.itemCofeePowder));
        Ic2Items.mugCoffee = new ItemStack((Item)new ItemMugCoffee(InternalName.itemMugCoffee));
        Ic2Items.hops = new ItemStack((Item)new ItemIC2(InternalName.itemHops));
        Ic2Items.barrel = new ItemStack((Item)new ItemBarrel(InternalName.itemBarrel));
        Ic2Items.mugBooze = new ItemStack((Item)new ItemBooze(InternalName.itemMugBooze));
        Ic2Items.woodrotor = new ItemStack((Item)new ItemWindRotor(InternalName.itemwoodrotor, 5, 10800, 0.25f, 10, 60, new ResourceLocation(IC2.textureDomain, "textures/items/rotors/rotorWoodmodel.png")));
        Ic2Items.ironrotor = new ItemStack((Item)new ItemWindRotor(InternalName.itemironrotor, 7, 86400, 0.5f, 14, 75, new ResourceLocation(IC2.textureDomain, "textures/items/rotors/rotorIronmodel.png")));
        Ic2Items.steelrotor = new ItemStack((Item)new ItemWindRotor(InternalName.itemsteelrotor, 9, 172800, 0.75f, 17, 90, new ResourceLocation(IC2.textureDomain, "textures/items/rotors/rotorSteelmodel.png")));
        Ic2Items.carbonrotor = new ItemStack((Item)new ItemWindRotor(InternalName.itemwcarbonrotor, 11, 604800, 1.0f, 20, 110, new ResourceLocation(IC2.textureDomain, "textures/items/rotors/rotorCarbonmodel.png")));
        Ic2Items.steamturbine = new ItemStack((Item)new ItemGradualInt(InternalName.itemSteamTurbine, ConfigUtil.getInt(MainConfig.get(), "balance/SteamKineticGenerator/rotorlivetime")));
        Ic2Items.steamturbineblade = new ItemStack((Item)new ItemIC2(InternalName.itemSteamTurbineBlade));
        Ic2Items.ironblockcuttingblade = new ItemStack((Item)new ItemBlockCuttingBlade(InternalName.itemIronBlockCuttingBlade, 3));
        Ic2Items.advironblockcuttingblade = new ItemStack((Item)new ItemBlockCuttingBlade(InternalName.itemAdvIronBlockCuttingBlade, 6));
        Ic2Items.diamondblockcuttingblade = new ItemStack((Item)new ItemBlockCuttingBlade(InternalName.itemDiamondBlockCuttingBlade, 9));
        ((BlockIC2Door)StackUtil.getBlock(Ic2Items.reinforcedDoorBlock)).setItemDropped(Ic2Items.reinforcedDoor.getItem());
    }

    private static void initMigration() {
        renames.put("blockfluidUuMatter", InternalName.fluidUuMatter);
        renames.put("blockfluidCf", InternalName.fluidConstructionFoam);
        renames.put("blockFluidcoolant", InternalName.fluidCoolant);
        renames.put("blockFluidhotcoolant", InternalName.fluidHotCoolant);
        renames.put("blockFluidpahoehoelava", InternalName.fluidPahoehoeLava);
        renames.put("blockbiomass", InternalName.fluidBiomass);
        renames.put("blockbiogas", InternalName.fluidBiogas);
        renames.put("blockdistilledwater", InternalName.fluidDistilledWater);
        renames.put("blocksuperheatedsteam", InternalName.fluidSuperheatedSteam);
        renames.put("blocksteam", InternalName.fluidSteam);
        dropped.add("itemArmorLappack");
        dropped.add("itemLithium");
        dropped.add("itemNanoSaberOff");
        dropped.add("itemDustIronSmall");
        dropped.add("itemDustBronze");
        dropped.add("itemDustClay");
        dropped.add("itemDustCoal");
        dropped.add("itemDustCopper");
        dropped.add("itemDustGold");
        dropped.add("itemDustIron");
        dropped.add("itemDustSilver");
        dropped.add("itemDustTin");
        dropped.add("itemIngotAdvIron");
        dropped.add("itemIngotAlloy");
        dropped.add("itemIngotBronze");
        dropped.add("itemIngotCopper");
        dropped.add("itemIngotTin");
        dropped.add("itemCellLava");
        dropped.add("itemCellWater");
        dropped.add("itemCellAir");
        dropped.add("itemCellWaterElectro");
        dropped.add("itemDustIronSmall");
        dropped.add("itemDustBronze");
        dropped.add("itemDustClay");
        dropped.add("itemDustCoal");
        dropped.add("itemDustCopper");
        dropped.add("itemDustGold");
        dropped.add("itemDustIron");
        dropped.add("itemDustSilver");
        dropped.add("itemDustTin");
        dropped.add("itemIngotAdvIron");
        dropped.add("itemIngotAlloy");
        dropped.add("itemIngotBronze");
        dropped.add("itemIngotCopper");
        dropped.add("itemIngotTin");
        dropped.add("itemCellCoal");
        dropped.add("itemFuelCoalCmpr");
        dropped.add("itemFuelCan");
        dropped.add("itemMatter");
        dropped.add("itemFuelPlantCmpr");
        dropped.add("itemCellBioRef");
        dropped.add("itemFuelCanEmpty");
        dropped.add("itemCellCoalRef");
        dropped.add("itemCellBio");
    }

    private static void registerIC2fluid(InternalName internalName, Material material, int color, int density, int viscosity, int luminosity, int temperature, boolean isGaseous) {
        Object block;
        if (!internalName.name().startsWith("fluid")) {
            throw new IllegalArgumentException("Invalid fluid block name: " + (Object)((Object)internalName));
        }
        String fluidName = "ic2" + internalName.name().substring("fluid".length()).toLowerCase(Locale.ENGLISH);
        Fluid fluid = new Ic2Fluid(fluidName).setDensity(density).setViscosity(viscosity).setLuminosity(luminosity).setTemperature(temperature).setGaseous(isGaseous);
        if (!FluidRegistry.registerFluid((Fluid)fluid)) {
            fluid = FluidRegistry.getFluid((String)fluidName);
        }
        if (!fluid.canBePlacedInWorld()) {
            block = new BlockIC2Fluid(internalName, fluid, material, color);
            fluid.setBlock(block);
            fluid.setUnlocalizedName(block.getUnlocalizedName());
        } else {
            block = fluid.getBlock();
        }
        fluids.put(internalName, fluid);
        fluidBlocks.put(internalName, (Block)block);
    }

    public static void onMissingMappings(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
            if (!mapping.name.startsWith("IC2:")) continue;
            String subName = mapping.name.substring("IC2".length() + 1);
            InternalName newName = renames.get(subName);
            if (newName != null) {
                String name = "IC2:" + newName.name();
                if (mapping.type == GameRegistry.Type.BLOCK) {
                    mapping.remap((Block)GameData.getBlockRegistry().getRaw(name));
                    continue;
                }
                mapping.remap((Item)GameData.getItemRegistry().getRaw(name));
                continue;
            }
            if (!dropped.contains(subName)) continue;
            mapping.ignore();
        }
    }

    public static Fluid getFluid(InternalName name) {
        return fluids.get((Object)name);
    }

    public static Block getFluidBlock(InternalName name) {
        return fluidBlocks.get((Object)name);
    }

    public static Collection<InternalName> getIc2FluidNames() {
        return fluids.keySet();
    }
}

