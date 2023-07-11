/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.dispenser.BehaviorProjectileDispense
 *  net.minecraft.dispenser.IPosition
 *  net.minecraft.entity.IProjectile
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.block.EntityDynamite;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class BehaviorDynamiteDispense
extends BehaviorProjectileDispense {
    private boolean sticky = false;

    public BehaviorDynamiteDispense(boolean sticky1) {
        this.sticky = sticky1;
    }

    protected IProjectile getProjectileEntity(World var1, IPosition var2) {
        return new EntityDynamite(var1, var2.getX(), var2.getY(), var2.getZ(), this.sticky);
    }
}

