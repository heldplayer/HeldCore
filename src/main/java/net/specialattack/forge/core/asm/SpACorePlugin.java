package net.specialattack.forge.core.asm;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import cpw.mods.fml.relauncher.Side;
import java.util.Map;

@TransformerExclusions({ "net.specialattack.forge.core.asm" })
public class SpACorePlugin implements IFMLLoadingPlugin, IFMLCallHook {

    protected static boolean debug = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "net.specialattack.forge.core.asm.SpACoreModTransformer", //
                "net.specialattack.forge.core.asm.SpACoreLoggerTransformer", //
                "net.specialattack.forge.core.asm.SpACoreDebugTransformer" };
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
        if (!((Boolean) data.get("runtimeDeobfuscationEnabled"))) {
            debug = true;
        }
        debug = debug && FMLCommonHandler.instance().getSide() == Side.CLIENT;
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
