/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.Vec3
 */
package ic2.core.util;

import ic2.api.Direction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class AabbUtil {
    public static Direction getIntersection(Vec3 origin, Vec3 direction, AxisAlignedBB bbox, Vec3 intersection) {
        double length = direction.lengthVector();
        Vec3 normalizedDirection = Vec3.createVectorHelper((double)(direction.xCoord / length), (double)(direction.yCoord / length), (double)(direction.zCoord / length));
        Direction intersectingDirection = AabbUtil.intersects(origin, normalizedDirection, bbox);
        if (intersectingDirection == null) {
            return null;
        }
        Vec3 planeOrigin = normalizedDirection.xCoord < 0.0 && normalizedDirection.yCoord < 0.0 && normalizedDirection.zCoord < 0.0 ? Vec3.createVectorHelper((double)bbox.maxX, (double)bbox.maxY, (double)bbox.maxZ) : (normalizedDirection.xCoord < 0.0 && normalizedDirection.yCoord < 0.0 && normalizedDirection.zCoord >= 0.0 ? Vec3.createVectorHelper((double)bbox.maxX, (double)bbox.maxY, (double)bbox.minZ) : (normalizedDirection.xCoord < 0.0 && normalizedDirection.yCoord >= 0.0 && normalizedDirection.zCoord < 0.0 ? Vec3.createVectorHelper((double)bbox.maxX, (double)bbox.minY, (double)bbox.maxZ) : (normalizedDirection.xCoord < 0.0 && normalizedDirection.yCoord >= 0.0 && normalizedDirection.zCoord >= 0.0 ? Vec3.createVectorHelper((double)bbox.maxX, (double)bbox.minY, (double)bbox.minZ) : (normalizedDirection.xCoord >= 0.0 && normalizedDirection.yCoord < 0.0 && normalizedDirection.zCoord < 0.0 ? Vec3.createVectorHelper((double)bbox.minX, (double)bbox.maxY, (double)bbox.maxZ) : (normalizedDirection.xCoord >= 0.0 && normalizedDirection.yCoord < 0.0 && normalizedDirection.zCoord >= 0.0 ? Vec3.createVectorHelper((double)bbox.minX, (double)bbox.maxY, (double)bbox.minZ) : (normalizedDirection.xCoord >= 0.0 && normalizedDirection.yCoord >= 0.0 && normalizedDirection.zCoord < 0.0 ? Vec3.createVectorHelper((double)bbox.minX, (double)bbox.minY, (double)bbox.maxZ) : Vec3.createVectorHelper((double)bbox.minX, (double)bbox.minY, (double)bbox.minZ)))))));
        Vec3 planeNormalVector = null;
        switch (intersectingDirection) {
            case XN: 
            case XP: {
                planeNormalVector = Vec3.createVectorHelper((double)1.0, (double)0.0, (double)0.0);
                break;
            }
            case YN: 
            case YP: {
                planeNormalVector = Vec3.createVectorHelper((double)0.0, (double)1.0, (double)0.0);
                break;
            }
            case ZN: 
            case ZP: {
                planeNormalVector = Vec3.createVectorHelper((double)0.0, (double)0.0, (double)1.0);
            }
        }
        Vec3 newIntersection = AabbUtil.getIntersectionWithPlane(origin, normalizedDirection, planeOrigin, planeNormalVector);
        intersection.xCoord = newIntersection.xCoord;
        intersection.yCoord = newIntersection.yCoord;
        intersection.zCoord = newIntersection.zCoord;
        return intersectingDirection;
    }

    public static Direction intersects(Vec3 origin, Vec3 direction, AxisAlignedBB bbox) {
        double[] ray = AabbUtil.getRay(origin, direction);
        if (direction.xCoord < 0.0 && direction.yCoord < 0.0 && direction.zCoord < 0.0) {
            if (origin.xCoord < bbox.minX) {
                return null;
            }
            if (origin.yCoord < bbox.minY) {
                return null;
            }
            if (origin.zCoord < bbox.minZ) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EF, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EH, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DH, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DC, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BC, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BF, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.HG, bbox)) > 0.0 && AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.FG, bbox)) < 0.0) {
                return Direction.ZP;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.CG, bbox)) < 0.0) {
                return Direction.YP;
            }
            return Direction.XP;
        }
        if (direction.xCoord < 0.0 && direction.yCoord < 0.0 && direction.zCoord >= 0.0) {
            if (origin.xCoord < bbox.minX) {
                return null;
            }
            if (origin.yCoord < bbox.minY) {
                return null;
            }
            if (origin.zCoord > bbox.maxZ) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.HG, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DH, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AD, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AB, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BF, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.FG, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DC, bbox)) > 0.0 && AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.CG, bbox)) > 0.0) {
                return Direction.XP;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BC, bbox)) < 0.0) {
                return Direction.YP;
            }
            return Direction.ZN;
        }
        if (direction.xCoord < 0.0 && direction.yCoord >= 0.0 && direction.zCoord < 0.0) {
            if (origin.xCoord < bbox.minX) {
                return null;
            }
            if (origin.yCoord > bbox.maxY) {
                return null;
            }
            if (origin.zCoord < bbox.minZ) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.FG, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EF, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AE, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AD, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DC, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.CG, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EH, bbox)) > 0.0 && AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.HG, bbox)) > 0.0) {
                return Direction.ZP;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DH, bbox)) < 0.0) {
                return Direction.XP;
            }
            return Direction.YN;
        }
        if (direction.xCoord < 0.0 && direction.yCoord >= 0.0 && direction.zCoord >= 0.0) {
            if (origin.xCoord < bbox.minX) {
                return null;
            }
            if (origin.yCoord > bbox.maxY) {
                return null;
            }
            if (origin.zCoord > bbox.maxZ) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EH, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AE, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AB, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BC, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.CG, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.HG, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AD, bbox)) > 0.0 && AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DH, bbox)) > 0.0) {
                return Direction.YN;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DC, bbox)) < 0.0) {
                return Direction.ZN;
            }
            return Direction.XP;
        }
        if (direction.xCoord >= 0.0 && direction.yCoord < 0.0 && direction.zCoord < 0.0) {
            if (origin.xCoord > bbox.maxX) {
                return null;
            }
            if (origin.yCoord < bbox.minY) {
                return null;
            }
            if (origin.zCoord < bbox.minZ) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AB, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AE, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EH, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.HG, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.CG, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BC, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EF, bbox)) > 0.0 && AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BF, bbox)) < 0.0) {
                return Direction.XN;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.FG, bbox)) < 0.0) {
                return Direction.ZP;
            }
            return Direction.YP;
        }
        if (direction.xCoord >= 0.0 && direction.yCoord < 0.0 && direction.zCoord >= 0.0) {
            if (origin.xCoord > bbox.maxX) {
                return null;
            }
            if (origin.yCoord < bbox.minY) {
                return null;
            }
            if (origin.zCoord > bbox.maxZ) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DC, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AD, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AE, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EF, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.FG, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.CG, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AB, bbox)) > 0.0 && AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BC, bbox)) > 0.0) {
                return Direction.ZN;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BF, bbox)) < 0.0) {
                return Direction.XN;
            }
            return Direction.YP;
        }
        if (direction.xCoord >= 0.0 && direction.yCoord >= 0.0 && direction.zCoord < 0.0) {
            if (origin.xCoord > bbox.maxX) {
                return null;
            }
            if (origin.yCoord > bbox.maxY) {
                return null;
            }
            if (origin.zCoord < bbox.minZ) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BF, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AB, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AD, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DH, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.HG, bbox)) < 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.FG, bbox)) > 0.0) {
                return null;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AE, bbox)) > 0.0 && AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EF, bbox)) > 0.0) {
                return Direction.XN;
            }
            if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EH, bbox)) < 0.0) {
                return Direction.YN;
            }
            return Direction.ZP;
        }
        if (origin.xCoord > bbox.maxX) {
            return null;
        }
        if (origin.yCoord > bbox.maxY) {
            return null;
        }
        if (origin.zCoord > bbox.maxZ) {
            return null;
        }
        if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EF, bbox)) < 0.0) {
            return null;
        }
        if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.EH, bbox)) > 0.0) {
            return null;
        }
        if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DH, bbox)) < 0.0) {
            return null;
        }
        if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.DC, bbox)) > 0.0) {
            return null;
        }
        if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BC, bbox)) < 0.0) {
            return null;
        }
        if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.BF, bbox)) > 0.0) {
            return null;
        }
        if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AB, bbox)) < 0.0 && AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AE, bbox)) > 0.0) {
            return Direction.XN;
        }
        if (AabbUtil.side(ray, AabbUtil.getEdgeRay(Edge.AD, bbox)) < 0.0) {
            return Direction.ZN;
        }
        return Direction.YN;
    }

    private static double[] getRay(Vec3 origin, Vec3 direction) {
        double[] ret = new double[]{origin.xCoord * direction.yCoord - direction.xCoord * origin.yCoord, origin.xCoord * direction.zCoord - direction.xCoord * origin.zCoord, -direction.xCoord, origin.yCoord * direction.zCoord - direction.yCoord * origin.zCoord, -direction.zCoord, direction.yCoord};
        return ret;
    }

    private static double[] getEdgeRay(Edge edge, AxisAlignedBB bbox) {
        switch (edge) {
            case AD: {
                return new double[]{-bbox.minY, -bbox.minZ, -1.0, 0.0, 0.0, 0.0};
            }
            case AB: {
                return new double[]{bbox.minX, 0.0, 0.0, -bbox.minZ, 0.0, 1.0};
            }
            case AE: {
                return new double[]{0.0, bbox.minX, 0.0, bbox.minY, -1.0, 0.0};
            }
            case DC: {
                return new double[]{bbox.maxX, 0.0, 0.0, -bbox.minZ, 0.0, 1.0};
            }
            case DH: {
                return new double[]{0.0, bbox.maxX, 0.0, bbox.minY, -1.0, 0.0};
            }
            case BC: {
                return new double[]{-bbox.maxY, -bbox.minZ, -1.0, 0.0, 0.0, 0.0};
            }
            case BF: {
                return new double[]{0.0, bbox.minX, 0.0, bbox.maxY, -1.0, 0.0};
            }
            case EH: {
                return new double[]{-bbox.minY, -bbox.maxZ, -1.0, 0.0, 0.0, 0.0};
            }
            case EF: {
                return new double[]{bbox.minX, 0.0, 0.0, -bbox.maxZ, 0.0, 1.0};
            }
            case CG: {
                return new double[]{0.0, bbox.maxX, 0.0, bbox.maxY, -1.0, 0.0};
            }
            case FG: {
                return new double[]{-bbox.maxY, -bbox.maxZ, -1.0, 0.0, 0.0, 0.0};
            }
            case HG: {
                return new double[]{bbox.maxX, 0.0, 0.0, -bbox.maxZ, 0.0, 1.0};
            }
        }
        return new double[0];
    }

    private static double side(double[] ray1, double[] ray2) {
        return ray1[2] * ray2[3] + ray1[5] * ray2[1] + ray1[4] * ray2[0] + ray1[1] * ray2[5] + ray1[0] * ray2[4] + ray1[3] * ray2[2];
    }

    private static Vec3 getIntersectionWithPlane(Vec3 origin, Vec3 direction, Vec3 planeOrigin, Vec3 planeNormalVector) {
        double distance = AabbUtil.getDistanceToPlane(origin, direction, planeOrigin, planeNormalVector);
        return Vec3.createVectorHelper((double)(origin.xCoord + direction.xCoord * distance), (double)(origin.yCoord + direction.yCoord * distance), (double)(origin.zCoord + direction.zCoord * distance));
    }

    private static double getDistanceToPlane(Vec3 origin, Vec3 direction, Vec3 planeOrigin, Vec3 planeNormalVector) {
        Vec3 base = Vec3.createVectorHelper((double)(planeOrigin.xCoord - origin.xCoord), (double)(planeOrigin.yCoord - origin.yCoord), (double)(planeOrigin.zCoord - origin.zCoord));
        return AabbUtil.dotProduct(base, planeNormalVector) / AabbUtil.dotProduct(direction, planeNormalVector);
    }

    private static double dotProduct(Vec3 a, Vec3 b) {
        return a.xCoord * b.xCoord + a.yCoord * b.yCoord + a.zCoord * b.zCoord;
    }

    static enum Edge {
        AD,
        AB,
        AE,
        DC,
        DH,
        BC,
        BF,
        EH,
        EF,
        CG,
        FG,
        HG;

    }
}

