
package codechicken.nei.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.forge.GuiContainerManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class TemplateRecipeHandler implements ICraftingHandler, IUsageHandler {
    public abstract class CachedRecipe {
        public abstract PositionedStack getResult();

        public ArrayList<PositionedStack> getIngredients() {
            return null;
        }

        public PositionedStack getIngredient() {
            return null;
        }

        public ArrayList<PositionedStack> getOtherStacks() {
            return null;
        }

        public PositionedStack getOtherStack() {
            return null;
        }

        public ArrayList<PositionedStack> getCycledIngredients(int cycle, ArrayList<PositionedStack> ingredients) {
            return null;
        }

        public void setIngredientPermutation(Collection<PositionedStack> ingredients, ItemStack ingredient) {}

        public boolean contains(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            return false;
        }

        final long offset = System.currentTimeMillis();
    }

    public ArrayList<CachedRecipe> arecipes = new ArrayList<CachedRecipe>();
    public int cycleticks = Math.abs((int) System.currentTimeMillis());

    public void loadUsageRecipes(ItemStack ingredient) {}

    public void loadTransferRects() {}

    public void drawExtras(GuiContainerManager gui, int recipe) {}

    public void loadCraftingRecipes(String outputId, Object... results) {}

    public void loadCraftingRecipes(ItemStack result) {}

    public abstract String getGuiTexture();

    public Class<? extends GuiContainer> getGuiClass() {
        return null;
    }

    public String getOverlayIdentifier() {
        return null;
    }

    @Override
    public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
        return false;
    }

    @Override
    public String getRecipeName() {
        return null;
    }

    @Override
    public int numRecipes() {
        return 0;
    }

    @Override
    public void drawBackground(GuiContainerManager gui, int recipe) {}

    @Override
    public void drawForeground(GuiContainerManager gui, int recipe) {}

    @Override
    public ArrayList<PositionedStack> getIngredientStacks(int recipe) {
        return null;
    }

    @Override
    public ArrayList<PositionedStack> getOtherStacks(int recipetype) {
        return null;
    }

    @Override
    public PositionedStack getResultStack(int recipe) {
        return null;
    }

    @Override
    public void onUpdate() {}

    @Override
    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe) {
        return null;
    }

    @Override
    public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe) {
        return null;
    }

    @Override
    public int recipiesPerPage() {
        return 0;
    }

    @Override
    public List<String> handleTooltip(GuiRecipe gui, List<String> currenttip, int recipe) {
        return null;
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe) {
        return null;
    }

    @Override
    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe) {
        return false;
    }

    @Override
    public boolean mouseClicked(GuiRecipe gui, int button, int recipe) {
        return false;
    }

    @Override
    public IUsageHandler getUsageHandler(String inputId, Object... ingredients) {
        return null;
    }

    @Override
    public ICraftingHandler getRecipeHandler(String outputId, Object... results) {
        return null;
    }
}
