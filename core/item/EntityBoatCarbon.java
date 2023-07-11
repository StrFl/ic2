/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.Ic2Items;
import ic2.core.item.EntityIC2Boat;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityBoatCarbon
extends EntityIC2Boat {
    public EntityBoatCarbon(World par1World) {
        super(par1World);
    }

    @Override
    protected ItemStack getItem() {
        return Ic2Items.boatCarbon.copy();
    }

    @Override
    protected double getBreakMotion() {
        return 0.4;
    }

    @Override
    protected void breakBoat(double motion) {
        this.entityDropItem(this.getItem(), 0.0f);
    }

    @Override
    public String getTexture() {
        return "textures/models/boatCarbon.png";
    }
}

