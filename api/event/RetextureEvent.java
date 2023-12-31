/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Cancelable
 *  net.minecraft.block.Block
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.WorldEvent
 */
package ic2.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

@Cancelable
public class RetextureEvent
extends WorldEvent {
    public final int x;
    public final int y;
    public final int z;
    public final int side;
    public final Block referencedBlock;
    public final int referencedMeta;
    public final int referencedSide;
    public boolean applied = false;

    public RetextureEvent(World world1, int x1, int y1, int z1, int side1, Block referencedBlock, int referencedMeta1, int referencedSide1) {
        super(world1);
        this.x = x1;
        this.y = y1;
        this.z = z1;
        this.side = side1;
        this.referencedBlock = referencedBlock;
        this.referencedMeta = referencedMeta1;
        this.referencedSide = referencedSide1;
    }
}

