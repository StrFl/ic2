/*
 * Decompiled with CFR 0.152.
 */
package ic2.core.init;

import ic2.core.IC2;
import ic2.core.util.Config;
import ic2.core.util.ConfigUtil;
import java.io.File;

public class MainConfig {
    public static boolean ignoreInvalidRecipes = false;
    private static Config config;

    public static void load() {
        config = new Config("ic2 general config");
        try {
            config.load(IC2.class.getResourceAsStream("/assets/ic2/config/general.ini"));
        }
        catch (Exception e) {
            throw new RuntimeException("Error loading base config", e);
        }
        File configFile = MainConfig.getFile();
        try {
            if (configFile.exists()) {
                config.load(configFile);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error loading user config", e);
        }
        MainConfig.save();
        ignoreInvalidRecipes = ConfigUtil.getBool(MainConfig.get(), "recipes/ignoreInvalidRecipes");
    }

    public static void save() {
        try {
            config.save(MainConfig.getFile());
        }
        catch (Exception e) {
            throw new RuntimeException("Error saving user config", e);
        }
    }

    public static Config get() {
        return config;
    }

    private static File getFile() {
        File folder = new File(IC2.platform.getMinecraftDir(), "config");
        folder.mkdirs();
        return new File(folder, "IC2.ini");
    }
}

