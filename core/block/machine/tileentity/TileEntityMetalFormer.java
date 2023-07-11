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
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;
import ic2.core.BasicMachineRecipeManager;
import ic2.core.ContainerBase;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.container.ContainerMetalFormer;
import ic2.core.block.machine.gui.GuiMetalFormer;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.init.MainConfig;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityMetalFormer
extends TileEntityStandardMachine
implements INetworkClientTileEntityEventListener {
    private int mode;
    private static final int EventSwitch = 0;

    public TileEntityMetalFormer() {
        super(10, 200, 1);
        this.inputSlot = new InvSlotProcessableGeneric((TileEntityInventory)this, "input", 0, 1, Recipes.metalformerExtruding);
    }

    public static void init() {
        Recipes.metalformerExtruding = new BasicMachineRecipeManager();
        Recipes.metalformerCutting = new BasicMachineRecipeManager();
        Recipes.metalformerRolling = new BasicMachineRecipeManager();
        if (ConfigUtil.getBool(MainConfig.get(), "recipes/allowCoinCrafting")) {
            TileEntityMetalFormer.addRecipeCutting(Ic2Items.casingiron, 1, StackUtil.copyWithSize(Ic2Items.coin, 2));
        }
    }

    public static void addRecipeCutting(ItemStack input, int amount, ItemStack output) {
        TileEntityMetalFormer.addRecipeCutting(new RecipeInputItemStack(input, amount), output);
    }

    public static void addRecipeCutting(IRecipeInput input, ItemStack output) {
        Recipes.metalformerCutting.addRecipe(input, null, output);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.setMode(nbttagcompound.getInteger("mode"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("mode", this.mode);
    }

    @Override
    public String getInventoryName() {
        return "MetalFormer";
    }

    public ContainerBase<TileEntityMetalFormer> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerMetalFormer(entityPlayer, this);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiMetalFormer(new ContainerMetalFormer(entityPlayer, this));
    }

    @Override
    public float getWrenchDropRate() {
        return 0.85f;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                this.cycleMode();
            }
        }
    }

    @Override
    public void onNetworkUpdate(String field) {
        super.onNetworkUpdate(field);
        if (field.equals("mode")) {
            this.setMode(this.mode);
        }
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode1) {
        InvSlotProcessableGeneric slot = (InvSlotProcessableGeneric)this.inputSlot;
        switch (mode1) {
            case 0: {
                slot.setRecipeManager(Recipes.metalformerExtruding);
                break;
            }
            case 1: {
                slot.setRecipeManager(Recipes.metalformerRolling);
                break;
            }
            case 2: {
                slot.setRecipeManager(Recipes.metalformerCutting);
                break;
            }
            default: {
                throw new RuntimeException("invalid mode: " + mode1);
            }
        }
        this.mode = mode1;
    }

    private void cycleMode() {
        this.setMode((this.getMode() + 1) % 3);
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

