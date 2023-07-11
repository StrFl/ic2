/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 */
package ic2.core.audio;

import ic2.core.audio.PositionSpec;
import java.lang.ref.WeakReference;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AudioPosition {
    private final WeakReference<World> worldRef;
    public final float x;
    public final float y;
    public final float z;

    public static AudioPosition getFrom(Object obj, PositionSpec positionSpec) {
        if (obj instanceof AudioPosition) {
            return (AudioPosition)obj;
        }
        if (obj instanceof Entity) {
            Entity e = (Entity)obj;
            return new AudioPosition(e.worldObj, (float)e.posX, (float)e.posY, (float)e.posZ);
        }
        if (obj instanceof TileEntity) {
            TileEntity te = (TileEntity)obj;
            return new AudioPosition(te.getWorldObj(), (float)te.xCoord + 0.5f, (float)te.yCoord + 0.5f, (float)te.zCoord + 0.5f);
        }
        return null;
    }

    public AudioPosition(World world, float x1, float y1, float z1) {
        this.worldRef = new WeakReference<World>(world);
        this.x = x1;
        this.y = y1;
        this.z = z1;
    }

    public World getWorld() {
        return (World)this.worldRef.get();
    }
}

