
package me.heldplayer.util.HeldCore;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

/**
 * ImRecording mod Objects
 * 
 */
public final class Objects {

    public static final String MOD_VERSION;

    static {

        Properties prop = new Properties();

        String version = "";

        try {
            InputStream stream = Objects.class.getClassLoader().getResourceAsStream("version.properties");
            prop.load(stream);
            stream.close();
            version = prop.getProperty("version");
        }
        catch (Exception e) {
            e.printStackTrace();
            version = "Error";
        }
        finally {
            MOD_VERSION = version;
        }

    }

    public static final String MOD_ID = "heldcore";
    public static final String MOD_NAME = "HeldCore";
    public static final String MOD_CHANNEL = "HeldCore";
    public static final String CLIENT_PROXY = "me.heldplayer.util.HeldCore.client.ClientProxy";
    public static final String SERVER_PROXY = "me.heldplayer.util.HeldCore.CommonProxy";

    public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, MOD_NAME, MOD_VERSION);

    public static Logger log;

}
