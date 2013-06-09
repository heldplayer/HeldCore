
package codechicken.nei.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Dummy class
 */
@SideOnly(Side.CLIENT)
public interface IConfigureNEI {

    public void loadConfig();

    public String getName();

    public String getVersion();

}
