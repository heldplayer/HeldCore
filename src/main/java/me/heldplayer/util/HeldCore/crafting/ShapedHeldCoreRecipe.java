
package me.heldplayer.util.HeldCore.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

@SuppressWarnings("unchecked")
public class ShapedHeldCoreRecipe implements IHeldCoreRecipe {

    public ItemStack output = null;
    public List<ItemStack>[] ingredients = null;
    public ICraftingResultHandler handler;
    public ItemStack tempOut;

    public int width = 0;
    public int height = 0;
    public boolean mirrored = true;

    public ShapedHeldCoreRecipe(ICraftingResultHandler handler, ItemStack output, Object... ingredients) {
        this.handler = handler;
        this.output = output.copy();

        String shape = "";
        int idx = 0;

        if (ingredients[idx] instanceof Boolean) {
            this.mirrored = (Boolean) ingredients[idx];
            if (ingredients[idx + 1] instanceof Object[]) {
                ingredients = (Object[]) ingredients[idx + 1];
            }
            else {
                idx = 1;
            }
        }

        if (ingredients[idx] instanceof String[]) {
            String[] parts = ((String[]) ingredients[idx++]);

            for (String s : parts) {
                this.width = s.length();
                shape += s;
            }

            this.height = parts.length;
        }
        else {
            while (ingredients[idx] instanceof String) {
                String s = (String) ingredients[idx++];
                shape += s;
                this.width = s.length();
                this.height++;
            }
        }

        if (this.width * this.height != shape.length()) {
            String ret = "Invalid shaped ore recipe: ";
            for (Object tmp : ingredients) {
                ret += tmp + ", ";
            }
            ret += this.output;
            throw new RuntimeException(ret);
        }

        HashMap<Character, List<ItemStack>> itemMap = new HashMap<Character, List<ItemStack>>();

        for (; idx < ingredients.length; idx += 2) {
            Character chr = (Character) ingredients[idx];
            Object in = ingredients[idx + 1];

            if (in instanceof ItemStack) {
                itemMap.put(chr, Arrays.asList(((ItemStack) in).copy()));
            }
            else if (in instanceof Item) {
                itemMap.put(chr, Arrays.asList(new ItemStack((Item) in)));
            }
            else if (in instanceof Block) {
                itemMap.put(chr, Arrays.asList(new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE)));
            }
            else if (in instanceof String) {
                itemMap.put(chr, OreDictionary.getOres((String) in));
            }
            else {
                String ret = "Invalid shaped ore recipe: ";
                for (Object tmp : ingredients) {
                    ret += tmp + ", ";
                }
                ret += this.output;
                throw new RuntimeException(ret);
            }
        }

        this.ingredients = new List[this.width * this.height];
        int x = 0;
        for (char chr : shape.toCharArray()) {
            this.ingredients[x++] = itemMap.get(chr);
        }
    }

    @Override
    public int getRecipeSize() {
        return this.ingredients.length;
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
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        return this.tempOut.copy();
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        for (int x = 0; x <= 3 - this.width; x++) {
            for (int y = 0; y <= 3 - this.height; ++y) {
                if (this.checkMatch(inv, x, y, false)) {
                    return true;
                }

                if (this.mirrored && this.checkMatch(inv, x, y, true)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
        this.tempOut = null;
        ArrayList<ItemStack> input = new ArrayList<ItemStack>();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int subX = x - startX;
                int subY = y - startY;
                List<ItemStack> target = null;

                if (subX >= 0 && subY >= 0 && subX < this.width && subY < this.height) {
                    if (mirror) {
                        target = this.ingredients[this.width - subX - 1 + subY * this.width];
                    }
                    else {
                        target = this.ingredients[subX + subY * this.width];
                    }
                }

                ItemStack slot = inv.getStackInRowAndColumn(x, y);

                if (target == null && slot != null) {
                    return false;
                }
                else if (target != null) {
                    boolean matched = false;

                    for (ItemStack item : target) {
                        matched = matched || this.checkItemEquals(item, slot);
                    }

                    if (!matched) {
                        return false;
                    }

                    input.add(slot);
                }
            }
        }

        this.tempOut = this.handler.getOutput(this, input);

        return true;
    }

    private boolean checkItemEquals(ItemStack target, ItemStack input) {
        if (input == null && target != null || input != null && target == null) {
            return false;
        }
        return (target.getItem() == input.getItem() && (target.getItemDamage() == OreDictionary.WILDCARD_VALUE || target.getItemDamage() == input.getItemDamage()));
    }

    public ShapedHeldCoreRecipe setMirrored(boolean mirror) {
        this.mirrored = mirror;
        return this;
    }

}
