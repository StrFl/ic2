/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IBlockCuttingBlade;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.BasicMachineRecipeManager;
import ic2.core.ContainerBase;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.container.ContainerBlockCutter;
import ic2.core.block.machine.gui.GuiBlockCutter;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityBlockCutter
extends TileEntityStandardMachine {
    private boolean bladetoweak = false;
    public final InvSlotConsumableClass cutterSlot;

    public TileEntityBlockCutter() {
        super(48, 900, 1, 2);
        this.inputSlot = new InvSlotProcessableGeneric((TileEntityInventory)this, "input", 0, 1, Recipes.blockcutter);
        this.cutterSlot = new InvSlotConsumableClass((TileEntityInventory)this, "cutterSlot", 1, 1, IBlockCuttingBlade.class);
    }

    public static void init() {
        Recipes.blockcutter = new BasicMachineRecipeManager();
    }

    @Override
    public RecipeOutput getOutput() {
        RecipeOutput ret;
        if (this.cutterSlot.isEmpty()) {
            if (!this.bladetoweak) {
                this.bladetoweak = true;
            }
            return null;
        }
        if (this.bladetoweak) {
            this.bladetoweak = false;
        }
        if ((ret = super.getOutput()) != null) {
            if (ret.metadata == null) {
                return null;
            }
            if (ret.metadata.getInteger("hardness") > ((IBlockCuttingBlade)this.cutterSlot.get().getItem()).gethardness()) {
                if (!this.bladetoweak) {
                    this.bladetoweak = true;
                }
                return null;
            }
            if (this.bladetoweak) {
                this.bladetoweak = false;
            }
        }
        return ret;
    }

    @Override
    public String getInventoryName() {
        return "BlockCutter";
    }

    public ContainerBase<TileEntityBlockCutter> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerBlockCutter(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiBlockCutter(new ContainerBlockCutter(entityPlayer, this));
    }

    public boolean isbladetoweak() {
        return this.bladetoweak;
    }

    @Override
    public float getWrenchDropRate() {
        return 0.8f;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

