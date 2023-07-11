/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package ic2.core.item.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorFluidTank;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemArmorJetpack
extends ItemArmorFluidTank {
    protected static AudioSource audioSource;
    private static boolean lastJetpackUsed;

    public ItemArmorJetpack(InternalName internalName) {
        super(internalName, InternalName.jetpack, BlocksItems.getFluid(InternalName.fluidBiogas), 30000);
    }

    public void use(ItemStack itemStack, double amount) {
        this.drainfromJetpack(itemStack, (int)amount);
        this.Updatedamage(itemStack);
    }

    public boolean useJetpack(EntityPlayer player, boolean hoverMode) {
        ItemStack jetpack = player.inventory.armorInventory[2];
        if (this.getCharge(jetpack) <= 0.0) {
            return false;
        }
        boolean electric = jetpack.getItem() != Ic2Items.jetpack.getItem();
        float power = 1.0f;
        float dropPercentage = 0.2f;
        if (electric) {
            power = 0.7f;
            dropPercentage = 0.05f;
        }
        if (this.getCharge(jetpack) / this.getMaxCharge(jetpack) <= (double)dropPercentage) {
            power = (float)((double)power * (this.getCharge(jetpack) / (this.getMaxCharge(jetpack) * (double)dropPercentage)));
        }
        if (IC2.keyboard.isForwardKeyDown(player)) {
            float forwardpower;
            float retruster = 0.15f;
            if (hoverMode) {
                retruster = 1.0f;
            }
            if (electric) {
                retruster += 0.15f;
            }
            if ((forwardpower = power * retruster * 2.0f) > 0.0f) {
                player.moveFlying(0.0f, 0.4f * forwardpower, 0.02f);
            }
        }
        int worldHeight = IC2.getWorldHeight(player.worldObj);
        double y = player.posY;
        int maxFlightHeight = electric ? (int)((float)worldHeight / 1.28f) : worldHeight;
        if (y > (double)(maxFlightHeight - 25)) {
            if (y > (double)maxFlightHeight) {
                y = maxFlightHeight;
            }
            power = (float)((double)power * (((double)maxFlightHeight - y) / 25.0));
        }
        double prevmotion = player.motionY;
        player.motionY = Math.min(player.motionY + (double)(power * 0.2f), (double)0.6f);
        if (hoverMode) {
            float maxHoverY = 0.0f;
            if (IC2.keyboard.isJumpKeyDown(player)) {
                maxHoverY = electric ? 0.1f : 0.2f;
            }
            if (IC2.keyboard.isSneakKeyDown(player)) {
                maxHoverY = electric ? -0.1f : -0.2f;
            }
            if (player.motionY > (double)maxHoverY) {
                player.motionY = maxHoverY;
                if (prevmotion > player.motionY) {
                    player.motionY = prevmotion;
                }
            }
        }
        int consume = 2;
        if (hoverMode) {
            consume = 1;
        }
        if (electric) {
            consume += 6;
        }
        if (!player.onGround) {
            this.use(jetpack, consume);
        }
        player.fallDistance = 0.0f;
        player.distanceWalkedModified = 0.0f;
        IC2.platform.resetPlayerInAirTime(player);
        return true;
    }

    public boolean drainfromJetpack(ItemStack pack, int amount) {
        if (this.isEmpty(pack)) {
            return false;
        }
        if (this.drain((ItemStack)pack, (int)amount, (boolean)false).amount < amount) {
            return false;
        }
        this.drain(pack, amount, true);
        return true;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (player.inventory.armorInventory[2] != itemStack) {
            return;
        }
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        boolean hoverMode = nbtData.getBoolean("hoverMode");
        byte toggleTimer = nbtData.getByte("toggleTimer");
        boolean jetpackUsed = false;
        if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
            toggleTimer = 10;
            boolean bl = hoverMode = !hoverMode;
            if (IC2.platform.isSimulating()) {
                nbtData.setBoolean("hoverMode", hoverMode);
                if (hoverMode) {
                    IC2.platform.messagePlayer(player, "Hover Mode enabled.", new Object[0]);
                } else {
                    IC2.platform.messagePlayer(player, "Hover Mode disabled.", new Object[0]);
                }
            }
        }
        if (IC2.keyboard.isJumpKeyDown(player) || hoverMode) {
            jetpackUsed = this.useJetpack(player, hoverMode);
        }
        if (IC2.platform.isSimulating() && toggleTimer > 0) {
            toggleTimer = (byte)(toggleTimer - 1);
            nbtData.setByte("toggleTimer", toggleTimer);
        }
        if (IC2.platform.isRendering() && player == IC2.platform.getPlayerInstance()) {
            if (lastJetpackUsed != jetpackUsed) {
                if (jetpackUsed) {
                    if (audioSource == null) {
                        audioSource = IC2.audioManager.createSource(player, PositionSpec.Backpack, "Tools/Jetpack/JetpackLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    }
                    if (audioSource != null) {
                        audioSource.play();
                    }
                } else if (audioSource != null) {
                    audioSource.remove();
                    audioSource = null;
                }
                lastJetpackUsed = jetpackUsed;
            }
            if (audioSource != null) {
                audioSource.updatePosition();
            }
        }
        if (jetpackUsed) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
        ItemStack itemStack = new ItemStack(Ic2Items.jetpack.getItem(), 1);
        this.filltank(itemStack);
        itemStack.setItemDamage(1);
        itemList.add(itemStack);
        itemStack = new ItemStack(Ic2Items.jetpack.getItem(), 1);
        itemStack.setItemDamage(this.getMaxDamage());
        itemList.add(itemStack);
    }

    static {
        lastJetpackUsed = false;
    }
}

