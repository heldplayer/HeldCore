
package net.specialattack.forge.core.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ShapelessSpACoreRecipe implements ISpACoreRecipe {

    public ItemStack output = null;
    public ArrayList<List<ItemStack>> ingredients = new ArrayList<List<ItemStack>>();
    public ICraftingResultHandler handler;

    public ItemStack tempOut;

    public ShapelessSpACoreRecipe(ICraftingResultHandler handler, ItemStack output, Object... ingredients) {
        this.handler = handler;

        this.output = output.copy();

        for (Object ingredient : ingredients) {
            if (ingredient instanceof ItemStack) {
                this.ingredients.add(Arrays.asList(((ItemStack) ingredient).copy()));
            }
            else if (ingredient instanceof Item) {
                this.ingredients.add(Arrays.asList(new ItemStack((Item) ingredient)));
            }
            else if (ingredient instanceof Block) {
                this.ingredients.add(Arrays.asList(new ItemStack((Block) ingredient)));
            }
            else if (ingredient instanceof String) {
                this.ingredients.add(OreDictionary.getOres((String) ingredient));
            }
            else if (ingredient instanceof ItemStack[]) {
                this.ingredients.add(Arrays.asList((ItemStack[]) ingredient));
            }
            else {
                StringBuilder str = new StringBuilder("Invalid shapeless SpACore recipe: ");
                for (Object tmp : ingredients) {
                    str.append(tmp).append(", ");
                }
                str.append(this.output);
                throw new RuntimeException(str.toString());
            }
        }
    }

    @Override
    public int getRecipeSize() {
        return this.ingredients.size();
    }

    @Override
    public ItemStack getOutput() {
        return this.output.copy();
    }

    @Override
    public ICraftingResultHandler getHandler() {
        return this.handler;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting) {
        return this.tempOut.copy();
    }

    @Override
    public boolean matches(InventoryCrafting crafting, World world) {
        ArrayList<List<ItemStack>> required = new ArrayList<List<ItemStack>>(this.ingredients);
        this.tempOut = null;
        ArrayList<ItemStack> input = new ArrayList<ItemStack>();

        for (int x = 0; x < crafting.getSizeInventory(); x++) {
            ItemStack slot = crafting.getStackInSlot(x);

            if (slot != null) {
                boolean inRecipe = false;
                Iterator<List<ItemStack>> req = required.iterator();

                while (req.hasNext()) {
                    boolean match = false;

                    List<ItemStack> next = req.next();

                    for (ItemStack item : next) {
                        match = match || this.checkItemEquals(item, slot);
                    }

                    if (match) {
                        inRecipe = true;
                        required.remove(next);
                        input.add(slot.copy());
                        break;
                    }
                }

                if (!inRecipe) {
                    return false;
                }
            }
        }

        if (required.isEmpty()) {
            this.tempOut = this.handler.getOutput(this, input);
            return true;
        }

        return false;
    }

    private boolean checkItemEquals(ItemStack target, ItemStack input) {
        return (target.getItem() == input.getItem() && (target.getItemDamage() == OreDictionary.WILDCARD_VALUE || target.getItemDamage() == input.getItemDamage()));
    }

}
