
package net.specialattack.forge.core.nei;

import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.nei.recipe.ShapedSpACoreRecipeHandler;
import net.specialattack.forge.core.nei.recipe.ShapelessSpACoreRecipeHandler;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIConfig implements IConfigureNEI {

    public static ShapedSpACoreRecipeHandler shapedSpACore;
    public static ShapelessSpACoreRecipeHandler shapelessSpACore;

    @Override
    public void loadConfig() {
        NEIConfig.shapedSpACore = new ShapedSpACoreRecipeHandler();
        API.registerRecipeHandler(NEIConfig.shapedSpACore);
        API.registerUsageHandler(NEIConfig.shapedSpACore);

        NEIConfig.shapelessSpACore = new ShapelessSpACoreRecipeHandler();
        API.registerRecipeHandler(NEIConfig.shapelessSpACore);
        API.registerUsageHandler(NEIConfig.shapelessSpACore);
    }

    @Override
    public String getName() {
        return Objects.MOD_NAME;
    }

    @Override
    public String getVersion() {
        return Objects.MOD_INFO.modVersion;
    }

}
