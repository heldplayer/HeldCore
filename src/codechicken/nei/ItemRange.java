
package codechicken.nei;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;

public class ItemRange {

    public ItemRange(int itemID) {}

    public ItemRange(int itemID, int damageStart, int damageEnd) {}

    public ItemRange(int itemIDFirst, int itemIDLast) {}

    public boolean isItemInRange(int id, int damage) {
        return false;
    }

    public String toString() {
        return "";
    }

    public ItemRange(String rangestring) {}

    public synchronized void resetHashes() {}

    public synchronized boolean addItemIfInRange(int item, int damage, NBTTagCompound compound) {
        return false;
    }

    public void onClick(int itemno, int button, boolean doubleclick) {}

    public synchronized void hideAllItems() {}

    public synchronized void showAllItems() {}

    public ArrayList<Integer> toIDList() {
        return null;
    }

}
