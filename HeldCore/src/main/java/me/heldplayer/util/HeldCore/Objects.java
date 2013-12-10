
package me.heldplayer.util.HeldCore;

import java.util.logging.Logger;

/**
 * ImRecording mod Objects
 * 
 */
public final class Objects {

    public static final String MOD_ID = "HeldCore";
    public static final String MOD_NAME = "HeldCore";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String MOD_CHANNEL = "HeldCore";
    public static final String CLIENT_PROXY = "me.heldplayer.util.HeldCore.client.ClientProxy";
    public static final String SERVER_PROXY = "me.heldplayer.util.HeldCore.CommonProxy";

    public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, MOD_NAME, MOD_VERSION);

    public static Logger log;

}
