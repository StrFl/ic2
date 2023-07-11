/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraft.world.World
 */
package ic2.api.crops;

import ic2.api.crops.CropCard;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public interface ICropTile {
    public CropCard getCrop();

    public void setCrop(CropCard var1);

    @Deprecated
    public short getID();

    @Deprecated
    public void setID(short var1);

    public byte getSize();

    public void setSize(byte var1);

    public byte getGrowth();

    public void setGrowth(byte var1);

    public byte getGain();

    public void setGain(byte var1);

    public byte getResistance();

    public void setResistance(byte var1);

    public byte getScanLevel();

    public void setScanLevel(byte var1);

    public NBTTagCompound getCustomData();

    public int getNutrientStorage();

    public void setNutrientStorage(int var1);

    public int getHydrationStorage();

    public void setHydrationStorage(int var1);

    public int getWeedExStorage();

    public void setWeedExStorage(int var1);

    public byte getHumidity();

    public byte getNutrients();

    public byte getAirQuality();

    public World getWorld();

    public ChunkCoordinates getLocation();

    public int getLightLevel();

    public boolean pick(boolean var1);

    public boolean harvest(boolean var1);

    public ItemStack[] harvest_automated(boolean var1);

    public void reset();

    public void updateState();

    public boolean isBlockBelow(Block var1);

    public boolean isBlockBelow(String var1);

    public ItemStack generateSeeds(CropCard var1, byte var2, byte var3, byte var4, byte var5);

    @Deprecated
    public ItemStack generateSeeds(short var1, byte var2, byte var3, byte var4, byte var5);
}

