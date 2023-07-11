/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package ic2.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IElectricItem;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.item.ItemBattery;
import ic2.core.item.tool.Guitoolbox;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBatteryChargeHotbar
extends ItemBattery
implements IBoxable {
    public ItemBatteryChargeHotbar(InternalName internalName, double maxCharge, double transferLimit, int tier) {
        super(internalName, maxCharge, transferLimit, tier);
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        Mode mode = this.getMode(stack);
        list.add(this.getNameOfMode(mode));
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof Guitoolbox) {
            list.add((mode.enabled ? EnumChatFormatting.RED : EnumChatFormatting.GREEN) + StatCollector.translateToLocal((String)"ic2.tooltip.mode.boxable"));
        }
    }

    public void onUpdate(ItemStack stack, World worldObj, Entity entity, int par4, boolean par5) {
        Mode mode = this.getMode(stack);
        if (entity instanceof EntityPlayerMP && worldObj.getTotalWorldTime() % 10L == (long)this.getTier(stack) && mode.enabled) {
            EntityPlayer thePlayer = (EntityPlayer)entity;
            ItemStack[] inventory = thePlayer.inventory.mainInventory;
            double limit = this.getTransferLimit(stack);
            int tier = this.getTier(stack);
            for (int i = 0; i < 9; ++i) {
                ItemStack toCharge = inventory[i];
                if (toCharge == null || !(toCharge.getItem() instanceof IElectricItem) || toCharge.getItem() instanceof ItemBatteryChargeHotbar || mode == Mode.NOT_IN_HAND && i == thePlayer.inventory.currentItem) continue;
                double charge = ElectricItem.manager.charge(toCharge, limit, tier, false, true);
                charge = ElectricItem.manager.discharge(stack, charge, tier, true, false, false);
                ElectricItem.manager.charge(toCharge, charge, tier, true, false);
                if ((limit -= charge) <= 0.0) break;
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityplayer) {
        if (world.isRemote) {
            return itemStack;
        }
        Mode mode = this.getMode(itemStack);
        mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
        this.setMode(itemStack, mode);
        IC2.platform.messagePlayer(entityplayer, StatCollector.translateToLocalFormatted((String)"ic2.tooltip.mode", (Object[])new Object[]{this.getNameOfMode(mode)}), new Object[0]);
        return itemStack;
    }

    private String getNameOfMode(Mode mode) {
        return StatCollector.translateToLocal((String)("ic2.tooltip.mode." + mode.toString().toLowerCase()));
    }

    public void setMode(ItemStack stack, Mode mode) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        nbt.setByte("mode", (byte)mode.ordinal());
    }

    public Mode getMode(ItemStack stack) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if (!nbt.hasKey("mode")) {
            return Mode.values()[0];
        }
        return this.getMode(nbt.getByte("mode"));
    }

    private Mode getMode(byte mode) {
        if (mode < 0 || mode >= Mode.values().length) {
            return Mode.values()[0];
        }
        return Mode.values()[mode];
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return this.getMode(itemstack) == Mode.DISABLED;
    }

    private static enum Mode {
        ENABLED(true),
        DISABLED(false),
        NOT_IN_HAND(true);

        private boolean enabled;

        private Mode(boolean enabled) {
            this.enabled = enabled;
        }
    }
}

