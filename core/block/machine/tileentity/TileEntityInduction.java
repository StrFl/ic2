/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.comp.Redstone;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableSmelting;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerInduction;
import ic2.core.block.machine.gui.GuiInduction;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityInduction
extends TileEntityElectricMachine
implements IHasGui,
IUpgradableBlock {
    public int soundTicker = IC2.random.nextInt(64);
    public static short maxHeat = (short)10000;
    public short heat = 0;
    public short progress = 0;
    public final InvSlotProcessable inputSlotA = new InvSlotProcessableSmelting(this, "inputA", 0, 1);
    public final InvSlotProcessable inputSlotB = new InvSlotProcessableSmelting(this, "inputB", 1, 1);
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlotOutput outputSlotA = new InvSlotOutput(this, "outputA", 3, 1);
    public final InvSlotOutput outputSlotB = new InvSlotOutput(this, "outputB", 4, 1);
    protected final Redstone redstone;

    public TileEntityInduction() {
        super(maxHeat, 2, 2);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 5, 2);
        this.redstone = this.addComponent(new Redstone(this));
    }

    @Override
    public String getInventoryName() {
        if (IC2.platform.isRendering()) {
            return "Induction Furnace";
        }
        return "InductionFurnace";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.heat = nbttagcompound.getShort("heat");
        this.progress = nbttagcompound.getShort("progress");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("heat", this.heat);
        nbttagcompound.setShort("progress", this.progress);
    }

    public String getHeat() {
        return "" + this.heat * 100 / maxHeat + "%";
    }

    public int gaugeProgressScaled(int i) {
        return i * this.progress / 4000;
    }

    public int gaugeFuelScaled(int i) {
        return (int)((double)i * Math.min(this.energy, (double)this.maxEnergy) / (double)this.maxEnergy);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        boolean newActive = this.getActive();
        if (this.heat == 0) {
            newActive = false;
        }
        if (this.progress >= 4000) {
            this.operate();
            needsInvUpdate = true;
            this.progress = 0;
            newActive = false;
        }
        boolean canOperate = this.canOperate();
        if (this.energy > 0.0 && (canOperate || this.redstone.hasRedstoneInput())) {
            this.energy -= 1.0;
            if (this.heat < maxHeat) {
                this.heat = (short)(this.heat + 1);
            }
            newActive = true;
        } else {
            this.heat = (short)(this.heat - Math.min(this.heat, 4));
        }
        if (!newActive || this.progress == 0) {
            if (canOperate) {
                if (this.energy >= 15.0) {
                    newActive = true;
                }
            } else {
                this.progress = 0;
            }
        } else if (!canOperate || this.energy < 15.0) {
            if (!canOperate) {
                this.progress = 0;
            }
            newActive = false;
        }
        if (newActive && canOperate) {
            this.progress = (short)(this.progress + this.heat / 30);
            this.energy -= 15.0;
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (newActive != this.getActive()) {
            this.setActive(newActive);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    public void operate() {
        this.operate(this.inputSlotA, this.outputSlotA);
        this.operate(this.inputSlotB, this.outputSlotB);
    }

    public void operate(InvSlotProcessable inputSlot, InvSlotOutput outputSlot) {
        if (!this.canOperate(inputSlot, outputSlot)) {
            return;
        }
        outputSlot.add(inputSlot.process().items);
        inputSlot.consume();
    }

    public boolean canOperate() {
        return this.canOperate(this.inputSlotA, this.outputSlotA) || this.canOperate(this.inputSlotB, this.outputSlotB);
    }

    public boolean canOperate(InvSlotProcessable inputSlot, InvSlotOutput outputSlot) {
        if (inputSlot.isEmpty()) {
            return false;
        }
        RecipeOutput output = inputSlot.process();
        if (output == null) {
            return false;
        }
        return outputSlot.canAdd(output.items);
    }

    public ContainerBase<TileEntityInduction> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerInduction(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiInduction(new ContainerInduction(entityPlayer, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }

    @Override
    public float getWrenchDropRate() {
        return 0.8f;
    }

    @Override
    public double getEnergy() {
        return this.energy;
    }

    @Override
    public boolean useEnergy(double amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
            return true;
        }
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.RedstoneSensitive, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

