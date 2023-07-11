/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Cancelable
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.WorldEvent
 */
package ic2.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

@Cancelable
public class PaintEvent
extends WorldEvent {
    public final int x;
    public final int y;
    public final int z;
    public final int side;
    public final int color;
    public boolean painted = false;

    public PaintEvent(World world1, int x1, int y1, int z1, int side1, int color1) {
        super(world1);
        this.x = x1;
        this.y = y1;
        this.z = z1;
        this.side = side1;
        this.color = color1;
    }
}

