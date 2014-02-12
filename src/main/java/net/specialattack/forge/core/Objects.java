
package net.specialattack.forge.core;

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

    public static final String MOD_ID = "spacore";
    public static final String MOD_NAME = "SpACore";
    public static final String CLIENT_PROXY = "net.specialattack.forge.core.client.ClientProxy";
    public static final String SERVER_PROXY = "net.specialattack.forge.core.CommonProxy";

    public static final ModInfo MOD_INFO = new ModInfo(Objects.MOD_ID, Objects.MOD_NAME, Objects.MOD_VERSION);

    public static Logger log;

}
