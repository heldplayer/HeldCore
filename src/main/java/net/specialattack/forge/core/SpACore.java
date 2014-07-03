
package net.specialattack.forge.core;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.config.Configuration;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.config.Config;
import net.specialattack.forge.core.config.ConfigCategory;
import net.specialattack.forge.core.config.ConfigValue;
import net.specialattack.forge.core.packet.PacketHandler;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.packet.Packet1TrackingStatus;
import net.specialattack.forge.core.sync.packet.Packet2TrackingBegin;
import net.specialattack.forge.core.sync.packet.Packet3TrackingUpdate;
import net.specialattack.forge.core.sync.packet.Packet4InitiateClientTracking;
import net.specialattack.forge.core.sync.packet.Packet5TrackingEnd;
import net.specialattack.forge.core.sync.packet.Packet6SetInterval;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(name = Objects.MOD_NAME, modid = Objects.MOD_ID, guiFactory = Objects.GUI_FACTORY)
public class SpACore extends SpACoreMod {

    @Instance(value = Objects.MOD_ID)
    public static SpACore instance;

    @SidedProxy(clientSide = Objects.CLIENT_PROXY, serverSide = Objects.SERVER_PROXY)
    public static CommonProxy proxy;

    public static File configFolder;
    // Config
    public static ConfigValue<String> modPack;
    public static ConfigValue<Boolean> optOut;
    public static ConfigValue<Integer> refreshRate;
    public static ConfigValue<Integer> textureMapId;
    public static ConfigValue<Boolean> showReportBugs;
    public static ConfigValue<Boolean> replaceModOptions;

    public static PacketHandler packetHandler;

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Objects.log = event.getModLog();

        SpACore.packetHandler = new PacketHandler("SpACore", Packet1TrackingStatus.class, Packet2TrackingBegin.class, Packet3TrackingUpdate.class, Packet4InitiateClientTracking.class, Packet5TrackingEnd.class, Packet6SetInterval.class);

        SpACore.configFolder = new File(event.getModConfigurationDirectory(), "SpACore");

        if (!SpACore.configFolder.exists()) {
            SpACore.configFolder.mkdir();
        }

        // Config
        ConfigCategory<?> category = new ConfigCategory(Configuration.CATEGORY_GENERAL, "config.spacore.category.general", null, "General mod settings");
        SpACore.modPack = new ConfigValue<String>("modPack", "config.spacore.key.modPack", null, "", "If this mod is running in a modpack, please set this config value to the name of the modpack");
        SpACore.modPack.setShowInGui(false).setRequiresMcRestart(true);
        SpACore.optOut = new ConfigValue<Boolean>("optOut", "config.spacore.key.optOut", null, Boolean.FALSE, "Set this to true to opt-out from statistics gathering. If you are configuring this mod for a modpack, please leave it set to false");
        SpACore.optOut.setRequiresMcRestart(true);
        SpACore.refreshRate = new ConfigValue<Integer>("refreshRate", "config.spacore.key.refreshRate", null, 5, "The refresh-rate used for syncing objects between server and client. A higher refresh-rate will decrease bandwidth and CPU usage, but will also cause objects to appear to lag");
        SpACore.textureMapId = new ConfigValue<Integer>("textureMapId", "config.spacore.key.textureMapId", Side.CLIENT, 10, "The ID of the texture map that SpACore assigns");
        SpACore.textureMapId.setRequiresMcRestart(true);
        SpACore.showReportBugs = new ConfigValue<Boolean>("showReportBugs", "config.spacore.key.showReportBugs", Side.CLIENT, true, "Should the mod add a 'Report a bug' button to the menu?");
        SpACore.replaceModOptions = new ConfigValue<Boolean>("replaceModOptions", "config.spacore.key.replaceModOptions", Side.CLIENT, true, "Should the ingame 'Mod Options' button be replaced with a 'Mods' button that actually works?");
        this.config = new Config(event.getSuggestedConfigurationFile());
        this.config.addCategory(category);
        category.addValue(SpACore.modPack);
        category.addValue(SpACore.optOut);
        category.addValue(SpACore.refreshRate);
        category.addValue(SpACore.textureMapId);
        category.addValue(SpACore.showReportBugs);
        category.addValue(SpACore.replaceModOptions);

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
        if (SpACore.allowSnooping()) {
            try {
                File file = new File(SpACore.configFolder, modId + ".version");

                if (!file.exists()) {
                    file.createNewFile();
                }

                UsageReporter reporter = new UsageReporter(modId, modVersion, SpACore.modPack.getValue(), FMLCommonHandler.instance().getSide(), SpACore.configFolder);

                Thread thread = new Thread(reporter, "Mod usage reporter for " + modId);
                thread.setDaemon(true);
                thread.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ModInfo getModInfo() {
        return Objects.MOD_INFO;
    }

    @Override
    public SpACoreProxy getProxy() {
        return SpACore.proxy;
    }

    @Override
    public boolean shouldReport() {
        return false;
    }

    @Override
    public boolean configChanged(OnConfigChangedEvent event) {
        Objects.log.info("Configuration changed!");
        return true;
    }

    public static boolean allowSnooping() {
        return MC.getGameSettings().snooperEnabled && !SpACore.optOut.getValue();
    }

}
