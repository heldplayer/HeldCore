
package me.heldplayer.util.HeldCore.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class HeldCorePlugin implements IFMLLoadingPlugin, IFMLCallHook {

    @Override
    @Deprecated
    public String[] getLibraryRequestClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "me.heldplayer.util.HeldCore.asm.HeldCoreAccessTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return "me.heldplayer.util.HeldCore.asm.HeldCoreModContainer";
    }

    @Override
    public String getSetupClass() {
        return "me.heldplayer.util.HeldCore.asm.HeldCorePlugin";
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public Void call() throws Exception {
        return null;
    }

}
