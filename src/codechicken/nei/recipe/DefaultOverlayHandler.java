package codechicken.nei.recipe;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;

public class DefaultOverlayHandler implements IOverlayHandler {

	public DefaultOverlayHandler(int x, int y) {
	}

	public DefaultOverlayHandler() {
		this(5, 11);
	}

	@Override
	public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe,
			int recipeIndex, boolean shift) {
	}

	public Slot[][] mapIngredSlots(GuiContainer gui,
			List<PositionedStack> ingredients) {
		return null;
	}
}
