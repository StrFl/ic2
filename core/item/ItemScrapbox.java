/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDispenser
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.oredict.OreDictionary
 */
package ic2.core.item;

import ic2.api.recipe.IScrapboxManager;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.BehaviorScrapboxDispense;
import ic2.core.item.ItemIC2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemScrapbox
extends ItemIC2 {
    public ItemScrapbox(InternalName internalName) {
        super(internalName);
        BlockDispenser.dispenseBehaviorRegistry.putObject((Object)this, (Object)new BehaviorScrapboxDispense());
    }

    public static void init() {
        ArrayList ores;
        Recipes.scrapboxDrops = new ScrapboxRecipeManager();
        if (IC2.suddenlyHoes) {
            ItemScrapbox.addDrop(Items.wooden_hoe, 9001.0f);
        } else {
            ItemScrapbox.addDrop(Items.wooden_hoe, 5.01f);
        }
        ItemScrapbox.addDrop(Blocks.dirt, 5.0f);
        ItemScrapbox.addDrop(Items.stick, 4.0f);
        ItemScrapbox.addDrop((Block)Blocks.grass, 3.0f);
        ItemScrapbox.addDrop(Blocks.gravel, 3.0f);
        ItemScrapbox.addDrop(Blocks.netherrack, 2.0f);
        ItemScrapbox.addDrop(Items.rotten_flesh, 2.0f);
        ItemScrapbox.addDrop(Items.apple, 1.5f);
        ItemScrapbox.addDrop(Items.bread, 1.5f);
        ItemScrapbox.addDrop(Ic2Items.filledTinCan.getItem(), 1.5f);
        ItemScrapbox.addDrop(Items.wooden_sword);
        ItemScrapbox.addDrop(Items.wooden_shovel);
        ItemScrapbox.addDrop(Items.wooden_pickaxe);
        ItemScrapbox.addDrop(Blocks.soul_sand);
        ItemScrapbox.addDrop(Items.sign);
        ItemScrapbox.addDrop(Items.leather);
        ItemScrapbox.addDrop(Items.feather);
        ItemScrapbox.addDrop(Items.bone);
        ItemScrapbox.addDrop(Items.cooked_porkchop, 0.9f);
        ItemScrapbox.addDrop(Items.cooked_beef, 0.9f);
        ItemScrapbox.addDrop(Blocks.pumpkin, 0.9f);
        ItemScrapbox.addDrop(Items.cooked_chicken, 0.9f);
        ItemScrapbox.addDrop(Items.minecart, 0.01f);
        ItemScrapbox.addDrop(Items.redstone, 0.9f);
        ItemScrapbox.addDrop(Ic2Items.rubber.getItem(), 0.8f);
        ItemScrapbox.addDrop(Items.glowstone_dust, 0.8f);
        ItemScrapbox.addDrop(Ic2Items.coalDust, 0.8f);
        ItemScrapbox.addDrop(Ic2Items.copperDust, 0.8f);
        ItemScrapbox.addDrop(Ic2Items.tinDust, 0.8f);
        ItemScrapbox.addDrop(Ic2Items.suBattery.getItem(), 0.7f);
        ItemScrapbox.addDrop(Ic2Items.ironDust, 0.7f);
        ItemScrapbox.addDrop(Ic2Items.goldDust, 0.7f);
        ItemScrapbox.addDrop(Items.slime_ball, 0.6f);
        ItemScrapbox.addDrop(Blocks.iron_ore, 0.5f);
        ItemScrapbox.addDrop((Item)Items.golden_helmet, 0.01f);
        ItemScrapbox.addDrop(Blocks.gold_ore, 0.5f);
        ItemScrapbox.addDrop(Items.cake, 0.5f);
        ItemScrapbox.addDrop(Items.diamond, 0.1f);
        ItemScrapbox.addDrop(Items.emerald, 0.05f);
        ItemScrapbox.addDrop(Items.ender_pearl, 0.08f);
        ItemScrapbox.addDrop(Items.blaze_rod, 0.04f);
        ItemScrapbox.addDrop(Items.egg, 0.8f);
        if (Ic2Items.copperOre != null) {
            ItemScrapbox.addDrop(Ic2Items.copperOre.getItem(), 0.7f);
        } else {
            ores = OreDictionary.getOres((String)"oreCopper");
            if (!ores.isEmpty()) {
                ItemScrapbox.addDrop(((ItemStack)ores.get(0)).copy(), 0.7f);
            }
        }
        if (Ic2Items.tinOre != null) {
            ItemScrapbox.addDrop(Ic2Items.tinOre.getItem(), 0.7f);
        } else {
            ores = OreDictionary.getOres((String)"oreTin");
            if (!ores.isEmpty()) {
                ItemScrapbox.addDrop(((ItemStack)ores.get(0)).copy(), 0.7f);
            }
        }
    }

    public static void addDrop(Item item) {
        ItemScrapbox.addDrop(new ItemStack(item), 1.0f);
    }

    public static void addDrop(Item item, float chance) {
        ItemScrapbox.addDrop(new ItemStack(item), chance);
    }

    public static void addDrop(Block block) {
        ItemScrapbox.addDrop(new ItemStack(block), 1.0f);
    }

    public static void addDrop(Block block, float chance) {
        ItemScrapbox.addDrop(new ItemStack(block), chance);
    }

    public static void addDrop(ItemStack item) {
        ItemScrapbox.addDrop(item, 1.0f);
    }

    public static void addDrop(ItemStack item, float chance) {
        Recipes.scrapboxDrops.addDrop(item, chance);
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        ItemStack itemStack;
        if (IC2.platform.isSimulating() && (itemStack = Recipes.scrapboxDrops.getDrop(itemstack, !entityplayer.capabilities.isCreativeMode)) != null) {
            entityplayer.dropPlayerItemWithRandomChoice(itemStack, false);
        }
        return itemstack;
    }

    static class ScrapboxRecipeManager
    implements IScrapboxManager {
        private final List<Drop> drops = new ArrayList<Drop>();

        ScrapboxRecipeManager() {
        }

        @Override
        public void addDrop(ItemStack drop, float rawChance) {
            this.drops.add(new Drop(drop, rawChance));
        }

        @Override
        public ItemStack getDrop(ItemStack input, boolean adjustInput) {
            if (this.drops.isEmpty()) {
                return null;
            }
            if (adjustInput) {
                --input.stackSize;
            }
            float chance = IC2.random.nextFloat() * Drop.topChance;
            int low = 0;
            int high = this.drops.size() - 1;
            while (low < high) {
                int mid = (high + low) / 2;
                if (chance < this.drops.get((int)mid).upperChanceBound) {
                    high = mid;
                    continue;
                }
                low = mid + 1;
            }
            return this.drops.get((int)low).item.copy();
        }

        @Override
        public Map<ItemStack, Float> getDrops() {
            HashMap<ItemStack, Float> ret = new HashMap<ItemStack, Float>(this.drops.size());
            for (Drop drop : this.drops) {
                ret.put(drop.item, Float.valueOf(drop.originalChance.floatValue() / Drop.topChance));
            }
            return ret;
        }
    }

    static class Drop {
        ItemStack item;
        Float originalChance;
        float upperChanceBound;
        static float topChance;

        Drop(ItemStack item1, float chance) {
            this.item = item1;
            this.originalChance = Float.valueOf(chance);
            this.upperChanceBound = topChance += chance;
        }
    }
}

