
package codechicken.nei.api;

import java.util.ArrayList;

import net.minecraft.inventory.Slot;
import codechicken.nei.PositionedStack;
import codechicken.nei.forge.GuiContainerManager;

/**
 * Dummy class
 */
public class DefaultOverlayRenderer implements IRecipeOverlayRenderer {

    public DefaultOverlayRenderer(ArrayList<PositionedStack> ai, IStackPositioner positioner) {}

    @Override
    public void renderOverlay(GuiContainerManager gui, Slot slot) {}

}
