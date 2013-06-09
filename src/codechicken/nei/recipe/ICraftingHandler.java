
package codechicken.nei.recipe;

import codechicken.nei.recipe.ICraftingHandler;

/**
 * Dummy class
 */
public interface ICraftingHandler extends IRecipeHandler {

    public ICraftingHandler getRecipeHandler(String outputId, Object... results);

}
