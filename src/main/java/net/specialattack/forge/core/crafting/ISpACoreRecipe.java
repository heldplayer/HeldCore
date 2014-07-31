package net.specialattack.forge.core.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public interface ISpACoreRecipe extends IRecipe {

    /**
     * Use me for getting the output that has not been modified yet
     *
     * @return Output
     */
    ItemStack getOutput();

    /**
     * Used for getting the crafting handler
     *
     * @return The handler
     */
    ICraftingResultHandler getHandler();

}
