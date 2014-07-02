
package net.specialattack.forge.core;

import org.apache.logging.log4j.Logger;

public final class Objects {

    public static final String MOD_ID = "spacore";
    public static final String MOD_NAME = "SpACore";
    public static final String CLIENT_PROXY = "net.specialattack.forge.core.client.ClientProxy";
    public static final String SERVER_PROXY = "net.specialattack.forge.core.CommonProxy";
    public static final String GUI_FACTORY = "net.specialattack.forge.core.client.gui.GuiFactory";

    public static final ModInfo MOD_INFO = new ModInfo(Objects.MOD_ID, Objects.MOD_NAME);

    public static Logger log;

}
