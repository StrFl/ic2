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
import ic2.api.recipe.IMachineRecipeManagerExt;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.BasicListRecipeManager;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.gui.GuiRecycler;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.init.MainConfig;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityRecycler
extends TileEntityStandardMachine {
    public TileEntityRecycler() {
        super(1, 45, 1);
        this.inputSlot = new InvSlotProcessableGeneric((TileEntityInventory)this, "input", 0, 1, Recipes.recycler);
    }

    public static void init() {
        Recipes.recycler = new RecyclerRecipeManager();
        Recipes.recyclerWhitelist = new BasicListRecipeManager();
        Recipes.recyclerBlacklist = new BasicListRecipeManager();
    }

    public static void initLate() {
        for (IRecipeInput input : ConfigUtil.asRecipeInputList(MainConfig.get(), "balance/recyclerBlacklist")) {
            Recipes.recyclerBlacklist.add(input);
        }
        for (IRecipeInput input : ConfigUtil.asRecipeInputList(MainConfig.get(), "balance/recyclerWhitelist")) {
            Recipes.recyclerWhitelist.add(input);
        }
    }

    @Override
    public String getInventoryName() {
        return "Recycler";
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiRecycler(new ContainerStandardMachine<TileEntityRecycler>(entityPlayer, this));
    }

    public static int recycleChance() {
        return 8;
    }

    @Override
    public String getStartSoundFile() {
        return "Machines/RecyclerOp.ogg";
    }

    @Override
    public String getInterruptSoundFile() {
        return "Machines/InterruptOne.ogg";
    }

    @Override
    public float getWrenchDropRate() {
        return 0.85f;
    }

    public static boolean getIsItemBlacklisted(ItemStack aStack) {
        if (Recipes.recyclerWhitelist.isEmpty()) {
            return Recipes.recyclerBlacklist.contains(aStack);
        }
        return !Recipes.recyclerWhitelist.contains(aStack);
    }

    @Override
    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        this.inputSlot.consume();
        if (IC2.random.nextInt(TileEntityRecycler.recycleChance()) == 0) {
            this.outputSlot.add(processResult);
        }
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }

    private static class RecyclerRecipeManager
    implements IMachineRecipeManagerExt {
        @Override
        public void addRecipe(IRecipeInput input, NBTTagCompound metadata, ItemStack ... outputs) {
        }

        @Override
        public boolean addRecipe(IRecipeInput input, NBTTagCompound metadata, boolean overwrite, ItemStack ... outputs) {
            return false;
        }

        @Override
        public RecipeOutput getOutputFor(ItemStack input, boolean adjustInput) {
            RecipeOutput ret = !TileEntityRecycler.getIsItemBlacklisted(input) ? new RecipeOutput(null, Ic2Items.scrap) : new RecipeOutput(null, new ItemStack[0]);
            if (adjustInput) {
                --input.stackSize;
            }
            return ret;
        }

        @Override
        public Map<IRecipeInput, RecipeOutput> getRecipes() {
            return null;
        }
    }
}

