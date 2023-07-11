/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.SoundCategory
 *  net.minecraft.client.audio.SoundHandler
 *  net.minecraft.client.audio.SoundManager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.client.event.sound.SoundLoadEvent
 *  net.minecraftforge.common.MinecraftForge
 *  paulscode.sound.SoundSystem
 *  paulscode.sound.SoundSystemConfig
 */
package ic2.core.audio;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.IC2;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.AudioSource;
import ic2.core.audio.AudioSourceClient;
import ic2.core.audio.PositionSpec;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

@SideOnly(value=Side.CLIENT)
public final class AudioManagerClient
extends AudioManager {
    public float fadingDistance = 16.0f;
    private boolean enabled = true;
    private int maxSourceCount = 32;
    private final int streamingSourceCount = 4;
    private SoundManager soundManager;
    private Field soundManagerLoaded;
    private volatile Thread initThread;
    private SoundSystem soundSystem = null;
    private float masterVolume = 0.5f;
    private int nextId = 0;
    private final Map<WeakObject, List<AudioSource>> objectToAudioSourceMap = new HashMap<WeakObject, List<AudioSource>>();

    @Override
    public void initialize() {
        this.enabled = ConfigUtil.getBool(MainConfig.get(), "misc/enableIc2Audio");
        this.maxSourceCount = ConfigUtil.getInt(MainConfig.get(), "misc/maxAudioSourceCount");
        if (this.maxSourceCount <= 6) {
            IC2.log.info(LogCategory.Audio, "The audio source limit is too low to enable IC2 sounds.");
            this.enabled = false;
        }
        if (!this.enabled) {
            IC2.log.debug(LogCategory.Audio, "Sounds disabled.");
            return;
        }
        if (this.maxSourceCount < 6) {
            this.enabled = false;
            return;
        }
        IC2.log.debug(LogCategory.Audio, "Using %d audio sources.", this.maxSourceCount);
        SoundSystemConfig.setNumberStreamingChannels((int)4);
        SoundSystemConfig.setNumberNormalChannels((int)(this.maxSourceCount - 4));
        this.soundManagerLoaded = ReflectionUtil.getField(SoundManager.class, Boolean.TYPE);
        if (this.soundManagerLoaded == null) {
            IC2.log.warn(LogCategory.Audio, "Can't find SoundManager.loaded, IC2 audio disabled.");
            this.enabled = false;
            return;
        }
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void onSoundSetup(SoundLoadEvent event) {
        if (!this.enabled) {
            return;
        }
        this.objectToAudioSourceMap.clear();
        Thread thread = this.initThread;
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        IC2.log.debug(LogCategory.Audio, "IC2 audio starting.");
        this.soundSystem = null;
        this.soundManager = AudioManagerClient.getSoundManager();
        this.initThread = new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        boolean loaded;
                        try {
                            loaded = AudioManagerClient.this.soundManagerLoaded.getBoolean(AudioManagerClient.this.soundManager);
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        if (loaded) {
                            AudioManagerClient.this.soundSystem = AudioManagerClient.getSoundSystem(AudioManagerClient.this.soundManager);
                            if (AudioManagerClient.this.soundSystem == null) {
                                IC2.log.warn(LogCategory.Audio, "IC2 audio unavailable.");
                                AudioManagerClient.this.enabled = false;
                            } else {
                                IC2.log.debug(LogCategory.Audio, "IC2 audio ready.");
                            }
                            break;
                        }
                        Thread.sleep(100L);
                    }
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                AudioManagerClient.this.initThread = null;
            }
        }, "IC2 audio init thread");
        this.initThread.setDaemon(true);
        this.initThread.start();
    }

    private static SoundManager getSoundManager() {
        SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        return (SoundManager)ReflectionUtil.getValue(handler, SoundManager.class);
    }

    private static SoundSystem getSoundSystem(SoundManager soundManager) {
        return (SoundSystem)ReflectionUtil.getValue(soundManager, SoundSystem.class);
    }

    @Override
    public void onTick() {
        if (!this.enabled || !this.valid()) {
            return;
        }
        IC2.platform.profilerStartSection("UpdateMasterVolume");
        float configSoundVolume = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER);
        if (configSoundVolume != this.masterVolume) {
            this.masterVolume = configSoundVolume;
        }
        IC2.platform.profilerEndStartSection("UpdateSourceVolume");
        EntityPlayer player = IC2.platform.getPlayerInstance();
        Vector<WeakObject> audioSourceObjectsToRemove = new Vector<WeakObject>();
        if (player == null) {
            audioSourceObjectsToRemove.addAll(this.objectToAudioSourceMap.keySet());
        } else {
            PriorityQueue<AudioSource> validAudioSources = new PriorityQueue<AudioSource>();
            for (Map.Entry<WeakObject, List<AudioSource>> entry : this.objectToAudioSourceMap.entrySet()) {
                if (entry.getKey().isEnqueued()) {
                    audioSourceObjectsToRemove.add(entry.getKey());
                    continue;
                }
                for (AudioSource audioSource : entry.getValue()) {
                    audioSource.updateVolume(player);
                    if (!(audioSource.getRealVolume() > 0.0f)) continue;
                    validAudioSources.add(audioSource);
                }
            }
            IC2.platform.profilerEndStartSection("Culling");
            int i = 0;
            while (!validAudioSources.isEmpty()) {
                if (i < this.maxSourceCount) {
                    ((AudioSource)validAudioSources.poll()).activate();
                } else {
                    ((AudioSource)validAudioSources.poll()).cull();
                }
                ++i;
            }
        }
        for (WeakObject obj : audioSourceObjectsToRemove) {
            this.removeSources(obj);
        }
        IC2.platform.profilerEndSection();
    }

    @Override
    public AudioSource createSource(Object obj, String initialSoundFile) {
        return this.createSource(obj, PositionSpec.Center, initialSoundFile, false, false, this.getDefaultVolume());
    }

    @Override
    public AudioSource createSource(Object obj, PositionSpec positionSpec, String initialSoundFile, boolean loop, boolean priorized, float volume) {
        if (!this.enabled) {
            return null;
        }
        if (!this.valid()) {
            return null;
        }
        String sourceName = AudioManagerClient.getSourceName(this.nextId);
        ++this.nextId;
        AudioSourceClient audioSource = new AudioSourceClient(this.soundSystem, sourceName, obj, positionSpec, initialSoundFile, loop, priorized, volume);
        WeakObject key = new WeakObject(obj);
        if (!this.objectToAudioSourceMap.containsKey(key)) {
            this.objectToAudioSourceMap.put(key, new LinkedList());
        }
        this.objectToAudioSourceMap.get(key).add(audioSource);
        return audioSource;
    }

    @Override
    public void removeSources(Object obj) {
        if (!this.valid()) {
            return;
        }
        WeakObject key = obj instanceof WeakObject ? (WeakObject)obj : new WeakObject(obj);
        if (!this.objectToAudioSourceMap.containsKey(key)) {
            return;
        }
        for (AudioSource audioSource : this.objectToAudioSourceMap.get(key)) {
            audioSource.remove();
        }
        this.objectToAudioSourceMap.remove(key);
    }

    @Override
    public void playOnce(Object obj, String soundFile) {
        this.playOnce(obj, PositionSpec.Center, soundFile, false, this.getDefaultVolume());
    }

    @Override
    public void playOnce(Object obj, PositionSpec positionSpec, String soundFile, boolean priorized, float volume) {
        if (!this.enabled) {
            return;
        }
        if (!this.valid()) {
            return;
        }
        AudioPosition position = AudioPosition.getFrom(obj, positionSpec);
        if (position == null) {
            return;
        }
        URL url = AudioSource.class.getClassLoader().getResource("ic2/sounds/" + soundFile);
        if (url == null) {
            IC2.log.warn(LogCategory.Audio, "Invalid sound file: %s.", soundFile);
            return;
        }
        String sourceName = this.soundSystem.quickPlay(priorized, url, soundFile, false, position.x, position.y, position.z, 2, this.fadingDistance * Math.max(volume, 1.0f));
        this.soundSystem.setVolume(sourceName, this.masterVolume * Math.min(volume, 1.0f));
    }

    @Override
    public float getDefaultVolume() {
        return 1.2f;
    }

    @Override
    public float getMasterVolume() {
        return this.masterVolume;
    }

    @Override
    protected boolean valid() {
        try {
            return this.soundSystem != null && this.soundManager != null && this.soundManagerLoaded.getBoolean(this.soundManager);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getSourceName(int id) {
        return "asm_snd" + id;
    }

    public static class WeakObject
    extends WeakReference<Object> {
        public WeakObject(Object referent) {
            super(referent);
        }

        public boolean equals(Object object) {
            if (object instanceof WeakObject) {
                return ((WeakObject)object).get() == this.get();
            }
            return this.get() == object;
        }

        public int hashCode() {
            Object object = this.get();
            if (object == null) {
                return 0;
            }
            return object.hashCode();
        }
    }
}

