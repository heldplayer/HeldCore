
package codechicken.nei;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Dummy class
 */
@SideOnly(Side.CLIENT)
public class NEIClientUtils extends NEIServerUtils {

    public static boolean shiftKey() {
        return false;
    }

}
