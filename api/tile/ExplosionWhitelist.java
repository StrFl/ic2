/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 */
package ic2.api.tile;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;

public final class ExplosionWhitelist {
    private static Set<Block> whitelist = new HashSet<Block>();

    public static void addWhitelistedBlock(Block block) {
        whitelist.add(block);
    }

    public static void removeWhitelistedBlock(Block block) {
        whitelist.remove(block);
    }

    public static boolean isBlockWhitelisted(Block block) {
        return whitelist.contains(block);
    }
}

