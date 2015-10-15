package net.specialattack.forge.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import net.minecraft.util.IIcon;
import net.specialattack.forge.core.asm.SpACorePlugin;
import net.specialattack.forge.core.config.ConfigManager;
import net.specialattack.forge.core.config.Configuration;
import net.specialattack.forge.core.packet.SpAPacketHandler;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.packet.*;
import net.specialattack.util.Scheduler;

@Mod(name = Objects.MOD_NAME, modid = Objects.MOD_ID, guiFactory = Objects.GUI_FACTORY)
public class SpACore extends SpACoreMod {

    @Mod.Instance(value = Objects.MOD_ID)
    public static SpACore instance;

    @SidedProxy(clientSide = Objects.CLIENT_PROXY, serverSide = Objects.SERVER_PROXY)
    public static CommonProxy proxy;
    @SidedProxy(clientSide = "net.specialattack.forge.core.client.ClientDebug", serverSide = "net.specialattack.forge.core.CommonDebug")
    public static CommonDebug debugProxy;

    public static SpAPacketHandler<SyncPacket> syncPacketHandler;

    public static Config config;
    public static ConfigManager configManager;

    @Configuration("spacore.cfg")
    public static class Config {

        @Configuration.Option(category = "sync")
        @Configuration.Alias(category = "general", name = "refreshRate")
        @Configuration.Syncronized(Configuration.CSide.SERVER)
        @Configuration.IntMinMax(min = 1, max = 40)
        public int refreshRate = 5; // Max 40 = 2 seconds, you REALLY don't want it to take this long to sync.

        @Configuration.Option(category = "client", side = Configuration.CSide.CLIENT, needsRestart = true)
        @Configuration.Alias(category = "general", name = "textureMapId")
        public int textureMapId = -1337;

        @Configuration.Option(category = "client", side = Configuration.CSide.CLIENT)
        @Configuration.Alias(category = "general", name = "showReportBugs")
        @Configuration.Debug
        public boolean showReportBugs = false;

        @Configuration.Option(category = "tweaks", side = Configuration.CSide.CLIENT)
        @Configuration.Alias(category = "general", name = "replaceModOptions")
        public boolean replaceModOptions = true;
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
                String result = "\n\t\tGLStateManager: " + SpACorePlugin.config.stateManager;
                result += "\n\t\tGLStateManager debug output: " + SpACorePlugin.config.stateManagerDebug;
                result += "\n\t\tDebug screen replacer: " + SpACorePlugin.config.debugScreen;
                return result;
            }
        });

        ConfigManager.initialized();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Objects.log = event.getModLog();

        SpACore.configManager = ConfigManager.registerConfig(SpACore.config = new Config());

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
    public SpACoreProxy getProxy() {
        return SpACore.proxy;
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

    public static void registerIconHolder(IIcon holder) {
        SpACore.proxy.registerIconHolder(holder);
    }

}
