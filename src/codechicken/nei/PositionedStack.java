
package codechicken.nei;

import codechicken.nei.PositionedStack;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Dummy class
 */
@SideOnly(Side.CLIENT)
public class PositionedStack {

    public ItemStack item;
    public ItemStack items[];
    public int relx;
    public int rely;

    public PositionedStack(Object itemStack, int i, int j) {}

    public void setMaxSize(int i) {}

    public PositionedStack copy() {
        return null;
    }

}
