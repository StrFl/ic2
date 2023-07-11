/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$ItemCraftedEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$ItemSmeltedEvent
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.stats.Achievement
 *  net.minecraft.stats.AchievementList
 *  net.minecraft.stats.StatBase
 *  net.minecraftforge.common.AchievementPage
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.EntityItemPickupEvent
 */
package ic2.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ic2.core.Ic2Items;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class IC2Achievements {
    public HashMap<String, Achievement> achievementList = new HashMap();
    private final int achievementBaseX = -4;
    private final int achievementBaseY = -5;

    public IC2Achievements() {
        this.registerAchievement("acquireResin", 2, 0, Ic2Items.resin, AchievementList.mineWood, false);
        if (Ic2Items.copperOre != null || Ic2Items.tinOre != null || Ic2Items.uraniumOre != null) {
            this.registerAchievement("mineOre", 4, 0, Ic2Items.copperOre == null ? (Ic2Items.tinOre == null ? Ic2Items.uraniumOre : Ic2Items.tinOre) : Ic2Items.copperOre, AchievementList.buildBetterPickaxe, false);
        }
        this.registerAchievement("acquireRefinedIron", 0, 0, new ItemStack(Items.iron_ingot), AchievementList.acquireIron, false);
        this.registerAchievement("buildCable", 0, 2, Ic2Items.insulatedCopperCableItem, AchievementList.acquireIron, false);
        this.registerAchievement("buildGenerator", 6, 2, Ic2Items.generator, "buildCable", false);
        this.registerAchievement("buildMacerator", 6, 0, Ic2Items.macerator, "buildGenerator", false);
        this.registerAchievement("buildCoalDiamond", 8, 0, Ic2Items.industrialDiamond, "buildMacerator", false);
        this.registerAchievement("buildElecFurnace", 8, 2, Ic2Items.electroFurnace, "buildGenerator", false);
        this.registerAchievement("buildIndFurnace", 10, 2, Ic2Items.inductionFurnace, "buildElecFurnace", false);
        this.registerAchievement("buildCompressor", 4, 4, Ic2Items.compressor, "buildGenerator", false);
        this.registerAchievement("dieFromOwnNuke", 0, 4, Ic2Items.nuke, "compressUranium", true);
        this.registerAchievement("buildExtractor", 8, 4, Ic2Items.extractor, "buildGenerator", false);
        this.registerAchievement("buildBatBox", 6, 6, Ic2Items.batBox, "buildGenerator", false);
        this.registerAchievement("buildDrill", 8, 6, Ic2Items.miningDrill, "buildBatBox", false);
        this.registerAchievement("buildDDrill", 10, 6, Ic2Items.diamondDrill, "buildDrill", false);
        this.registerAchievement("buildChainsaw", 4, 6, Ic2Items.chainsaw, "buildBatBox", false);
        this.registerAchievement("killCreeperChainsaw", 2, 6, Ic2Items.chainsaw, "buildChainsaw", true);
        this.registerAchievement("buildMFE", 6, 8, Ic2Items.mfeUnit, "buildBatBox", false);
        this.registerAchievement("buildMassFab", 8, 8, Ic2Items.massFabricator, "buildBatBox", false);
        this.registerAchievement("buildQArmor", 12, 8, Ic2Items.quantumBodyarmor, "acquireMatter", false);
        this.registerAchievement("starveWithQHelmet", 14, 8, Ic2Items.filledTinCan, "buildQArmor", true);
        this.registerAchievement("buildMiningLaser", 4, 8, Ic2Items.miningLaser, "buildMFE", false);
        this.registerAchievement("killDragonMiningLaser", 2, 8, Ic2Items.miningLaser, "buildMiningLaser", true);
        this.registerAchievement("buildMFS", 6, 10, Ic2Items.mfsUnit, "buildMFE", false);
        this.registerAchievement("buildTeleporter", 4, 10, Ic2Items.teleporter, "buildMFS", false);
        this.registerAchievement("teleportFarAway", 2, 10, Ic2Items.teleporter, "buildTeleporter", true);
        this.registerAchievement("buildTerraformer", 8, 10, Ic2Items.terraformer, "buildMFS", false);
        this.registerAchievement("terraformEndCultivation", 10, 10, Ic2Items.cultivationTerraformerBlueprint, "buildTerraformer", true);
        AchievementPage.registerAchievementPage((AchievementPage)new AchievementPage("IndustrialCraft 2", this.achievementList.values().toArray(new Achievement[this.achievementList.size()])));
        MinecraftForge.EVENT_BUS.register((Object)this);
        FMLCommonHandler.instance().bus().register((Object)this);
    }

    public Achievement registerAchievement(String textId, int x, int y, ItemStack icon, Achievement requirement, boolean special) {
        Achievement achievement = new Achievement("ic2." + textId, textId, -4 + x, -5 + y, icon, requirement);
        if (special) {
            achievement.setSpecial();
        }
        achievement.registerStat();
        this.achievementList.put(textId, achievement);
        return achievement;
    }

    public Achievement registerAchievement(String textId, int x, int y, ItemStack icon, String requirement, boolean special) {
        Achievement achievement = new Achievement("ic2." + textId, textId, -4 + x, -5 + y, icon, this.getAchievement(requirement));
        if (special) {
            achievement.setSpecial();
        }
        achievement.registerStat();
        this.achievementList.put(textId, achievement);
        return achievement;
    }

    public void issueAchievement(EntityPlayer entityplayer, String textId) {
        if (this.achievementList.containsKey(textId)) {
            entityplayer.triggerAchievement((StatBase)this.achievementList.get(textId));
        }
    }

    public Achievement getAchievement(String textId) {
        if (this.achievementList.containsKey(textId)) {
            return this.achievementList.get(textId);
        }
        return null;
    }

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        EntityPlayer player = event.player;
        ItemStack stack = event.crafting;
        if (player == null) {
            return;
        }
        if (stack == null) {
            return;
        }
        if (stack.isItemEqual(Ic2Items.generator)) {
            this.issueAchievement(player, "buildGenerator");
        } else if (stack.getItem() == Ic2Items.insulatedCopperCableItem.getItem()) {
            this.issueAchievement(player, "buildCable");
        } else if (stack.isItemEqual(Ic2Items.macerator)) {
            this.issueAchievement(player, "buildMacerator");
        } else if (stack.isItemEqual(Ic2Items.electroFurnace)) {
            this.issueAchievement(player, "buildElecFurnace");
        } else if (stack.isItemEqual(Ic2Items.compressor)) {
            this.issueAchievement(player, "buildCompressor");
        } else if (stack.isItemEqual(Ic2Items.batBox)) {
            this.issueAchievement(player, "buildBatBox");
        } else if (stack.isItemEqual(Ic2Items.mfeUnit)) {
            this.issueAchievement(player, "buildMFE");
        } else if (stack.isItemEqual(Ic2Items.teleporter)) {
            this.issueAchievement(player, "buildTeleporter");
        } else if (stack.isItemEqual(Ic2Items.massFabricator)) {
            this.issueAchievement(player, "buildMassFab");
        } else if (stack.getItem() == Ic2Items.quantumBodyarmor.getItem() || stack.getItem() == Ic2Items.quantumBoots.getItem() || stack.getItem() == Ic2Items.quantumHelmet.getItem() || stack.getItem() == Ic2Items.quantumLeggings.getItem()) {
            this.issueAchievement(player, "buildQArmor");
        } else if (stack.isItemEqual(Ic2Items.extractor)) {
            this.issueAchievement(player, "buildExtractor");
        } else if (stack.getItem() == Ic2Items.miningDrill.getItem()) {
            this.issueAchievement(player, "buildDrill");
        } else if (stack.getItem() == Ic2Items.diamondDrill.getItem()) {
            this.issueAchievement(player, "buildDDrill");
        } else if (stack.getItem() == Ic2Items.chainsaw.getItem()) {
            this.issueAchievement(player, "buildChainsaw");
        } else if (stack.getItem() == Ic2Items.miningLaser.getItem()) {
            this.issueAchievement(player, "buildMiningLaser");
        } else if (stack.isItemEqual(Ic2Items.mfsUnit)) {
            this.issueAchievement(player, "buildMFS");
        } else if (stack.isItemEqual(Ic2Items.terraformer)) {
            this.issueAchievement(player, "buildTerraformer");
        } else if (stack.isItemEqual(Ic2Items.coalChunk)) {
            this.issueAchievement(player, "buildCoalDiamond");
        } else if (stack.isItemEqual(Ic2Items.inductionFurnace)) {
            this.issueAchievement(player, "buildIndFurnace");
        }
    }

    @SubscribeEvent
    public void onSmelting(PlayerEvent.ItemSmeltedEvent event) {
        EntityPlayer player = event.player;
        ItemStack stack = event.smelting;
        if (player == null) {
            return;
        }
        if (stack.getItem() == Items.iron_ingot) {
            this.issueAchievement(player, "acquireRefinedIron");
        }
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if (Ic2Items.copperOre != null && event.item.getEntityItem().isItemEqual(Ic2Items.copperOre) || Ic2Items.tinOre != null && event.item.getEntityItem().isItemEqual(Ic2Items.tinOre) || Ic2Items.uraniumOre != null && event.item.getEntityItem().isItemEqual(Ic2Items.uraniumOre)) {
            this.issueAchievement(event.entityPlayer, "mineOre");
        }
    }
}

