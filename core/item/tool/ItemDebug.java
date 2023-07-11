/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameData
 *  net.minecraft.block.Block
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.MovingObjectPosition$MovingObjectType
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import cpw.mods.fml.common.registry.GameData;
import ic2.api.crops.CropCard;
import ic2.api.item.IBoxable;
import ic2.api.item.IDebuggable;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.IC2;
import ic2.core.WorldData;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.crop.TileEntityCrop;
import ic2.core.init.InternalName;
import ic2.core.item.InfiniteElectricItemManager;
import ic2.core.item.ItemIC2;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemDebug
extends ItemIC2
implements ISpecialElectricItem,
IBoxable {
    private static IElectricItemManager manager = null;

    public ItemDebug(InternalName internalName) {
        super(internalName);
        this.setHasSubtypes(false);
        if (!Util.inDev()) {
            this.setCreativeTab(null);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "ic2.debugItem";
    }

    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        block36: {
            ByteArrayOutputStream chatBuffer;
            ByteArrayOutputStream consoleBuffer;
            block35: {
                TileEntity tileentity = world.getTileEntity(x, y, z);
                if (tileentity instanceof IDebuggable) {
                    if (!IC2.platform.isSimulating()) {
                        return false;
                    }
                    IDebuggable dbg = (IDebuggable)tileentity;
                    if (dbg.isDebuggable() && IC2.platform.isSimulating()) {
                        IC2.platform.messagePlayer(player, dbg.getDebugText(), new Object[0]);
                    }
                    return IC2.platform.isSimulating();
                }
                NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
                int modeIdx = nbtData.getInteger("mode");
                if (modeIdx > Mode.modes.length) {
                    modeIdx = 0;
                }
                Mode mode = Mode.modes[modeIdx];
                if (IC2.keyboard.isModeSwitchKeyDown(player)) {
                    if (IC2.platform.isSimulating()) {
                        mode = Mode.modes[(mode.ordinal() + 1) % Mode.modes.length];
                        nbtData.setInteger("mode", mode.ordinal());
                        IC2.platform.messagePlayer(player, "Debug Item Mode: " + mode.getName(), new Object[0]);
                    }
                    return false;
                }
                consoleBuffer = new ByteArrayOutputStream();
                PrintStream console = new PrintStream(consoleBuffer);
                chatBuffer = new ByteArrayOutputStream();
                PrintStream chat = new PrintStream(chatBuffer);
                switch (mode) {
                    case InterfacesFields: {
                        String plat;
                        MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(world, player, true);
                        if (position == null) {
                            return false;
                        }
                        MovingObjectPosition entityPosition = Util.traceEntities(player, position.hitVec, true);
                        if (entityPosition != null) {
                            position = entityPosition;
                        }
                        String string = IC2.platform.isRendering() ? (IC2.platform.isSimulating() ? "sp" : "client") : (plat = "server");
                        if (position.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                            x = position.blockX;
                            y = position.blockY;
                            z = position.blockZ;
                            Block block = world.getBlock(x, y, z);
                            String blockName = GameData.getBlockRegistry().getNameForObject((Object)block);
                            int meta = world.getBlockMetadata(x, y, z);
                            TileEntity tileEntity = world.getTileEntity(x, y, z);
                            String message = "[" + plat + "] block id: " + blockName + " meta: " + meta + " name: " + block.getUnlocalizedName() + " te: " + tileEntity;
                            chat.println(message);
                            console.println(message);
                            if (tileEntity != null) {
                                message = "[" + plat + "] interfaces:";
                                Class<?> c = tileEntity.getClass();
                                do {
                                    for (Class<?> i : c.getInterfaces()) {
                                        message = message + " " + i.getName();
                                    }
                                } while ((c = c.getSuperclass()) != null);
                                chat.println(message);
                                console.println(message);
                            }
                            console.println("block fields:");
                            ItemDebug.dumpObjectFields(console, block);
                            if (tileEntity == null) break;
                            console.println("tile entity fields:");
                            ItemDebug.dumpObjectFields(console, tileEntity);
                            break;
                        }
                        if (position.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                            String message = "[" + plat + "] entity: " + position.entityHit;
                            chat.println(message);
                            console.println(message);
                            if (!(position.entityHit instanceof EntityItem)) break;
                            ItemStack entStack = ((EntityItem)position.entityHit).getEntityItem();
                            String name = GameData.getItemRegistry().getNameForObject((Object)entStack.getItem());
                            message = "[" + plat + "] item id: " + name + " meta: " + entStack.getItemDamage() + " size: " + entStack.stackSize + " name: " + entStack.getUnlocalizedName();
                            chat.println(message);
                            console.println(message);
                            console.println("NBT: " + entStack.getTagCompound());
                            break;
                        }
                        return false;
                    }
                    case TileData: {
                        Object te;
                        if (!IC2.platform.isSimulating()) {
                            return false;
                        }
                        TileEntity tileEntity = world.getTileEntity(x, y, z);
                        if (tileEntity instanceof TileEntityBlock) {
                            te = (TileEntityBlock)tileEntity;
                            chat.println("Block: Active=" + ((TileEntityBlock)te).getActive() + " Facing=" + ((TileEntityBlock)te).getFacing());
                        }
                        if (tileEntity instanceof TileEntityBaseGenerator) {
                            te = (TileEntityBaseGenerator)tileEntity;
                            chat.println("BaseGen: Fuel=" + ((TileEntityBaseGenerator)te).fuel + " Storage=" + ((TileEntityBaseGenerator)te).storage);
                        }
                        if (tileEntity instanceof TileEntityElectricMachine) {
                            te = (TileEntityElectricMachine)tileEntity;
                            chat.println("ElecMachine: Energy=" + ((TileEntityElectricMachine)te).energy);
                        }
                        if (tileEntity instanceof IEnergyStorage) {
                            te = (IEnergyStorage)tileEntity;
                            chat.println("EnergyStorage: Stored=" + te.getStored());
                        }
                        if (tileEntity instanceof IReactor) {
                            te = (IReactor)tileEntity;
                            chat.println("Reactor: Heat=" + te.getHeat() + " MaxHeat=" + te.getMaxHeat() + " HEM=" + te.getHeatEffectModifier() + " Output=" + te.getReactorEnergyOutput());
                        }
                        if (tileEntity instanceof IPersonalBlock) {
                            te = (IPersonalBlock)tileEntity;
                            chat.println("PersonalBlock: CanAccess=" + te.permitsAccess(player.getGameProfile()));
                        }
                        if (!(tileEntity instanceof TileEntityCrop)) break;
                        te = (TileEntityCrop)tileEntity;
                        CropCard crop = ((TileEntityCrop)te).getCrop();
                        String name = crop != null ? crop.owner() + ":" + crop.name() : "none";
                        chat.println("PersonalBlock: Crop=" + name + " Size=" + ((TileEntityCrop)te).size + " Growth=" + ((TileEntityCrop)te).statGrowth + " Gain=" + ((TileEntityCrop)te).statGain + " Resistance=" + ((TileEntityCrop)te).statResistance + " Nutrients=" + ((TileEntityCrop)te).nutrientStorage + " Water=" + ((TileEntityCrop)te).waterStorage + " GrowthPoints=" + ((TileEntityCrop)te).growthPoints);
                        break;
                    }
                    case EnergyNet: {
                        if (!IC2.platform.isSimulating()) {
                            return false;
                        }
                        if (WorldData.get((World)world).energyNet.dumpDebugInfo(console, chat, x, y, z)) break;
                        return false;
                    }
                    case Accelerate: 
                    case AccelerateX100: {
                        if (!IC2.platform.isSimulating()) {
                            return false;
                        }
                        TileEntity tileEntity = world.getTileEntity(x, y, z);
                        if (tileEntity == null) {
                            chat.println("No tile entity.");
                            break;
                        }
                        int count = mode == Mode.Accelerate ? 1000 : 100000;
                        chat.println("Running " + count + " ticks on " + tileEntity + ".");
                        for (int i = 0; i < count; ++i) {
                            tileEntity.updateEntity();
                        }
                        break;
                    }
                }
                console.flush();
                chat.flush();
                if (!IC2.platform.isRendering()) break block35;
                try {
                    consoleBuffer.writeTo(new FileOutputStream(FileDescriptor.out));
                }
                catch (IOException e) {
                    IC2.log.warn(LogCategory.Item, e, "Stdout write failed.");
                }
                for (String line : chatBuffer.toString().split("[\\r\\n]+")) {
                    IC2.platform.messagePlayer(player, line, new Object[0]);
                }
                break block36;
            }
            if (!(player instanceof EntityPlayerMP)) break block36;
            try {
                IC2.network.get().sendConsole((EntityPlayerMP)player, consoleBuffer.toString("UTF-8"));
                IC2.network.get().sendChat((EntityPlayerMP)player, chatBuffer.toString("UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                IC2.log.warn(LogCategory.Item, e, "String encoding failed.");
            }
        }
        return IC2.platform.isSimulating();
    }

    private static void dumpObjectFields(PrintStream ps, Object o) {
        Class<?> fieldDeclaringClass = o.getClass();
        do {
            Field[] fields;
            for (Field field : fields = fieldDeclaringClass.getDeclaredFields()) {
                if ((field.getModifiers() & 8) != 0 && (fieldDeclaringClass == Block.class || fieldDeclaringClass == TileEntity.class)) continue;
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    ArrayList<Object> value = field.get(o);
                    ps.println(field.getName() + " class: " + fieldDeclaringClass.getName() + " type: " + field.getType());
                    ps.println("    identity hash: " + System.identityHashCode(o) + " hash: " + o.hashCode() + " modifiers: " + field.getModifiers());
                    if (field.getType().isArray()) {
                        ArrayList<Object> array = new ArrayList<Object>();
                        for (int i = 0; i < Array.getLength(value); ++i) {
                            array.add(Array.get(value, i));
                        }
                        value = array;
                    }
                    if (value instanceof Iterable) {
                        ps.println("    values:");
                        int i = 0;
                        Iterator it = ((Iterable)value).iterator();
                        while (it.hasNext()) {
                            ps.println("      [" + i + "] " + ItemDebug.getValueString(it.next()));
                            ++i;
                        }
                    } else {
                        ps.println("    value: " + ItemDebug.getValueString(value));
                    }
                }
                catch (IllegalAccessException e) {
                    ps.println("name: " + fieldDeclaringClass.getName() + "." + field.getName() + " type: " + field.getType() + " value: <can't access>");
                }
                catch (NullPointerException e) {
                    ps.println("name: " + fieldDeclaringClass.getName() + "." + field.getName() + " type: " + field.getType() + " value: <null>");
                }
                field.setAccessible(accessible);
            }
        } while ((fieldDeclaringClass = fieldDeclaringClass.getSuperclass()) != null);
    }

    private static String getValueString(Object o) {
        if (o == null) {
            return "<null>";
        }
        String ret = o.toString();
        if (o.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(o); ++i) {
                ret = ret + " [" + i + "] " + Array.get(o, i);
            }
        }
        if (ret.length() > 100) {
            ret = ret.substring(0, 90) + "... (" + ret.length() + " more)";
        }
        return ret;
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return true;
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return 2.147483647E9;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return 1;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return 2.147483647E9;
    }

    @Override
    public IElectricItemManager getManager(ItemStack itemStack) {
        if (manager == null) {
            manager = new InfiniteElectricItemManager();
        }
        return manager;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    private static enum Mode {
        InterfacesFields("Interfaces and Fields"),
        TileData("Tile Data"),
        EnergyNet("Energy Net"),
        Accelerate("Accelerate"),
        AccelerateX100("Accelerate x100");

        static final Mode[] modes;
        private final String name;

        private Mode(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }

        static {
            modes = Mode.values();
        }
    }
}

