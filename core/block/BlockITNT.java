/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.logging.log4j.Level
 */
package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.block.BlockIC2Explosive;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.EntityItnt;
import ic2.core.block.EntityNuke;
import ic2.core.block.machine.tileentity.TileEntityNuke;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public class BlockITNT
extends BlockIC2Explosive {
    public boolean isITNT;

    public BlockITNT(InternalName internalName1) {
        super(internalName1, internalName1 == InternalName.blockITNT);
        if (internalName1 == InternalName.blockITNT) {
            this.isITNT = true;
        } else {
            this.isITNT = false;
            GameRegistry.registerTileEntity(TileEntityNuke.class, (String)"Nuke");
        }
        this.setHardness(0.0f);
        this.setStepSound(Block.soundTypeGrass);
    }

    @Override
    public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset) {
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.flint_and_steel && this.isITNT) {
            par1World.setBlockMetadataWithNotify(x, y, z, 1, 7);
            this.removedByPlayer(par1World, player, x, y, z);
            return true;
        }
        return super.onBlockActivated(par1World, x, y, z, player, side, xOffset, yOffset, zOffset);
    }

    @Override
    public EntityIC2Explosive getExplosionEntity(World world, int x, int y, int z, EntityLivingBase igniter) {
        EntityIC2Explosive ret;
        if (this.isITNT) {
            ret = new EntityItnt(world, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5);
        } else {
            TileEntity te = world.getTileEntity(x, y, z);
            if (ConfigUtil.getBool(MainConfig.get(), "protection/enableNuke") && te instanceof TileEntityNuke) {
                float NukeExplosivePower = ((TileEntityNuke)te).getNukeExplosivePower();
                if (NukeExplosivePower < 0.0f) {
                    return null;
                }
                int RadiationRange = ((TileEntityNuke)te).getRadiationRange();
                ret = new EntityNuke(world, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, NukeExplosivePower, RadiationRange);
            } else {
                return null;
            }
        }
        ret.setIgniter(igniter);
        return ret;
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack) {
        if (!this.isITNT && entityliving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entityliving;
            String playerName = player.getGameProfile().getName() + "/" + player.getGameProfile().getId();
            IC2.log.log(LogCategory.PlayerActivity, Level.INFO, "Player %s placed a nuke at %s.", playerName, Util.formatPosition((IBlockAccess)world, x, y, z));
        }
    }

    @Override
    public void onIgnite(World world, EntityPlayer player, int x, int y, int z) {
        if (!this.isITNT) {
            String playerName = player == null ? null : player.getGameProfile().getName() + "/" + player.getGameProfile().getId();
            IC2.log.log(LogCategory.PlayerActivity, Level.INFO, "Nuke at %s was ignited %s.", Util.formatPosition((IBlockAccess)world, x, y, z), playerName == null ? "indirectly" : "by " + playerName);
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityNuke) {
                ((TileEntityNuke)te).onIgnite(player);
            }
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return this.isITNT ? EnumRarity.common : EnumRarity.uncommon;
    }

    public void getSubBlocks(Item item, CreativeTabs tabs, List itemList) {
        if (this.isITNT || ConfigUtil.getBool(MainConfig.get(), "protection/enableNuke")) {
            super.getSubBlocks(item, tabs, itemList);
        }
    }

    public boolean hasTileEntity(int metadata) {
        return this.internalName != InternalName.blockITNT;
    }

    public TileEntity createTileEntity(World world, int metadata) {
        if (this.internalName != InternalName.blockITNT) {
            return new TileEntityNuke();
        }
        return null;
    }
}

