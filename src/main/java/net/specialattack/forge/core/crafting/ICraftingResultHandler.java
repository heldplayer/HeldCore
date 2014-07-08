package net.specialattack.forge.core.crafting;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface ICraftingResultHandler {

    ItemStack getOutput(ISpACoreRecipe recipe, List<ItemStack> input);

    String getOwningModName();

    String getOwningModId();

    boolean isValidRecipeInput(ItemStack input);

    String getNEIOverlayText();

}
