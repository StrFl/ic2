/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.ITickCallback;
import ic2.core.Ic2Items;
import ic2.core.item.ItemCropSeed;
import ic2.core.item.tool.ContainerCropnalyzer;
import ic2.core.item.tool.GuiCropnalyzer;
import ic2.core.item.tool.HandHeldInventory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class HandHeldCropnalyzer
extends HandHeldInventory
implements ITickCallback {
    public HandHeldCropnalyzer(EntityPlayer player, ItemStack stack) {
        super(player, stack, 3);
        if (IC2.platform.isSimulating()) {
            IC2.tickHandler.addContinuousTickCallback(player.worldObj, this);
        }
    }

    public String getInventoryName() {
        if (this.hasCustomInventoryName()) {
            return this.containerStack.getTagCompound().getString("display");
        }
        return "Cropnalyzer";
    }

    public boolean hasCustomInventoryName() {
        return StackUtil.getOrCreateNbtData(this.containerStack).hasKey("display");
    }

    public ContainerBase<HandHeldCropnalyzer> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerCropnalyzer(entityPlayer, this, 223);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiCropnalyzer(new ContainerCropnalyzer(entityPlayer, this, 223));
    }

    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
        super.onGuiClosed(entityPlayer);
        if (IC2.platform.isSimulating()) {
            IC2.tickHandler.removeContinuousTickCallback(entityPlayer.worldObj, this);
        }
    }

    @Override
    public void tickCallback(World world) {
        double get;
        double need;
        if (this.inventory[2] != null && this.inventory[2].getItem() instanceof IElectricItem && (need = ((IElectricItem)this.containerStack.getItem()).getMaxCharge(this.containerStack) - ElectricItem.manager.getCharge(this.containerStack)) > 0.0 && (get = ElectricItem.manager.discharge(this.inventory[2], need, Integer.MAX_VALUE, false, true, false)) > 0.0) {
            ElectricItem.manager.charge(this.containerStack, get, 3, true, false);
        }
        if (this.inventory[1] == null && this.inventory[0] != null && this.inventory[0].getItem() == Ic2Items.cropSeed.getItem()) {
            byte level = ItemCropSeed.getScannedFromStack(this.inventory[0]);
            if (level == 4) {
                this.inventory[1] = this.inventory[0];
                this.inventory[0] = null;
                return;
            }
            double ned = HandHeldCropnalyzer.energyForLevel(level);
            double got = ElectricItem.manager.discharge(this.containerStack, ned, 2, true, false, false);
            if (!Util.isSimilar(got, ned)) {
                return;
            }
            ItemCropSeed.incrementScannedOfStack(this.inventory[0]);
            this.inventory[1] = this.inventory[0];
            this.inventory[0] = null;
        }
    }

    public static int energyForLevel(int i) {
        switch (i) {
            default: {
                return 10;
            }
            case 1: {
                return 90;
            }
            case 2: {
                return 900;
            }
            case 3: 
        }
        return 9000;
    }

    public CropCard crop() {
        return Crops.instance.getCropCard(this.inventory[1]);
    }

    public int getScannedLevel() {
        if (this.inventory[1] == null || this.inventory[1].getItem() != Ic2Items.cropSeed.getItem()) {
            return -1;
        }
        return ItemCropSeed.getScannedFromStack(this.inventory[1]);
    }

    public String getSeedName() {
        return StatCollector.translateToLocal((String)this.crop().displayName());
    }

    public String getSeedTier() {
        switch (this.crop().tier()) {
            default: {
                return "0";
            }
            case 1: {
                return "I";
            }
            case 2: {
                return "II";
            }
            case 3: {
                return "III";
            }
            case 4: {
                return "IV";
            }
            case 5: {
                return "V";
            }
            case 6: {
                return "VI";
            }
            case 7: {
                return "VII";
            }
            case 8: {
                return "VIII";
            }
            case 9: {
                return "IX";
            }
            case 10: {
                return "X";
            }
            case 11: {
                return "XI";
            }
            case 12: {
                return "XII";
            }
            case 13: {
                return "XIII";
            }
            case 14: {
                return "XIV";
            }
            case 15: {
                return "XV";
            }
            case 16: 
        }
        return "XVI";
    }

    public String getSeedDiscovered() {
        return this.crop().discoveredBy();
    }

    public String getSeedDesc(int i) {
        return this.crop().desc(i);
    }

    public int getSeedGrowth() {
        return ItemCropSeed.getGrowthFromStack(this.inventory[1]);
    }

    public int getSeedGain() {
        return ItemCropSeed.getGainFromStack(this.inventory[1]);
    }

    public int getSeedResistence() {
        return ItemCropSeed.getResistanceFromStack(this.inventory[1]);
    }
}

