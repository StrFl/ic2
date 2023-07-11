/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.util;

import ic2.api.util.IKeyboard;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.entity.player.EntityPlayer;

public class Keyboard
implements IKeyboard {
    private final Map<EntityPlayer, Set<Key>> playerKeys = new WeakHashMap<EntityPlayer, Set<Key>>();

    @Override
    public boolean isAltKeyDown(EntityPlayer player) {
        return this.get(player, Key.alt);
    }

    @Override
    public boolean isBoostKeyDown(EntityPlayer player) {
        return this.get(player, Key.boost);
    }

    @Override
    public boolean isForwardKeyDown(EntityPlayer player) {
        return this.get(player, Key.forward);
    }

    @Override
    public boolean isJumpKeyDown(EntityPlayer player) {
        return this.get(player, Key.jump);
    }

    @Override
    public boolean isModeSwitchKeyDown(EntityPlayer player) {
        return this.get(player, Key.modeSwitch);
    }

    @Override
    public boolean isSideinventoryKeyDown(EntityPlayer player) {
        return this.get(player, Key.sideInventory);
    }

    @Override
    public boolean isHudModeKeyDown(EntityPlayer player) {
        return this.get(player, Key.hubMode);
    }

    @Override
    public boolean isSneakKeyDown(EntityPlayer player) {
        return player.isSneaking();
    }

    public void sendKeyUpdate() {
    }

    public void processKeyUpdate(EntityPlayer player, int keyState) {
        this.playerKeys.put(player, Key.fromInt(keyState));
    }

    public void removePlayerReferences(EntityPlayer player) {
        this.playerKeys.remove(player);
    }

    private boolean get(EntityPlayer player, Key key) {
        Set<Key> keys = this.playerKeys.get(player);
        if (keys == null) {
            return false;
        }
        return keys.contains((Object)key);
    }

    protected static enum Key {
        alt,
        boost,
        forward,
        modeSwitch,
        jump,
        sideInventory,
        hubMode;

        public static final Key[] keys;

        public static int toInt(Iterable<Key> keySet) {
            int ret = 0;
            for (Key key : keySet) {
                ret |= 1 << key.ordinal();
            }
            return ret;
        }

        public static Set<Key> fromInt(int keyState) {
            EnumSet<Key> ret = EnumSet.noneOf(Key.class);
            int i = 0;
            while (keyState != 0) {
                if ((keyState & 1) != 0) {
                    ret.add(keys[i]);
                }
                ++i;
                keyState >>= 1;
            }
            return ret;
        }

        static {
            keys = Key.values();
        }
    }
}

