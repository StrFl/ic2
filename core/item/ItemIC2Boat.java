/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.EntityBoatCarbon;
import ic2.core.item.EntityBoatElectric;
import ic2.core.item.EntityBoatRubber;
import ic2.core.item.EntityIC2Boat;
import ic2.core.item.ItemIC2;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemIC2Boat
extends ItemIC2 {
    public ItemIC2Boat(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(true);
        Ic2Items.boatCarbon = new ItemStack((Item)this, 1, 0);
        Ic2Items.boatRubber = new ItemStack((Item)this, 1, 1);
        Ic2Items.boatRubberBroken = new ItemStack((Item)this, 1, 2);
        Ic2Items.boatElectric = new ItemStack((Item)this, 1, 3);
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        float f8;
        float f6;
        double d3;
        float f5;
        EntityIC2Boat entityboat = this.makeBoat(par1ItemStack, par2World, par3EntityPlayer);
        if (entityboat == null) {
            return par1ItemStack;
        }
        float f = 1.0f;
        float f1 = par3EntityPlayer.prevRotationPitch + (par3EntityPlayer.rotationPitch - par3EntityPlayer.prevRotationPitch) * f;
        float f2 = par3EntityPlayer.prevRotationYaw + (par3EntityPlayer.rotationYaw - par3EntityPlayer.prevRotationYaw) * f;
        double d0 = par3EntityPlayer.prevPosX + (par3EntityPlayer.posX - par3EntityPlayer.prevPosX) * (double)f;
        double d1 = par3EntityPlayer.prevPosY + (par3EntityPlayer.posY - par3EntityPlayer.prevPosY) * (double)f + (double)par3EntityPlayer.getEyeHeight();
        if (par2World.isRemote) {
            d1 -= (double)par3EntityPlayer.getDefaultEyeHeight();
        }
        double d2 = par3EntityPlayer.prevPosZ + (par3EntityPlayer.posZ - par3EntityPlayer.prevPosZ) * (double)f;
        Vec3 vec3 = Vec3.createVectorHelper((double)d0, (double)d1, (double)d2);
        float f3 = MathHelper.cos((float)(-f2 * ((float)Math.PI / 180) - (float)Math.PI));
        float f4 = MathHelper.sin((float)(-f2 * ((float)Math.PI / 180) - (float)Math.PI));
        float f7 = f4 * (f5 = -MathHelper.cos((float)(-f1 * ((float)Math.PI / 180))));
        Vec3 vec31 = vec3.addVector((double)f7 * (d3 = 5.0), (double)(f6 = MathHelper.sin((float)(-f1 * ((float)Math.PI / 180)))) * d3, (double)(f8 = f3 * f5) * d3);
        MovingObjectPosition movingobjectposition = par2World.rayTraceBlocks(vec3, vec31, true);
        if (movingobjectposition == null) {
            return par1ItemStack;
        }
        Vec3 vec32 = par3EntityPlayer.getLook(f);
        boolean flag = false;
        float f9 = 1.0f;
        List list = par2World.getEntitiesWithinAABBExcludingEntity((Entity)par3EntityPlayer, par3EntityPlayer.boundingBox.addCoord(vec32.xCoord * d3, vec32.yCoord * d3, vec32.zCoord * d3).expand((double)f9, (double)f9, (double)f9));
        for (Entity entity : list) {
            float f10;
            AxisAlignedBB axisalignedbb;
            if (!entity.canBeCollidedWith() || !(axisalignedbb = entity.boundingBox.expand((double)(f10 = entity.getCollisionBorderSize()), (double)f10, (double)f10)).isVecInside(vec3)) continue;
            flag = true;
        }
        if (flag) {
            return par1ItemStack;
        }
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k = movingobjectposition.blockZ;
            if (par2World.getBlock(i, j, k) == Blocks.snow_layer) {
                --j;
            }
            entityboat.setPosition((float)i + 0.5f, (float)j + 1.0f, (float)k + 0.5f);
            entityboat.rotationYaw = ((MathHelper.floor_double((double)((double)(par3EntityPlayer.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3) - 1) * 90;
            if (!par2World.getCollidingBoundingBoxes((Entity)entityboat, entityboat.boundingBox.expand(-0.1, -0.1, -0.1)).isEmpty()) {
                return par1ItemStack;
            }
            if (!par2World.isRemote) {
                par2World.spawnEntityInWorld((Entity)entityboat);
            }
            if (!par3EntityPlayer.capabilities.isCreativeMode) {
                --par1ItemStack.stackSize;
            }
        }
        return par1ItemStack;
    }

    protected EntityIC2Boat makeBoat(ItemStack stack, World world, EntityPlayer player) {
        switch (stack.getItemDamage()) {
            case 0: {
                return new EntityBoatCarbon(world);
            }
            case 1: {
                return new EntityBoatRubber(world);
            }
            case 3: {
                return new EntityBoatElectric(world);
            }
        }
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        InternalName ret;
        switch (itemStack.getItemDamage()) {
            case 0: {
                ret = InternalName.boatCarbon;
                break;
            }
            case 1: {
                ret = InternalName.boatRubber;
                break;
            }
            case 2: {
                ret = InternalName.boatRubberBroken;
                break;
            }
            case 3: {
                ret = InternalName.boatElectric;
                break;
            }
            default: {
                return null;
            }
        }
        return "ic2." + ret.name();
    }

    public void getSubItems(Item item, CreativeTabs tabs, List itemList) {
        ItemStack stack;
        for (int meta = 0; meta <= Short.MAX_VALUE && this.getUnlocalizedName(stack = new ItemStack((Item)this, 1, meta)) != null; ++meta) {
            itemList.add(stack);
        }
    }
}

