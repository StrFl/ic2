/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  cpw.mods.fml.common.registry.GameData
 *  net.minecraft.block.Block
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.CompressedStreamTools
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.stats.Achievement
 *  net.minecraft.stats.AchievementList
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraft.world.ChunkCoordIntPair
 *  net.minecraft.world.ChunkPosition
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 */
package ic2.core.network;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.registry.GameData;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.IC2;
import ic2.core.block.invslot.InvSlot;
import ic2.core.util.Tuple;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class DataEncoder {
    private static final Class<?>[] classIds = new Class[]{Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, Character.class, String.class, Enum.class, ItemStack.class, Block.class, Item.class, NBTTagCompound.class, Potion.class, Enchantment.class, Achievement.class, ChunkCoordinates.class, ChunkCoordIntPair.class, ChunkPosition.class, TileEntity.class, World.class, FluidStack.class, FluidTank.class, UUID.class, GameProfile.class, InvSlot.class, Tuple.T2.class, Tuple.T3.class, CropCard.class, Collection.class, Object.class};

    public static Object decode(DataInputStream is) throws IOException {
        try {
            return DataEncoder.decode(is, is.read());
        }
        catch (IllegalArgumentException e) {
            IC2.platform.displayError("An unknown data type was received over multiplayer to be decoded.\nThis could happen due to corrupted data or a bug.", new Object[0]);
            return null;
        }
    }

    public static void encode(DataOutputStream os, Object o) throws IOException {
        try {
            DataEncoder.encode(os, o, true);
        }
        catch (IllegalArgumentException e) {
            IC2.platform.displayError(e, "An unknown data type was attempted to be encoded for sending through\nmultiplayer.\nThis could happen due to a bug.", new Object[0]);
        }
    }

    public static void encode(DataOutputStream os, Object o, boolean withType) throws IOException {
        if (o == null) {
            if (!withType) {
                throw new IllegalArgumentException("o has to be non-null without types");
            }
            os.writeByte(0);
            return;
        }
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            int i;
            if (withType) {
                os.writeByte(1);
            }
            int typeId = DataEncoder.getTypeId(o.getClass().getComponentType());
            os.writeByte(typeId);
            os.writeBoolean(o.getClass().getComponentType().isPrimitive());
            int len = Array.getLength(o);
            DataEncoder.writeVarInt(os, len);
            boolean anyTypeMismatch = false;
            for (i = 0; i < len; ++i) {
                Object value = Array.get(o, i);
                if (value != null && DataEncoder.getTypeId(value.getClass()) == typeId) continue;
                anyTypeMismatch = true;
                break;
            }
            os.writeBoolean(anyTypeMismatch);
            for (i = 0; i < len; ++i) {
                DataEncoder.encode(os, Array.get(o, i), anyTypeMismatch);
            }
        } else {
            if (withType) {
                os.writeByte(DataEncoder.getTypeId(o.getClass()));
            }
            if (o instanceof Byte) {
                os.writeByte(((Byte)o).byteValue());
            } else if (o instanceof Short) {
                os.writeShort(((Short)o).shortValue());
            } else if (o instanceof Integer) {
                os.writeInt((Integer)o);
            } else if (o instanceof Long) {
                os.writeLong((Long)o);
            } else if (o instanceof Float) {
                os.writeFloat(((Float)o).floatValue());
            } else if (o instanceof Double) {
                os.writeDouble((Double)o);
            } else if (o instanceof Boolean) {
                os.writeBoolean((Boolean)o);
            } else if (o instanceof Character) {
                os.writeChar(((Character)o).charValue());
            } else if (o instanceof String) {
                os.writeUTF((String)o);
            } else if (o instanceof Enum) {
                DataEncoder.writeVarInt(os, ((Enum)o).ordinal());
            } else if (o instanceof ItemStack) {
                ItemStack stack = (ItemStack)o;
                DataEncoder.encode(os, stack.getItem(), false);
                os.writeByte(stack.stackSize);
                os.writeShort(stack.getItemDamage());
                DataEncoder.encode(os, stack.stackTagCompound, true);
            } else if (o instanceof Block) {
                os.writeUTF(GameData.getBlockRegistry().getNameForObject(o));
            } else if (o instanceof Item) {
                os.writeUTF(GameData.getItemRegistry().getNameForObject(o));
            } else if (o instanceof NBTTagCompound) {
                CompressedStreamTools.write((NBTTagCompound)((NBTTagCompound)o), (DataOutput)os);
            } else if (o instanceof Potion) {
                os.writeInt(((Potion)o).id);
            } else if (o instanceof Enchantment) {
                os.writeInt(((Enchantment)o).effectId);
            } else if (o instanceof Achievement) {
                os.writeUTF(((Achievement)o).statId);
            } else if (o instanceof ChunkCoordinates) {
                ChunkCoordinates oa = (ChunkCoordinates)o;
                os.writeInt(oa.posX);
                os.writeInt(oa.posY);
                os.writeInt(oa.posZ);
            } else if (o instanceof ChunkCoordIntPair) {
                ChunkCoordIntPair oa = (ChunkCoordIntPair)o;
                os.writeInt(oa.chunkXPos);
                os.writeInt(oa.chunkZPos);
            } else if (o instanceof ChunkPosition) {
                ChunkPosition oa = (ChunkPosition)o;
                os.writeInt(oa.chunkPosX);
                os.writeInt(oa.chunkPosY);
                os.writeInt(oa.chunkPosZ);
            } else if (o instanceof TileEntity) {
                TileEntity oa = (TileEntity)o;
                DataEncoder.encode(os, oa.getWorldObj(), false);
                os.writeInt(oa.xCoord);
                os.writeInt(oa.yCoord);
                os.writeInt(oa.zCoord);
            } else if (o instanceof World) {
                os.writeInt(((World)o).provider.dimensionId);
            } else if (o instanceof FluidStack) {
                FluidStack fs = (FluidStack)o;
                os.writeInt(fs.fluidID);
                os.writeInt(fs.amount);
                DataEncoder.encode(os, fs.tag, true);
            } else if (o instanceof FluidTank) {
                FluidTank tank = (FluidTank)o;
                DataEncoder.encode(os, tank.getFluid(), true);
                os.writeInt(tank.getCapacity());
            } else if (o instanceof UUID) {
                UUID uuid = (UUID)o;
                os.writeLong(uuid.getMostSignificantBits());
                os.writeLong(uuid.getLeastSignificantBits());
            } else if (o instanceof GameProfile) {
                GameProfile gp = (GameProfile)o;
                DataEncoder.encode(os, gp.getId(), true);
                os.writeUTF(gp.getName());
            } else if (o instanceof InvSlot) {
                InvSlot slot = (InvSlot)o;
                ItemStack[] contents = new ItemStack[slot.size()];
                for (int i = 0; i < slot.size(); ++i) {
                    contents[i] = slot.get(i);
                }
                DataEncoder.encode(os, contents, false);
            } else if (o instanceof Tuple.T2) {
                Tuple.T2 t = (Tuple.T2)o;
                DataEncoder.encode(os, t.a, true);
                DataEncoder.encode(os, t.b, true);
            } else if (o instanceof Tuple.T3) {
                Tuple.T3 t = (Tuple.T3)o;
                DataEncoder.encode(os, t.a, true);
                DataEncoder.encode(os, t.b, true);
                DataEncoder.encode(os, t.c, true);
            } else if (o instanceof CropCard) {
                CropCard cropCard = (CropCard)o;
                os.writeUTF(cropCard.owner());
                os.writeUTF(cropCard.name());
            } else if (o instanceof Collection) {
                Object[] data = ((Collection)o).toArray();
                DataEncoder.encode(os, data, false);
            } else {
                throw new IllegalArgumentException("unhandled class: " + clazz);
            }
        }
    }

    public static <T> T decode(DataInputStream is, Class<T> clazz) throws IOException {
        return (T)DataEncoder.decode(is, DataEncoder.getTypeId(clazz));
    }

    public static Object decode(DataInputStream is, int type) throws IOException {
        switch (type) {
            case 0: {
                return null;
            }
            case 1: {
                int typeId = is.read();
                Class<?> componentType = DataEncoder.getClass(typeId);
                boolean primitive = is.readBoolean();
                if (primitive) {
                    componentType = DataEncoder.unbox(componentType);
                }
                int len = DataEncoder.readVarInt(is);
                boolean anyNull = is.readBoolean();
                Object array = Array.newInstance(componentType, len);
                for (int i = 0; i < len; ++i) {
                    if (anyNull) {
                        typeId = is.read();
                    }
                    Array.set(array, i, DataEncoder.decode(is, typeId));
                }
                return array;
            }
            case 2: {
                return is.readByte();
            }
            case 3: {
                return is.readShort();
            }
            case 4: {
                return is.readInt();
            }
            case 5: {
                return is.readLong();
            }
            case 6: {
                return Float.valueOf(is.readFloat());
            }
            case 7: {
                return is.readDouble();
            }
            case 8: {
                return is.readBoolean();
            }
            case 9: {
                return Character.valueOf(is.readChar());
            }
            case 10: {
                return is.readUTF();
            }
            case 11: {
                return DataEncoder.readVarInt(is);
            }
            case 12: {
                Item item = DataEncoder.decode(is, Item.class);
                int size = is.read();
                short meta = is.readShort();
                NBTTagCompound nbt = (NBTTagCompound)DataEncoder.decode(is);
                ItemStack ret = new ItemStack(item, size, (int)meta);
                ret.setTagCompound(nbt);
                return ret;
            }
            case 13: {
                return GameData.getBlockRegistry().getRaw(is.readUTF());
            }
            case 14: {
                return GameData.getItemRegistry().getRaw(is.readUTF());
            }
            case 15: {
                return CompressedStreamTools.read((DataInputStream)is);
            }
            case 16: {
                return Potion.potionTypes[is.readInt()];
            }
            case 17: {
                return Enchantment.enchantmentsList[is.readInt()];
            }
            case 18: {
                String id = is.readUTF();
                for (Object achievement : AchievementList.achievementList) {
                    if (!((Achievement)achievement).statId.equals(id)) continue;
                    return achievement;
                }
                return null;
            }
            case 19: {
                return new ChunkCoordinates(is.readInt(), is.readInt(), is.readInt());
            }
            case 20: {
                return new ChunkCoordIntPair(is.readInt(), is.readInt());
            }
            case 21: {
                return new ChunkPosition(is.readInt(), is.readInt(), is.readInt());
            }
            case 22: {
                World world = DataEncoder.decode(is, World.class);
                int x = is.readInt();
                int y = is.readInt();
                int z = is.readInt();
                if (world != null) {
                    return world.getTileEntity(x, y, z);
                }
                return null;
            }
            case 23: {
                return IC2.platform.getWorld(is.readInt());
            }
            case 24: {
                FluidStack ret = new FluidStack(is.readInt(), is.readInt());
                ret.tag = (NBTTagCompound)DataEncoder.decode(is);
                return ret;
            }
            case 25: {
                return new FluidTank((FluidStack)DataEncoder.decode(is), is.readInt());
            }
            case 26: {
                return new UUID(is.readLong(), is.readLong());
            }
            case 27: {
                return new GameProfile((UUID)DataEncoder.decode(is), is.readUTF());
            }
            case 28: {
                ItemStack[] contents = (ItemStack[])DataEncoder.decode(is, 1);
                InvSlot ret = new InvSlot(contents.length);
                for (int i = 0; i < contents.length; ++i) {
                    ret.put(i, contents[i]);
                }
                return ret;
            }
            case 29: {
                return new Tuple.T2<Object, Object>(DataEncoder.decode(is), DataEncoder.decode(is));
            }
            case 30: {
                return new Tuple.T3<Object, Object, Object>(DataEncoder.decode(is), DataEncoder.decode(is), DataEncoder.decode(is));
            }
            case 31: {
                return Crops.instance.getCropCard(is.readUTF(), is.readUTF());
            }
            case 32: {
                return Arrays.asList((Object[])DataEncoder.decode(is, 1));
            }
            case 33: {
                return new Object();
            }
        }
        throw new IllegalArgumentException("unhandled type: " + type);
    }

    public static <T> boolean copyValue(T src, T dst) {
        if (src == null || dst == null) {
            return false;
        }
        if (dst instanceof ItemStack) {
            ItemStack srcT = (ItemStack)src;
            ItemStack dstT = (ItemStack)dst;
            dstT.func_150996_a(srcT.getItem());
            dstT.setItemDamage(srcT.getItemDamage());
            dstT.stackTagCompound = srcT.stackTagCompound;
        } else if (dst instanceof FluidStack) {
            FluidStack srcT = (FluidStack)src;
            FluidStack dstT = (FluidStack)dst;
            dstT.fluidID = srcT.fluidID;
            dstT.amount = srcT.amount;
            dstT.tag = srcT.tag;
        } else if (dst instanceof FluidTank) {
            FluidTank srcT = (FluidTank)src;
            FluidTank dstT = (FluidTank)dst;
            dstT.setFluid(srcT.getFluid());
            dstT.setCapacity(srcT.getCapacity());
        } else if (dst instanceof InvSlot) {
            InvSlot srcT = (InvSlot)src;
            InvSlot dstT = (InvSlot)dst;
            if (srcT.size() != dstT.size()) {
                throw new RuntimeException("Can't sync InvSlots with mismatched sizes.");
            }
            for (int i = 0; i < srcT.size(); ++i) {
                if (DataEncoder.copyValue(srcT.get(i), dstT.get(i))) continue;
                dstT.put(i, srcT.get(i));
            }
        } else if (dst instanceof Collection) {
            Collection srcT = (Collection)src;
            Collection dstT = (Collection)dst;
            dstT.clear();
            dstT.addAll(srcT);
        } else {
            return false;
        }
        return true;
    }

    private static int getTypeId(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            clazz = DataEncoder.box(clazz);
        }
        for (int i = 0; i < classIds.length; ++i) {
            if (!classIds[i].isAssignableFrom(clazz)) continue;
            return i + 2;
        }
        throw new IllegalArgumentException("unhandled class: " + clazz);
    }

    private static Class<?> getClass(int typeId) {
        return classIds[typeId - 2];
    }

    private static Class<?> box(Class<?> clazz) {
        if (clazz == Byte.TYPE) {
            return Byte.class;
        }
        if (clazz == Short.TYPE) {
            return Short.class;
        }
        if (clazz == Integer.TYPE) {
            return Integer.class;
        }
        if (clazz == Long.TYPE) {
            return Long.class;
        }
        if (clazz == Float.TYPE) {
            return Float.class;
        }
        if (clazz == Double.TYPE) {
            return Double.class;
        }
        if (clazz == Boolean.TYPE) {
            return Boolean.class;
        }
        if (clazz == Character.TYPE) {
            return Character.class;
        }
        return clazz;
    }

    private static Class<?> unbox(Class<?> clazz) {
        if (clazz == Byte.class) {
            return Byte.TYPE;
        }
        if (clazz == Short.class) {
            return Short.TYPE;
        }
        if (clazz == Integer.class) {
            return Integer.TYPE;
        }
        if (clazz == Long.class) {
            return Long.TYPE;
        }
        if (clazz == Float.class) {
            return Float.TYPE;
        }
        if (clazz == Double.class) {
            return Double.TYPE;
        }
        if (clazz == Boolean.class) {
            return Boolean.TYPE;
        }
        if (clazz == Character.class) {
            return Character.TYPE;
        }
        return clazz;
    }

    public static void writeVarInt(DataOutputStream os, int i) throws IOException {
        if (i < 0) {
            throw new IllegalArgumentException("only positive numbers are supported");
        }
        do {
            int part = i & 0x7F;
            if ((i >>>= 7) != 0) {
                part |= 0x80;
            }
            os.writeByte(part);
        } while (i != 0);
    }

    public static int readVarInt(DataInputStream is) throws IOException {
        int i = 0;
        int shift = 0;
        while (true) {
            int part = is.readUnsignedByte();
            i |= (part & 0x7F) << shift;
            if ((part & 0x80) == 0) break;
            shift += 7;
        }
        return i;
    }
}

