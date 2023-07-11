/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.IBlockAccess
 *  paulscode.sound.SoundSystem
 */
package ic2.core.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.audio.AudioManagerClient;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import paulscode.sound.SoundSystem;

@SideOnly(value=Side.CLIENT)
public final class AudioSourceClient
extends AudioSource
implements Comparable<AudioSourceClient> {
    private SoundSystem soundSystem;
    private String sourceName;
    private boolean valid = false;
    private boolean culled = false;
    private Reference<Object> obj;
    private AudioPosition position;
    private PositionSpec positionSpec;
    private float configuredVolume;
    private float realVolume;
    private boolean isPlaying = false;

    public AudioSourceClient(SoundSystem soundSystem1, String sourceName1, Object obj1, PositionSpec positionSpec1, String initialSoundFile, boolean loop, boolean priorized, float volume) {
        this.soundSystem = soundSystem1;
        this.sourceName = sourceName1;
        this.obj = new WeakReference<Object>(obj1);
        this.positionSpec = positionSpec1;
        URL url = AudioSource.class.getClassLoader().getResource("ic2/sounds/" + initialSoundFile);
        if (url == null) {
            IC2.log.warn(LogCategory.Audio, "Invalid sound file: %s.", initialSoundFile);
            return;
        }
        this.position = AudioPosition.getFrom(obj1, positionSpec1);
        soundSystem1.newSource(priorized, sourceName1, url, initialSoundFile, loop, this.position.x, this.position.y, this.position.z, 0, ((AudioManagerClient)IC2.audioManager).fadingDistance * Math.max(volume, 1.0f));
        this.valid = true;
        this.setVolume(volume);
    }

    @Override
    public int compareTo(AudioSourceClient x) {
        if (this.culled) {
            return (int)((this.realVolume * 0.9f - x.realVolume) * 128.0f);
        }
        return (int)((this.realVolume - x.realVolume) * 128.0f);
    }

    @Override
    public void remove() {
        if (!this.check()) {
            return;
        }
        if (this.sourceName == null) {
            return;
        }
        this.stop();
        this.soundSystem.removeSource(this.sourceName);
        this.sourceName = null;
        this.valid = false;
    }

    @Override
    public void play() {
        if (!this.check()) {
            return;
        }
        if (this.isPlaying) {
            return;
        }
        this.isPlaying = true;
        if (this.culled) {
            return;
        }
        this.soundSystem.play(this.sourceName);
    }

    @Override
    public void pause() {
        if (!this.check()) {
            return;
        }
        if (!this.isPlaying || this.culled) {
            return;
        }
        this.isPlaying = false;
        this.soundSystem.pause(this.sourceName);
    }

    @Override
    public void stop() {
        if (!this.check() || !this.isPlaying) {
            return;
        }
        this.isPlaying = false;
        if (this.culled) {
            return;
        }
        this.soundSystem.stop(this.sourceName);
    }

    @Override
    public void flush() {
        if (!this.check()) {
            return;
        }
        if (!this.isPlaying || this.culled) {
            return;
        }
        this.soundSystem.flush(this.sourceName);
    }

    @Override
    public void cull() {
        if (!this.check() || this.culled) {
            return;
        }
        this.soundSystem.cull(this.sourceName);
        this.culled = true;
    }

    @Override
    public void activate() {
        if (!this.check() || !this.culled) {
            return;
        }
        this.soundSystem.activate(this.sourceName);
        this.culled = false;
        if (this.isPlaying) {
            this.isPlaying = false;
            this.play();
        }
    }

    @Override
    public float getVolume() {
        if (!this.check()) {
            return 0.0f;
        }
        return this.soundSystem.getVolume(this.sourceName);
    }

    @Override
    public float getRealVolume() {
        return this.realVolume;
    }

    @Override
    public void setVolume(float volume) {
        if (!this.check()) {
            return;
        }
        this.configuredVolume = volume;
        this.soundSystem.setVolume(this.sourceName, 0.001f);
    }

    @Override
    public void setPitch(float pitch) {
        if (!this.check()) {
            return;
        }
        this.soundSystem.setPitch(this.sourceName, pitch);
    }

    @Override
    public void updatePosition() {
        if (!this.check()) {
            return;
        }
        this.position = AudioPosition.getFrom(this.obj.get(), this.positionSpec);
        if (this.position == null) {
            return;
        }
        this.soundSystem.setPosition(this.sourceName, this.position.x, this.position.y, this.position.z);
    }

    @Override
    public void updateVolume(EntityPlayer player) {
        float distance;
        if (!this.check() || !this.isPlaying) {
            this.realVolume = 0.0f;
            return;
        }
        float maxDistance = ((AudioManagerClient)IC2.audioManager).fadingDistance * Math.max(this.configuredVolume, 1.0f);
        float rolloffFactor = 1.0f;
        float referenceDistance = 1.0f;
        float x = (float)player.posX;
        float y = (float)player.posY;
        float z = (float)player.posZ;
        if (this.position != null && this.position.getWorld() == player.worldObj) {
            float deltaX = this.position.x - x;
            float deltaY = this.position.y - y;
            float deltaZ = this.position.z - z;
            distance = (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        } else {
            distance = Float.POSITIVE_INFINITY;
        }
        if (distance > maxDistance) {
            this.realVolume = 0.0f;
            this.cull();
            return;
        }
        if (distance < referenceDistance) {
            distance = referenceDistance;
        }
        float gain = 1.0f - rolloffFactor * (distance - referenceDistance) / (maxDistance - referenceDistance);
        float newRealVolume = gain * this.configuredVolume * IC2.audioManager.getMasterVolume();
        float dx = (this.position.x - x) / distance;
        float dy = (this.position.y - y) / distance;
        float dz = (this.position.z - z) / distance;
        if ((double)newRealVolume > 0.1) {
            int i = 0;
            while ((float)i < distance) {
                int zi;
                int yi;
                int xi = Util.roundToNegInf(x);
                Block block = player.worldObj.getBlock(xi, yi = Util.roundToNegInf(y), zi = Util.roundToNegInf(z));
                if (!block.isAir((IBlockAccess)player.worldObj, xi, yi, zi)) {
                    newRealVolume = block.isNormalCube((IBlockAccess)player.worldObj, xi, yi, zi) ? (newRealVolume *= 0.6f) : (newRealVolume *= 0.8f);
                }
                x += dx;
                y += dy;
                z += dz;
                ++i;
            }
        }
        if ((double)Math.abs(this.realVolume / newRealVolume - 1.0f) > 0.06) {
            this.soundSystem.setVolume(this.sourceName, IC2.audioManager.getMasterVolume() * Math.min(newRealVolume, 1.0f));
        }
        this.realVolume = newRealVolume;
    }

    private boolean check() {
        if (this.valid && IC2.audioManager.valid()) {
            return true;
        }
        this.valid = false;
        return false;
    }
}

