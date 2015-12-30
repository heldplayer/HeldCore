package net.specialattack.forge.core.nei.recipe;
/*
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.specialattack.forge.core.crafting.CraftingHelper;
import net.specialattack.forge.core.crafting.FakeShapedSpACoreRecipe;
import net.specialattack.forge.core.crafting.ShapedSpACoreRecipe;

@SuppressWarnings("unchecked")
public class ShapedSpACoreRecipeHandler extends TemplateRecipeHandler {

    @Override
    public String getRecipeName() {
        return I18n.format("spacore:recipe.shaped.modded");
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(84, 23, 24, 18), "crafting"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("crafting") && this.getClass() == ShapedSpACoreRecipeHandler.class) {
            List<IRecipe> allrecipes = new ArrayList<IRecipe>(CraftingManager.getInstance().getRecipeList());
            allrecipes.addAll(CraftingHelper.fakeRecipes);
            for (IRecipe irecipe : allrecipes) {
                ShapedSpACoreRecipeHandler.CachedShapedRecipe recipe = null;

                if (irecipe instanceof FakeShapedSpACoreRecipe) {
                    recipe = ((FakeShapedSpACoreRecipe) irecipe).isEnabled() ? new ShapedSpACoreRecipeHandler.CachedShapedRecipe((FakeShapedSpACoreRecipe) irecipe) : null;
                } else if (irecipe instanceof ShapedSpACoreRecipe) {
                    recipe = new ShapedSpACoreRecipeHandler.CachedShapedRecipe((ShapedSpACoreRecipe) irecipe);
                }
                if (recipe == null) {
                    continue;
                }

                recipe.computeVisuals();
                this.arecipes.add(recipe);
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        List<IRecipe> allrecipes = new ArrayList<IRecipe>(CraftingManager.getInstance().getRecipeList());
        allrecipes.addAll(CraftingHelper.fakeRecipes);
        for (IRecipe irecipe : allrecipes) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
                ShapedSpACoreRecipeHandler.CachedShapedRecipe recipe = null;

                if (irecipe instanceof FakeShapedSpACoreRecipe) {
                    recipe = ((FakeShapedSpACoreRecipe) irecipe).isEnabled() ? new ShapedSpACoreRecipeHandler.CachedShapedRecipe((FakeShapedSpACoreRecipe) irecipe) : null;
                } else if (irecipe instanceof ShapedSpACoreRecipe) {
                    recipe = new ShapedSpACoreRecipeHandler.CachedShapedRecipe((ShapedSpACoreRecipe) irecipe);
                }
                if (recipe == null) {
                    continue;
                }
                recipe.computeVisuals();
                this.arecipes.add(recipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<IRecipe> allrecipes = new ArrayList<IRecipe>(CraftingManager.getInstance().getRecipeList());
        allrecipes.addAll(CraftingHelper.fakeRecipes);
        for (IRecipe irecipe : allrecipes) {
            ShapedSpACoreRecipeHandler.CachedShapedRecipe recipe = null;

            if (irecipe instanceof FakeShapedSpACoreRecipe) {
                recipe = ((FakeShapedSpACoreRecipe) irecipe).isEnabled() ? new ShapedSpACoreRecipeHandler.CachedShapedRecipe((FakeShapedSpACoreRecipe) irecipe) : null;
            } else if (irecipe instanceof ShapedSpACoreRecipe) {
                recipe = new ShapedSpACoreRecipeHandler.CachedShapedRecipe((ShapedSpACoreRecipe) irecipe);
            }
            if (recipe == null || !recipe.contains(recipe.ingredients, ingredient)) {
                continue;
            }
            recipe.computeVisuals();
            if (recipe.contains(recipe.ingredients, ingredient) && recipe.recipe.handler.isValidRecipeInput(ingredient)) {
                recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                this.arecipes.add(recipe);
            }
        }
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/crafting_table.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "crafting";
    }

    @Override
    public void drawExtras(int recipeId) {
        super.drawExtras(recipeId);
        ShapedSpACoreRecipeHandler.CachedShapedRecipe recipe = (ShapedSpACoreRecipeHandler.CachedShapedRecipe) this.arecipes.get(recipeId);

        if (recipe != null && recipe.recipe != null && recipe.recipe.handler != null) {
            GuiDraw.drawStringC(recipe.recipe.handler.getOwningModName(), 124, 8, 0x404040, false);
        }
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiCrafting.class;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!NEIClientUtils.shiftKey() && this.cycleticks % 20 == 0) {
            for (TemplateRecipeHandler.CachedRecipe cachedRecipe : this.arecipes) {
                ShapedSpACoreRecipeHandler.CachedShapedRecipe recipe = (ShapedSpACoreRecipeHandler.CachedShapedRecipe) cachedRecipe;

                recipe.getCycledIngredients(this.cycleticks / 20, recipe.ingredients);

                recipe.setResult(recipe.recipe.handler.getOutput(recipe.recipe, recipe.input));
            }
        }
    }

    @Override
    public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
        return super.hasOverlay(gui, container, recipe) || this.isRecipe2x2(recipe) && RecipeInfo.hasDefaultOverlay(gui, "crafting2x2");
    }

    @Override
    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe) {
        IRecipeOverlayRenderer renderer = super.getOverlayRenderer(gui, recipe);
        if (renderer != null) {
            return renderer;
        }

        IStackPositioner positioner = RecipeInfo.getStackPositioner(gui, "crafting2x2");
        if (positioner == null) {
            return null;
        }
        return new DefaultOverlayRenderer(this.getIngredientStacks(recipe), positioner);
    }

    @Override
    public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe) {
        IOverlayHandler handler = super.getOverlayHandler(gui, recipe);
        if (handler != null) {
            return handler;
        }

        return RecipeInfo.getOverlayHandler(gui, "crafting2x2");
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipeId) {
        ShapedSpACoreRecipeHandler.CachedShapedRecipe recipe = (ShapedSpACoreRecipeHandler.CachedShapedRecipe) this.arecipes.get(recipeId);

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

    public boolean isRecipe2x2(int recipe) {
        for (PositionedStack stack : this.getIngredientStacks(recipe)) {
            if (stack.relx > 43 || stack.rely > 24) {
                return false;
            }
        }

        return true;
    }

    public class CachedShapedRecipe extends TemplateRecipeHandler.CachedRecipe {

        public List<PositionedStack> ingredients;
        public List<ItemStack> input;
        public PositionedStack result;
        public ShapedSpACoreRecipe recipe;

        public CachedShapedRecipe(ShapedSpACoreRecipe recipe) {
            this.recipe = recipe;
            this.ingredients = new ArrayList<PositionedStack>();
            this.input = new ArrayList<ItemStack>();
            this.setResult(recipe.getOutput());
            this.setIngredients(recipe.width, recipe.height, recipe.ingredients);
        }

        public void setIngredients(int width, int height, List<ItemStack>[] items) {
            this.input.clear();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (items[y * width + x] == null) {
                        continue;
                    }

                    PositionedStack stack = new PositionedStack(items[y * width + x], 25 + x * 18, 6 + y * 18, false);
                    stack.setMaxSize(1);
                    this.ingredients.add(stack);
                }
            }

            this.getCycledIngredients(ShapedSpACoreRecipeHandler.this.cycleticks / 20, this.ingredients);
            this.setResult(this.recipe.handler.getOutput(this.recipe, this.input));
        }

        @Override
        public List<PositionedStack> getCycledIngredients(int cycle, List<PositionedStack> ingredients) {
            List<PositionedStack> result = super.getCycledIngredients(cycle, ingredients);

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
        public List<PositionedStack> getIngredients() {
            return this.ingredients;
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

        public void computeVisuals() {
            for (PositionedStack p : this.ingredients) {
                p.generatePermutations();
            }

            this.result.generatePermutations();
        }

    }

}
*/
