
package codechicken.nei.recipe;

import codechicken.nei.recipe.ICraftingHandler;

public interface ICraftingHandler extends IRecipeHandler {

    public ICraftingHandler getRecipeHandler(String outputId, Object... results);

}
