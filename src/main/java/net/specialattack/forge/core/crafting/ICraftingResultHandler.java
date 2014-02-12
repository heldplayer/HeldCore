
package net.specialattack.forge.core.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface ICraftingResultHandler {

    ItemStack getOutput(ISpACoreRecipe recipe, List<ItemStack> input);

    String getOwningModName();

    String getOwningModId();

    boolean isValidRecipeInput(ItemStack input);

    String getNEIOverlayText();

}
