/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.network.FMLNetworkEvent$ClientConnectedToServerEvent
 *  cpw.mods.fml.common.network.FMLNetworkEvent$ClientDisconnectionFromServerEvent
 *  cpw.mods.fml.common.network.internal.FMLProxyPacket
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.S3FPacketCustomPayload
 */
package ic2.core.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import ic2.core.IC2;
import ic2.core.network.DataEncoder;
import ic2.core.network.IRpcProvider;
import ic2.core.network.Rpc;
import ic2.core.util.LogCategory;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

@ChannelHandler.Sharable
public class RpcHandler
extends SimpleChannelInboundHandler<Packet> {
    private static ConcurrentMap<String, IRpcProvider<?>> providers = new ConcurrentHashMap();
    private static ConcurrentMap<Integer, Rpc<?>> pending = new ConcurrentHashMap();

    public static boolean registerProvider(IRpcProvider<?> provider) {
        return providers.putIfAbsent(provider.getClass().getName(), provider) == null;
    }

    public static <V> Rpc<V> run(Class<? extends IRpcProvider<V>> provider, Object ... args) {
        int id = IC2.random.nextInt();
        Rpc rpc = new Rpc();
        Rpc prev = pending.putIfAbsent(id, rpc);
        if (prev != null) {
            return RpcHandler.run(provider, args);
        }
        IC2.network.get().initiateRpc(id, provider, args);
        return rpc;
    }

    protected static void processRpcRequest(DataInputStream is, EntityPlayerMP player) throws IOException {
        int id = is.readInt();
        String providerClassName = is.readUTF();
        Object[] args = (Object[])DataEncoder.decode(is);
        IRpcProvider provider = (IRpcProvider)providers.get(providerClassName);
        if (provider == null) {
            IC2.log.warn(LogCategory.Network, "Invalid RPC request from %s.", player.getCommandSenderName());
            return;
        }
        Object result = provider.executeRpc(args);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(buffer);
        os.writeByte(6);
        os.writeInt(id);
        DataEncoder.encode(os, result, true);
        os.close();
        IC2.network.get().sendPacket(buffer.toByteArray(), player);
    }

    public RpcHandler() {
        FMLCommonHandler.instance().bus().register((Object)this);
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        String nettyHandlerName = "ic2_rpc_handler";
        if (event.manager.channel().pipeline().get("ic2_rpc_handler") == null) {
            try {
                event.manager.channel().pipeline().addBefore("packet_handler", "ic2_rpc_handler", (ChannelHandler)this);
            }
            catch (Exception e) {
                throw new RuntimeException("Can't insert handler in " + event.manager.channel().pipeline().names() + ".", e);
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        for (Rpc rpc : pending.values()) {
            rpc.cancel(true);
        }
        pending.clear();
    }

    protected void channelRead0(ChannelHandlerContext ctx, Packet oPacket) throws Exception {
        FMLProxyPacket packet = null;
        if (oPacket instanceof FMLProxyPacket) {
            packet = (FMLProxyPacket)oPacket;
        } else if (oPacket instanceof S3FPacketCustomPayload) {
            packet = new FMLProxyPacket((S3FPacketCustomPayload)oPacket);
        }
        if (packet == null || !packet.channel().equals("ic2")) {
            ctx.fireChannelRead((Object)oPacket);
            return;
        }
        if (packet.payload().getByte(0) == 6) {
            this.processRpcResponse((InputStream)new ByteBufInputStream(packet.payload()));
        } else {
            ctx.fireChannelRead((Object)oPacket);
        }
    }

    private void processRpcResponse(InputStream isRaw) {
        try {
            isRaw.read();
            DataInputStream is = new DataInputStream(isRaw);
            int id = is.readInt();
            Object result = DataEncoder.decode(is);
            Rpc rpc = (Rpc)pending.remove(id);
            if (rpc == null) {
                IC2.log.warn(LogCategory.Network, "RPC %d wasn't found while trying to process its response.", id);
            } else {
                rpc.finish(result);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

