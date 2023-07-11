/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.network.FMLNetworkEvent$ClientCustomPacketEvent
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  io.netty.buffer.ByteBufInputStream
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.core.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkItemEventListener;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.network.INetworkUpdateListener;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.item.IHandHeldInventory;
import ic2.core.network.DataEncoder;
import ic2.core.network.IRpcProvider;
import ic2.core.network.NetworkManager;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import io.netty.buffer.ByteBufInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.InflaterInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

@SideOnly(value=Side.CLIENT)
public class NetworkManagerClient
extends NetworkManager {
    private ByteArrayOutputStream largePacketBuffer;

    @Override
    protected boolean isClient() {
        return true;
    }

    @Override
    public void initiateClientItemEvent(ItemStack itemStack, int event) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(buffer);
            os.writeByte(1);
            DataEncoder.encode(os, itemStack, false);
            os.writeInt(event);
            os.close();
            this.sendPacket(buffer.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initiateKeyUpdate(int keyState) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(buffer);
            os.writeByte(2);
            os.writeInt(keyState);
            os.close();
            this.sendPacket(buffer.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initiateClientTileEntityEvent(TileEntity te, int event) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(buffer);
            os.writeByte(3);
            DataEncoder.encode(os, te, false);
            os.writeInt(event);
            os.close();
            this.sendPacket(buffer.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initiateRpc(int id, Class<? extends IRpcProvider<?>> provider, Object[] args) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(buffer);
            os.writeByte(4);
            os.writeInt(id);
            os.writeUTF(provider.getName());
            DataEncoder.encode(os, args);
            os.close();
            this.sendPacket(buffer.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        assert (!this.getClass().getName().equals(NetworkManager.class.getName()));
        this.onPacketData((InputStream)new ByteBufInputStream(event.packet.payload()), (EntityPlayer)Minecraft.getMinecraft().thePlayer);
    }

    @Override
    protected void onPacketData(InputStream isRaw, EntityPlayer player) {
        block36: {
            isRaw.mark(Integer.MAX_VALUE);
            DataInputStream is = new DataInputStream(isRaw);
            try {
                if (isRaw.available() == 0) {
                    return;
                }
                block1 : switch (is.read()) {
                    case 0: {
                        int len;
                        int state = is.read();
                        if ((state & 1) != 0) {
                            this.largePacketBuffer = new ByteArrayOutputStream(16384);
                        }
                        byte[] buffer = new byte[4096];
                        while ((len = is.read(buffer)) != -1) {
                            this.largePacketBuffer.write(buffer, 0, len);
                        }
                        if ((state & 2) != 0) {
                            ByteArrayInputStream inflateInput = new ByteArrayInputStream(this.largePacketBuffer.toByteArray());
                            InflaterInputStream inflate = new InflaterInputStream(inflateInput);
                            ByteArrayOutputStream inflateBuffer = new ByteArrayOutputStream(16384);
                            while ((len = inflate.read(buffer)) != -1) {
                                inflateBuffer.write(buffer, 0, len);
                            }
                            inflate.close();
                            byte[] subData = inflateBuffer.toByteArray();
                            switch (state >> 2) {
                                case 0: {
                                    NetworkManagerClient.processInitPacket(subData);
                                    break;
                                }
                                case 1: {
                                    NetworkManagerClient.processChatPacket(subData);
                                    break;
                                }
                                case 2: {
                                    NetworkManagerClient.processConsolePacket(subData);
                                }
                            }
                            this.largePacketBuffer = null;
                        }
                        break;
                    }
                    case 1: {
                        TileEntity te = DataEncoder.decode(is, TileEntity.class);
                        int event = is.readInt();
                        if (te instanceof INetworkTileEntityEventListener) {
                            ((INetworkTileEntityEventListener)te).onNetworkEvent(event);
                        }
                        break;
                    }
                    case 2: {
                        UUID uuid = new UUID(is.readLong(), is.readLong());
                        ItemStack stack = DataEncoder.decode(is, ItemStack.class);
                        int event = is.readInt();
                        WorldClient world = Minecraft.getMinecraft().theWorld;
                        for (Object obj : world.playerEntities) {
                            EntityPlayer entityPlayer = (EntityPlayer)obj;
                            if (!uuid.equals(entityPlayer.getGameProfile().getId())) continue;
                            if (stack.getItem() instanceof INetworkItemEventListener) {
                                ((INetworkItemEventListener)stack.getItem()).onNetworkEvent(stack, entityPlayer, event);
                                break block1;
                            }
                            break block36;
                        }
                        break;
                    }
                    case 4: {
                        EntityPlayer entityPlayer = IC2.platform.getPlayerInstance();
                        boolean isAdmin = is.readByte() != 0;
                        switch (is.readByte()) {
                            case 0: {
                                TileEntity te = DataEncoder.decode(is, TileEntity.class);
                                int windowId = is.readInt();
                                if (te instanceof IHasGui) {
                                    IC2.platform.launchGuiClient(entityPlayer, (IHasGui)te, isAdmin);
                                }
                                entityPlayer.openContainer.windowId = windowId;
                                break block1;
                            }
                            case 1: {
                                int currentItemPosition = is.readInt();
                                int windowId = is.readInt();
                                if (currentItemPosition != entityPlayer.inventory.currentItem) {
                                    return;
                                }
                                ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
                                if (currentItem != null && currentItem.getItem() instanceof IHandHeldInventory) {
                                    IC2.platform.launchGuiClient(entityPlayer, ((IHandHeldInventory)currentItem.getItem()).getInventory(entityPlayer, currentItem), isAdmin);
                                }
                                entityPlayer.openContainer.windowId = windowId;
                                break block1;
                            }
                        }
                        break;
                    }
                    case 5: {
                        int dimensionId = is.readInt();
                        double x = is.readDouble();
                        double y = is.readDouble();
                        double z = is.readDouble();
                        WorldClient world = Minecraft.getMinecraft().theWorld;
                        if (world.provider.dimensionId != dimensionId) {
                            return;
                        }
                        world.playSoundEffect(x, y, z, "random.explode", 4.0f, (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f);
                        world.spawnParticle("hugeexplosion", x, y, z, 0.0, 0.0, 0.0);
                        break;
                    }
                    case 6: {
                        throw new RuntimeException("Received unexpected RPC packet");
                    }
                    case 7: {
                        int dimensionId = is.readInt();
                        int x = is.readInt();
                        int y = is.readInt();
                        int z = is.readInt();
                        String componentName = is.readUTF();
                        int dataLen = is.readInt();
                        if (dataLen > 65536) {
                            throw new IOException("data length limit exceeded");
                        }
                        byte[] data = new byte[dataLen];
                        is.readFully(data);
                        WorldClient world = Minecraft.getMinecraft().theWorld;
                        if (world.provider.dimensionId != dimensionId) {
                            return;
                        }
                        TileEntity teRaw = world.getTileEntity(x, y, z);
                        if (!(teRaw instanceof TileEntityBlock)) {
                            return;
                        }
                        TileEntityComponent component = ((TileEntityBlock)teRaw).getComponent(componentName);
                        if (component == null) {
                            return;
                        }
                        DataInputStream dataIs = new DataInputStream(new ByteArrayInputStream(data));
                        component.onNetworkUpdate(dataIs);
                        break;
                    }
                    default: {
                        isRaw.reset();
                        super.onPacketData(isRaw, player);
                    }
                }
            }
            catch (IOException e) {
                IC2.log.warn(LogCategory.Network, e, "Network read failed.");
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    private static void processInitPacket(byte[] data) throws IOException {
        buffer = new ByteArrayInputStream(data);
        is = new DataInputStream(buffer);
        dimensionId = is.readInt();
        world = Minecraft.getMinecraft().theWorld;
        if (world.provider.dimensionId != dimensionId) {
            return;
        }
        block4: while (true) {
            try {
                x = is.readInt();
            }
            catch (EOFException e) {
                break;
            }
            y = is.readInt();
            z = is.readInt();
            fieldData = new byte[is.readInt()];
            is.readFully(fieldData);
            fieldDataBuffer = new ByteArrayInputStream(fieldData);
            fieldDataStream = new DataInputStream(fieldDataBuffer);
            fieldValues = new HashMap<String, Object>();
            while (true) {
                try {
                    fieldName = fieldDataStream.readUTF();
                }
                catch (EOFException e) {
                    break;
                }
                value = DataEncoder.decode(fieldDataStream);
                fieldValues.put(fieldName, value);
            }
            block = world.getBlock(x, y, z);
            if (block == Blocks.air) continue;
            if (block instanceof BlockTileEntity) {
                tileEntityId = (Integer)fieldValues.get("tileEntityId");
                te = ((BlockTileEntity)block).getTileEntity(tileEntityId);
                if (te != null) {
                    world.setTileEntity(x, y, z, te);
                }
            } else {
                te = world.getTileEntity(x, y, z);
            }
            if (te == null) continue;
            var14_17 = fieldValues.entrySet().iterator();
            while (true) {
                if (var14_17.hasNext()) ** break;
                continue block4;
                entry = var14_17.next();
                ReflectionUtil.setValueRecursive(te, (String)entry.getKey(), entry.getValue());
                if (!(te instanceof INetworkUpdateListener)) continue;
                ((INetworkUpdateListener)te).onNetworkUpdate((String)entry.getKey());
            }
            break;
        }
        is.close();
    }

    private static void processChatPacket(byte[] data) {
        String messages;
        try {
            messages = new String(data, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            IC2.log.warn(LogCategory.Network, e, "String decoding failed.");
            return;
        }
        for (String line : messages.split("[\\r\\n]+")) {
            IC2.platform.messagePlayer(null, line, new Object[0]);
        }
    }

    private static void processConsolePacket(byte[] data) {
        String messages;
        try {
            messages = new String(data, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            IC2.log.warn(LogCategory.Network, e, "String decoding failed.");
            return;
        }
        PrintStream console = new PrintStream(new FileOutputStream(FileDescriptor.out));
        for (String line : messages.split("[\\r\\n]+")) {
            console.println(line);
        }
        console.flush();
    }
}

