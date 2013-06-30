
package me.heldplayer.util.HeldCore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * Host class for HeldCore
 * 
 * @author heldplayer
 * 
 */
public class Updater implements Runnable {

    public static ITickHandler tickHandler = null;
    public static String notice = EnumChatFormatting.BOLD + "NOTICE!" + EnumChatFormatting.RESET + " The following mods are out-of-date";
    public static String outOfDateList = "";
    public static boolean hide = false;

    public static Logger log = Logger.getLogger("HeldCore");
    public static final String version = "01.02.04.00";

    private String modId;
    private String modVersion;
    private boolean silent;

    @Deprecated
    public static void initializeUpdater(String modId, String modVersion) {
        initializeUpdater(modId, modVersion, false);
    }

    /**
     * Creates an updater for a mod.
     * 
     * @param modId
     *        The mod ID of the mod.
     * @param modVersion
     *        The current version of the mod
     * @param silent
     *        Whether the message on the main menu should be hidden for this mod
     */
    public static void initializeUpdater(String modId, String modVersion, boolean silent) {
        Updater updater = new Updater(modId, modVersion, silent);
        Thread thread = new Thread(updater, modId + " update checker");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();

        if (tickHandler != null) {
            return;
        }

        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return;
        }

        tickHandler = new GuiDrawer();

        TickRegistry.registerTickHandler(tickHandler, Side.CLIENT);
    }

    protected Updater(String modId, String modVersion, boolean silent) {
        this.modId = modId;
        this.modVersion = modVersion;
        this.silent = silent;
    }

    @Override
    public void run() {
        HttpURLConnection request = null;
        InputStream stream = null;

        try {
            request = (HttpURLConnection) new URL("http://dsiwars.x10.mx/files/version.php?mod=" + this.modId).openConnection();
            request.setRequestMethod("GET");
            request.connect();

            stream = request.getInputStream();

            if (request.getResponseCode() == 200) {
                long time = System.currentTimeMillis();
                while (stream.available() <= 0) {
                    if (time + 5000L < System.currentTimeMillis()) {
                        throw new RuntimeException("Read took too long");
                    }
                }

                byte[] bytes = new byte[stream.available()];

                stream.read(bytes);

                String latestVersion = new String(bytes);

                String[] version = this.modVersion.split("\\.");
                String[] lastVersion = latestVersion.split("\\.");

                for (int i = 0; i < version.length && i < lastVersion.length; i++) {
                    int newest = Integer.parseInt(lastVersion[i]);
                    int old = Integer.parseInt(version[i]);
                    if (newest > old) {
                        if (!this.silent) {
                            if (!outOfDateList.isEmpty()) {
                                outOfDateList += ", ";
                            }

                            outOfDateList += this.modId;
                        }

                        log.log(Level.INFO, "The mod '" + this.modId + "' is has a new version available!");
                        log.log(Level.INFO, "   Current version: " + this.modVersion + "  new version: " + latestVersion);

                        break;
                    }
                    else if (newest < old) {
                        break;
                    }
                }
            }
            else {
                throw new RuntimeException("Server returned HTTP response code " + request.getResponseCode());
            }
        }
        catch (Exception e) {
            try {
                stream.close();
            }
            catch (Exception e2) {}
            log.log(Level.SEVERE, "Update check failed for '" + this.modId + "': " + e.getMessage());
        }
        finally {

            if (request != null) {
                request.disconnect();
            }
        }
    }

    static {
        log.setParent(FMLLog.getLogger());

        String os = System.getProperty("os_architecture");
        String java = System.getProperty("java_version");

        if (os != null && os.equalsIgnoreCase("ppc")) {
            hide = true;
        }
        else if (java != null && java.startsWith("1.5")) {
            hide = true;
        }
        else {
            Thread thread = new Thread(new RunnableMinecraftChecker());
            thread.setDaemon(true);
            thread.start();
        }
        log.log(Level.INFO, "Current implemented version of HeldCore is " + version);
        log.log(Level.WARNING, "This version will now be running on ALL mods by heldplayer and may break older or newer versions");
    }

}
