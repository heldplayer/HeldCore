package net.specialattack.forge.core.crafting;

import net.minecraft.item.ItemStack;

@SuppressWarnings("unchecked")
public class FakeShapedSpACoreRecipe extends ShapedSpACoreRecipe implements IFakeRecipe {

    public boolean enabled = true;

    public FakeShapedSpACoreRecipe(ICraftingResultHandler handler, ItemStack output, Object... ingredients) {
        super(handler, output, ingredients);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }



}
