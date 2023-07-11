/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameData
 *  net.minecraft.block.Block
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.registry.GameData;
import ic2.api.item.IBoxable;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.audio.PositionSpec;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.ItemIC2;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemToolWrench
extends ItemIC2
implements IBoxable {
    public ItemToolWrench(InternalName internalName) {
        super(internalName);
        this.setMaxDamage(160);
        this.setMaxStackSize(1);
    }

    public boolean canTakeDamage(ItemStack stack, int amount) {
        return true;
    }

    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!this.canTakeDamage(stack, 1)) {
            return false;
        }
        Block block = world.getBlock(x, y, z);
        if (block.isAir((IBlockAccess)world, x, y, z)) {
            return false;
        }
        int metaData = world.getBlockMetadata(x, y, z);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IWrenchable) {
            IWrenchable wrenchable = (IWrenchable)tileEntity;
            if (IC2.keyboard.isAltKeyDown(player)) {
                for (int step = 1; step < 6 && !wrenchable.wrenchCanSetFacing(player, side = player.isSneaking() ? (wrenchable.getFacing() + 6 - step) % 6 : (wrenchable.getFacing() + step) % 6); ++step) {
                }
            } else if (player.isSneaking()) {
                side += side % 2 * -2 + 1;
            }
            if (wrenchable.wrenchCanSetFacing(player, side)) {
                if (IC2.platform.isSimulating()) {
                    wrenchable.setFacing((short)side);
                    this.damage(stack, 1, player);
                }
                if (IC2.platform.isRendering()) {
                    IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.getDefaultVolume());
                }
                return IC2.platform.isSimulating();
            }
            if (this.canTakeDamage(stack, 10) && wrenchable.wrenchCanRemove(player)) {
                if (IC2.platform.isSimulating()) {
                    ItemStack wrenchDrop;
                    if (ConfigUtil.getBool(MainConfig.get(), "protection/wrenchLogging")) {
                        String playerName = player.getGameProfile().getName() + "/" + player.getGameProfile().getId();
                        String blockName = tileEntity.getClass().getName().replace("TileEntity", "");
                        IC2.log.info(LogCategory.PlayerActivity, "Player %s used the wrench to remove the %s (%s-%d) at %s.", playerName, blockName, GameData.getBlockRegistry().getNameForObject((Object)block), metaData, Util.formatPosition((IBlockAccess)world, x, y, z));
                    }
                    boolean dropOriginalBlock = false;
                    if (wrenchable.getWrenchDropRate() < 1.0f && this.overrideWrenchSuccessRate(stack)) {
                        if (!this.canTakeDamage(stack, 200)) {
                            IC2.platform.messagePlayer(player, "Not enough energy for lossless wrench operation", new Object[0]);
                            return true;
                        }
                        dropOriginalBlock = true;
                        this.damage(stack, 200, player);
                    } else {
                        dropOriginalBlock = world.rand.nextFloat() <= wrenchable.getWrenchDropRate();
                        this.damage(stack, 10, player);
                    }
                    ArrayList drops = block.getDrops(world, x, y, z, metaData, 0);
                    if (dropOriginalBlock && (wrenchDrop = wrenchable.getWrenchDrop(player)) != null) {
                        if (drops.isEmpty()) {
                            drops.add(wrenchDrop);
                        } else {
                            drops.set(0, wrenchDrop);
                        }
                    }
                    for (ItemStack itemStack : drops) {
                        StackUtil.dropAsEntity(world, x, y, z, itemStack);
                    }
                    world.setBlockToAir(x, y, z);
                }
                if (IC2.platform.isRendering()) {
                    IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.getDefaultVolume());
                }
                return IC2.platform.isSimulating();
            }
        }
        if (block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation((int)side))) {
            if (IC2.platform.isSimulating()) {
                this.damage(stack, 1, player);
            }
            if (IC2.platform.isRendering()) {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return IC2.platform.isSimulating();
        }
        return false;
    }

    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    public void damage(ItemStack is, int damage, EntityPlayer player) {
        is.damageItem(damage, (EntityLivingBase)player);
    }

    public boolean overrideWrenchSuccessRate(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

