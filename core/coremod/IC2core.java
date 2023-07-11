/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.IFMLLoadingPlugin
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package ic2.core.coremod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IC2core
implements IFMLLoadingPlugin {
    public static Logger log;

    public IC2core() {
        log = LogManager.getLogger((String)"IC2-core");
    }

    public String[] getASMTransformerClass() {
        return null;
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return "ic2.core.coremod.Setup";
    }

    public void injectData(Map<String, Object> data) {
    }

    public String[] getLibraryRequestClass() {
        return null;
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

