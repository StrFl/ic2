/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.BiomeGenDesert
 */
package ic2.core.block.generator.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.generator.container.ContainerSolarGenerator;
import ic2.core.block.generator.gui.GuiSolarGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenDesert;

public class TileEntitySolarGenerator
extends TileEntityBaseGenerator {
    public final Double solarbasevalue;
    public int ticker = IC2.random.nextInt(this.tickRate());
    public boolean sunIsVisible = false;

    public TileEntitySolarGenerator() {
        super(1, 1, 2);
        this.solarbasevalue = ConfigUtil.getDouble(MainConfig.get(), "balance/energy/generator/solar");
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        this.updateSunVisibility();
    }

    @Override
    public int gaugeFuelScaled(int i) {
        return i;
    }

    @Override
    public boolean gainEnergy() {
        if (this.ticker++ % this.tickRate() == 0) {
            this.updateSunVisibility();
        }
        if (this.sunIsVisible) {
            this.storage += this.solarbasevalue * (double)((float)TileEntitySolarGenerator.getskylight(this.worldObj, this.xCoord, this.zCoord) / 15.0f);
            return true;
        }
        return false;
    }

    @Override
    public boolean gainFuel() {
        return false;
    }

    private static int getskylight(World world, int x, int z) {
        return world.getBlockLightValue(x, 255, z);
    }

    public void updateSunVisibility() {
        this.sunIsVisible = TileEntitySolarGenerator.isSunVisible(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord);
    }

    public static boolean isSunVisible(World world, int x, int y, int z) {
        return TileEntitySolarGenerator.getskylight(world, x, z) > 4 && !world.provider.hasNoSky && world.canBlockSeeTheSky(x, y, z) && (world.getWorldChunkManager().getBiomeGenAt(x, z) instanceof BiomeGenDesert || !world.isRaining() && !world.isThundering());
    }

    @Override
    public boolean needsFuel() {
        return true;
    }

    @Override
    public String getInventoryName() {
        return "Solar Panel";
    }

    public ContainerBase<TileEntitySolarGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerSolarGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiSolarGenerator(new ContainerSolarGenerator(entityPlayer, this));
    }

    public int tickRate() {
        return 128;
    }

    @Override
    public boolean delayActiveUpdate() {
        return true;
    }
}

