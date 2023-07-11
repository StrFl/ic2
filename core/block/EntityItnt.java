/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.Ic2Items;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.util.StackUtil;
import net.minecraft.world.World;

public class EntityItnt
extends EntityIC2Explosive {
    public EntityItnt(World world, double x, double y, double z) {
        super(world, x, y, z, 60, 5.5f, 0.9f, 0.3f, StackUtil.getBlock(Ic2Items.industrialTnt), 0);
    }

    public EntityItnt(World world) {
        this(world, 0.0, 0.0, 0.0);
    }
}

