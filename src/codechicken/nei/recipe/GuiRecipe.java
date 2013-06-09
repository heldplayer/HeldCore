
package codechicken.nei.recipe;

import codechicken.nei.api.IGuiContainerOverlay;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public abstract class GuiRecipe extends GuiContainer implements IGuiContainerOverlay {

    public GuiRecipe(Container par1Container) {
        super(par1Container);
    }

}
