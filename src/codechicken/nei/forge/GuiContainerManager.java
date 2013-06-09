
package codechicken.nei.forge;

import net.minecraft.item.ItemStack;

/**
 * Dummy class
 */
public class GuiContainerManager {

    public static void addTooltipHandler(IContainerTooltipHandler handler) {}

    public void bindTexture(String s) {}

    public void drawTexturedModalRect(int x, int y, int tx, int ty, int w, int h) {}

    public void drawGradientRect(int x, int y, int w, int h, int colour1, int colour2) {}

    public void drawText(int x, int y, String text, int colour, boolean shadow) {}

    public void drawTextCentered(int x, int y, int w, int h, String text, int colour, boolean shadow) {}

    public void drawTextCentered(String text, int x, int y, int colour, boolean shadow) {}

    public void drawText(int x, int y, String text, boolean shadow) {}

    public void drawText(int x, int y, String text, int colour) {}

    public void drawTextCentered(int x, int y, int w, int h, String text, int colour) {}

    public void drawTextCentered(String text, int x, int y, int colour) {}

    public void drawText(int x, int y, String text) {}

    public void setColouredItemRender(boolean enable) {}

    public void drawItem(int i, int j, ItemStack itemstack) {}

    public int getStringWidth(String s) {
        return 0;
    }

}
