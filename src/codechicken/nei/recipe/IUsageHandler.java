
package codechicken.nei.recipe;

import codechicken.nei.recipe.IUsageHandler;

/**
 * Dummy class
 */
public interface IUsageHandler extends IRecipeHandler {

    public IUsageHandler getUsageHandler(String inputId, Object... ingredients);

}
