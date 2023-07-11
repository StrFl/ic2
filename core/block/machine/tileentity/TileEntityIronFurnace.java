/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableSmelting;
import ic2.core.block.machine.container.ContainerIronFurnace;
import ic2.core.block.machine.gui.GuiIronFurnace;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityIronFurnace
extends TileEntityInventory
implements IHasGui {
    public int fuel = 0;
    public int maxFuel = 0;
    public short progress = 0;
    public final short operationLength = (short)160;
    public final InvSlotProcessable inputSlot = new InvSlotProcessableSmelting(this, "input", 0, 1);
    public final InvSlotOutput outputSlot = new InvSlotOutput(this, "output", 2, 1);
    public final InvSlotConsumableFuel fuelSlot = new InvSlotConsumableFuel((TileEntityInventory)this, "fuel", 1, 1, true);

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        try {
            this.fuel = nbttagcompound.getInteger("fuel");
        }
        catch (Throwable e) {
            this.fuel = nbttagcompound.getShort("fuel");
        }
        try {
            this.maxFuel = nbttagcompound.getInteger("maxFuel");
        }
        catch (Throwable e) {
            this.maxFuel = nbttagcompound.getShort("maxFuel");
        }
        this.progress = nbttagcompound.getShort("progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("fuel", this.fuel);
        nbttagcompound.setInteger("maxFuel", this.maxFuel);
        nbttagcompound.setShort("progress", this.progress);
    }

    public int gaugeProgressScaled(int i) {
        return this.progress * i / 160;
    }

    public int gaugeFuelScaled(int i) {
        if (this.maxFuel == 0) {
            this.maxFuel = this.fuel;
            if (this.maxFuel == 0) {
                this.maxFuel = 160;
            }
        }
        return this.fuel * i / this.maxFuel;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.fuel <= 0 && this.canOperate()) {
            this.fuel = this.maxFuel = this.fuelSlot.consumeFuel();
            if (this.fuel > 0) {
                needsInvUpdate = true;
            }
        }
        if (this.isBurning() && this.canOperate()) {
            this.progress = (short)(this.progress + 1);
            if (this.progress >= 160) {
                this.progress = 0;
                this.operate();
                needsInvUpdate = true;
            }
        } else {
            this.progress = 0;
        }
        if (this.fuel > 0) {
            --this.fuel;
        }
        if (this.getActive() != this.isBurning()) {
            this.setActive(this.isBurning());
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    public void operate() {
        this.outputSlot.add(this.inputSlot.process().items);
        this.inputSlot.consume();
    }

    public boolean isBurning() {
        return this.fuel > 0;
    }

    public boolean canOperate() {
        RecipeOutput output = this.inputSlot.process();
        if (output == null) {
            return false;
        }
        return this.outputSlot.canAdd(output.items);
    }

    public ItemStack getResultFor(ItemStack itemstack) {
        return FurnaceRecipes.smelting().getSmeltingResult(itemstack);
    }

    @Override
    public String getInventoryName() {
        return "Iron Furnace";
    }

    public ContainerBase<TileEntityIronFurnace> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerIronFurnace(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiIronFurnace(new ContainerIronFurnace(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }
}

