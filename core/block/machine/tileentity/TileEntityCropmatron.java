/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.IFluidTank
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankElectricMachine;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.container.ContainerCropmatron;
import ic2.core.block.machine.gui.GuiCropmatron;
import ic2.core.crop.TileEntityCrop;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityCropmatron
extends TileEntityLiquidTankElectricMachine
implements IHasGui {
    public int scanX = -5;
    public int scanY = -1;
    public int scanZ = -5;
    public final InvSlotConsumable fertilizerSlot = new InvSlotConsumableId((TileEntityInventory)this, "fertilizer", 1, 7, Ic2Items.fertilizer.getItem());
    public final InvSlotConsumable weedExSlot = new InvSlotConsumableId((TileEntityInventory)this, "weedEx", 8, 7, Ic2Items.weedEx.getItem());
    public final InvSlotOutput wasseroutputSlot;
    public final InvSlotConsumableLiquidByTank wasserinputSlot = new InvSlotConsumableLiquidByTank(this, "wasserinputSlot", 1, InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, (IFluidTank)this.fluidTank);

    public TileEntityCropmatron() {
        super(1000, 1, 0, 2);
        this.wasseroutputSlot = new InvSlotOutput(this, "wasseroutputSlot", 2, 1);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.wasseroutputSlot.add(processResult);
        }
        this.fertilizerSlot.organize();
        this.weedExSlot.organize();
        if (this.energy >= 31.0) {
            this.scan();
        }
    }

    public void scan() {
        ++this.scanX;
        if (this.scanX > 5) {
            this.scanX = -5;
            ++this.scanZ;
            if (this.scanZ > 5) {
                this.scanZ = -5;
                ++this.scanY;
                if (this.scanY > 1) {
                    this.scanY = -1;
                }
            }
        }
        this.energy -= 1.0;
        TileEntity te = this.worldObj.getTileEntity(this.xCoord + this.scanX, this.yCoord + this.scanY, this.zCoord + this.scanZ);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop)te;
            if (!this.fertilizerSlot.isEmpty() && this.fertilizerSlot.consume(1, true, false) != null && crop.applyFertilizer(false)) {
                this.energy -= 10.0;
                this.fertilizerSlot.consume(1);
            }
            if (this.getTankAmount() > 0 && crop.applyHydration(this)) {
                this.energy -= 10.0;
            }
            if (!this.weedExSlot.isEmpty() && this.weedExSlot.damage(1, true) != null && crop.applyWeedEx(false)) {
                this.energy -= 10.0;
                this.weedExSlot.damage(1, false);
            }
        }
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.wasserinputSlot.isEmpty() && this.wasserinputSlot.transferToTank((IFluidTank)this.fluidTank, (MutableObject<ItemStack>)(output = new MutableObject()), simulate) && (output.getValue() == null || this.wasseroutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    public String getInventoryName() {
        return "Crop-Matron";
    }

    public ContainerBase<TileEntityCropmatron> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerCropmatron(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiCropmatron(new ContainerCropmatron(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return FluidRegistry.WATER.getID() == fluid.getID();
    }
}

