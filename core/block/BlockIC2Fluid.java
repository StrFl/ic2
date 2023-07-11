/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 *  net.minecraftforge.fluids.BlockFluidClassic
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 */
package ic2.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockIC2Fluid
extends BlockFluidClassic {
    protected IIcon[] fluidIcon;
    protected Fluid fluid;
    private final int color;

    public BlockIC2Fluid(InternalName internalName, Fluid fluid, Material material, int color) {
        super(fluid, material);
        if (!fluid.getName().startsWith("ic2")) {
            throw new RuntimeException("Invalid fluid name: " + fluid.getName());
        }
        this.setCreativeTab(IC2.tabIC2);
        this.setBlockName(internalName.name());
        GameRegistry.registerBlock((Block)this, ItemBlockIC2.class, (String)internalName.name());
        this.fluid = fluid;
        this.color = color;
        if (this.density <= FluidRegistry.WATER.getDensity()) {
            this.displacements.put(Blocks.water, false);
            this.displacements.put(Blocks.flowing_water, false);
        }
        if (this.density <= FluidRegistry.LAVA.getDensity()) {
            this.displacements.put(Blocks.lava, false);
            this.displacements.put(Blocks.flowing_lava, false);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        String name = this.fluidName.substring("ic2".length());
        this.fluidIcon = this.fluid.equals(BlocksItems.getFluid(InternalName.fluidPahoehoeLava)) || this.fluid.equals(BlocksItems.getFluid(InternalName.fluidBiogas)) || this.fluid.equals(BlocksItems.getFluid(InternalName.fluidSuperheatedSteam)) || this.fluid.equals(BlocksItems.getFluid(InternalName.fluidSteam)) ? new IIcon[]{iconRegister.registerIcon(IC2.textureDomain + ":fluids/" + name + "_still"), iconRegister.registerIcon(IC2.textureDomain + ":fluids/" + name + "_still")} : new IIcon[]{iconRegister.registerIcon(IC2.textureDomain + ":fluids/" + name + "_still"), iconRegister.registerIcon(IC2.textureDomain + ":fluids/" + name + "_flow")};
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (IC2.platform.isSimulating()) {
            if (this.fluid.equals(BlocksItems.getFluid(InternalName.fluidPahoehoeLava))) {
                if (this.isSourceBlock((IBlockAccess)world, x, y, z) && world.getBlockLightValue(x, y, z) >= world.rand.nextInt(120)) {
                    world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.basaltBlock));
                } else if (!this.hardenFromNeighbors(world, x, y, z)) {
                    world.scheduleBlockUpdate(x, y, z, (Block)this, this.tickRate(world));
                }
            } else if (this.fluid.equals(BlocksItems.getFluid(InternalName.fluidHotWater))) {
                if (this.isSourceBlock((IBlockAccess)world, x, y, z) && world.getBlock(x, y - 2, z) != Blocks.flowing_lava && world.getBlock(x, y - 1, z) != this && world.rand.nextInt(60) == 0) {
                    world.setBlock(x, y, z, (Block)Blocks.flowing_water, 0, 3);
                } else {
                    world.scheduleBlockUpdate(x, y, z, (Block)this, this.tickRate(world));
                }
            }
        }
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        this.hardenFromNeighbors(world, x, y, z);
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.hardenFromNeighbors(world, x, y, z);
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        if (this.fluid.equals(BlocksItems.getFluid(InternalName.fluidBiogas))) {
            world.setBlock(x, y, z, Blocks.air, 0, 7);
        }
    }

    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        int id;
        if (!IC2.platform.isSimulating()) {
            return;
        }
        if (this.fluid.equals(BlocksItems.getFluid(InternalName.fluidPahoehoeLava))) {
            entity.setFire(10);
        }
        if (this.fluid.equals(BlocksItems.getFluid(InternalName.fluidHotCoolant))) {
            entity.setFire(30);
        }
        if (entity instanceof EntityPlayer) {
            if (this.fluid.equals(BlocksItems.getFluid(InternalName.fluidConstructionFoam)) && !((EntityPlayer)entity).isPotionActive(Potion.moveSlowdown.id)) {
                ((EntityPlayer)entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 300, 2, true));
            }
            if (this.fluid.equals(BlocksItems.getFluid(InternalName.fluidUuMatter)) && !((EntityPlayer)entity).isPotionActive(Potion.regeneration.id)) {
                ((EntityPlayer)entity).addPotionEffect(new PotionEffect(Potion.regeneration.id, 100, 1, true));
            }
            if (this.fluid.equals(BlocksItems.getFluid(InternalName.fluidSteam)) || this.fluid.equals(BlocksItems.getFluid(InternalName.fluidSuperheatedSteam))) {
                ((EntityPlayer)entity).addPotionEffect(new PotionEffect(Potion.blindness.id, 300, 0, true));
            }
        }
        if (entity instanceof EntityLivingBase && this.fluid.equals(BlocksItems.getFluid(InternalName.fluidHotWater)) && !((EntityLivingBase)entity).isPotionActive(id = ((EntityLivingBase)entity).isEntityUndead() ? Potion.wither.id : Potion.regeneration.id)) {
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(id, 100, IC2.random.nextInt(2), true));
        }
    }

    public IIcon getIcon(int side, int meta) {
        return side != 0 && side != 1 ? this.fluidIcon[1] : this.fluidIcon[0];
    }

    public String getUnlocalizedName() {
        return super.getUnlocalizedName().substring(5);
    }

    public int getColor() {
        return this.color;
    }

    private boolean hardenFromNeighbors(World world, int x, int y, int z) {
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        if (this.fluid.equals(BlocksItems.getFluid(InternalName.fluidPahoehoeLava))) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                LiquidUtil.LiquidData data = LiquidUtil.getLiquid(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
                if (data == null || data.liquid.getTemperature() > this.fluid.getTemperature() / 4) continue;
                if (this.isSourceBlock((IBlockAccess)world, x, y, z)) {
                    world.setBlock(x, y, z, StackUtil.getBlock(Ic2Items.basaltBlock));
                } else {
                    world.setBlockToAir(x, y, z);
                }
                return true;
            }
        }
        return false;
    }
}

