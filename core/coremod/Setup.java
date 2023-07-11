/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.FMLInjectionData
 *  cpw.mods.fml.relauncher.IFMLCallHook
 */
package ic2.core.coremod;

import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.IFMLCallHook;
import ic2.core.init.Libraries;
import java.io.File;
import java.util.Map;

public class Setup
implements IFMLCallHook {
    private File mcDir;

    public Void call() throws Exception {
        Libraries.init(this.mcDir, (String)FMLInjectionData.data()[4]);
        return null;
    }

    public void injectData(Map<String, Object> data) {
        this.mcDir = (File)data.get("mcLocation");
    }
}

