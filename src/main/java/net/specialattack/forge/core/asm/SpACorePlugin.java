package net.specialattack.forge.core.asm;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import net.specialattack.forge.core.config.ConfigManager;
import net.specialattack.forge.core.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@IFMLLoadingPlugin.TransformerExclusions({ "net.specialattack.forge.core.asm" })
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(100)
public class SpACorePlugin implements IFMLLoadingPlugin, IFMLCallHook {

    protected static boolean debug = false;
    public static final Logger LOG = LogManager.getLogger("SpACore-ASM");

    public static Config config = new Config();

    @Configuration("spacore-asm.cfg")
    public static class Config {

        @Configuration.Option(category = "client", side = Configuration.CSide.CLIENT, needsRestart = true)
        @Configuration.Comment("EXPERIMENTAL! Set to true to enable handling of the render state to increase performance.")
        public boolean stateManager = false;

        @Configuration.Option(category = "client", side = Configuration.CSide.CLIENT, needsRestart = true)
        @Configuration.Comment("Set to true to enable outputting of transformed classes after having the state manager injected.")
        public boolean stateManagerDebug = false;

        @Configuration.Option(category = "client", side = Configuration.CSide.CLIENT, needsRestart = true)
        @Configuration.Comment("Set to true to change the debug screen (F3) to look more like the 1.8 debug screen.")
        public boolean debugScreen = true;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "net.specialattack.forge.core.asm.SpACoreModTransformer", // Note: this one is preferably always first
                "net.specialattack.forge.core.asm.SpACoreHookTransformer", //
                "net.specialattack.forge.core.asm.SpACoreDebugGuiTransformer", //
                "net.specialattack.forge.core.asm.SpACoreGLTransformer", // Note: this one is preferably always last
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return "net.specialattack.forge.core.asm.SpACorePlugin";
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if (data != null) {
            if (data.containsKey("mcLocation")) {
                try {
                    ConfigManager.configFolder = new File(((File) data.get("mcLocation")).getCanonicalFile(), "config");
                } catch (IOException e) {
                    throw new IllegalStateException("Failed getting Minecraft config directory", e);
                }
            }
            if (data.containsKey("runtimeDeobfuscationEnabled") && data.get("runtimeDeobfuscationEnabled") == Boolean.FALSE) {
                ConfigManager.debug = true;
            }
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return "net.specialattack.forge.core.asm.SpACoreAccessTransformer";
    }

    @Override
    public Void call() {
        ConfigManager.registerConfig(SpACorePlugin.config);
        return null;
    }
}
