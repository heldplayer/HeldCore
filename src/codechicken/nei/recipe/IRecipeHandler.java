
package codechicken.nei.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.forge.GuiContainerManager;

/**
 * Dummy class
 */
public interface IRecipeHandler {

    public String getRecipeName();

    public int numRecipes();

    public void drawBackground(GuiContainerManager gui, int recipe);

    public void drawForeground(GuiContainerManager gui, int recipe);

    public ArrayList<PositionedStack> getIngredientStacks(int recipe);

    public ArrayList<PositionedStack> getOtherStacks(int recipetype);

    public PositionedStack getResultStack(int recipe);

    public void onUpdate();

    public boolean hasOverlay(GuiContainer gui, Container container, int recipe);

    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe);

    public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe);

    public int recipiesPerPage();

    public List<String> handleTooltip(GuiRecipe gui, List<String> currenttip, int recipe);

    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe);

    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe);

    public boolean mouseClicked(GuiRecipe gui, int button, int recipe);

}
