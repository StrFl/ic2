/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.BiomeGenBase
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 */
package ic2.core.util;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public final class BiomUtil {
    public static int gerBiomTemperature(World world, int xpos, int zpos) {
        if (BiomeDictionary.isBiomeOfType((BiomeGenBase)world.getWorldChunkManager().getBiomeGenAt(xpos, zpos), (BiomeDictionary.Type)BiomeDictionary.Type.HOT)) {
            return 45;
        }
        if (BiomeDictionary.isBiomeOfType((BiomeGenBase)world.getWorldChunkManager().getBiomeGenAt(xpos, zpos), (BiomeDictionary.Type)BiomeDictionary.Type.COLD)) {
            return 0;
        }
        return 25;
    }
}

