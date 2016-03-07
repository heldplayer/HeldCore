package net.specialattack.forge.core;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.specialattack.forge.core.client.texture.IconHolder;
import net.specialattack.forge.core.config.ConfigManager;
import net.specialattack.forge.core.config.Configuration;
import net.specialattack.forge.core.packet.SpAPacketHandler;
import net.specialattack.forge.core.seasonal.CommonSeasonal;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.packet.*;
import net.specialattack.util.Consumer;
import net.specialattack.util.Scheduler;

@Mod(name = Objects.MOD_NAME, modid = Objects.MOD_ID, guiFactory = Objects.GUI_FACTORY, certificateFingerprint = "50d7581cf144a9c1c8d2529f263849db7717cdf9")
public class SpACore extends SpACoreMod {

    @Mod.Instance(value = Objects.MOD_ID)
    public static SpACore instance;

    @SidedProxy(clientSide = "net.specialattack.forge.core.client.ClientProxy", serverSide = "net.specialattack.forge.core.CommonProxy")
    public static CommonProxy proxy;
    @SidedProxy(clientSide = "net.specialattack.forge.core.client.ClientDebug", serverSide = "net.specialattack.forge.core.CommonDebug")
    public static CommonDebug debugProxy;
    @SidedProxy(clientSide = "net.specialattack.forge.core.seasonal.ClientSeasonal", serverSide = "net.specialattack.forge.core.seasonal.CommonSeasonal")
    public static CommonSeasonal seasonalProxy;

    public static SpAPacketHandler<SyncPacket> syncPacketHandler;

    public static SpACore.Config config;
    public static ConfigManager configManager;

    @Configuration("spacore.cfg")
    public static class Config {

        @Configuration.Option(category = "sync")
        @Configuration.Alias(category = "general", name = "refreshRate")
        @Configuration.Syncronized(Configuration.CSide.SERVER)
        @Configuration.IntMinMax(min = 1, max = 100)
        public int refreshRate = 5; // Max 100 = 5 seconds, you REALLY don't want it to take this long to sync, but might be good for high latency connections

        @Configuration.Option(category = "client", side = Configuration.CSide.CLIENT)
        @Configuration.Alias(category = "general", name = "showReportBugs")
        @Configuration.Debug
        public boolean showReportBugs = false;

        @Configuration.Option(category = "common")
        public boolean enableSeasonals = true;
    }

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
            @Override
            public String getLabel() {
                return "SpACore ASM Transformers";
            }

            @Override
            public String call() throws Exception {
                //String result = "\n\t\tGLStateManager: " + SpACorePlugin.config.stateManager;
                //result += "\n\t\tGLStateManager debug output: " + SpACorePlugin.config.stateManagerDebug;
                //return result;
                return "";
            }
        });

        ConfigManager.initialized();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Objects.log = event.getModLog();

        SpACore.configManager = ConfigManager.registerConfig(SpACore.config = new SpACore.Config());

        SpACore.syncPacketHandler = new SpAPacketHandler<SyncPacket>("SpACore:Sync", //
                // Server packets
                S01Connection.class, S02TrackStorage.class, S03StartSyncing.class, S04UpdateSyncable.class, //
                // Client packets
                C01Connection.class, C02RequestSync.class);

        super.preInit(event);
    }

    @Override
    public ModInfo getModInfo() {
        return Objects.MOD_INFO;
    }

    @Override
    public SpACoreProxy[] getProxies() {
        return new SpACoreProxy[] { SpACore.proxy, SpACore.seasonalProxy, SpACore.debugProxy };
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        CommonProxy.serverScheduler = new Scheduler(Scheduler.Type.SERVER);
        Scheduler.addScheduler(CommonProxy.serverScheduler);

        SyncHandler.onServerAboutToStart(event);
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        SyncHandler.onServerStopped(event);

        Scheduler.removeScheduler(CommonProxy.serverScheduler);
        CommonProxy.serverScheduler = null;
    }

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        System.err.println("Invalid fingerprint detected!");
        System.err.println("Expected: " + event.expectedFingerprint);
        System.err.println("Actual:   " + event.fingerprints.toString());
    }

    public static void registerIconHolder(IconHolder holder) {
        SpACore.proxy.registerIconHolder(holder);
    }

    public static void registerIconProvider(Consumer provider) {
        SpACore.proxy.registerIconProvider(provider);
    }
}
