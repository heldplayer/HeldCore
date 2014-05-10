
package net.specialattack.forge.core;

import java.io.InputStream;
import java.util.Properties;

public class ModInfo {

    public final String modId;
    public final String modName;
    public final String modVersion;

    public ModInfo(String modId, String modName) {
        this.modId = modId;
        this.modName = modName;

        Properties prop = new Properties();

        String version = "";

        try {
            InputStream stream = Objects.class.getClassLoader().getResourceAsStream(this.modId.toLowerCase() + ".version");
            prop.load(stream);
            stream.close();
            version = prop.getProperty("version");
        }
        catch (Exception e) {
            e.printStackTrace();
            version = "Error";
        }
        finally {
            modVersion = version;
        }
    }

}
