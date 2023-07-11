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

public class EntityBoatRubber
extends EntityIC2Boat {
    public EntityBoatRubber(World par1World) {
        super(par1World);
    }

    @Override
    protected ItemStack getItem() {
        return Ic2Items.boatRubber.copy();
    }

    @Override
    protected double getBreakMotion() {
        return 0.23;
    }

    @Override
    protected void breakBoat(double motion) {
        this.playSound("random.pop", 16.0f, 8.0f);
        this.entityDropItem((motion > 0.26 ? Ic2Items.boatRubberBroken : Ic2Items.boatRubber).copy(), 0.0f);
    }

    @Override
    public String getTexture() {
        return "textures/models/boatRubber.png";
    }
}

