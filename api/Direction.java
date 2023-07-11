/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.api;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public enum Direction {
    XN,
    XP,
    YN,
    YP,
    ZN,
    ZP;

    public final int xOffset;
    public final int yOffset;
    public final int zOffset;
    public static final Direction[] directions;
    public static final Set<Direction> noDirections;
    public static final Set<Direction> allDirections;

    private Direction() {
        int side = this.ordinal() / 2;
        int sign = this.getSign();
        this.xOffset = side == 0 ? sign : 0;
        this.yOffset = side == 1 ? sign : 0;
        this.zOffset = side == 2 ? sign : 0;
    }

    public static Direction fromSideValue(int side) {
        return directions[(side + 2) % 6];
    }

    public static Direction fromForgeDirection(ForgeDirection dir) {
        if (dir == ForgeDirection.UNKNOWN) {
            return null;
        }
        return Direction.fromSideValue(dir.ordinal());
    }

    public TileEntity applyToTileEntity(TileEntity te) {
        return this.applyTo(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
    }

    public TileEntity applyTo(World world, int x, int y, int z) {
        int[] coords = new int[]{x, y, z};
        int n = this.ordinal() / 2;
        coords[n] = coords[n] + this.getSign();
        if (world != null && world.blockExists(coords[0], coords[1], coords[2])) {
            try {
                return world.getTileEntity(coords[0], coords[1], coords[2]);
            }
            catch (Exception e) {
                throw new RuntimeException("error getting TileEntity at dim " + world.provider.dimensionId + " " + coords[0] + "/" + coords[1] + "/" + coords[2]);
            }
        }
        return null;
    }

    public Direction getInverse() {
        return directions[this.ordinal() ^ 1];
    }

    public int toSideValue() {
        return (this.ordinal() + 4) % 6;
    }

    private int getSign() {
        return this.ordinal() % 2 * 2 - 1;
    }

    public ForgeDirection toForgeDirection() {
        return ForgeDirection.getOrientation((int)this.toSideValue());
    }

    static {
        directions = Direction.values();
        noDirections = EnumSet.noneOf(Direction.class);
        allDirections = EnumSet.allOf(Direction.class);
    }
}

