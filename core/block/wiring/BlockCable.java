/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockColored
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.util.ForgeDirection
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.wiring;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Direction;
import ic2.api.energy.EnergyNet;
import ic2.api.event.PaintEvent;
import ic2.api.event.RetextureEvent;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.block.BlockMultiID;
import ic2.core.block.BlockTextureStitched;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.block.wiring.TileEntityCableDetector;
import ic2.core.block.wiring.TileEntityCableSplitter;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.item.tool.ItemToolCutter;
import ic2.core.util.AabbUtil;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.mutable.MutableObject;

public class BlockCable
extends BlockMultiID {
    private static final int[] coloredMetas = new int[]{0, 3, 4, 6, 7, 8, 9, 13};
    protected int colorMultiplier = -1;
    @SideOnly(value=Side.CLIENT)
    private IIcon[][] coloredTextures;

    public BlockCable(InternalName internalName1) {
        super(internalName1, Material.iron, ItemBlockIC2.class);
        this.setHardness(0.2f);
        this.setStepSound(soundTypeCloth);
        this.setCreativeTab(null);
        Ic2Items.copperCableBlock = new ItemStack((Block)this, 1, 1);
        Ic2Items.insulatedCopperCableBlock = new ItemStack((Block)this, 1, 0);
        Ic2Items.goldCableBlock = new ItemStack((Block)this, 1, 2);
        Ic2Items.insulatedGoldCableBlock = new ItemStack((Block)this, 1, 3);
        Ic2Items.doubleInsulatedGoldCableBlock = new ItemStack((Block)this, 1, 4);
        Ic2Items.ironCableBlock = new ItemStack((Block)this, 1, 5);
        Ic2Items.insulatedIronCableBlock = new ItemStack((Block)this, 1, 6);
        Ic2Items.doubleInsulatedIronCableBlock = new ItemStack((Block)this, 1, 7);
        Ic2Items.trippleInsulatedIronCableBlock = new ItemStack((Block)this, 1, 8);
        Ic2Items.glassFiberCableBlock = new ItemStack((Block)this, 1, 9);
        Ic2Items.tinCableBlock = new ItemStack((Block)this, 1, 10);
        Ic2Items.detectorCableBlock = new ItemStack((Block)this, 1, 11);
        Ic2Items.splitterCableBlock = new ItemStack((Block)this, 1, 12);
        Ic2Items.insulatedtinCableBlock = new ItemStack((Block)this, 1, 13);
        GameRegistry.registerTileEntity(TileEntityCable.class, (String)"Cable");
        GameRegistry.registerTileEntity(TileEntityCableDetector.class, (String)"Detector Cable");
        GameRegistry.registerTileEntity(TileEntityCableSplitter.class, (String)"SplitterCable");
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @Override
    public String getTextureFolder(int id) {
        return "wiring/cable";
    }

    @Override
    public String getTextureName(int index) {
        ItemStack itemStack;
        Item item = Ic2Items.copperCableItem.getItem();
        String ret = item.getUnlocalizedName(itemStack = new ItemStack((Block)this, 1, index));
        if (ret == null) {
            return null;
        }
        return ret.substring(4).replace("item", "block");
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        this.coloredTextures = new IIcon[coloredMetas.length][90];
        for (int index = 0; index < coloredMetas.length; ++index) {
            int meta = coloredMetas[index];
            for (int color = 1; color < 16; ++color) {
                String name = IC2.textureDomain + ":" + this.getTextureFolder(index) + "/" + this.getTextureName(meta) + "." + Util.getColorName(color).name();
                for (int side = 0; side < 6; ++side) {
                    String subName = name + ":" + side;
                    BlockTextureStitched texture = new BlockTextureStitched(subName, side);
                    this.coloredTextures[index][(color - 1) * 6 + side] = texture;
                    ((TextureMap)iconRegister).setTextureEntry(subName, (TextureAtlasSprite)texture);
                }
            }
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe(blockAccess, x, y, z);
        if (te == null) {
            return null;
        }
        if (te.foamed == 0) {
            if (te instanceof TileEntityCableDetector || te instanceof TileEntityCableSplitter || te.color == 0) {
                return super.getIcon(blockAccess, x, y, z, side);
            }
            int cableType = te.cableType == 14 ? 13 : (int)te.cableType;
            return this.coloredTextures[Arrays.binarySearch(coloredMetas, cableType)][(te.color - 1) * 6 + side];
        }
        if (te.foamed == 1) {
            return StackUtil.getBlock(Ic2Items.constructionFoam).getIcon(side, 0);
        }
        Block referencedBlock = te.getReferencedBlock(side);
        if (referencedBlock != null) {
            try {
                return referencedBlock.getIcon(te.retextureRefSide[side], te.retextureRefMeta[side]);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return StackUtil.getBlock(Ic2Items.constructionFoamWall).getIcon(side, (int)te.foamColor);
    }

    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 absDirection) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return null;
        }
        TileEntityCable tileEntityCable = te;
        Vec3 direction = Vec3.createVectorHelper((double)(absDirection.xCoord - origin.xCoord), (double)(absDirection.yCoord - origin.yCoord), (double)(absDirection.zCoord - origin.zCoord));
        double maxLength = direction.lengthVector();
        double halfThickness = tileEntityCable.foamed > 0 ? 0.5 : (double)tileEntityCable.getCableThickness() / 2.0;
        boolean hit = false;
        Vec3 intersection = Vec3.createVectorHelper((double)0.0, (double)0.0, (double)0.0);
        Direction intersectingDirection = AabbUtil.getIntersection(origin, direction, AxisAlignedBB.getBoundingBox((double)((double)x + 0.5 - halfThickness), (double)((double)y + 0.5 - halfThickness), (double)((double)z + 0.5 - halfThickness), (double)((double)x + 0.5 + halfThickness), (double)((double)y + 0.5 + halfThickness), (double)((double)z + 0.5 + halfThickness)), intersection);
        if (intersectingDirection != null && intersection.distanceTo(origin) <= maxLength) {
            hit = true;
        } else if (halfThickness < 0.5) {
            int mask = 1;
            for (Direction dir : Direction.directions) {
                if ((tileEntityCable.connectivity & mask) == 0) {
                    mask *= 2;
                    continue;
                }
                mask *= 2;
                AxisAlignedBB bbox = null;
                switch (dir) {
                    case XN: {
                        bbox = AxisAlignedBB.getBoundingBox((double)x, (double)((double)y + 0.5 - halfThickness), (double)((double)z + 0.5 - halfThickness), (double)((double)x + 0.5), (double)((double)y + 0.5 + halfThickness), (double)((double)z + 0.5 + halfThickness));
                        break;
                    }
                    case XP: {
                        bbox = AxisAlignedBB.getBoundingBox((double)((double)x + 0.5), (double)((double)y + 0.5 - halfThickness), (double)((double)z + 0.5 - halfThickness), (double)((double)x + 1.0), (double)((double)y + 0.5 + halfThickness), (double)((double)z + 0.5 + halfThickness));
                        break;
                    }
                    case YN: {
                        bbox = AxisAlignedBB.getBoundingBox((double)((double)x + 0.5 - halfThickness), (double)y, (double)((double)z + 0.5 - halfThickness), (double)((double)x + 0.5 + halfThickness), (double)((double)y + 0.5), (double)((double)z + 0.5 + halfThickness));
                        break;
                    }
                    case YP: {
                        bbox = AxisAlignedBB.getBoundingBox((double)((double)x + 0.5 - halfThickness), (double)((double)y + 0.5), (double)((double)z + 0.5 - halfThickness), (double)((double)x + 0.5 + halfThickness), (double)((double)y + 1.0), (double)((double)z + 0.5 + halfThickness));
                        break;
                    }
                    case ZN: {
                        bbox = AxisAlignedBB.getBoundingBox((double)((double)x + 0.5 - halfThickness), (double)((double)y + 0.5 - halfThickness), (double)z, (double)((double)x + 0.5 + halfThickness), (double)((double)y + 0.5), (double)((double)z + 0.5));
                        break;
                    }
                    case ZP: {
                        bbox = AxisAlignedBB.getBoundingBox((double)((double)x + 0.5 - halfThickness), (double)((double)y + 0.5 - halfThickness), (double)((double)z + 0.5), (double)((double)x + 0.5 + halfThickness), (double)((double)y + 0.5 + halfThickness), (double)((double)z + 1.0));
                    }
                }
                intersectingDirection = AabbUtil.getIntersection(origin, direction, bbox, intersection);
                if (intersectingDirection == null || !(intersection.distanceTo(origin) <= maxLength)) continue;
                hit = true;
                break;
            }
        }
        if (hit && intersectingDirection != null) {
            return new MovingObjectPosition(x, y, z, intersectingDirection.toSideValue(), intersection);
        }
        return null;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z, int meta) {
        double halfThickness = TileEntityCable.getCableThickness(meta);
        if (meta == 13) {
            halfThickness = TileEntityCable.getCableThickness(14);
        }
        return AxisAlignedBB.getBoundingBox((double)((double)x + 0.5 - halfThickness), (double)((double)y + 0.5 - halfThickness), (double)((double)z + 0.5 - halfThickness), (double)((double)x + 0.5 + halfThickness), (double)((double)y + 0.5 + halfThickness), (double)((double)z + 0.5 + halfThickness));
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return this.getCommonBoundingBoxFromPool(world, x, y, z, false);
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return this.getCommonBoundingBoxFromPool(world, x, y, z, true);
    }

    public AxisAlignedBB getCommonBoundingBoxFromPool(World world, int x, int y, int z, boolean selectionBoundingBox) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return this.getCollisionBoundingBoxFromPool(world, x, y, z, 3);
        }
        double halfThickness = te.foamed == 1 && selectionBoundingBox ? 0.5 : (double)te.getCableThickness() / 2.0;
        double minX1 = (double)x + 0.5 - halfThickness;
        double minY1 = (double)y + 0.5 - halfThickness;
        double minZ1 = (double)z + 0.5 - halfThickness;
        double maxX1 = (double)x + 0.5 + halfThickness;
        double maxY1 = (double)y + 0.5 + halfThickness;
        double maxZ1 = (double)z + 0.5 + halfThickness;
        if ((te.connectivity & 1) != 0) {
            minX1 = x;
        }
        if ((te.connectivity & 4) != 0) {
            minY1 = y;
        }
        if ((te.connectivity & 0x10) != 0) {
            minZ1 = z;
        }
        if ((te.connectivity & 2) != 0) {
            maxX1 = x + 1;
        }
        if ((te.connectivity & 8) != 0) {
            maxY1 = y + 1;
        }
        if ((te.connectivity & 0x20) != 0) {
            maxZ1 = z + 1;
        }
        return AxisAlignedBB.getBoundingBox((double)minX1, (double)minY1, (double)minZ1, (double)maxX1, (double)maxY1, (double)maxZ1);
    }

    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe(world, x, y, z);
        if (te == null) {
            return false;
        }
        return te.foamed > 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset) {
        ItemStack cur = player.getCurrentEquippedItem();
        if (cur != null && (StackUtil.equals((Block)Blocks.sand, cur) || cur.getItem() == Ic2Items.constructionFoam.getItem())) {
            TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
            if (te == null) {
                return false;
            }
            if (StackUtil.equals((Block)Blocks.sand, cur) && te.foamed == 1 && te.changeFoam((byte)2) || cur.getItem() == Ic2Items.constructionFoam.getItem() && te.foamed == 0 && te.changeFoam((byte)1)) {
                if (IC2.platform.isSimulating() && !player.capabilities.isCreativeMode) {
                    --cur.stackSize;
                    if (cur.stackSize <= 0) {
                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);
        if (IC2.platform.isSimulating()) {
            TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
            if (te == null) {
                return;
            }
            te.onNeighborBlockChange();
        }
    }

    public boolean removedByPlayer(World world, EntityPlayer entityPlayer, int x, int y, int z) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            world.setBlockToAir(x, y, z);
        }
        if (te.foamed > 0) {
            te.changeFoam((byte)0);
            world.notifyBlocksOfNeighborChange(x, y, z, (Block)this);
            return false;
        }
        return world.setBlockToAir(x, y, z);
    }

    public int getCableColor(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe(blockAccess, x, y, z);
        if (te == null) {
            return 0;
        }
        return te.color;
    }

    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int color) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return false;
        }
        color = BlockColored.func_150031_c((int)color);
        return te.changeColor(color);
    }

    @SubscribeEvent
    public void colorBlock(PaintEvent event) {
        if (event.world.getBlock(event.x, event.y, event.z) != this) {
            return;
        }
        TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)event.world, event.x, event.y, event.z);
        if (te == null) {
            return;
        }
        event.painted = te.changeColor(event.color);
    }

    public boolean canHarvestBlock(EntityPlayer player, int md) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te != null) {
            if (te.cableType == 14) {
                ret.add(new ItemStack(Ic2Items.insulatedCopperCableItem.getItem(), 1, 13));
                return ret;
            }
            ret.add(new ItemStack(Ic2Items.insulatedCopperCableItem.getItem(), 1, (int)te.cableType));
        } else {
            ret.add(new ItemStack(Ic2Items.insulatedCopperCableItem.getItem(), 1, metadata));
        }
        return ret;
    }

    @Override
    public Class<? extends TileEntity> getTeClass(int meta, MutableObject<Class<?>[]> ctorArgTypes, MutableObject<Object[]> ctorArgs) {
        if (meta >= 13) {
            ++meta;
        }
        if (ctorArgTypes != null) {
            ctorArgTypes.setValue((Object)new Class[]{Short.TYPE});
        }
        if (ctorArgs != null) {
            ctorArgs.setValue((Object)new Object[]{(short)meta});
        }
        switch (meta) {
            case 11: {
                return TileEntityCableDetector.class;
            }
            case 12: {
                return TileEntityCableSplitter.class;
            }
        }
        return TileEntityCable.class;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return IC2.platform.getRenderId("cable");
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
        if (entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().getItem() instanceof ItemToolCutter) {
            ItemToolCutter.cutInsulationFrom(entityplayer.getCurrentEquippedItem(), world, i, j, k);
        }
    }

    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe(blockAccess, x, y, z);
        if (te == null) {
            return 0;
        }
        if (te instanceof TileEntityCableDetector) {
            return te.getActive() ? 15 : 0;
        }
        return 0;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tabs, List itemList) {
    }

    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return 0.0f;
        }
        if (te.foamed == 1) {
            return 0.01f;
        }
        if (te.foamed == 2) {
            return 3.0f;
        }
        return 0.2f;
    }

    public float getExplosionResistance(Entity exploder, World world, int x, int y, int z, double src_x, double src_y, double src_z) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
        if (te == null) {
            return 0.0f;
        }
        if (te.foamed == 2) {
            return 90.0f;
        }
        return 6.0f;
    }

    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        TileEntityCable te = (TileEntityCable)this.getOwnTe(world, x, y, z);
        if (te == null) {
            return 0;
        }
        if (te.foamed == 2) {
            return 255;
        }
        return 0;
    }

    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int direction) {
        int meta = world.getBlockMetadata(x, y, z);
        return meta == 11 || meta == 12;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        ArrayList<ItemStack> ret = this.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        if (ret.isEmpty()) {
            return null;
        }
        return (ItemStack)ret.get(0);
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        if (IC2.platform.isSimulating()) {
            TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)world, x, y, z);
            if (te == null) {
                return 0;
            }
            if (te instanceof TileEntityCableDetector) {
                TileEntityCableDetector tec = (TileEntityCableDetector)te;
                return (int)Util.map(EnergyNet.instance.getNodeStats(te).getEnergyIn() / (tec.getConductorBreakdownEnergy() - 1.0), 1.0, 15.0);
            }
        }
        return 0;
    }

    @SubscribeEvent
    public void onRetexture(RetextureEvent event) {
        if (event.world.getBlock(event.x, event.y, event.z) != this) {
            return;
        }
        TileEntityCable te = (TileEntityCable)this.getOwnTe((IBlockAccess)event.world, event.x, event.y, event.z);
        if (te == null) {
            return;
        }
        if (te.retexture(event.side, event.referencedBlock, event.referencedMeta, event.referencedSide)) {
            event.applied = true;
        }
    }

    public int colorMultiplier(IBlockAccess par1iBlockAccess, int x, int y, int z) {
        if (this.colorMultiplier != -1) {
            return this.colorMultiplier;
        }
        return super.colorMultiplier(par1iBlockAccess, x, y, z);
    }
}

