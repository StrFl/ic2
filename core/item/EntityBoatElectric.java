/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.item.EntityIC2Boat;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityBoatElectric
extends EntityIC2Boat {
    private static final double euConsume = 4.0;
    private boolean accelerated = false;

    public EntityBoatElectric(World par1World) {
        super(par1World);
    }

    @Override
    protected ItemStack getItem() {
        return Ic2Items.boatElectric.copy();
    }

    @Override
    protected double getBreakMotion() {
        return 0.5;
    }

    @Override
    protected void breakBoat(double motion) {
        this.entityDropItem(this.getItem(), 0.0f);
    }

    @Override
    protected double getAccelerationFactor() {
        return this.accelerated ? 1.5 : 0.25;
    }

    @Override
    protected double getTopSpeed() {
        return 0.7;
    }

    @Override
    protected boolean isOnWater(AxisAlignedBB aabb) {
        return this.worldObj.isAABBInMaterial(aabb, Material.water) || this.worldObj.isAABBInMaterial(aabb, Material.lava);
    }

    @Override
    public String getTexture() {
        return "textures/models/boatElectric.png";
    }

    @Override
    public void onUpdate() {
        this.isImmuneToFire = true;
        this.extinguish();
        if (this.ridingEntity != null) {
            this.ridingEntity.extinguish();
        }
        this.accelerated = false;
        if (this.riddenByEntity instanceof EntityPlayer && IC2.keyboard.isForwardKeyDown((EntityPlayer)this.riddenByEntity)) {
            EntityPlayer player = (EntityPlayer)this.riddenByEntity;
            for (int i = 0; i < 4; ++i) {
                ItemStack stack = player.inventory.armorInventory[i];
                if (stack == null || ElectricItem.manager.discharge(stack, 4.0, Integer.MAX_VALUE, true, true, true) != 4.0) continue;
                ElectricItem.manager.discharge(stack, 4.0, Integer.MAX_VALUE, true, true, false);
                this.accelerated = true;
                break;
            }
        }
        super.onUpdate();
    }
}

