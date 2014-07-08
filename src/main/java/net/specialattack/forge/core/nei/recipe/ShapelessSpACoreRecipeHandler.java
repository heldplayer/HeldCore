package net.specialattack.forge.core.nei.recipe;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.ShapedRecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.specialattack.forge.core.crafting.ShapelessSpACoreRecipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class ShapelessSpACoreRecipeHandler extends ShapedRecipeHandler {

    public int[][] stackorder = new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 }, { 0, 2 }, { 1, 2 }, { 2, 0 }, { 2, 1 }, { 2, 2 } };

    @Override
    public String getRecipeName() {
        return NEIClientUtils.translate("recipe.shapeless.spacore");
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("crafting") && this.getClass() == ShapelessSpACoreRecipeHandler.class) {
            List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
            for (IRecipe irecipe : allrecipes) {
                CachedShapelessRecipe recipe = null;
                if (irecipe instanceof ShapelessSpACoreRecipe) {
                    recipe = new CachedShapelessRecipe((ShapelessSpACoreRecipe) irecipe);
                }

                if (recipe == null) {
                    continue;
                }

                this.arecipes.add(recipe);
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
        for (IRecipe irecipe : allrecipes) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
                CachedShapelessRecipe recipe = null;
                if (irecipe instanceof ShapelessSpACoreRecipe) {
                    recipe = new CachedShapelessRecipe((ShapelessSpACoreRecipe) irecipe);
                }
                if (recipe == null) {
                    continue;
                }
                this.arecipes.add(recipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
        for (IRecipe irecipe : allrecipes) {
            CachedShapelessRecipe recipe = null;
            if (irecipe instanceof ShapelessSpACoreRecipe) {
                recipe = new CachedShapelessRecipe((ShapelessSpACoreRecipe) irecipe);
            }
            if (recipe == null) {
                continue;
            }
            if (recipe.contains(recipe.ingredients, ingredient) && recipe.recipe.handler.isValidRecipeInput(ingredient)) {
                recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                this.arecipes.add(recipe);
            }
        }
    }

    @Override
    public boolean isRecipe2x2(int recipe) {
        return this.getIngredientStacks(recipe).size() <= 4;
    }

    @Override
    public void drawExtras(int recipeId) {
        super.drawExtras(recipeId);
        CachedShapelessRecipe recipe = (CachedShapelessRecipe) this.arecipes.get(recipeId);

        if (recipe != null && recipe.recipe != null && recipe.recipe.handler != null) {
            GuiDraw.drawStringC(recipe.recipe.handler.getOwningModName(), 124, 8, 0x404040, false);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!NEIClientUtils.shiftKey() && this.cycleticks % 20 == 0) {
            for (CachedRecipe cachedRecipe : this.arecipes) {
                CachedShapelessRecipe recipe = (CachedShapelessRecipe) cachedRecipe;

                recipe.getCycledIngredients(this.cycleticks / 20, recipe.ingredients);

                recipe.setResult(recipe.recipe.handler.getOutput(recipe.recipe, recipe.input));
            }
        }
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipeId) {
        CachedShapelessRecipe recipe = (CachedShapelessRecipe) this.arecipes.get(recipeId);

        currenttip = super.handleItemTooltip(gui, stack, currenttip, recipeId);

        Point mousepos = GuiDraw.getMousePosition();
        Point relMouse = new Point(mousepos.x - gui.guiLeft, mousepos.y - gui.guiTop);

        Point recipepos = gui.getRecipePosition(recipeId);

        if (recipe != null && currenttip.isEmpty() && stack == null && new Rectangle(recipepos.x, recipepos.y, 166, 60).contains(relMouse)) {
            String tooltip = recipe.recipe.handler.getNEIOverlayText();

            if (tooltip != null) {
                currenttip.add(tooltip);
            }
        }

        return currenttip;
    }

    public class CachedShapelessRecipe extends CachedRecipe {

        public ArrayList<PositionedStack> ingredients;
        public List<ItemStack> input;
        public PositionedStack result;
        public ShapelessSpACoreRecipe recipe;

        public CachedShapelessRecipe(ShapelessSpACoreRecipe recipe) {
            this.recipe = recipe;
            this.ingredients = new ArrayList<PositionedStack>();
            this.input = new ArrayList<ItemStack>();
            this.setResult(recipe.getOutput());
            this.setIngredients(recipe);
        }

        public void setIngredients(List<List<ItemStack>> items) {
            this.ingredients.clear();
            this.input.clear();
            for (int ingred = 0; ingred < items.size(); ingred++) {
                PositionedStack stack = new PositionedStack(items.get(ingred), 25 + ShapelessSpACoreRecipeHandler.this.stackorder[ingred][0] * 18, 6 + ShapelessSpACoreRecipeHandler.this.stackorder[ingred][1] * 18);
                stack.setMaxSize(1);
                this.ingredients.add(stack);
            }

            this.getCycledIngredients(ShapelessSpACoreRecipeHandler.this.cycleticks / 20, this.ingredients);
            this.setResult(this.recipe.handler.getOutput(this.recipe, this.input));
        }

        // Strangeness @Override
        public ArrayList<PositionedStack> getCycledIngredients(int cycle, ArrayList<PositionedStack> ingredients) {
            ArrayList<PositionedStack> result = (ArrayList<PositionedStack>) super.getCycledIngredients(cycle, ingredients);

            this.input.clear();

            for (PositionedStack stack : result) {
                this.input.add(stack.item);
            }

            return result;
        }

        @Override
        public PositionedStack getResult() {
            return this.result;
        }

        public void setResult(ItemStack output) {
            if (this.result != null) {
                this.result.item = output;
                this.result.items = new ItemStack[] { output };
            } else {
                this.result = new PositionedStack(output, 119, 24);
            }
        }

        @Override
        public ArrayList<PositionedStack> getIngredients() {
            return this.ingredients;
        }

        public void setIngredients(ShapelessSpACoreRecipe recipe) {
            ArrayList<List<ItemStack>> items = recipe.ingredients;

            this.setIngredients(items);
        }

        @Override
        public void setIngredientPermutation(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            for (PositionedStack stack : ingredients) {
                for (int i = 0; i < stack.items.length; i++) {
                    if (NEIServerUtils.areStacksSameTypeCrafting(ingredient, stack.items[i])) {
                        stack.item = ingredient;
                        stack.item.setItemDamage(ingredient.getItemDamage());
                        stack.items = new ItemStack[] { stack.item };
                        stack.setPermutationToRender(0);
                        break;
                    }
                }
            }
        }

    }

}
