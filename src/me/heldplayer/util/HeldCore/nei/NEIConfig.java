
package me.heldplayer.util.HeldCore.nei;

import me.heldplayer.util.HeldCore.nei.recipe.ShapedHeldCoreRecipeHandler;
import me.heldplayer.util.HeldCore.nei.recipe.ShapelessHeldCoreRecipeHandler;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIConfig implements IConfigureNEI {

    public static ShapedHeldCoreRecipeHandler shapedHeldCore;
    public static ShapelessHeldCoreRecipeHandler shapelessHeldCore;

    @Override
    public void loadConfig() {
        NEIConfig.shapedHeldCore = new ShapedHeldCoreRecipeHandler();
        API.registerRecipeHandler(NEIConfig.shapedHeldCore);
        API.registerUsageHandler(NEIConfig.shapedHeldCore);

        NEIConfig.shapelessHeldCore = new ShapelessHeldCoreRecipeHandler();
        API.registerRecipeHandler(NEIConfig.shapelessHeldCore);
        API.registerUsageHandler(NEIConfig.shapelessHeldCore);
    }

    @Override
    public String getName() {
        return "HeldCore";
    }

    @Override
    public String getVersion() {
        return "@VERSION@";
    }

}
