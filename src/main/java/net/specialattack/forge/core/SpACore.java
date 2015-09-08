package net.specialattack.forge.core;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.relauncher.Side;
import java.io.File;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.config.Configuration;
import net.specialattack.forge.core.asm.SpACorePlugin;
import net.specialattack.forge.core.config.Config;
import net.specialattack.forge.core.config.ConfigCategory;
import net.specialattack.forge.core.config.ConfigValue;
import net.specialattack.forge.core.packet.SpAPacketHandler;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.packet.*;
import net.specialattack.util.Scheduler;

@Mod(name = Objects.MOD_NAME, modid = Objects.MOD_ID, guiFactory = Objects.GUI_FACTORY)
public class SpACore extends SpACoreMod {

    @Instance(value = Objects.MOD_ID)
    public static SpACore instance;

    @SidedProxy(clientSide = Objects.CLIENT_PROXY, serverSide = Objects.SERVER_PROXY)
    public static CommonProxy proxy;

    public static File configFolder;
    // Config
    public static ConfigValue<Integer> refreshRate;
    public static ConfigValue<Integer> textureMapId;
    public static ConfigValue<Boolean> showReportBugs;
    public static ConfigValue<Boolean> replaceModOptions;

    public static SpAPacketHandler<SyncPacket> syncPacketHandler;

    @EventHandler
    public void construction(FMLConstructionEvent event) {
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
            @Override
            public String getLabel() {
                return "SpACore ASM Transformers";
            }

            @Override
            public String call() throws Exception {
                String result = "\n\t\tGLStateManager: " + SpACorePlugin.stateManager;
                result += "\n\t\tGLStateManager debug output: " + SpACorePlugin.stateManagerDebug;
                result += "\n\t\tTexture exception muter: " + SpACorePlugin.loggerTransformer;
                result += "\n\t\tDebug screen replacer: " + SpACorePlugin.debugScreen;
                return result;
            }
        });
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Objects.log = event.getModLog();

        SpACore.syncPacketHandler = new SpAPacketHandler<SyncPacket>("SpACore:Sync", //
                // Server packets
                S01Connection.class, S02TrackStorage.class, S03StartSyncing.class, S04UpdateSyncable.class, //
                // Client packets
                C01Connection.class, C02RequestSync.class);

        SpACore.configFolder = new File(event.getModConfigurationDirectory(), "SpACore");

        if (!SpACore.configFolder.exists()) {
            SpACore.configFolder.mkdir();
        }

        // Config
        ConfigCategory<?> category = new ConfigCategory(Configuration.CATEGORY_GENERAL, "spacore:config.general", null);
        SpACore.refreshRate = new ConfigValue<Integer>("refreshRate", "spacore:config.general.refreshRate", null, 5);
        SpACore.textureMapId = new ConfigValue<Integer>("textureMapId", "spacore:config.general.textureMapId", Side.CLIENT, 10);
        SpACore.textureMapId.setRequiresMcRestart(true);
        SpACore.showReportBugs = new ConfigValue<Boolean>("showReportBugs", "spacore:config.general.showReportBugs", Side.CLIENT, false);
        SpACore.replaceModOptions = new ConfigValue<Boolean>("replaceModOptions", "spacore:config.general.replaceModOptions", Side.CLIENT, true);
        this.config = new Config(event.getSuggestedConfigurationFile());
        this.config.addCategory(category);
        category.addValue(SpACore.refreshRate);
        category.addValue(SpACore.textureMapId);
        //category.addValue(SpACore.showReportBugs);
        category.addValue(SpACore.replaceModOptions);

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

    @Override
    public boolean configChanged(OnConfigChangedEvent event) {
        //if (MC.getWorld() != null) {
        // TODO
        // OldSyncHandler.Client.sendUpdateInterval();
        //}
        return true;
    }

    @EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        CommonProxy.serverScheduler = new Scheduler(Scheduler.Type.SERVER);
        Scheduler.addScheduler(CommonProxy.serverScheduler);

        SyncHandler.onServerAboutToStart(event);
    }

    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        SyncHandler.onServerStopped(event);

        Scheduler.removeScheduler(CommonProxy.serverScheduler);
        CommonProxy.serverScheduler = null;
    }

    public static void registerIconHolder(IIcon holder) {
        SpACore.proxy.registerIconHolder(holder);
    }

}
