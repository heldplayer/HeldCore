
package codechicken.nei;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Dummy class
 */
public class MultiItemRange {

    public boolean isItemInRange(int itemid, int damage) {
        return false;
    }

    public String toString() {
        return "";
    }

    public MultiItemRange(String rangestring) {}

    public MultiItemRange() {}

    public MultiItemRange add(ItemRange range) {
        return null;
    }

    public MultiItemRange add(Collection<?> ranges) {
        return null;
    }

    public MultiItemRange add(MultiItemRange range) {
        return null;
    }

    public MultiItemRange add(int itemID) {
        return null;
    }

    public MultiItemRange add(int itemID, int damageStart, int damageEnd) {
        return null;
    }

    public MultiItemRange add(int itemIDFirst, int itemIDLast) {
        return null;
    }

    public MultiItemRange add(Item item, int damageStart, int damageEnd) {
        return null;
    }

    public MultiItemRange add(Block block, int damageStart, int damageEnd) {
        return null;
    }

    public MultiItemRange add(Item item) {
        return null;
    }

    public MultiItemRange add(Block block) {
        return null;
    }

    public MultiItemRange add(ItemStack item) {
        return null;
    }

    public int getNumSlots() {
        return 0;
    }

    public void slotClicked(int slot, int button, boolean doubleclick) {}

    public void hideAllItems() {}

    public void showAllItems() {}

    public int getWidth() {
        return 0;
    }

    public void resetHashes() {}

    public void updateState(ItemVisibilityHash vis) {}

    public void addItemIfInRange(int item, int damage, NBTTagCompound compound) {}

}
