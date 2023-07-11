/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.ChunkCoordIntPair
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraft.world.chunk.EmptyChunk
 *  net.minecraft.world.gen.ChunkProviderServer
 *  net.minecraftforge.common.util.FakePlayer
 *  net.minecraftforge.oredict.OreDictionary
 */
package ic2.core.util;

import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.util.LogCategory;
import ic2.core.util.Vector3;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.oredict.OreDictionary;

public final class Util {
    private static final boolean inDev = System.getProperty("INDEV") != null;
    private static final Map<Class<?>, Boolean> checkedClasses = new HashMap();

    public static int roundToNegInf(float x) {
        int ret = (int)x;
        if ((float)ret > x) {
            --ret;
        }
        return ret;
    }

    public static int roundToNegInf(double x) {
        int ret = (int)x;
        if ((double)ret > x) {
            --ret;
        }
        return ret;
    }

    public static int saturatedCast(double x) {
        if (x > 2.147483647E9) {
            return Integer.MAX_VALUE;
        }
        if (x < -2.147483648E9) {
            return Integer.MIN_VALUE;
        }
        return (int)x;
    }

    public static int limit(int value, int min, int max) {
        if (value <= min) {
            return min;
        }
        if (value >= max) {
            return max;
        }
        return value;
    }

    public static double limit(double value, double min, double max) {
        if (Double.isNaN(value) || value <= min) {
            return min;
        }
        if (value >= max) {
            return max;
        }
        return value;
    }

    public static double map(double value, double srcMax, double dstMax) {
        if (value < 0.0 || Double.isNaN(value)) {
            value = 0.0;
        }
        if (value > srcMax) {
            value = srcMax;
        }
        return value / srcMax * dstMax;
    }

    public static double lerp(double start, double end, double fraction) {
        assert (fraction >= 0.0 && fraction <= 1.0);
        return start + (end - start) * fraction;
    }

    public static int square(int x) {
        return x * x;
    }

    public static double square(double x) {
        return x * x;
    }

    public static boolean isSimilar(double a, double b) {
        return Math.abs(a - b) < 1.0E-6;
    }

    public static int countInArray(Object[] oa, Class<?> cls) {
        int ret = 0;
        for (Object o : oa) {
            if (!cls.isAssignableFrom(o.getClass())) continue;
            ++ret;
        }
        return ret;
    }

    public static InternalName getColorName(int color) {
        switch (color) {
            case 0: {
                return InternalName.black;
            }
            case 1: {
                return InternalName.red;
            }
            case 2: {
                return InternalName.green;
            }
            case 3: {
                return InternalName.brown;
            }
            case 4: {
                return InternalName.blue;
            }
            case 5: {
                return InternalName.purple;
            }
            case 6: {
                return InternalName.cyan;
            }
            case 7: {
                return InternalName.lightGray;
            }
            case 8: {
                return InternalName.gray;
            }
            case 9: {
                return InternalName.pink;
            }
            case 10: {
                return InternalName.lime;
            }
            case 11: {
                return InternalName.yellow;
            }
            case 12: {
                return InternalName.lightBlue;
            }
            case 13: {
                return InternalName.magenta;
            }
            case 14: {
                return InternalName.orange;
            }
            case 15: {
                return InternalName.white;
            }
        }
        return null;
    }

    public static boolean inDev() {
        return inDev;
    }

    public static boolean hasAssertions() {
        boolean ret = false;
        if (!$assertionsDisabled) {
            ret = true;
            if (!true) {
                throw new AssertionError();
            }
        }
        return ret;
    }

    public static boolean matchesOD(ItemStack stack, Object match) {
        if (match instanceof ItemStack) {
            return stack == null || stack.isItemEqual((ItemStack)match);
        }
        if (match instanceof String) {
            if (stack == null) {
                return false;
            }
            for (int oreId : OreDictionary.getOreIDs((ItemStack)stack)) {
                if (!OreDictionary.getOreName((int)oreId).equals(match)) continue;
                return true;
            }
            return false;
        }
        return stack == match;
    }

    public static String asString(TileEntity te) {
        if (te == null) {
            return null;
        }
        return te + " (" + Util.formatPosition(te) + ")";
    }

