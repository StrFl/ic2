/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.block.generator.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.WorldData;
import ic2.core.block.generator.container.ContainerWindGenerator;
import ic2.core.block.generator.gui.GuiWindGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TileEntityWindGenerator
extends TileEntityBaseGenerator {
    public double subproduction = 0.0;
    public double substorage = 0.0;
    public final Float windbasevalue;
    public int ticker = IC2.random.nextInt(this.tickRate());
    public int obscuratedBlockCount;

    public TileEntityWindGenerator() {
        super(4, 1, 5);
        this.windbasevalue = Float.valueOf(ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/wind"));
    }

    @Override
    public int gaugeFuelScaled(int i) {
        double prod = this.subproduction / 3.0;
        int re = (int)(prod * (double)i);
        if (re < 0) {
            return 0;
        }
        if (re > i) {
            return i;
        }
        return re;
    }

    public int getOverheatScaled(int i) {
        double prod = (this.subproduction - 5.0) / 5.0;
        if (this.subproduction <= 5.0) {
            return 0;
        }
        if (this.subproduction >= 10.0) {
            return i;
        }
        return (int)(prod * (double)i);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        this.updateObscuratedBlockCount();
    }

    @Override
    public boolean gainEnergy() {
        ++this.ticker;
        if (this.ticker % this.tickRate() == 0) {
            if (this.ticker % (8 * this.tickRate()) == 0) {
                this.updateObscuratedBlockCount();
            }
            this.subproduction = WorldData.get((World)this.worldObj).windSim.getWindAt(this.yCoord) / 10.0 * (1.0 - (double)this.obscuratedBlockCount / 567.0);
            if (this.subproduction <= 0.0) {
                return false;
            }
            if (this.subproduction > 5.0 && (double)this.worldObj.rand.nextInt(5000) <= this.subproduction - 5.0) {
                this.subproduction = 0.0;
                this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, StackUtil.getBlock(Ic2Items.generator), Ic2Items.generator.getItemDamage(), 7);
                for (int i = this.worldObj.rand.nextInt(5); i > 0; --i) {
                    StackUtil.dropAsEntity(this.worldObj, this.xCoord, this.yCoord, this.zCoord, new ItemStack(Items.iron_ingot));
                }
                return false;
            }
            this.subproduction *= (double)this.windbasevalue.floatValue();
        }
        this.substorage += this.subproduction;
        this.production = (short)this.substorage;
        if (this.storage + (double)this.production >= (double)this.maxStorage) {
            this.substorage = 0.0;
            return false;
        }
        this.storage += (double)this.production;
        this.substorage -= (double)this.production;
        return true;
    }

    @Override
    public boolean gainFuel() {
        return false;
    }

    public void updateObscuratedBlockCount() {
        this.obscuratedBlockCount = -1;
        for (int x = -4; x < 5; ++x) {
            for (int y = -2; y < 5; ++y) {
                for (int z = -4; z < 5; ++z) {
                    if (this.worldObj.isAirBlock(x + this.xCoord, y + this.yCoord, z + this.zCoord)) continue;
                    ++this.obscuratedBlockCount;
                }
            }
        }
    }

    @Override
    public boolean needsFuel() {
        return true;
    }

    @Override
    public String getInventoryName() {
        return "Wind Mill";
    }

    public int tickRate() {
        return 128;
    }

    @Override
    public String getOperationSoundFile() {
        return "Generators/WindGenLoop.ogg";
    }

    @Override
    public boolean delayActiveUpdate() {
        return true;
    }

    public ContainerBase<TileEntityWindGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerWindGenerator(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiWindGenerator(new ContainerWindGenerator(entityPlayer, this));
    }
}

