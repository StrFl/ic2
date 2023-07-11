/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ChunkCoordinates
 *  net.minecraft.util.IChatComponent
 *  net.minecraft.world.World
 */
package ic2.core;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class Ic2Player
extends EntityPlayer {
    public Ic2Player(World world) {
        super(world, new GameProfile(UUID.nameUUIDFromBytes("[IC2]".getBytes(Charsets.UTF_8)), "[IC2]"));
    }

    public boolean canCommandSenderUseCommand(int i, String s) {
        return false;
    }

    public ChunkCoordinates getPlayerCoordinates() {
        return null;
    }

    public void addChatMessage(IChatComponent var1) {
    }
}

