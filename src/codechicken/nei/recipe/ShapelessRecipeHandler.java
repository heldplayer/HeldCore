
package codechicken.nei.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import codechicken.nei.PositionedStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShapelessRecipeHandler extends TemplateRecipeHandler {
    public int[][] stackorder;

    public class CachedShapelessRecipe extends CachedRecipe {
        public CachedShapelessRecipe() {}

        public CachedShapelessRecipe(ItemStack output) {}

        public CachedShapelessRecipe(ShapelessRecipes recipe) {}

        public CachedShapelessRecipe(Object[] input, ItemStack output) {}

        public CachedShapelessRecipe(List<?> input, ItemStack output) {}

        public void setIngredients(List<?> items) {}

        public void setIngredients(ShapelessRecipes recipe) {}

        public void setResult(ItemStack output) {}

        @Override
        public ArrayList<PositionedStack> getIngredients() {
            return null;
        }

        @Override
        public PositionedStack getResult() {
            return this.result;
        }

        public ArrayList<PositionedStack> ingredients;
        public PositionedStack result;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {}

    @Override
    public String getGuiTexture() {
        return null;
    }
}