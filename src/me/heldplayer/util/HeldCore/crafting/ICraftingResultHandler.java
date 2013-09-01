
package me.heldplayer.util.HeldCore.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface ICraftingResultHandler {

    ItemStack getOutput(IHeldCoreRecipe recipe, List<ItemStack> input);

}
