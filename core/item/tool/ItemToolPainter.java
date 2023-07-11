/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockColored
 *  net.minecraft.block.BlockStainedGlass
 *  net.minecraft.block.BlockStainedGlassPane
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.event.entity.player.EntityInteractEvent
 *  net.minecraftforge.oredict.OreDictionary
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ic2.api.event.PaintEvent;
import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.audio.PositionSpec;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.oredict.OreDictionary;

public class ItemToolPainter
extends ItemIC2
implements IBoxable {
    private static final String[] dyes = new String[]{"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};
    public final int color;

    public ItemToolPainter(InternalName internalName, int col) {
        super(internalName);
        this.setMaxDamage(32);
        this.setMaxStackSize(1);
        this.color = col;
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float a, float b, float c) {
        int targetMeta;
        PaintEvent event = new PaintEvent(world, i, j, k, side, this.color);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.painted) {
            if (IC2.platform.isSimulating()) {
                this.damagePainter(entityplayer);
            }
            if (IC2.platform.isRendering()) {
                IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/Painter.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return IC2.platform.isSimulating();
        }
        Block block = world.getBlock(i, j, k);
        if (block.recolourBlock(world, i, j, k, ForgeDirection.VALID_DIRECTIONS[side], targetMeta = BlockColored.func_150031_c((int)this.color)) || this.colorBlock(world, i, j, k, block, targetMeta)) {
            this.damagePainter(entityplayer);
            if (IC2.platform.isRendering()) {
                IC2.audioManager.playOnce(entityplayer, PositionSpec.Hand, "Tools/Painter.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return IC2.platform.isSimulating();
        }
        return false;
    }

    private boolean colorBlock(World world, int x, int y, int z, Block block, int targetMeta) {
        if (block instanceof BlockColored || block instanceof BlockStainedGlass || block instanceof BlockStainedGlassPane) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta == targetMeta) {
                return false;
            }
            world.setBlockMetadataWithNotify(x, y, z, targetMeta, 3);
            return true;
        }
        if (block == Blocks.hardened_clay) {
            world.setBlock(x, y, z, Blocks.stained_hardened_clay, targetMeta, 3);
            return true;
        }
        if (block == Blocks.glass) {
            world.setBlock(x, y, z, (Block)Blocks.stained_glass, targetMeta, 3);
            return true;
        }
        if (block == Blocks.glass_pane) {
            world.setBlock(x, y, z, (Block)Blocks.stained_glass_pane, targetMeta, 3);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public boolean onEntityInteract(EntityInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        Entity entity = event.entity;
        if (entity.worldObj.isRemote || player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != this) {
            return true;
        }
        boolean ret = true;
        if (entity instanceof EntitySheep) {
            EntitySheep sheep = (EntitySheep)entity;
            int clr = BlockColored.func_150031_c((int)this.color);
            if (sheep.getFleeceColor() != clr) {
                ret = false;
                ((EntitySheep)entity).setFleeceColor(clr);
                this.damagePainter(player);
            }
        }
        return ret;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (IC2.platform.isSimulating() && IC2.keyboard.isModeSwitchKeyDown(entityplayer)) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
            boolean newValue = !nbtData.getBoolean("autoRefill");
            nbtData.setBoolean("autoRefill", newValue);
            if (newValue) {
                IC2.platform.messagePlayer(entityplayer, "Painter automatic refill mode enabled", new Object[0]);
            } else {
                IC2.platform.messagePlayer(entityplayer, "Painter automatic refill mode disabled", new Object[0]);
            }
        }
        return itemstack;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debugTooltips) {
        info.add(StatCollector.translateToLocal((String)(Items.dye.getUnlocalizedName(new ItemStack(Items.dye, 1, this.color)) + ".name")));
    }

    private void damagePainter(EntityPlayer player) {
        if (player.inventory.mainInventory[player.inventory.currentItem].getItemDamage() >= player.inventory.mainInventory[player.inventory.currentItem].getMaxDamage() - 1) {
            int dyeIS = -1;
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(player.inventory.mainInventory[player.inventory.currentItem]);
            if (nbtData.getBoolean("autoRefill")) {
                block0: for (int l = 0; l < player.inventory.mainInventory.length; ++l) {
                    if (player.inventory.mainInventory[l] == null) continue;
                    for (ItemStack ore : OreDictionary.getOres((String)dyes[this.color])) {
                        if (!ore.isItemEqual(player.inventory.mainInventory[l])) continue;
                        dyeIS = l;
                        continue block0;
                    }
                }
            }
            if (dyeIS == -1) {
                player.inventory.mainInventory[player.inventory.currentItem] = Ic2Items.painter.copy();
            } else {
                if (--player.inventory.mainInventory[dyeIS].stackSize <= 0) {
                    player.inventory.mainInventory[dyeIS] = null;
                }
                player.inventory.mainInventory[player.inventory.currentItem].setItemDamage(0);
            }
        } else {
            player.inventory.mainInventory[player.inventory.currentItem].damageItem(1, (EntityLivingBase)player);
        }
        player.openContainer.detectAndSendChanges();
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

