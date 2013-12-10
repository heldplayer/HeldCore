
package me.heldplayer.util.HeldCore;

import java.io.File;
import java.io.IOException;

import me.heldplayer.util.HeldCore.config.Config;
import me.heldplayer.util.HeldCore.config.ConfigValue;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import me.heldplayer.util.HeldCore.sync.packet.PacketHandler;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;

@Mod(name = Objects.MOD_NAME, modid = Objects.MOD_ID, version = Objects.MOD_VERSION)
@NetworkMod(clientSideRequired = false, serverSideRequired = false, channels = { Objects.MOD_CHANNEL }, packetHandler = PacketHandler.class)
public class HeldCore extends HeldCoreMod {

    @Instance(value = Objects.MOD_ID)
    public static HeldCore instance;

    @SidedProxy(clientSide = Objects.CLIENT_PROXY, serverSide = Objects.SERVER_PROXY)
    public static CommonProxy proxy;

    public static File configFolder;
    // Config
    public static ConfigValue<String> modPack;
    public static ConfigValue<Boolean> optOut;
    public static ConfigValue<Integer> refreshRate;
    public static ConfigValue<Integer> textureMapId;

    @Override
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Objects.log = event.getModLog();

        configFolder = new File(event.getModConfigurationDirectory(), "HeldCore");

        if (!configFolder.exists()) {
            configFolder.mkdir();
        }

        // Config
        modPack = new ConfigValue<String>("modPack", Configuration.CATEGORY_GENERAL, null, "", "If this mod is running in a modpack, please set this config value to the name of the modpack");
        optOut = new ConfigValue<Boolean>("optOut", Configuration.CATEGORY_GENERAL, null, Boolean.FALSE, "Set this to true to opt-out from statistics gathering. If you are configuring this mod for a modpack, please leave it set to false");
        refreshRate = new ConfigValue<Integer>("refreshRate", Configuration.CATEGORY_GENERAL, null, 5, "The refresh-rate used for syncing objects between server and client. A higher refresh-rate will decrease bandwidth and CPU usage, but will also cause objects to appear to lag");
        textureMapId = new ConfigValue<Integer>("textureMapId", Configuration.CATEGORY_GENERAL, Side.CLIENT, 10, "The ID of the texture map that HeldCore assigns");
        this.config = new Config(event.getSuggestedConfigurationFile());
        this.config.addConfigKey(modPack);
        this.config.addConfigKey(optOut);
        this.config.addConfigKey(refreshRate);
        this.config.addConfigKey(textureMapId);
        this.config.load();
        this.config.saveOnChange();

        super.preInit(event);
    }

    @Override
    @EventHandler
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        SyncHandler.reset();
    }

    public static void initializeReporter(String modId, String modVersion) {
        if (optOut.getValue()) {
            return;
        }
        try {
            File file = new File(configFolder, modId + ".version");

            if (!file.exists()) {
                file.createNewFile();
            }

            UsageReporter reporter = new UsageReporter(modId, modVersion, modPack.getValue(), FMLCommonHandler.instance().getSide(), configFolder);

            Thread thread = new Thread(reporter, "Mod usage reporter for " + modId);
            thread.setDaemon(true);
            thread.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ModInfo getModInfo() {
        return Objects.MOD_INFO;
    }

    @Override
    public HeldCoreProxy getProxy() {
        return proxy;
    }

    @Override
    public boolean shouldReport() {
        return false;
    }

}
