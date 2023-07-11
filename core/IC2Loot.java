/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.WeightedRandomChestContent
 *  net.minecraftforge.common.ChestGenHooks
 */
package ic2.core;

import ic2.core.Ic2Items;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class IC2Loot {
    private static final WeightedRandomChestContent[] MINESHAFT_CORRIDOR = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.bronzePickaxe.copy(), 1, 1, 1), new WeightedRandomChestContent(Ic2Items.filledTinCan.copy(), 4, 16, 8), new WeightedRandomChestContent(Ic2Items.iridiumShard.copy(), 2, 5, 8)};
    private static final WeightedRandomChestContent[] STRONGHOLD_CORRIDOR = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.iridiumOre.copy(), 1, 4, 1), new WeightedRandomChestContent(Ic2Items.iridiumShard.copy(), 4, 14, 8)};
    private static final WeightedRandomChestContent[] STRONGHOLD_CROSSING = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.bronzePickaxe.copy(), 1, 1, 1), new WeightedRandomChestContent(Ic2Items.iridiumOre.copy(), 1, 1, 1), new WeightedRandomChestContent(Ic2Items.iridiumShard.copy(), 3, 7, 4)};
    private static final WeightedRandomChestContent[] VILLAGE_BLACKSMITH = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.bronzeIngot.copy(), 2, 4, 5), new WeightedRandomChestContent(Ic2Items.iridiumShard.copy(), 3, 7, 4)};
    private static final WeightedRandomChestContent[] BONUS_CHEST = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.treetap.copy(), 1, 1, 2)};
    private static final WeightedRandomChestContent[] DUNGEON_CHEST = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.copperIngot.copy(), 2, 5, 100), new WeightedRandomChestContent(Ic2Items.tinIngot.copy(), 2, 5, 100), new WeightedRandomChestContent(Ic2Items.iridiumOre.copy(), 1, 2, 20), new WeightedRandomChestContent(Ic2Items.iridiumShard.copy(), 6, 14, 100), new WeightedRandomChestContent(new ItemStack(Items.record_blocks), 1, 1, 5), new WeightedRandomChestContent(new ItemStack(Items.record_chirp), 1, 1, 5), new WeightedRandomChestContent(new ItemStack(Items.record_far), 1, 1, 5), new WeightedRandomChestContent(new ItemStack(Items.record_mall), 1, 1, 5), new WeightedRandomChestContent(new ItemStack(Items.record_mellohi), 1, 1, 5), new WeightedRandomChestContent(new ItemStack(Items.record_stal), 1, 1, 5), new WeightedRandomChestContent(new ItemStack(Items.record_strad), 1, 1, 5), new WeightedRandomChestContent(new ItemStack(Items.record_ward), 1, 1, 5), new WeightedRandomChestContent(new ItemStack(Items.record_11), 1, 1, 5), new WeightedRandomChestContent(new ItemStack(Items.record_wait), 1, 1, 5)};
    private static final WeightedRandomChestContent[] bronzeToolsArmor = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.bronzePickaxe.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeSword.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeHelmet.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeChestplate.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeLeggings.copy(), 1, 1, 3), new WeightedRandomChestContent(Ic2Items.bronzeBoots.copy(), 1, 1, 3)};
    private static final WeightedRandomChestContent[] ingots = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.copperIngot.copy(), 2, 6, 9), new WeightedRandomChestContent(Ic2Items.tinIngot.copy(), 1, 5, 8)};
    private static WeightedRandomChestContent[] rubberSapling = new WeightedRandomChestContent[0];

    public IC2Loot() {
        if (Ic2Items.rubberSapling != null) {
            rubberSapling = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Ic2Items.rubberSapling.copy(), 1, 4, 4)};
        }
        IC2Loot.addLoot("mineshaftCorridor", MINESHAFT_CORRIDOR, ingots);
        IC2Loot.addLoot("pyramidDesertyChest", bronzeToolsArmor, ingots);
        IC2Loot.addLoot("pyramidJungleChest", bronzeToolsArmor, ingots);
        IC2Loot.addLoot("strongholdCorridor", STRONGHOLD_CORRIDOR, bronzeToolsArmor, ingots);
        IC2Loot.addLoot("strongholdCrossing", STRONGHOLD_CROSSING, bronzeToolsArmor, ingots);
        IC2Loot.addLoot("villageBlacksmith", VILLAGE_BLACKSMITH, bronzeToolsArmor, ingots, rubberSapling);
        IC2Loot.addLoot("bonusChest", new WeightedRandomChestContent[][]{BONUS_CHEST});
        IC2Loot.addLoot("dungeonChest", new WeightedRandomChestContent[][]{DUNGEON_CHEST});
    }

    private static void addLoot(String category, WeightedRandomChestContent[] ... loot) {
        ChestGenHooks cgh = ChestGenHooks.getInfo((String)category);
        WeightedRandomChestContent[][] weightedRandomChestContentArray = loot;
        int n = weightedRandomChestContentArray.length;
        for (int i = 0; i < n; ++i) {
            WeightedRandomChestContent[] lootList;
            for (WeightedRandomChestContent lootEntry : lootList = weightedRandomChestContentArray[i]) {
                cgh.addItem(lootEntry);
            }
        }
    }
}

