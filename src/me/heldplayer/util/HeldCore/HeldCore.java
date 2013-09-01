
package me.heldplayer.util.HeldCore;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import me.heldplayer.util.HeldCore.config.Config;
import me.heldplayer.util.HeldCore.config.ConfigValue;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.MemoryConnection;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.TcpConnection;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "HeldCore", version = "@VERSION@")
public class HeldCore implements IConnectionHandler {

    public static Logger log;
    public static File configFolder;
    // Config
    private Config config;
    public static ConfigValue<String> modPack;
    public static ConfigValue<Boolean> optOut;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        HeldCore.log = event.getModLog();

        configFolder = new File(event.getModConfigurationDirectory(), "HeldCore");

        if (!configFolder.exists()) {
            configFolder.mkdir();
        }

        // Config
        modPack = new ConfigValue<String>("modPack", Configuration.CATEGORY_GENERAL, null, "", "If this mod is running in a modpack, please set this config value to the name of the modpack");
        optOut = new ConfigValue<Boolean>("optOut", Configuration.CATEGORY_GENERAL, null, Boolean.FALSE, "Set this to true to opt-out from statistics gathering. If you are configuring this mod for a modpack, please leave it set to false");
        this.config = new Config(event.getSuggestedConfigurationFile());
        this.config.addConfigKey(modPack);
        this.config.addConfigKey(optOut);
        this.config.load();
        this.config.saveOnChange();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        TickRegistry.registerTickHandler(new SyncHandler(), Side.SERVER);

        NetworkRegistry.instance().registerConnectionHandler(this);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        SyncHandler.reset(Side.SERVER);
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
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
        SyncHandler.startTracking(manager);
    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {}

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {}

    @Override
    public void connectionClosed(INetworkManager manager) {
        if (manager instanceof TcpConnection) {
            TcpConnection tcpConnection = (TcpConnection) manager;
        }
        else if (manager instanceof MemoryConnection) {
            MemoryConnection memoryConnection = (MemoryConnection) manager;
        }
        else if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            if (manager instanceof NetClientHandler) {
                SyncHandler.reset(Side.CLIENT);
            }
        }
    }

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
        SyncHandler.reset(Side.CLIENT);
    }

}
