
package me.heldplayer.util.HeldCore.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public interface IHeldCoreRecipe extends IRecipe {

    /**
     * Use me for getting the output that has not been modified yet
     * 
     * @return
     */
    ItemStack getOutput();

}
