/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.event.LaserEvent;
import ic2.api.item.ElectricItem;
import ic2.api.network.INetworkItemEventListener;
import ic2.core.IC2;
import ic2.core.audio.PositionSpec;
import ic2.core.init.InternalName;
import ic2.core.item.tool.EntityMiningLaser;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.util.StackUtil;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ItemToolMiningLaser
extends ItemElectricTool
implements INetworkItemEventListener {
    private static final int EventShotMining = 0;
    private static final int EventShotLowFocus = 1;
    private static final int EventShotLongRange = 2;
    private static final int EventShotHorizontal = 3;
    private static final int EventShotSuperHeat = 4;
    private static final int EventShotScatter = 5;
    private static final int EventShotExplosive = 6;

    public ItemToolMiningLaser(InternalName internalName) {
        super(internalName, 100);
        this.maxCharge = 300000;
        this.transferLimit = 512;
        this.tier = 3;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        String mode;
        super.addInformation(stack, player, list, par4);
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        switch (nbtData.getInteger("laserSetting")) {
            case 0: {
                mode = StatCollector.translateToLocal((String)"ic2.tooltip.mode.mining");
                break;
            }
            case 1: {
                mode = StatCollector.translateToLocal((String)"ic2.tooltip.mode.lowFocus");
                break;
            }
            case 2: {
                mode = StatCollector.translateToLocal((String)"ic2.tooltip.mode.longRange");
                break;
            }
            case 3: {
                mode = StatCollector.translateToLocal((String)"ic2.tooltip.mode.horizontal");
                break;
            }
            case 4: {
                mode = StatCollector.translateToLocal((String)"ic2.tooltip.mode.superHeat");
                break;
            }
            case 5: {
                mode = StatCollector.translateToLocal((String)"ic2.tooltip.mode.scatter");
                break;
            }
            case 6: {
                mode = StatCollector.translateToLocal((String)"ic2.tooltip.mode.explosive");
                break;
            }
            default: {
                return;
            }
        }
        list.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.mode", (Object[])new Object[]{mode}));
    }

    @Override
    public List<String> getHudInfo(ItemStack itemStack) {
        LinkedList<String> info = new LinkedList<String>();
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemStack);
        String mode = StatCollector.translateToLocal((String)ItemToolMiningLaser.getModeString(nbtData.getInteger("laserSetting")));
        info.addAll(super.getHudInfo(itemStack));
        info.add(StatCollector.translateToLocalFormatted((String)"ic2.tooltip.mode", (Object[])new Object[]{mode}));
        return info;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!IC2.platform.isSimulating()) {
            return stack;
        }
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        int laserSetting = nbtData.getInteger("laserSetting");
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            laserSetting = (laserSetting + 1) % 7;
            nbtData.setInteger("laserSetting", laserSetting);
            IC2.platform.messagePlayer(player, "ic2.tooltip.mode", ItemToolMiningLaser.getModeString(laserSetting));
        } else {
            int consume = (new int[]{1250, 100, 5000, 0, 2500, 10000, 5000})[laserSetting];
            if (!ElectricItem.manager.use(stack, consume, (EntityLivingBase)player)) {
                return stack;
            }
            switch (laserSetting) {
                case 0: {
                    if (!this.shootLaser(world, (EntityLivingBase)player, stack, Float.POSITIVE_INFINITY, 5.0f, Integer.MAX_VALUE, false, false)) break;
                    IC2.network.get().initiateItemEvent(player, stack, 0, true);
                    break;
                }
                case 1: {
                    if (!this.shootLaser(world, (EntityLivingBase)player, stack, 4.0f, 5.0f, 1, false, false)) break;
                    IC2.network.get().initiateItemEvent(player, stack, 1, true);
                    break;
                }
                case 2: {
                    if (!this.shootLaser(world, (EntityLivingBase)player, stack, Float.POSITIVE_INFINITY, 20.0f, Integer.MAX_VALUE, false, false)) break;
                    IC2.network.get().initiateItemEvent(player, stack, 2, true);
                    break;
                }
                case 3: {
                    break;
                }
                case 4: {
                    if (!this.shootLaser(world, (EntityLivingBase)player, stack, Float.POSITIVE_INFINITY, 8.0f, Integer.MAX_VALUE, false, true)) break;
                    IC2.network.get().initiateItemEvent(player, stack, 4, true);
                    break;
                }
                case 5: {
                    for (int x = -2; x <= 2; ++x) {
                        for (int y = -2; y <= 2; ++y) {
                            this.shootLaser(world, (EntityLivingBase)player, stack, Float.POSITIVE_INFINITY, 12.0f, Integer.MAX_VALUE, false, false, player.rotationYaw + 20.0f * (float)x, player.rotationPitch + 20.0f * (float)y);
                        }
                    }
                    IC2.network.get().initiateItemEvent(player, stack, 5, true);
                    break;
                }
                case 6: {
                    if (!this.shootLaser(world, (EntityLivingBase)player, stack, Float.POSITIVE_INFINITY, 12.0f, Integer.MAX_VALUE, true, false)) break;
                    IC2.network.get().initiateItemEvent(player, stack, 6, true);
                }
            }
        }
        return super.onItemRightClick(stack, world, player);
    }

    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
        if (!IC2.keyboard.isModeSwitchKeyDown(entityPlayer) && nbtData.getInteger("laserSetting") == 3) {
            if (Math.abs(entityPlayer.posY + (double)entityPlayer.getEyeHeight() - 0.1 - ((double)y + 0.5)) < 1.5) {
                if (ElectricItem.manager.use(itemstack, 3000.0, (EntityLivingBase)entityPlayer) && this.shootLaser(world, (EntityLivingBase)entityPlayer, itemstack, Float.POSITIVE_INFINITY, 5.0f, Integer.MAX_VALUE, false, false, entityPlayer.rotationYaw, 0.0, (double)y + 0.5)) {
                    IC2.network.get().initiateItemEvent(entityPlayer, itemstack, 3, true);
                }
            } else {
                IC2.platform.messagePlayer(entityPlayer, "Mining laser aiming angle too steep", new Object[0]);
            }
        }
        return false;
    }

    public boolean shootLaser(World world, EntityLivingBase entityliving, ItemStack laseritem, float range, float power, int blockBreaks, boolean explosive, boolean smelt) {
        return this.shootLaser(world, entityliving, laseritem, range, power, blockBreaks, explosive, smelt, entityliving.rotationYaw, entityliving.rotationPitch);
    }

    public boolean shootLaser(World world, EntityLivingBase entityliving, ItemStack laseritem, float range, float power, int blockBreaks, boolean explosive, boolean smelt, double yawDeg, double pitchDeg) {
        return this.shootLaser(world, entityliving, laseritem, range, power, blockBreaks, explosive, smelt, yawDeg, pitchDeg, entityliving.posY + (double)entityliving.getEyeHeight() - 0.1);
    }

    public boolean shootLaser(World world, EntityLivingBase entityliving, ItemStack laseritem, float range, float power, int blockBreaks, boolean explosive, boolean smelt, double yawDeg, double pitchDeg, double y) {
        EntityMiningLaser tLaser = new EntityMiningLaser(world, entityliving, range, power, blockBreaks, explosive, yawDeg, pitchDeg, y);
        LaserEvent.LaserShootEvent tEvent = new LaserEvent.LaserShootEvent(world, tLaser, entityliving, range, power, blockBreaks, explosive, smelt, laseritem);
        MinecraftForge.EVENT_BUS.post((Event)tEvent);
        if (tLaser.takeDataFromEvent(tEvent)) {
            world.spawnEntityInWorld((Entity)tLaser);
            return true;
        }
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public void onNetworkEvent(ItemStack stack, EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaser.ogg", true, IC2.audioManager.getDefaultVolume());
                break;
            }
            case 1: {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserLowFocus.ogg", true, IC2.audioManager.getDefaultVolume());
                break;
            }
            case 2: {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserLongRange.ogg", true, IC2.audioManager.getDefaultVolume());
                break;
            }
            case 3: {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaser.ogg", true, IC2.audioManager.getDefaultVolume());
                break;
            }
            case 4: {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaser.ogg", true, IC2.audioManager.getDefaultVolume());
                break;
            }
            case 5: {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserScatter.ogg", true, IC2.audioManager.getDefaultVolume());
                break;
            }
            case 6: {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserExplosive.ogg", true, IC2.audioManager.getDefaultVolume());
            }
        }
    }

    private static String getModeString(int mode) {
        switch (mode) {
            case 0: {
                return "ic2.tooltip.mode.mining";
            }
            case 1: {
                return "ic2.tooltip.mode.lowFocus";
            }
            case 2: {
                return "ic2.tooltip.mode.longRange";
            }
            case 3: {
                return "ic2.tooltip.mode.horizontal";
            }
            case 4: {
                return "ic2.tooltip.mode.superHeat";
            }
            case 5: {
                return "ic2.tooltip.mode.scatter";
            }
            case 6: {
                return "ic2.tooltip.mode.explosive";
            }
        }
        assert (false);
        return "";
    }
}

