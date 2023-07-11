/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.personal;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.block.personal.TileEntityEnergyOMat;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemPersonalBlock;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockPersonal
extends BlockMultiID {
    public BlockPersonal(InternalName internalName1) {
        super(internalName1, Material.iron, ItemPersonalBlock.class);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0f);
        this.setStepSound(soundTypeMetal);
        this.canBlockGrass = false;
        Ic2Items.personalSafe = new ItemStack((Block)this, 1, 0);
        Ic2Items.tradeOMat = new ItemStack((Block)this, 1, 1);
        Ic2Items.energyOMat = new ItemStack((Block)this, 1, 2);
        GameRegistry.registerTileEntity(TileEntityPersonalChest.class, (String)"Personal Safe");
        GameRegistry.registerTileEntity(TileEntityTradeOMat.class, (String)"Trade-O-Mat");
        GameRegistry.registerTileEntity(TileEntityEnergyOMat.class, (String)"Energy-O-Mat");
    }

    @Override
    public String getTextureFolder(int id) {
        return "personal";
    }

    public int damageDropped(int meta) {
        return meta;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return IC2.platform.getRenderId("personal");
    }

    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        if (IC2.platform.isSimulating() && IC2.platform.isRendering()) {
            return super.getDrops(world, x, y, z, metadata, fortune);
        }
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack((Block)this, 1, metadata));
        return ret;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        try {
            switch (meta) {
                case 0: {
                    return TileEntityPersonalChest.class;
                }
                case 1: {
                    return TileEntityTradeOMat.class;
                }
                case 2: {
                    return TileEntityEnergyOMat.class;
                }
            }
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset) {
        if (player.isSneaking()) {
            return false;
        }
        int meta = world.getBlockMetadata(x, y, z);
        TileEntityBlock te = (TileEntityBlock)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return false;
        }
        if (IC2.platform.isSimulating() && meta != 1 && meta != 2 && te instanceof IPersonalBlock && !((IPersonalBlock)((Object)te)).permitsAccess(player.getGameProfile())) {
            IC2.platform.messagePlayer(player, "This safe is owned by " + ((IPersonalBlock)((Object)te)).getOwner().getName(), new Object[0]);
            return false;
        }
        return super.onBlockActivated(world, x, y, z, player, side, xOffset, yOffset, zOffset);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() == 0 ? EnumRarity.uncommon : EnumRarity.common;
    }

    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }
}