    public static String formatPosition(TileEntity te) {
        return Util.formatPosition((IBlockAccess)te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
    }

    public static String formatPosition(IBlockAccess world, int x, int y, int z) {
        int dimId = world instanceof World && ((World)world).provider != null ? ((World)world).provider.dimensionId : Integer.MIN_VALUE;
        return Util.formatPosition(dimId, x, y, z);
    }

    public static String formatPosition(int dimId, int x, int y, int z) {
        return "dim " + dimId + ": " + x + "/" + y + "/" + z;
    }

    public static String toSiString(double value, int digits) {
        int dVal;
        String si;
        double mul;
        if (value == 0.0) {
            return "0 ";
        }
        if (Double.isNaN(value)) {
            return "NaN ";
        }
        String ret = "";
        if (value < 0.0) {
            ret = "-";
            value = -value;
        }
        if (Double.isInfinite(value)) {
            return ret + "\u221e ";
        }
        double log = Math.log10(value);
        if (log >= 0.0) {
            int reduce = (int)Math.floor(log / 3.0);
            mul = 1.0 / Math.pow(10.0, reduce * 3);
            switch (reduce) {
                case 0: {
                    si = "";
                    break;
                }
                case 1: {
                    si = "k";
                    break;
                }
                case 2: {
                    si = "M";
                    break;
                }
                case 3: {
                    si = "G";
                    break;
                }
                case 4: {
                    si = "T";
                    break;
                }
                case 5: {
                    si = "P";
                    break;
                }
                case 6: {
                    si = "E";
                    break;
                }
                case 7: {
                    si = "Z";
                    break;
                }
                case 8: {
                    si = "Y";
                    break;
                }
                default: {
                    si = "E" + reduce * 3;
                    break;
                }
            }
        } else {
            int expand = (int)Math.ceil(-log / 3.0);
            mul = Math.pow(10.0, expand * 3);
            switch (expand) {
                case 0: {
                    si = "";
                    break;
                }
                case 1: {
                    si = "m";
                    break;
                }
                case 2: {
                    si = "\u00b5";
                    break;
                }
                case 3: {
                    si = "n";
                    break;
                }
                case 4: {
                    si = "p";
                    break;
                }
                case 5: {
                    si = "f";
                    break;
                }
                case 6: {
                    si = "a";
                    break;
                }
                case 7: {
                    si = "z";
                    break;
                }
                case 8: {
                    si = "y";
                    break;
                }
                default: {
                    si = "E-" + expand * 3;
                }
            }
        }
        int iVal = (int)Math.floor(value *= mul);
        value -= (double)iVal;
        int iDigits = 1;
        if (iVal > 0) {
            iDigits = (int)((double)iDigits + Math.floor(Math.log10(iVal)));
        }
        if ((double)(dVal = (int)Math.round(value * (mul = Math.pow(10.0, digits - iDigits)))) >= mul) {
            dVal = (int)((double)dVal - mul);
            iDigits = 1;
            if (++iVal > 0) {
                iDigits = (int)((double)iDigits + Math.floor(Math.log10(iVal)));
            }
        }
        ret = ret + Integer.toString(iVal);
        if (digits > iDigits && dVal != 0) {
            ret = ret + String.format(".%0" + (digits - iDigits) + "d", dVal);
        }
        ret = ret.replaceFirst("(\\.\\d*?)0+$", "$1");
        return ret + " " + si;
    }

    public static void exit(int status) {
        Method exit = null;
        try {
            exit = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
            exit.setAccessible(true);
        }
        catch (Exception e) {
            IC2.log.warn(LogCategory.General, e, "Method lookup failed.");
            try {
                Field security = System.class.getDeclaredField("security");
                security.setAccessible(true);
                security.set(null, null);
                exit = System.class.getMethod("exit", Integer.TYPE);
            }
            catch (Exception f) {
                throw new Error(f);
            }
        }
        try {
            exit.invoke(null, status);
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }

    public static MovingObjectPosition traceEntities(EntityPlayer player, boolean alwaysCollide) {
        Vector3 start = new Vector3(player.posX, player.posY + (double)(player.worldObj.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()), player.posZ);
        double distance = player instanceof EntityPlayerMP ? ((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance() : 5.0;
        return Util.traceEntities(player.worldObj, start.toVec3(), new Vector3(player.getLookVec()).scale(distance).add(start).toVec3(), (Entity)player, alwaysCollide);
    }

    public static MovingObjectPosition traceEntities(EntityPlayer player, Vec3 end, boolean alwaysCollide) {
        Vector3 start = new Vector3(player.posX, player.posY + (double)(player.worldObj.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()), player.posZ);
        return Util.traceEntities(player.worldObj, start.toVec3(), end, (Entity)player, alwaysCollide);
    }

    public static MovingObjectPosition traceEntities(World world, Vec3 start, Vec3 end, Entity exclude, boolean alwaysCollide) {
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox((double)Math.min(start.xCoord, end.xCoord), (double)Math.min(start.yCoord, end.yCoord), (double)Math.min(start.zCoord, end.zCoord), (double)Math.max(start.xCoord, end.xCoord), (double)Math.max(start.yCoord, end.yCoord), (double)Math.max(start.zCoord, end.zCoord));
        List entities = world.getEntitiesWithinAABBExcludingEntity(exclude, aabb);
        MovingObjectPosition closest = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (Entity entity : entities) {
            double distance;
            MovingObjectPosition pos;
            if (!alwaysCollide && !entity.canBeCollidedWith() || (pos = entity.boundingBox.calculateIntercept(start, end)) == null || !((distance = start.squareDistanceTo(pos.hitVec)) < minDist)) continue;
            pos.entityHit = entity;
            pos.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
            minDist = distance;
            closest = pos;
        }
        return closest;
    }

    public static boolean isFakePlayer(EntityPlayer entity, boolean fuzzy) {
        if (entity == null) {
            return false;
        }
        if (!(entity instanceof EntityPlayerMP)) {
            return true;
        }
        if (fuzzy) {
            return entity instanceof FakePlayer;
        }
        return entity.getClass() != EntityPlayerMP.class;
    }

    public static Chunk getLoadedChunk(World world, int chunkX, int chunkZ) {
        Chunk chunk = null;
        if (world.getChunkProvider() instanceof ChunkProviderServer) {
            ChunkProviderServer cps = (ChunkProviderServer)world.getChunkProvider();
            try {
                chunk = (Chunk)cps.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int((int)chunkX, (int)chunkZ));
            }
            catch (NoSuchFieldError e) {
                if (cps.chunkExists(chunkX, chunkZ)) {
                    chunk = cps.provideChunk(chunkX, chunkZ);
                }
            }
        } else {
            chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        }
        if (chunk instanceof EmptyChunk) {
            return null;
        }
        return chunk;
    }

    public static boolean checkMcCoordBounds(int x, int y, int z) {
        return Util.checkMcCoordBounds(x, z) && y >= 0 && y < 256;
    }

    public static boolean checkMcCoordBounds(int x, int z) {
        return x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000;
    }

    public static boolean checkInterfaces(Class<?> cls) {
        Boolean cached = checkedClasses.get(cls);
        if (cached != null) {
            return cached;
        }
        HashSet interfaces = new HashSet();
        Class<?> c = cls;
        do {
            for (Class<?> i : c.getInterfaces()) {
                interfaces.add(i);
            }
        } while ((c = c.getSuperclass()) != null);
        boolean result = true;
        for (Class clazz : interfaces) {
            for (Method method : clazz.getMethods()) {
                boolean found = false;
                c = cls;
                do {
                    try {
                        Method match = c.getDeclaredMethod(method.getName(), method.getParameterTypes());
                        if (match.getReturnType() != method.getReturnType()) continue;
                        found = true;
                        break;
                    }
                    catch (NoSuchMethodException noSuchMethodException) {
                        // empty catch block
                    }
                } while ((c = c.getSuperclass()) != null);
                if (found) continue;
                IC2.log.info(LogCategory.General, "Can't find method %s.%s in %s.", method.getDeclaringClass(), method.getName(), cls.getName());
                result = false;
            }
        }
        checkedClasses.put(cls, result);
        return result;
    }
}

