/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.FluidRegistry
 */
package ic2.core.block.generator.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.generator.container.ContainerWaterGenerator;
import ic2.core.block.generator.gui.GuiWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

public class TileEntityWaterGenerator
extends TileEntityBaseGenerator {
    public final Float waterbasevalue;
    public int ticker;
    public int water = 0;
    public int microStorage = 0;
    public int maxWater = 2000;
    public final InvSlotConsumableLiquid fuelSlot;

    public TileEntityWaterGenerator() {
        super(2, 1, 4);
        this.production = 2;
        this.ticker = IC2.random.nextInt(this.tickRate());
        this.waterbasevalue = Float.valueOf(ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/water"));
        this.fuelSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "fuel", 1, InvSlot.Access.NONE, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, FluidRegistry.WATER);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        this.updateWaterCount();
    }

    @Override
    public int gaugeFuelScaled(int i) {
        if (this.fuel <= 0) {
            return 0;
        }
        return this.fuel * i / this.maxWater;
    }

    @Override
    public boolean gainFuel() {
        if (this.fuel + 500 > this.maxWater) {
            return false;
        }
        if (!this.fuelSlot.isEmpty()) {
            ItemStack liquid = this.fuelSlot.consume(1);
            if (liquid == null) {
                return false;
            }
            this.fuel += 500;
            this.production = liquid.getItem().hasContainerItem(liquid) ? 1 : 2;
            return true;
        }
        if (this.fuel <= 0) {
            this.flowPower();
            this.production = this.microStorage / 100;
            this.microStorage -= this.production * 100;
            if (this.production > 0) {
                ++this.fuel;
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean gainFuelSub(ItemStack stack) {
        return false;
    }

    @Override
    public boolean needsFuel() {
        return this.fuel <= this.maxWater;
    }

    public void flowPower() {
        if (this.ticker++ % this.tickRate() == 0) {
            this.updateWaterCount();
        }
        this.water = Math.round((float)this.water * this.waterbasevalue.floatValue());
        if (this.water > 0) {
            this.microStorage += this.water;
        }
    }

    public void updateWaterCount() {
        int count = 0;
        for (int x = this.xCoord - 1; x < this.xCoord + 2; ++x) {
            for (int y = this.yCoord - 1; y < this.yCoord + 2; ++y) {
                for (int z = this.zCoord - 1; z < this.zCoord + 2; ++z) {
                    Block block = this.worldObj.getBlock(x, y, z);
                    if (block != Blocks.water && block != Blocks.flowing_water) continue;
                    ++count;
                }
            }
        }
        this.water = count;
    }

    @Override
    public String getInventoryName() {
        return "Water Mill";
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiWaterGenerator(new ContainerWaterGenerator(entityPlayer, this));
    }

    public int tickRate() {
        return 128;
    }

    @Override
    public String getOperationSoundFile() {
        return "Generators/WatermillLoop.ogg";
    }

    @Override
    public boolean delayActiveUpdate() {
        return true;
    }

    public ContainerBase<TileEntityWaterGenerator> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerWaterGenerator(entityPlayer, this);
    }
}

