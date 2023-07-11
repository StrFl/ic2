/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.WorldServer
 */
package ic2.core.block.machine.tileentity;

import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Redstone;
import ic2.core.item.armor.ItemArmorHazmat;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;

public class TileEntityTesla
extends TileEntityBlock {
    protected final Redstone redstone;
    protected final Energy energy;
    private int ticker = IC2.random.nextInt(32);

    public TileEntityTesla() {
        this.redstone = this.addComponent(new Redstone(this));
        this.energy = this.addComponent(Energy.asBasicSink(this, 10000.0, 2));
    }

    @Override
    protected void updateEntityServer() {
        int damage;
        super.updateEntityServer();
        if (!this.redstone.hasRedstoneInput()) {
            return;
        }
        if (this.energy.useEnergy(1.0) && this.ticker++ % 32 == 0 && (damage = (int)this.energy.getEnergy() / 400) > 0 && this.shock(damage)) {
            this.energy.useEnergy(damage * 400);
        }
    }

    protected boolean shock(int damage) {
        int r = 4;
        List entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox((double)(this.xCoord - 4), (double)(this.yCoord - 4), (double)(this.zCoord - 4), (double)(this.xCoord + 4 + 1), (double)(this.yCoord + 4 + 1), (double)(this.zCoord + 4 + 1)));
        for (EntityLivingBase entity : entities) {
            if (ItemArmorHazmat.hasCompleteHazmat(entity) || !entity.attackEntityFrom((DamageSource)IC2DamageSource.electricity, (float)damage)) continue;
            if (this.worldObj instanceof WorldServer) {
                WorldServer world = (WorldServer)this.worldObj;
                Random rnd = world.rand;
                for (int i = 0; i < damage; ++i) {
                    world.func_147487_a("reddust", entity.posX + (double)rnd.nextFloat() - 0.5, entity.posY + (double)(rnd.nextFloat() * 2.0f) - 1.0, entity.posZ + (double)rnd.nextFloat() - 0.5, 0, (double)0.1f, (double)0.1f, 1.0, 1.0);
                }
            }
            return true;
        }
        return false;
    }
}

