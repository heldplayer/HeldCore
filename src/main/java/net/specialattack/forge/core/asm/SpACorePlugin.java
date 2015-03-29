package net.specialattack.forge.core.asm;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import java.util.Map;

@TransformerExclusions({ "net.specialattack.forge.core.asm" })
public class SpACorePlugin implements IFMLLoadingPlugin, IFMLCallHook {

    protected static boolean debug = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "net.specialattack.forge.core.asm.SpACoreModTransformer", //
                //"net.specialattack.forge.core.asm.SpACoreDebugTransformer", //
                "net.specialattack.forge.core.asm.SpACoreLoggerTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return "net.specialattack.forge.core.asm.SpACoreModContainer";
    }

    @Override
    public String getSetupClass() {
        return "net.specialattack.forge.core.asm.SpACorePlugin";
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // Enable when NEI bugs
        /*
        if (FMLLaunchHandler.side().isClient() && !((Boolean) data.get("runtimeDeobfuscationEnabled"))) {
            debug = true;
        }
        //*/
    }

    @Override
    public String getAccessTransformerClass() {
        return "net.specialattack.forge.core.asm.SpACoreAccessTransformer";
    }

    @Override
    public Void call() {
        return null;
    }

}
