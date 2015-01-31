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

        String version = null;

        try {
            InputStream stream = Objects.class.getClassLoader().getResourceAsStream(this.modId.toLowerCase() + ".version");
            prop.load(stream);
            stream.close();
            version = prop.getProperty("version");
        } catch (Exception e) {
            version = "MISSING";
            e.printStackTrace();
        } finally {
            this.modVersion = version;
        }
    }

}
