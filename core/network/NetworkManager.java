/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.network.FMLEventChannel
 *  cpw.mods.fml.common.network.FMLNetworkEvent$ServerCustomPacketEvent
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.common.network.internal.FMLProxyPacket
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.Unpooled
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.ICrafting
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.NetHandlerPlayServer
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 */
package ic2.core.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkItemEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.WorldData;
import ic2.core.block.TileEntityBlock;
import ic2.core.item.IHandHeldInventory;
import ic2.core.network.ClientModifiable;
import ic2.core.network.DataEncoder;
import ic2.core.network.IPlayerItemDataListener;
import ic2.core.network.IRpcProvider;
import ic2.core.network.RpcHandler;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class NetworkManager {
    private static FMLEventChannel channel;
    public static final int updatePeriod = 1;
    private static final int maxPacketDataLength = 32766;
    public static final String channelName = "ic2";

    public NetworkManager() {
        if (channel == null) {
            channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelName);
        }
        channel.register((Object)this);
    }

    protected boolean isClient() {
        return false;
    }

    public void onTickEnd(World world) {
        WorldData worldData = WorldData.get(world);
        if (--worldData.ticksLeftToNetworkUpdate == 0) {
            this.sendUpdatePacket(world);
            worldData.ticksLeftToNetworkUpdate = 1;
        }
    }

    public void sendPlayerItemData(EntityPlayer player, int slot, Object ... data) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(buffer);
        try {
            os.writeByte(10);
            os.writeByte(slot);
            DataEncoder.encode(os, player.inventory.mainInventory[slot].getItem(), false);
            os.writeShort(data.length);
            for (Object o : data) {
                DataEncoder.encode(os, o);
            }
            os.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] packetData = buffer.toByteArray();
        if (IC2.platform.isSimulating()) {
            this.sendPacket(packetData, (EntityPlayerMP)player);
        } else {
            this.sendPacket(packetData);
        }
    }

    public void updateTileEntityField(TileEntity te, String field) {
        if (!this.isClient()) {
            WorldData worldData = WorldData.get(te.getWorldObj());
            worldData.networkedFieldsToUpdate.add(new TileEntityField(te, field));
        } else if (this.getClientModifiableField(te.getClass(), field) == null) {
            IC2.log.warn(LogCategory.Network, "Field update for %s failed.", te);
        } else {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(buffer);
            try {
                os.writeByte(13);
                DataEncoder.encode(os, te, false);
                NetworkManager.retrieveFieldData(te, field, os);
                os.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            byte[] packetData = buffer.toByteArray();
            this.sendPacket(packetData);
        }
    }

    private Field getClientModifiableField(Class<?> cls, String fieldName) {
        Field field = ReflectionUtil.getFieldRecursive(cls, fieldName);
        if (field == null) {
            IC2.log.warn(LogCategory.Network, "Can't find field %s in %s.", fieldName, cls.getName());
            return null;
        }
        if (field.getAnnotation(ClientModifiable.class) == null) {
            IC2.log.warn(LogCategory.Network, "The field %s in %s is not modifiable.", fieldName, cls.getName());
            return null;
        }
        return field;
    }

    public void updateTileEntityFieldTo(TileEntity te, String field, EntityPlayerMP player) {
        assert (IC2.platform.isSimulating());
        WorldData worldData = WorldData.get(te.getWorldObj());
        worldData.networkedFieldsToUpdate.add(new TileEntityField(te, field, player));
    }

    public void sendComponentUpdate(TileEntityBlock te, String componentName, EntityPlayerMP player, ByteArrayOutputStream data) {
        assert (IC2.platform.isSimulating());
        if (player.worldObj != te.getWorldObj()) {
            throw new IllegalArgumentException("mismatched world (te " + te.getWorldObj() + ", player " + player.worldObj + ")");
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(64);
        DataOutputStream os = new DataOutputStream(buffer);
        try {
            os.writeByte(7);
            os.writeInt(te.getWorldObj().provider.dimensionId);
            os.writeInt(te.xCoord);
            os.writeInt(te.yCoord);
            os.writeInt(te.zCoord);
            os.writeUTF(componentName);
            os.writeInt(data.size());
            data.writeTo(os);
            os.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.sendPacket(buffer.toByteArray(), player);
    }

    public void initiateTileEntityEvent(TileEntity te, int event, boolean limitRange) {
        if (te.getWorldObj().playerEntities.isEmpty()) {
            return;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(buffer);
        try {
            os.writeByte(1);
            DataEncoder.encode(os, te, false);
            os.writeInt(event);
            os.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] packetData = buffer.toByteArray();
        int maxDistance = limitRange ? 400 : MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance() + 16;
        for (Object obj : te.getWorldObj().playerEntities) {
            if (!(obj instanceof EntityPlayerMP)) continue;
            EntityPlayerMP entityPlayer = (EntityPlayerMP)obj;
            int distanceX = te.xCoord - (int)entityPlayer.posX;
            int distanceZ = te.zCoord - (int)entityPlayer.posZ;
            int distance = limitRange ? distanceX * distanceX + distanceZ * distanceZ : Math.max(Math.abs(distanceX), Math.abs(distanceZ));
            if (distance > maxDistance) continue;
            this.sendPacket(packetData, entityPlayer);
        }
    }

    public void initiateItemEvent(EntityPlayer player, ItemStack itemStack, int event, boolean limitRange) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(buffer);
        try {
            os.writeByte(2);
            os.writeLong(player.getGameProfile().getId().getMostSignificantBits());
            os.writeLong(player.getGameProfile().getId().getLeastSignificantBits());
            DataEncoder.encode(os, itemStack, false);
            os.writeInt(event);
            os.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        byte[] packetData = buffer.toByteArray();
        int maxDistance = limitRange ? 400 : MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance() + 16;
        for (Object obj : player.worldObj.playerEntities) {
            if (!(obj instanceof EntityPlayerMP)) continue;
            EntityPlayerMP entityPlayer = (EntityPlayerMP)obj;
            int distanceX = (int)player.posX - (int)entityPlayer.posX;
            int distanceZ = (int)player.posZ - (int)entityPlayer.posZ;
            int distance = limitRange ? distanceX * distanceX + distanceZ * distanceZ : Math.max(Math.abs(distanceX), Math.abs(distanceZ));
            if (distance > maxDistance) continue;
            this.sendPacket(packetData, entityPlayer);
        }
    }

    @Deprecated
    public void requestInitialData(INetworkDataProvider dataProvider) {
    }

    public void initiateClientItemEvent(ItemStack itemStack, int event) {
    }

    public void initiateClientTileEntityEvent(TileEntity te, int event) {
    }

    public void initiateRpc(int id, Class<? extends IRpcProvider<?>> provider, Object[] args) {
    }

    public void initiateGuiDisplay(EntityPlayerMP entityPlayer, IHasGui inventory, int windowId) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(buffer);
            os.writeByte(4);
            MinecraftServer server = MinecraftServer.getServer();
            boolean isAdmin = server.getConfigurationManager().func_152596_g(entityPlayer.getGameProfile());
            os.writeByte(isAdmin ? 1 : 0);
            if (inventory instanceof TileEntity) {
                TileEntity te = (TileEntity)inventory;
                os.writeByte(0);
                DataEncoder.encode(os, te, false);
            } else if (entityPlayer.inventory.getCurrentItem() != null && entityPlayer.inventory.getCurrentItem().getItem() instanceof IHandHeldInventory) {
                os.writeByte(1);
                os.writeInt(entityPlayer.inventory.currentItem);
            } else {
                IC2.platform.displayError("An unknown GUI type was attempted to be displayed.\nThis could happen due to corrupted data from a player or a bug.\n\n(Technical information: " + inventory + ")", new Object[0]);
            }
            os.writeInt(windowId);
            os.close();
            this.sendPacket(buffer.toByteArray(), entityPlayer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendInitialData(TileEntity te, EntityPlayerMP player) {
        if (te instanceof INetworkDataProvider) {
            WorldData worldData = WorldData.get(te.getWorldObj());
            for (String field : ((INetworkDataProvider)te).getNetworkedFields()) {
                worldData.networkedFieldsToUpdate.add(new TileEntityField(te, field, player));
            }
        }
    }

    public void sendChat(EntityPlayerMP player, String message) {
        try {
            this.sendLargePacket(player, 1, message.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            IC2.log.warn(LogCategory.Network, e, "String encoding failed.");
        }
    }

    public void sendConsole(EntityPlayerMP player, String message) {
        try {
            this.sendLargePacket(player, 2, message.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            IC2.log.warn(LogCategory.Network, e, "String encoding failed.");
        }
    }

    public void sendContainerFields(ContainerBase<?> container, String ... fieldNames) {
        for (String fieldName : fieldNames) {
            this.sendContainerField(container, fieldName);
        }
    }

    public void sendContainerField(ContainerBase<?> container, String fieldName) {
        if (this.isClient() && this.getClientModifiableField(((Object)container).getClass(), fieldName) == null) {
            IC2.log.warn(LogCategory.Network, "Field update for %s failed.", new Object[]{container});
            return;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(buffer);
        try {
            os.writeByte(11);
            DataEncoder.writeVarInt(os, container.windowId);
            NetworkManager.retrieveFieldData(container, fieldName, os);
            os.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] packetData = buffer.toByteArray();
        if (IC2.platform.isSimulating()) {
            for (ICrafting crafter : container.getCrafters()) {
                if (!(crafter instanceof EntityPlayerMP)) continue;
                this.sendPacket(packetData, (EntityPlayerMP)crafter);
            }
        } else {
            this.sendPacket(packetData);
        }
    }

    public void sendContainerEvent(ContainerBase<?> container, String event) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(buffer);
        try {
            os.writeByte(12);
            DataEncoder.writeVarInt(os, container.windowId);
            os.writeUTF(event);
            os.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] packetData = buffer.toByteArray();
        if (IC2.platform.isSimulating()) {
            for (ICrafting crafter : container.getCrafters()) {
                if (!(crafter instanceof EntityPlayerMP)) continue;
                this.sendPacket(packetData, (EntityPlayerMP)crafter);
            }
        } else {
            this.sendPacket(packetData);
        }
    }

    private void sendUpdatePacket(World world) {
        WorldData worldData = WorldData.get(world);
        if (worldData.networkedFieldsToUpdate.isEmpty()) {
            return;
        }
        HashMap data = new HashMap();
        for (TileEntityField tef : worldData.networkedFieldsToUpdate) {
            List<EntityPlayerMP> receivers;
            if (tef.te.isInvalid()) continue;
            if (tef.target == null) {
                receivers = NetworkManager.getPlayersInRange(world, tef.te);
            } else {
                if (tef.te.getWorldObj() != tef.target.worldObj) continue;
                receivers = Arrays.asList(tef.target);
            }
            for (EntityPlayerMP player : receivers) {
                ByteArrayOutputStream teData;
                HashMap<TileEntity, ByteArrayOutputStream> playerData = (HashMap<TileEntity, ByteArrayOutputStream>)data.get(player);
                if (playerData == null) {
                    playerData = new HashMap<TileEntity, ByteArrayOutputStream>();
                    data.put(player, playerData);
                }
                if ((teData = (ByteArrayOutputStream)playerData.get(tef.te)) == null) {
                    teData = new ByteArrayOutputStream(512);
                    playerData.put(tef.te, teData);
                }
                try {
                    NetworkManager.retrieveFieldData(tef.te, tef.field, teData);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        worldData.networkedFieldsToUpdate.clear();
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(16384);
            for (Map.Entry entry : data.entrySet()) {
                EntityPlayerMP player = (EntityPlayerMP)entry.getKey();
                Map playerData = (Map)entry.getValue();
                DataOutputStream os = new DataOutputStream(buffer);
                os.writeInt(player.worldObj.provider.dimensionId);
                for (Map.Entry entry2 : playerData.entrySet()) {
                    TileEntity te = (TileEntity)entry2.getKey();
                    ByteArrayOutputStream fieldData = (ByteArrayOutputStream)entry2.getValue();
                    os.writeInt(te.xCoord);
                    os.writeInt(te.yCoord);
                    os.writeInt(te.zCoord);
                    os.writeInt(fieldData.size());
                    fieldData.writeTo(os);
                }
                os.close();
                this.sendLargePacket(player, 0, buffer.toByteArray());
                buffer.reset();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<EntityPlayerMP> getPlayersInRange(World world, TileEntity te) {
        ArrayList<EntityPlayerMP> ret = new ArrayList<EntityPlayerMP>();
        for (Object obj : world.playerEntities) {
            if (!(obj instanceof EntityPlayerMP)) continue;
            EntityPlayerMP player = (EntityPlayerMP)obj;
            int distance = Math.min(Math.abs(te.xCoord - (int)player.posX), Math.abs(te.zCoord - (int)player.posZ));
            if (distance > MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance() + 16) continue;
            ret.add(player);
        }
        return ret;
    }

    private void sendLargePacket(EntityPlayerMP player, int id, byte[] data) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(16384);
        try {
            DeflaterOutputStream deflate = new DeflaterOutputStream(buffer);
            deflate.write(data);
            deflate.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        data = buffer.toByteArray();
        int maxSize = 32764;
        for (int offset = 0; offset < data.length; offset += maxSize) {
            buffer.reset();
            buffer.write(0);
            int state = 0;
            if (offset == 0) {
                state |= 1;
            }
            if (offset + maxSize > data.length) {
                state |= 2;
            }
            buffer.write(state |= id << 2);
            buffer.write(data, offset, Math.min(maxSize, data.length - offset));
            byte[] packetData = buffer.toByteArray();
            this.sendPacket(packetData, player);
        }
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        if (this.getClass() == NetworkManager.class) {
            this.onPacketData((InputStream)new ByteBufInputStream(event.packet.payload()), (EntityPlayer)((NetHandlerPlayServer)event.handler).playerEntity);
        }
    }

    protected void onPacketData(InputStream isRaw, EntityPlayer player) {
        try {
            if (isRaw.available() == 0) {
                return;
            }
            int id = isRaw.read();
            DataInputStream is = new DataInputStream(isRaw);
            switch (id) {
                case 1: {
                    ItemStack stack = DataEncoder.decode(is, ItemStack.class);
                    int event = is.readInt();
                    if (!(stack.getItem() instanceof INetworkItemEventListener)) break;
                    ((INetworkItemEventListener)stack.getItem()).onNetworkEvent(stack, player, event);
                    break;
                }
                case 2: {
                    int keyState = is.readInt();
                    IC2.keyboard.processKeyUpdate(player, keyState);
                    break;
                }
                case 3: {
                    TileEntity te = DataEncoder.decode(is, TileEntity.class);
                    int event = is.readInt();
                    if (!(te instanceof INetworkClientTileEntityEventListener)) break;
                    ((INetworkClientTileEntityEventListener)te).onNetworkEvent(player, event);
                    break;
                }
                case 4: {
                    RpcHandler.processRpcRequest(is, (EntityPlayerMP)player);
                    break;
                }
                case 10: {
                    byte slot = is.readByte();
                    Item item = DataEncoder.decode(is, Item.class);
                    int dataCount = is.readShort();
                    Object[] subData = new Object[dataCount];
                    for (int i = 0; i < dataCount; ++i) {
                        subData[i] = DataEncoder.decode(is);
                    }
                    if (slot < 0 || slot > 9) break;
                    ItemStack itemStack = player.inventory.mainInventory[slot];
                    if (itemStack != null && itemStack.getItem() == item && item instanceof IPlayerItemDataListener) {
                        ((IPlayerItemDataListener)item).onPlayerItemNetworkData(player, slot, subData);
                    }
                    break;
                }
                case 11: {
                    int windowId = DataEncoder.readVarInt(is);
                    String fieldName = is.readUTF();
                    Object value = DataEncoder.decode(is);
                    if (!(player.openContainer instanceof ContainerBase) || player.openContainer.windowId != windowId || !this.isClient() && this.getClientModifiableField(player.openContainer.getClass(), fieldName) == null) break;
                    ReflectionUtil.setValueRecursive(player.openContainer, fieldName, value);
                    break;
                }
                case 12: {
                    int windowId = DataEncoder.readVarInt(is);
                    String event = is.readUTF();
                    if (!(player.openContainer instanceof ContainerBase) || player.openContainer.windowId != windowId) break;
                    ((ContainerBase)player.openContainer).onContainerEvent(event);
                    break;
                }
                case 13: {
                    TileEntity te = DataEncoder.decode(is, TileEntity.class);
                    String fieldName = is.readUTF();
                    Object value = DataEncoder.decode(is);
                    if (te == null || !this.isClient() && this.getClientModifiableField(te.getClass(), fieldName) == null) break;
                    ReflectionUtil.setValueRecursive(te, fieldName, value);
                    break;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initiateKeyUpdate(int keyState) {
    }

    public void sendLoginData() {
    }

    public void initiateExplosionEffect(World world, double x, double y, double z) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(buffer);
            os.writeByte(5);
            os.writeInt(world.provider.dimensionId);
            os.writeDouble(x);
            os.writeDouble(y);
            os.writeDouble(z);
            os.close();
            byte[] packetData = buffer.toByteArray();
            for (Object obj : world.playerEntities) {
                EntityPlayerMP player;
                if (!(obj instanceof EntityPlayerMP) || !((player = (EntityPlayerMP)obj).getDistanceSq(x, y, z) < 128.0)) continue;
                this.sendPacket(packetData, player);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendPacket(byte[] data) {
        if (IC2.platform.isSimulating()) {
            channel.sendToAll(NetworkManager.makePacket(data));
        } else {
            channel.sendToServer(NetworkManager.makePacket(data));
        }
    }

    protected void sendPacket(byte[] data, EntityPlayerMP player) {
        channel.sendTo(NetworkManager.makePacket(data), player);
    }

    private static void retrieveFieldData(Object object, String fieldName, OutputStream out) throws IOException {
        DataOutputStream os = new DataOutputStream(out);
        os.writeUTF(fieldName);
        try {
            DataEncoder.encode(os, ReflectionUtil.getValueRecursive(object, fieldName));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        os.flush();
    }

    private static FMLProxyPacket makePacket(byte[] data) {
        return new FMLProxyPacket(Unpooled.wrappedBuffer((byte[])data), channelName);
    }

    public static class TileEntityField {
        TileEntity te;
        String field;
        EntityPlayerMP target = null;

        TileEntityField(TileEntity te1, String field1) {
            this.te = te1;
            this.field = field1;
        }

        TileEntityField(TileEntity te1, String field1, EntityPlayerMP target1) {
            this.te = te1;
            this.field = field1;
            this.target = target1;
        }

        public boolean equals(Object obj) {
            if (obj instanceof TileEntityField) {
                TileEntityField tef = (TileEntityField)obj;
                return tef.te == this.te && tef.field.equals(this.field) && tef.target == this.target;
            }
            return false;
        }

        public int hashCode() {
            return this.te.hashCode() * 31 ^ this.field.hashCode();
        }
    }
}

